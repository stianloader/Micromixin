package org.stianloader.micromixin.transform.internal.annotation;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.util.CodeCopyUtil;

public class VirtualClInitMergeAnnotation extends MixinAnnotation<MixinMethodStub> {

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
            to.methods.add(target);
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
        AbstractInsnNode startInInsn = source.method.instructions.getFirst();
        AbstractInsnNode endInInsn = source.method.instructions.getLast();
        if (startInInsn == null || endInInsn == null) {
            throw new IllegalStateException("Abstract clinit method: " + source.owner.name + "." + source.getName() + source.getDesc());
        }
        Map<LabelNode, LabelNode> labelMap = new HashMap<LabelNode, LabelNode>();
        CodeCopyUtil.copyTo(source.method, startInInsn, endInInsn, sourceStub, target, previousOutInsn, to, remapper, hctx.lineAllocator, labelMap, true, false);
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        // NOP
    }
}
