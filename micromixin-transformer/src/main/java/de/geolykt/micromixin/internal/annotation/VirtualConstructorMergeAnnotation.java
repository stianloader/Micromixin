package de.geolykt.micromixin.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import de.geolykt.micromixin.internal.HandlerContextHelper;
import de.geolykt.micromixin.internal.MixinMethodStub;
import de.geolykt.micromixin.internal.MixinStub;
import de.geolykt.micromixin.internal.util.CodeCopyUtil;
import de.geolykt.micromixin.internal.util.Remapper;

public class VirtualConstructorMergeAnnotation implements MixinAnnotation<MixinMethodStub> {

    @NotNull
    private static MethodInsnNode getConstructorInvokeInsn(@NotNull ClassNode node, @NotNull MethodNode source) {
        AbstractInsnNode insn = source.instructions.getFirst();
        int newDepth = 0;
        while (insn != null) {
            if (insn.getOpcode() != Opcodes.INVOKESPECIAL) {
                if (insn.getOpcode() == Opcodes.NEW &&
                        (((TypeInsnNode) insn).desc.equals(node.superName) || ((TypeInsnNode) insn).desc.equals(node.name))) {
                    newDepth++;
                }
                insn = insn.getNext();
                continue;
            }
            if (((MethodInsnNode) insn).name.equals("<init>") && (((MethodInsnNode) insn).owner.equals(node.superName) || ((MethodInsnNode) insn).owner.equals(node.name))) {
                AbstractInsnNode prev = insn.getPrevious();
                while (prev != null && prev.getOpcode() == -1) {
                    prev = prev.getPrevious();
                }
                if (prev == null) {
                    throw new IllegalStateException("The first instruction of a mixin is a constructor invocation. This does not make much sense!");
                }
                if (newDepth != 0) {
                    newDepth--;
                    insn = insn.getNext();
                    continue;
                }
                break;
            }
            insn = insn.getNext();
        }
        if (insn == null) {
            throw new NullPointerException("Instructions exhausted");
        } else {
            return (MethodInsnNode) insn;
        }
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        if (!source.method.desc.equals("()V")) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + source.method.name + source.method.desc + ". Expected no-args constructor!");
        }
        AbstractInsnNode firstInsn = getConstructorInvokeInsn(source.owner, source.method).getNext();
        if (firstInsn == null) {
            return; // Nothing to merge
        }
        AbstractInsnNode lastInsn = source.method.instructions.getLast();
        if (lastInsn == null) {
            throw new AssertionError(); // Sadly `InsnList#getLast` is not nullable due to empty lists being a thing.
        }
        for (MethodNode m : to.methods) {
            if (m.name.equals("<init>")) {
                MethodInsnNode targetInsn = getConstructorInvokeInsn(to, m);
                if (targetInsn.owner.equals(to.superName)) {
                    CodeCopyUtil.copyTo(source.method, firstInsn, lastInsn, sourceStub, m, targetInsn, to, remapper, true);
                }
            }
        }
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        // NOP
    }
}
