package org.stianloader.micromixin.transform.internal.selectors.constant;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.stianloader.micromixin.transform.internal.annotation.ConstantSelector;

/**
 * A special type of {@link ConstantSelector} which selects all constants for a given type.
 */
public class WildcardConstantSelector extends ConstantSelector {

    private final int type;
    @NotNull
    private final String typeDescriptor;

    public WildcardConstantSelector(@NotNull String typeDescriptor) {
        this.type = typeDescriptor.codePointAt(0);
        this.typeDescriptor = typeDescriptor;
    }

    @Override
    public boolean matchesConstant(@NotNull AbstractInsnNode insn) {
        if (insn instanceof InsnNode) {
            if (this.type == 'I') {
                return insn.getOpcode() >= Opcodes.ICONST_M1 && insn.getOpcode() <= Opcodes.ICONST_5;
            } else if (this.type == 'J') {
                return insn.getOpcode() == Opcodes.LCONST_0 || insn.getOpcode() == Opcodes.LCONST_1;
            } else if (this.type == 'D') {
                return insn.getOpcode() == Opcodes.DCONST_0 || insn.getOpcode() == Opcodes.DCONST_1;
            } else if (this.type == 'F') {
                return insn.getOpcode() == Opcodes.FCONST_0
                        || insn.getOpcode() == Opcodes.FCONST_1
                        || insn.getOpcode() == Opcodes.FCONST_2;
            } else if (this.type == 'L') {
                return insn.getOpcode() == Opcodes.ACONST_NULL;
            }
        } else if (insn instanceof LdcInsnNode) {
            Object ldcCst = ((LdcInsnNode) insn).cst;
            if (this.type == 'I') {
                return ldcCst instanceof Integer;
            } else if (this.type == 'J') {
                return ldcCst instanceof Long;
            } else if (this.type == 'D') {
                return ldcCst instanceof Double;
            } else if (this.type == 'F') {
                return ldcCst instanceof Float;
            } else if (this.type == 'L') {
                if (ldcCst instanceof String) {
                    return this.typeDescriptor.equals("Ljava/lang/String;");
                } else if (ldcCst instanceof Type) {
                    return this.typeDescriptor.equals("Ljava/lang/Class;");
                }
            }
        } else if (insn instanceof IntInsnNode && this.type == 'I') {
            return insn.getOpcode() == Opcodes.BIPUSH || insn.getOpcode() == Opcodes.SIPUSH;
        }
        return false;
    }

}
