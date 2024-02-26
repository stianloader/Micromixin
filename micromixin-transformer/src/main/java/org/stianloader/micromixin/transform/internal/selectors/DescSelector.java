package org.stianloader.micromixin.transform.internal.selectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.stianloader.micromixin.transform.SimpleRemapper;
import org.stianloader.micromixin.transform.api.InjectionPointTargetConstraint;
import org.stianloader.micromixin.transform.internal.MemberDesc;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.annotation.MixinDescAnnotation;

public class DescSelector implements MixinTargetSelector, InjectionPointTargetConstraint {

    @NotNull
    private final MixinDescAnnotation desc;

    public DescSelector(@NotNull MixinDescAnnotation desc) {
        this.desc = desc;
    }

    @Override
    @Nullable
    public MethodNode selectMethod(@NotNull ClassNode within, @NotNull MixinStub source) {
        if (this.desc.target.desc.codePointAt(0) != '(' || !(this.desc.target.owner.equals(within.name) || this.desc.target.owner.equals(source.sourceNode.name))) {
            return null;
        }
        for (MethodNode method : within.methods) {
            if (this.desc.target.desc.equals(method.desc)
                    && this.desc.target.name.equals(method.name)) {
                return method;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DescSelector[desc = " + this.desc + "]";
    }

    @Override
    public boolean isValid(@NotNull AbstractInsnNode insn, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        MemberDesc target = this.desc.target.remap(remapper, sharedBuilder);
        if (insn instanceof MethodInsnNode) {
            MethodInsnNode mInsn = (MethodInsnNode) insn;
            return target.owner.equals(mInsn.owner)
                    && target.name.equals(mInsn.name)
                    && target.desc.equals(mInsn.desc);
        } else if (insn instanceof TypeInsnNode) {
            Type owner = this.desc.owner;
            return (owner != null && remapper.remapSingleDesc(owner.getDescriptor(), sharedBuilder).equals(((TypeInsnNode)insn).desc))
                    || remapper.remapSingleDesc(this.desc.value, sharedBuilder).equals(((TypeInsnNode)insn).desc)
                    || ('L' + remapper.remapInternalName(this.desc.value, sharedBuilder) + ';').equals(((TypeInsnNode)insn).desc);
        } else if (insn instanceof FieldInsnNode) {
            FieldInsnNode fInsn = (FieldInsnNode) insn;
            return target.owner.equals(fInsn.owner)
                    && target.name.equals(fInsn.name)
                    && target.desc.equals(fInsn.desc);
        }
        throw new IllegalArgumentException("Instructions of type " + insn.getClass().getName() + " cannot be verified by " + this.toString() + ". This indicates a bug in Micromixin or one of custom-made injection point selectors (should those be present).");
    }
}
