package org.stianloader.micromixin.internal.util.locals;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.stianloader.micromixin.internal.util.ASMUtil;
import org.stianloader.micromixin.internal.util.PrintUtils;
import org.stianloader.micromixin.internal.util.commenttable.CommentTableSection;
import org.stianloader.micromixin.internal.util.commenttable.TabularTableSection;

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
        int initialFrameSize = ASMUtil.getInitialFrameSize(this.sourceMethod);
        int reducedIndex = 0;
        for (int i = 0; i < maxLocals; i++) {
            BasicValue local = frame.getLocal(i);
            String marker = (i == initialFrameSize) ? ">" : ""; // The first captured local is marked with ">".
            String localIndex = PrintUtils.prettyBracketedInt(i, prettyPrintMax, sharedBuilder);
            Type localType = local.getType();
            if (localType == null) {
                table.addRow("", localIndex, "", "", "");
                continue;
            }
            String type = PrintUtils.prettyType(localType, sharedBuilder);
            String name;
            boolean noCapture = i < initialFrameSize; // All locals that are apart of the initial frame are not captured.
            String capture = noCapture ? "" : "<capture>";
            if (i == 0
                    && (sourceMethod.access & Opcodes.ACC_STATIC) == 0
                    && localType.getInternalName().equals(this.sourceOwner.name)) {
                name = "this";
            } else {
                if (noCapture) {
                    name = "arg" + reducedIndex++;
                } else {
                    if (i == initialFrameSize) {
                        reducedIndex = 0;
                    }
                    name = "local" + reducedIndex++;
                }
            }
            table.addRow(marker, localIndex, type, name, capture);
            if (ASMUtil.isCategory2(localType.getDescriptor().codePointAt(0))) {
                // Write the second long/double part as for some ungodly reason category 2 types take up 2 values on the stack
                table.addRow("", PrintUtils.prettyBracketedInt(++i, prettyPrintMax, sharedBuilder), "<top>", "", "");
            }
        }
        return table;
    }
}
