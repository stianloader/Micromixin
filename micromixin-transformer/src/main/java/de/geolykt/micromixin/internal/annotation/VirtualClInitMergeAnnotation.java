package de.geolykt.micromixin.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.HandlerContextHelper;
import de.geolykt.micromixin.internal.MixinMethodStub;
import de.geolykt.micromixin.internal.MixinStub;
import de.geolykt.micromixin.internal.util.CodeCopyUtil;
import de.geolykt.micromixin.internal.util.Remapper;

public class VirtualClInitMergeAnnotation implements MixinAnnotation<MixinMethodStub> {

    @NotNull
    private final MethodNode src;

    public VirtualClInitMergeAnnotation(@NotNull MethodNode src) {
        this.src = src;
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
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
        AbstractInsnNode last = target.instructions.getLast();
        while (last.getOpcode() == -1) {
            last = last.getPrevious();
        }
        if (last.getOpcode() != Opcodes.RETURN) {
            throw new IllegalStateException("Invalid clinit block: " + to.name + "." + target.name + target.desc + ": Last instruction should be a RETURN opcode.");
        }
        CodeCopyUtil.copyTo(this.src, sourceStub, target, last, to, remapper);
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        // NOP
    }
}
