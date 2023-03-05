package de.geolykt.micromixin.internal.util.locals;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import de.geolykt.micromixin.internal.util.ASMUtil;
import de.geolykt.micromixin.internal.util.PrintUtils;
import de.geolykt.micromixin.internal.util.commenttable.CommentTableSection;
import de.geolykt.micromixin.internal.util.commenttable.TabularTableSection;

public class LocalCaptureResult {

    @NotNull
    public final ClassNode sourceOwner;

    @NotNull
    public final MethodNode sourceMethod;

    @Nullable
    public final Throwable error;

    @Nullable
    public final Frame<BasicValue> frame;

    @Nullable
    public final Frame<BasicValue>[] frames;

    public LocalCaptureResult(@NotNull ClassNode sourceOwner, @NotNull MethodNode sourceMethod, @NotNull Throwable error) {
        this.sourceOwner = sourceOwner;
        this.sourceMethod = sourceMethod;
        this.error = error;
        this.frame = null;
        this.frames = null;
    }

    public LocalCaptureResult(@NotNull ClassNode sourceOwner, @NotNull MethodNode sourceMethod, @Nullable Frame<BasicValue> frame, Frame<BasicValue>[] frames) {
        this.sourceOwner = sourceOwner;
        this.sourceMethod = sourceMethod;
        this.error = null;
        this.frame = frame;
        this.frames = frames;
    }

    @NotNull
    public CommentTableSection asLocalPrintTable(@NotNull StringBuilder sharedBuilder) {
        TabularTableSection table = new TabularTableSection();
        Frame<BasicValue> frame = this.frame;
        if (frame == null) {
            if (this.error == null) {
                throw new IllegalStateException("Unreachable instruction.");
            } else {
                throw new IllegalStateException("Error occured during analysis", this.error);
            }
        }
        table.addRow("", "LOCAL", "TYPE", "NAME");
        int maxLocals = frame.getLocals();
        int prettyPrintMax = Math.max(100, maxLocals);
        int initialFrameSize = ASMUtil.getInitialFrameSize(sourceMethod);
        for (int i = 0; i < maxLocals; i++) {
            BasicValue local = frame.getLocal(i);
            String marker = (i == initialFrameSize) ? ">" : ""; // The first captured local is marked with ">".
            String localIndex = PrintUtils.prettyBracketedInt(i, prettyPrintMax, sharedBuilder);
            String type = PrintUtils.prettyType(local.getType(), sharedBuilder);
            String name;
            String capture = (i < initialFrameSize) ? "" : "<capture>"; // All locals that are apart of the initial frame are not captured.
            if (i == 0
                    && (sourceMethod.access & Opcodes.ACC_STATIC) == 0
                    && local.getType().getInternalName().equals(sourceOwner.name)) {
                name = "this";
            } else {
                name = "local" + i;
            }
            table.addRow(marker, localIndex, type, name, capture);
        }
        return table;
    }
}
