package de.geolykt.micromixin.internal.util;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public class PrintUtils {

    private PrintUtils() {
        throw new AssertionError();
    }

    @Contract(mutates = "param4", pure = false, value = "null, _, _, _ -> fail; _, null, _, _ -> fail; _, _, _, null -> fail; !null, !null, _, !null -> param4")
    @NotNull
    public static final StringBuilder fastPrettyMethodName(@NotNull String name, @NotNull String methodDesc, int access, @NotNull StringBuilder out) {
        access &= ~Opcodes.ACC_STATIC; // Sponge - what are you doing?
        PrintUtils.stringifyAccessMethod(access, out);
        out.append(' ');
        PrintUtils.fastPrettySingleDesc(methodDesc, methodDesc.lastIndexOf(')') + 1, out);
        out.append(' ');
        out.append(name);
        out.append("()");
        return out;
    }

    @NotNull
    public static final String prettyBracketedInt(int value, int maxValue, @NotNull StringBuilder sharedBuilder) {
        sharedBuilder.setLength(0);
        sharedBuilder.append('[');
        String string = Integer.toString(value);
        String maxString = Integer.toString(maxValue);
        int count = maxString.length() - string.length();
        while (count-- != 0) {
            sharedBuilder.append(' ');
        }
        return sharedBuilder.append(string).append(']').toString();
    }

    public static final void fastPrettySingleDesc(@NotNull String desc, int startIndex, @NotNull StringBuilder output) {
        switch (desc.codePointAt(startIndex)) {
        case 'V':
            output.append("void");
            return;
        case 'B':
            output.append("byte");
            return;
        case 'Z':
            output.append("boolean");
            return;
        case 'S':
            output.append("short");
            return;
        case 'C':
            output.append("char");
            return;
        case 'I':
            output.append("int");
            return;
        case 'J':
            output.append("long");
            return;
        case 'F':
            output.append("float");
            return;
        case 'D':
            output.append("double");
            return;
        case '[':
            PrintUtils.fastPrettySingleDesc(desc, startIndex + 1, output);
            output.append("[]");
            return;
        case 'L':
            int beginIndex = Math.max(startIndex, desc.lastIndexOf('/')) + 1;
            output.append(desc, beginIndex, desc.length() - 1);
            return;
        }
    }

    @NotNull
    public static final String prettySingleDesc(@NotNull String desc, @NotNull StringBuilder sharedBuilder) {
        sharedBuilder.setLength(0);
        PrintUtils.fastPrettySingleDesc(desc, 0, sharedBuilder);
        return sharedBuilder.toString();
    }

    @NotNull
    public static final String prettyType(Type type, @NotNull StringBuilder sharedBuilder) {
        return PrintUtils.prettySingleDesc(type.getDescriptor(), sharedBuilder);
    }

    @Contract(mutates = "param2", pure = false, value = "_, !null -> param2; _, null -> fail")
    @NotNull
    public static final StringBuilder stringifyAccessMethod(int acc, @NotNull StringBuilder out) {
        // Remove synthetic access modifiers
        acc &= ~(Opcodes.ACC_BRIDGE /* For fields: `volatile` */ | Opcodes.ACC_DEPRECATED
                | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_ENUM
                | Opcodes.ACC_VARARGS /* For fields `transient` would be 128 */);
        boolean reqWhitespace = false;
        if ((acc & Opcodes.ACC_PUBLIC) != 0) {
            out.append("public");
            acc &= ~Opcodes.ACC_PUBLIC;
            reqWhitespace = true;
        }
        if ((acc & Opcodes.ACC_PRIVATE) != 0) {
            if (reqWhitespace) {
                out.append(' ');
            } else {
                reqWhitespace = true;
            }
            out.append("private");
            acc &= ~Opcodes.ACC_PRIVATE;
        }
        if ((acc & Opcodes.ACC_PROTECTED) != 0) {
            if (reqWhitespace) {
                out.append(' ');
            } else {
                reqWhitespace = true;
            }
            out.append("protected");
            acc &= ~Opcodes.ACC_PROTECTED;
        }
        if ((acc & Opcodes.ACC_STATIC) != 0) {
            if (reqWhitespace) {
                out.append(' ');
            } else {
                reqWhitespace = true;
            }
            out.append("static");
            acc &= ~Opcodes.ACC_STATIC;
        }
        if ((acc & Opcodes.ACC_FINAL) != 0) {
            if (reqWhitespace) {
                out.append(' ');
            } else {
                reqWhitespace = true;
            }
            out.append("final");
            acc &= ~Opcodes.ACC_FINAL;
        }
        if ((acc & Opcodes.ACC_SYNCHRONIZED) != 0) {
            if (reqWhitespace) {
                out.append(' ');
            } else {
                reqWhitespace = true;
            }
            out.append("synchronized");
            acc &= ~Opcodes.ACC_SYNCHRONIZED;
        }
        if (acc != 0) {
            throw new IllegalStateException("Was not able to fully reduce access modifiers: " + acc);
        }
        return out;
    }

    public static final void fastPrettyPrintCallbackInfo(@NotNull MethodNode targetMethod, @NotNull StringBuilder out) {
        switch (targetMethod.desc.codePointBefore(targetMethod.desc.length())) {
        case 'V':
            out.append("CallbackInfo");
            return;
        case ';':
            out.append("CallbackInfoReturnable<");
            PrintUtils.fastPrettySingleDesc(targetMethod.desc, targetMethod.desc.lastIndexOf(')') + 1, out);
            out.append('>');
            return;
        default:
            out.append("CallbackInfoReturnable<");
            out.append(PrintUtils.getSimpleWrapperClassName(targetMethod.desc.codePointBefore(targetMethod.desc.length())));
            out.append('>');
            return;
        }
    }

    @NotNull
    public static final String getSimpleWrapperClassName(int primitiveType) {
        switch (primitiveType) {
        case 'B':
            return "Byte";
        case 'Z':
            return "Boolean";
        case 'S':
            return "Short";
        case 'C':
            return "Character";
        case 'I':
            return "Integer";
        case 'J':
            return "Long";
        case 'F':
            return "Float";
        case 'D':
            return "Double";
        default:
            throw new IllegalArgumentException(new String(new int[] {primitiveType}, 0, 1) + " is not a valid primitive.");
        }
    }

    @NotNull
    public static final List<String> getExpectedCallbackSignature(@NotNull MethodNode currentHandler, @NotNull MethodNode target, @NotNull Frame<BasicValue> frame, @NotNull StringBuilder sharedBuilder) {
        // TODO split long lines
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("");
        lines.add("/**");
        lines.add(" * Expected callback signature");
        lines.add(" * /");
        sharedBuilder.setLength(0);
        PrintUtils.stringifyAccessMethod(currentHandler.access, sharedBuilder).append(" void ").append(currentHandler.name);
        sharedBuilder.append('(');
        PrintUtils.fastPrettyPrintCallbackInfo(target, sharedBuilder);
        sharedBuilder.append(" ci");
        int maxLocals = frame.getLocals();
        for (int i = 0; i < maxLocals; i++) {
            sharedBuilder.append(", ");
            PrintUtils.fastPrettySingleDesc(frame.getLocal(i).getType().getDescriptor(), 0, sharedBuilder);
            sharedBuilder.append(" local").append(i);
        }
        sharedBuilder.append(") {");
        lines.add(sharedBuilder.toString());
        lines.add("    // Method body");
        lines.add("}");
        lines.add("");
        return lines;
    }
}
