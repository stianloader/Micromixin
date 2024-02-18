package org.stianloader.micromixin.internal.selectors.constant;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.stianloader.micromixin.internal.annotation.ConstantSelector;

public class LongConstantSelector extends ConstantSelector {
    private final long value;

    public LongConstantSelector(long value) {
        this.value = value;
    }

    @Override
    public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
        if (insn instanceof InsnNode) {
            return (insn.getOpcode() == Opcodes.LCONST_0 && this.value == 0)
                    || (insn.getOpcode() == Opcodes.LCONST_1 && this.value == 1);
        } else if (insn instanceof LdcInsnNode) {
            return (((LdcInsnNode) insn).cst instanceof Long) && ((Long) ((LdcInsnNode) insn).cst) == this.value;
        }
        return false;
    }
}