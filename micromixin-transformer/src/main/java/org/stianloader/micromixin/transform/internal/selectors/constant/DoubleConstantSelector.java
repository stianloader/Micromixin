package org.stianloader.micromixin.transform.internal.selectors.constant;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.stianloader.micromixin.transform.internal.annotation.ConstantSelector;

public class DoubleConstantSelector extends ConstantSelector {
    private final double value;

    public DoubleConstantSelector(double value) {
        this.value = value;
    }

    @Override
    public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
        if (insn instanceof InsnNode) {
            return (insn.getOpcode() == Opcodes.DCONST_0 && this.value == 0)
                    || (insn.getOpcode() == Opcodes.DCONST_1 && this.value == 1);
        } else if (insn instanceof LdcInsnNode) {
            return (((LdcInsnNode) insn).cst instanceof Double) && ((Double) ((LdcInsnNode) insn).cst) == this.value;
        }
        return false;
    }
}
