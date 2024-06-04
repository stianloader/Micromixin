package org.stianloader.micromixin.transform.internal.util.smap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.internal.util.Objects;
import org.stianloader.micromixin.transform.internal.util.smap.FileSection.FileSectionEntry;
import org.stianloader.micromixin.transform.internal.util.smap.LineSection.LineInfo;

/**
 * The {@link MultiplexLineNumberAllocator} "multiplexes" the {@link LineNumberNode LineNumberNodes} of multiple
 * classes into a continuous stream of line numbers. While the {@link MultiplexLineNumberAllocator} tries
 * it's best to keep the line numbers as compact as possible, it is inevitably going to be a bit inefficient
 * for the sake of the sanity of the developers (i.e. me) and maintainers (i.e. me).
 */
public class MultiplexLineNumberAllocator {

    private static class LineAllocationFrame {
        private final int allocatedStart;
        private final int allocationSize;
        @NotNull
        private final ClassNode allocator;
        private final int allocatorStart;

        public LineAllocationFrame(@NotNull ClassNode allocator, int size, int allocatedStart, int allocatorStart) {
            this.allocator = allocator;
            this.allocationSize = size;
            this.allocatedStart = allocatedStart;
            this.allocatorStart = allocatorStart;
        }
    }

    @NotNull
    private final List<LineAllocationFrame> allocationFrames = new ArrayList<LineAllocationFrame>();
    private int currentAllocatedStart;
    private int currentAllocationSize = -1;
    @NotNull
    private ClassNode currentAllocatorNode;
    private int currentAllocatorStart;
    @NotNull
    private final List<LineNumberNode> currentRangeNodes = new ArrayList<LineNumberNode>();
    @NotNull
    private final Map<String, Integer> fileIds = new HashMap<String, Integer>();

    @NotNull
    private final String primaryFileName;

    public MultiplexLineNumberAllocator(@NotNull ClassNode primaryNode) {
        String primaryFileName = primaryNode.sourceFile;
        if (primaryFileName == null) {
            primaryFileName = "sourceFile";
        }
        this.primaryFileName = primaryFileName;
        this.currentAllocatorNode = primaryNode;
        this.currentAllocatedStart = 1;
        this.currentAllocatorStart = 1;
        for (MethodNode method : primaryNode.methods) {
            for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (insn instanceof LineNumberNode) {
                    this.currentAllocationSize = Math.max(this.currentAllocationSize, ((LineNumberNode) insn).line);
                }
            }
        }
        this.flushFrame();
    }

    @NotNull
    public SMAPRoot exportToSMAP(@NotNull String stratum) {
        this.flushFrame();
        SMAPRoot smap = new SMAPRoot(primaryFileName, stratum);
        List<FileSectionEntry> fileEntries = new ArrayList<FileSectionEntry>();
        List<LineInfo> lineInfos = new ArrayList<LineInfo>();
        for (LineAllocationFrame frame : this.allocationFrames) {
            String fileName = frame.allocator.sourceFile;
            if (fileName == null) {
                fileName = "sourceFile";
            }
            String sourcePath = frame.allocator.nestHostClass;
            if (sourcePath == null) {
                sourcePath = frame.allocator.outerClass;
                if (sourcePath == null) {
                    sourcePath = frame.allocator.name;
                }
            }
            int firstDollar = sourcePath.indexOf('$');
            if (firstDollar != -1) {
                sourcePath = sourcePath.substring(0, firstDollar);
            }
            sourcePath += ".java";
            int fileId = this.fileIds.get(frame.allocator.name).intValue();
            fileEntries.add(new FileSectionEntry(fileId, fileName, sourcePath));
            lineInfos.add(new LineInfo(frame.allocatorStart, fileId, frame.allocationSize, 1, frame.allocatedStart));
        }
        FileSection fileSection = new FileSection(fileEntries);
        LineSection lineSection = new LineSection(lineInfos);
        smap.appendStratum(stratum, fileSection, lineSection, "Stianloader Micromixin");
        return smap;
    }

    private void flushFrame() {
        if (this.currentAllocationSize == -1) {
            return; // Nothing to flush
        }
        if (!this.fileIds.containsKey(this.currentAllocatorNode.name)) {
            this.fileIds.put(this.currentAllocatorNode.name, this.fileIds.size() + 1);
        }
        this.allocationFrames.add(new LineAllocationFrame(this.currentAllocatorNode, this.currentAllocationSize, this.currentAllocatedStart, this.currentAllocatorStart));
        this.currentAllocatedStart += this.currentAllocationSize + 1;
        this.currentAllocatorStart = 1;
        this.currentRangeNodes.clear();
        this.currentAllocationSize = -1;
    }

    private void growAllocatorRangeBackwards(int shift) {
        if (shift < 0) {
            throw new IllegalArgumentException("shift < 0");
        }
        if (this.currentAllocatorStart - shift < 0) {
            throw new IllegalArgumentException("this.currentAllocatorStart - shift < 0");
        }
        this.currentAllocatorStart -= shift;
        this.currentAllocationSize += shift;
        for (LineNumberNode lnn : this.currentRangeNodes) {
            lnn.line += shift;
        }
    }

    @NotNull
    @Contract(mutates = "this", pure = false, value = "!null, !null -> new; null, _ -> fail; _, null -> fail")
    public LineNumberNode reserve(@NotNull ClassNode allocator, @NotNull LineNumberNode originNode, @NotNull LabelNode copiedStart) {
        if (this.currentAllocatorNode != allocator) {
            this.currentAllocatorNode = Objects.requireNonNull(allocator, "allocator may not be null");
            this.flushFrame();
        }
        if (this.currentAllocationSize == -1) {
            this.currentAllocatorStart = originNode.line;
            this.currentAllocationSize = 0;
        } else if (originNode.line < this.currentAllocatorStart) {
            this.growAllocatorRangeBackwards(this.currentAllocatorStart - originNode.line);
        } else if (this.currentAllocatorStart + this.currentAllocationSize < originNode.line) {
            this.currentAllocationSize = originNode.line - this.currentAllocatorStart;
        }
        LineNumberNode copied = new LineNumberNode(this.currentAllocatedStart + originNode.line - this.currentAllocatorStart, copiedStart);
        this.currentRangeNodes.add(copied);
        return copied;
    }
}
