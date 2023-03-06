package de.geolykt.micromixin.internal.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.MixinParseException;

public class ASMUtil {

    public static final String CALLBACK_INFO_NAME = "org/spongepowered/asm/mixin/injection/callback/CallbackInfo";
    public static final String CALLBACK_INFO_DESC = "L" + CALLBACK_INFO_NAME + ";";
    public static final String CALLBACK_INFO_RETURNABLE_NAME = "org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable";
    public static final String CALLBACK_INFO_RETURNABLE_DESC = "L" + CALLBACK_INFO_RETURNABLE_NAME + ";";
    public static final int CI_LEN = CALLBACK_INFO_NAME.length();
    public static final int CIR_LEN = CALLBACK_INFO_RETURNABLE_NAME.length();

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
}
