package org.stianloader.micromixin.transform.internal.selectors.constant;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.stianloader.micromixin.transform.internal.annotation.ConstantSelector;

public class StringConstantSelector extends ConstantSelector {
    @NotNull
    private final String value;

    public StringConstantSelector(@NotNull String value) {
        this.value = value;
    }

    @Override
    public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
        return insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode) insn).cst.equals(this.value);
    }
}