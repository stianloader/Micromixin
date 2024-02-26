package org.stianloader.micromixin.transform.internal.selectors.constant;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.stianloader.micromixin.transform.internal.annotation.ConstantSelector;

public class FloatConstantSelector extends ConstantSelector {
    private final float value;

    public FloatConstantSelector(float value) {
        this.value = value;
    }

    @Override
    public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
        if (insn instanceof InsnNode) {
            return (insn.getOpcode() == Opcodes.FCONST_0 && this.value == 0)
                    || (insn.getOpcode() == Opcodes.FCONST_1 && this.value == 1)
                    || (insn.getOpcode() == Opcodes.FCONST_2 && this.value == 2);
        } else if (insn instanceof LdcInsnNode) {
            return (((LdcInsnNode) insn).cst instanceof Float) && ((Float) ((LdcInsnNode) insn).cst) == this.value;
        }
        return false;
    }
}