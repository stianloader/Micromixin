package org.stianloader.micromixin.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.SimpleRemapper;
import org.stianloader.micromixin.internal.HandlerContextHelper;
import org.stianloader.micromixin.internal.MixinMethodStub;
import org.stianloader.micromixin.internal.MixinStub;
import org.stianloader.micromixin.internal.util.CodeCopyUtil;

public class VirtualClInitMergeAnnotation extends MixinAnnotation<MixinMethodStub> {

    @NotNull
    private final MethodNode src;

    public VirtualClInitMergeAnnotation(@NotNull MethodNode src) {
        this.src = src;
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        MethodNode target = null;
        for (MethodNode method : to.methods) {
            if (method.name.equals("<clinit>") && method.desc.equals("()V")) {
                target = method;
                break;
            }
        }
        if (target == null) {
            target = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            target.instructions.add(new InsnNode(Opcodes.RETURN));
        }
        AbstractInsnNode nextOutInsn = target.instructions.getLast();
        while (nextOutInsn.getOpcode() == -1) {
            nextOutInsn = nextOutInsn.getPrevious();
        }
        if (nextOutInsn.getOpcode() != Opcodes.RETURN) {
            throw new IllegalStateException("Invalid clinit block: " + to.name + "." + target.name + target.desc + ": Last instruction should be a RETURN opcode.");
        }
        AbstractInsnNode previousOutInsn = nextOutInsn.getPrevious();
        if (previousOutInsn == null) {
            previousOutInsn = new LabelNode();
            target.instructions.insertBefore(nextOutInsn, previousOutInsn);
        }
        AbstractInsnNode startInInsn = src.instructions.getFirst();
        AbstractInsnNode endInInsn = src.instructions.getLast();
        if (startInInsn == null || endInInsn == null) {
            throw new IllegalStateException("abstract clinit method: " + source.owner.name + "." + source.getName() + source.getDesc());
        }
        CodeCopyUtil.copyTo(src, startInInsn, endInInsn, sourceStub, target, previousOutInsn, to, remapper, hctx.lineAllocator, true, false);
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        // NOP
    }
}
