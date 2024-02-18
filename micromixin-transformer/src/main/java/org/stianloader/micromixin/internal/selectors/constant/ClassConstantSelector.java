package org.stianloader.micromixin.internal.selectors.constant;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.stianloader.micromixin.internal.annotation.ConstantSelector;

public class ClassConstantSelector extends ConstantSelector {
    @NotNull
    private final String value;

    public ClassConstantSelector(@NotNull String value) {
        this.value = value;
    }

    @Override
    public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
        if (insn.getOpcode() != Opcodes.LDC) {
            return false;
        }

        LdcInsnNode ldcInsn = (LdcInsnNode) insn;
        if (!(ldcInsn.cst instanceof Type)) {
            return false;
        }
        return ((Type) ldcInsn.cst).getDescriptor().equals(this.value);
    }
}