package de.geolykt.micromixin.internal.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.util.locals.LocalsCapture;
import de.geolykt.micromixin.supertypes.ClassWrapperPool;

public class ASMUtil {

    public static final String CALLBACK_INFO_NAME = "org/spongepowered/asm/mixin/injection/callback/CallbackInfo";
    public static final String CALLBACK_INFO_DESC = "L" + CALLBACK_INFO_NAME + ";";
    public static final String CALLBACK_INFO_RETURNABLE_NAME = "org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable";
    public static final String CALLBACK_INFO_RETURNABLE_DESC = "L" + CALLBACK_INFO_RETURNABLE_NAME + ";";
    public static final int CI_LEN = CALLBACK_INFO_NAME.length();
    public static final int CIR_LEN = CALLBACK_INFO_RETURNABLE_NAME.length();

    /**
     * Obtain the amount of arguments a method descriptor defines, irrespective of
     * their size on the stack (so both int and long are counted as 1).
     *
     * @param methodDesc The method descriptor to use
     * @return The amount of arguments
     */
    public static int getArgumentCount(@NotNull String methodDesc) {
        DescString dstring = new DescString(methodDesc);
        int count = 0;
        while (dstring.hasNext()) {
            dstring.nextType();
            count++;
        }
        return count;
    }

    @Nullable
    public static FieldNode getField(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        for (FieldNode field : node.fields) {
            if (field.name.equals(name) && field.desc.equals(desc)) {
                return field;
            }
        }
        return null;
    }

    public static int getInitialFrameSize(@NotNull MethodNode method) {
        int initialFrameSize = 0;
        if ((method.access & Opcodes.ACC_STATIC) == 0) {
            initialFrameSize++;
        }
        DescString descString = new DescString(method.desc);
        while (descString.hasNext()) {
            initialFrameSize++;
            if (ASMUtil.isCategory2(descString.nextReferenceType())) {
                initialFrameSize++;
            }
        }
        return initialFrameSize;
    }

    @NotNull
    public static LabelNode getLabelNodeBefore(@NotNull AbstractInsnNode insn, @NotNull InsnList merge) {
        AbstractInsnNode lookbehind = insn.getPrevious();
        while (lookbehind.getOpcode() == -1 && !(lookbehind instanceof LabelNode)) {
            lookbehind = lookbehind.getPrevious();
            if (lookbehind == null) {
                // reached beginning of list (insn was first instruction - unlikely but possible with stuff like constants being used in an INVOKESTATIC context)
                LabelNode l = new LabelNode();
                merge.insert(l);
                return l;
            }
        }
        if (lookbehind instanceof LabelNode) {
            return (LabelNode) lookbehind;
        }
        LabelNode l = new LabelNode();
        merge.insertBefore(insn, l);
        return l;
    }

    @Nullable
    public static String getLastType(@NotNull String methodDesc) {
        DescString dstring = new DescString(methodDesc);
        String last = null;
        while (dstring.hasNext()) {
            last = dstring.nextType();
        }
        return last;
    }

    public static int getLoadOpcode(int descReturnType) {
        switch (descReturnType) {
        case '[': // array
        case 'L': // object
            return Opcodes.ALOAD;
        case 'I': // int
        case 'S': // short
        case 'C': // char
        case 'Z': // boolean
        case 'B': // byte
            return Opcodes.ILOAD;
        case 'J': // long
            return Opcodes.LLOAD;
        case 'F': // float
            return Opcodes.FLOAD;
        case 'D': // double
            return Opcodes.DLOAD;
        default:
            throw new IllegalStateException("Unknown return type: " + descReturnType + " (" + ((char) descReturnType) + ")");
        }
    }

    @Nullable
    public static MethodNode getMethod(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        for (MethodNode method : node.methods) {
            if (method.name.equals(name) && method.desc.equals(desc)) {
                return method;
            }
        }
        return null;
    }

    @NotNull
    public static AbstractInsnNode getNext(AbstractInsnNode insn) {
        AbstractInsnNode next = insn.getNext();
        while (next.getOpcode() == -1) {
            next = next.getNext();
        }
        return next;
    }

    public static int getReturnOpcode(int descReturnType) {
        switch (descReturnType) {
        case 'V': // void
            return Opcodes.RETURN;
        case '[': // array
        case 'L': // object
            return Opcodes.ARETURN;
        case 'I': // int
        case 'S': // short
        case 'C': // char
        case 'Z': // boolean
        case 'B': // byte
            return Opcodes.IRETURN;
        case 'J': // long
            return Opcodes.LRETURN;
        case 'F': // float
            return Opcodes.FRETURN;
        case 'D': // double
            return Opcodes.DRETURN;
        default:
            throw new IllegalStateException("Unknown return type: " + descReturnType + " (" + ((char) descReturnType) + ")");
        }
    }

    /**
     * Obtains the descriptor type returned by a method descriptor.
     * Throws an arbitrary exception if the descriptor is not a method descriptor.
     *
     * @param methodDesc The method descriptor to get the return type of.
     * @return The returned type of the method
     */
    @NotNull
    public static String getReturnType(String methodDesc) {
        return methodDesc.substring(methodDesc.lastIndexOf(')') + 1);
    }

    public static int getStoreOpcode(int descReturnType) {
        switch (descReturnType) {
        case '[': // array
        case 'L': // object
            return Opcodes.ASTORE;
        case 'I': // int
        case 'S': // short
        case 'C': // char
        case 'Z': // boolean
        case 'B': // byte
            return Opcodes.ISTORE;
        case 'J': // long
            return Opcodes.LSTORE;
        case 'F': // float
            return Opcodes.FSTORE;
        case 'D': // double
            return Opcodes.DSTORE;
        default:
            throw new IllegalStateException("Unknown return type: " + descReturnType + " (" + ((char) descReturnType) + ")");
        }
    }

    @NotNull
    public static String getTargetDesc(@NotNull MethodNode method, @NotNull StringBuilder sharedBuilder) {
        // To be fair, I am not really honest if argument capture has any effect on the selected method.
        sharedBuilder.setLength(0);
        sharedBuilder.append('(');
        DescString dstring = new DescString(method.desc);
        boolean ciPresent = false;
        while (dstring.hasNext()) {
            String selected = dstring.nextType();
            if (selected.equals(CALLBACK_INFO_RETURNABLE_DESC) || selected.equals(CALLBACK_INFO_DESC)) {
                ciPresent = true;
                break;
            }
        }
        sharedBuilder.append(")V"); // Void methods are targeted unless otherwise specified
        if (!ciPresent) {
            throw new MixinParseException("Method " + method.name + method.desc + " should have a CallbackInfo or a CallbackInfoReturnable as one of it's arguments. But it does not.");
        }
        return sharedBuilder.toString();
    }

    public static boolean hasField(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        // TODO Also check supers?
        return getField(node, name, desc) != null;
    }

    public static boolean hasMethod(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        // TODO Also check supers? (and interfaces too?)
        return getMethod(node, name, desc) != null;
    }

    public static boolean hasReducedAccess(int witnessAccess, int testAccess) {
        if ((witnessAccess & Opcodes.ACC_PUBLIC) != 0) {
            return (testAccess & Opcodes.ACC_PUBLIC) == 0;
        } else if ((witnessAccess & Opcodes.ACC_PROTECTED) != 0) {
            return (testAccess & (Opcodes.ACC_PROTECTED | Opcodes.ACC_PUBLIC)) == 0;
        } else if ((witnessAccess & Opcodes.ACC_PRIVATE) != 0) {
            return false; // You can't go below private
        } else {
            // Package-protected
            return (testAccess & Opcodes.ACC_PRIVATE) != 0;
        }
    }

    public static boolean isCategory2(int descType) {
        return descType == 'J' || descType == 'D';
    }

    public static boolean isReturn(int opcode) {
        return Opcodes.IRETURN <= opcode && opcode <= Opcodes.RETURN;
    }

    public static int popReturn(@NotNull String methodDesc) {
        switch (methodDesc.codePointBefore(methodDesc.length())) {
        case ';': // Object or array
        case 'I': // int
        case 'S': // short
        case 'C': // char
        case 'Z': // boolean
        case 'B': // byte
        case 'F': // float
            return Opcodes.POP; // Category 1 (1 slot in operand stack)
        case 'J': // long
        case 'D': // double
            return Opcodes.POP2; // Category 2 (2 slots in operand stack)
        case 'V': // void (nothing to pop)
            return Opcodes.NOP;
        default:
            throw new IllegalStateException("Unable to infer the required pop opcode corresponding to the return type of method descriptor: " + methodDesc);
        }
    }

    public static void shiftDownByDesc(@NotNull String desc, boolean category2, @NotNull ClassNode owner, @NotNull MethodNode target, @NotNull AbstractInsnNode previousInsn, @NotNull ClassWrapperPool cwPool) {
        InsnList inject = new InsnList();
        Frame<BasicValue> frame = LocalsCapture.captureLocals(owner, target, previousInsn, cwPool).frame;
        int startIndex;
        if (frame == null) {
            AbstractInsnNode insn = target.instructions.getFirst();
            startIndex = ASMUtil.getInitialFrameSize(target);
            while (insn != null) {
                if (insn instanceof VarInsnNode) {
                    int var = ((VarInsnNode) insn).var;
                    if (insn.getOpcode() == Opcodes.DSTORE
                            || insn.getOpcode() == Opcodes.LSTORE
                            || insn.getOpcode() == Opcodes.DLOAD
                            || insn.getOpcode() == Opcodes.LLOAD) {
                        var++;
                    }
                    startIndex = Math.max(var, startIndex);
                }
                insn = insn.getNext();
            }
        } else {
            startIndex = frame.getLocals();
        }

        int[] typesReverse;

        {
            int i = 0;
            DescString dString = new DescString(desc);
            while (dString.hasNext()) {
                i++;
                dString.nextReferenceType();
            }
            typesReverse = new int[i];
            dString.reset();
            while (dString.hasNext()) {
                typesReverse[--i] = dString.nextReferenceType();
            }
        }

        int storeIndex = startIndex;
        for (int type : typesReverse) {
            if (ASMUtil.isCategory2(type)) {
                if (category2) {
                    inject.add(new InsnNode(Opcodes.DUP2_X2));
                    inject.add(new InsnNode(Opcodes.POP2));
                } else {
                    inject.add(new InsnNode(Opcodes.DUP_X2));
                    inject.add(new InsnNode(Opcodes.POP));
                }
                storeIndex += 2;
            } else {
                if (category2) {
                    inject.add(new InsnNode(Opcodes.DUP2_X1));
                    inject.add(new InsnNode(Opcodes.POP2));
                } else {
                    inject.add(new InsnNode(Opcodes.DUP_X1));
                    inject.add(new InsnNode(Opcodes.POP));
                }
                storeIndex++;
            }
            inject.add(new VarInsnNode(ASMUtil.getStoreOpcode(type), storeIndex));
        }
        target.maxLocals = Math.max(target.maxLocals, storeIndex);
        target.maxStack = Math.max(target.maxStack, storeIndex - startIndex + (category2 ? 2 : 1));
        for (int i = typesReverse.length; i > 0;) {
            int type = typesReverse[--i];
            inject.add(new VarInsnNode(ASMUtil.getLoadOpcode(type), storeIndex));
            if (ASMUtil.isCategory2(type)) {
                storeIndex -= 2;
            } else {
                storeIndex--;
            }
        }
        target.instructions.insert(previousInsn, inject);
    }
}
