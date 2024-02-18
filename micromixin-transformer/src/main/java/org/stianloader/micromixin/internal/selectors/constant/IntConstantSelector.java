package org.stianloader.micromixin.internal.selectors.constant;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.stianloader.micromixin.internal.annotation.ConstantSelector;

public class IntConstantSelector extends ConstantSelector {
    private final int value;

    public IntConstantSelector(int value) {
        this.value = value;
    }

    @Override
    public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
        if (insn instanceof InsnNode) {
            if (this.value < -1 || this.value > 5) {
                return false;
            }
            return (insn.getOpcode() == Opcodes.ICONST_0 && this.value == 0)
                    || (insn.getOpcode() == Opcodes.ICONST_1 && this.value == 1)
                    || (insn.getOpcode() == Opcodes.ICONST_2 && this.value == 2)
                    || (insn.getOpcode() == Opcodes.ICONST_3 && this.value == 3)
                    || (insn.getOpcode() == Opcodes.ICONST_4 && this.value == 4)
                    || (insn.getOpcode() == Opcodes.ICONST_5 && this.value == 5)
                    || (insn.getOpcode() == Opcodes.ICONST_M1 && this.value == -1);
        } else if (insn instanceof LdcInsnNode) {
            return (((LdcInsnNode) insn).cst instanceof Integer) && ((Integer) ((LdcInsnNode) insn).cst) == this.value;
        } else if (insn instanceof IntInsnNode) {
            return (insn.getOpcode() == Opcodes.BIPUSH || insn.getOpcode() == Opcodes.SIPUSH) && ((IntInsnNode) insn).operand == value;
        }
        return false;
    }
}