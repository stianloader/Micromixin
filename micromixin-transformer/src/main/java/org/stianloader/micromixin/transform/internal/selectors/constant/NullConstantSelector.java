package org.stianloader.micromixin.transform.internal.selectors.constant;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.stianloader.micromixin.transform.internal.annotation.ConstantSelector;

public class NullConstantSelector extends ConstantSelector {

    @NotNull
    public static final ConstantSelector INSTANCE = new NullConstantSelector();

    private NullConstantSelector() {
        // Reduced visibility to reduce allocation rates (at the cost of increased flat memory expenditures)
    }

    @Override
    public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
        return insn.getOpcode() == Opcodes.ACONST_NULL;
    }
}