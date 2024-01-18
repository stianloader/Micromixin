package org.stianloader.micromixin.internal.annotation;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.stianloader.micromixin.internal.MixinParseException;

public abstract class ConstantSelector {

    @NotNull
    public static ConstantSelector parse(@NotNull List<String> args) {
        for (String s : args) {
            int equalIndex = s.indexOf('=');
            if (equalIndex == -1) {
                continue;
            }
            String key = s.substring(0, equalIndex);
            String value = s.substring(equalIndex + 1);
            if (key.equals("nullValue") && value.equals("true")) {
                return new NullConstantSelector();
            } else if (key.equals("intValue")) {
                return new IntConstantSelector(Integer.parseInt(value));
            } else if (key.equals("stringValue")) {
                return new StringConstantSelector(value);
            }
        }
        throw new MixinParseException("Cannot find any constant values in @At(\"CONSTANT\") args. An example would be @At(value = \"CONSTANT\", args = {\"intValue=5\"}). Note: Whitespaces are not allowed between either side of the equals.");
    }

    public abstract boolean matchesConstant(@NotNull AbstractInsnNode insn);

    public static class NullConstantSelector extends ConstantSelector {
        @Override
        public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
            return insn.getOpcode() == Opcodes.ACONST_NULL;
        }
    }

    public static class IntConstantSelector extends ConstantSelector {
        private final int value;

        public IntConstantSelector(int value) {
            this.value = value;
        }

        @Override
        public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
            if (insn instanceof InsnNode) {
                if (this.value < 1 || this.value > 5) {
                    return false;
                }
                return (insn.getOpcode() == Opcodes.ICONST_0 && value == 0)
                        || (insn.getOpcode() == Opcodes.ICONST_1 && value == 1)
                        || (insn.getOpcode() == Opcodes.ICONST_2 && value == 2)
                        || (insn.getOpcode() == Opcodes.ICONST_3 && value == 3)
                        || (insn.getOpcode() == Opcodes.ICONST_4 && value == 4)
                        || (insn.getOpcode() == Opcodes.ICONST_5 && value == 5)
                        || (insn.getOpcode() == Opcodes.ICONST_M1 && value == -1);
            } else if (insn instanceof LdcInsnNode) {
                return (((LdcInsnNode) insn).cst instanceof Integer) && ((Integer) ((LdcInsnNode) insn).cst) == this.value;
            } else if (insn instanceof IntInsnNode) {
                return (insn.getOpcode() == Opcodes.BIPUSH || insn.getOpcode() == Opcodes.SIPUSH) && ((IntInsnNode) insn).operand == value;
            }
            return false;
        }
    }

    public static class StringConstantSelector extends ConstantSelector {
        @NotNull
        private final String value;

        public StringConstantSelector(@NotNull String value) {
            this.value = value;
        }

        @Override
        public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
            return insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode) insn).cst.equals(value);
        }
    }
}
