package org.stianloader.micromixin.test.j8.codegen;

import java.util.Objects;

class CodegenCommon {

    public static final String[][] PRIMITIVES = {
            { "int", "I" },
            { "long", "J" },
            { "short", "S" },
            { "char", "C" },
            { "byte", "B" },
            { "boolean", "Z" },
            { "float", "F" },
            { "double", "D" }
    };

    public static final String[][] IDENTITY_TABLE_MINIMAL = {
            { "int",    "I", "0",    "-957256832",   "0",                  "-957256832" },
            { "double", "D", "0D",   "2.5D",         Objects.toString(0D), Objects.toString(2.5D) },
            { "Object", "L", "null", "Boolean.TRUE", "null",               "true" }
    };

    public static final String[][] IDENTITY_TABLE = {
            { "int", "I", "0", "-957256832", "intValue" },
            { "long", "J", "0L", "Long.MAX_VALUE", "longValue" },
            { "short", "S", "((short) 0)", "((short) -58)", "intValue" },
            { "char", "C", "'\\u0000'", "'c'", "intValue" },
            { "byte", "B", "((byte) 0)", "((byte) 126)", "intValue" },
            { "boolean", "Z", "false", "true", null },
            { "float", "F", "0F", "1.2F", "floatValue" },
            { "double", "D", "0D", "2.5D", "doubleValue" },
            { "Object", "Nul", "null", "Boolean.TRUE", "nullValue" },
            { "Object", "Nul2", "Boolean.FALSE", "null", null },
            { "String", "Str", "\"Test\"", "\"Test2\"", "stringValue" },
            { "String", "NulStr", "null", "\"non-null\"", "nullValue" },
            { "String", "NulStr2", "\"\"", "null", "stringValue" },
            { "String", "EmptyStr", "\"non-empty\"", "\"\"", "stringValue" },
            { "Class<?>", "Class", "void.class", "int.class", "classValue" },
            { "Class<?>", "NulClass", "null", "Object.class", "nullValue" },
            { "Class<?>", "NulClass2", "Object.class", "null", "classValue" }
    };

    public static final String[][] NUMERICS_TABLE = {
            { "int", "I" },
            { "long", "J" },
            { "short", "S" },
            { "char", "C" },
            { "byte", "B" },
            { "float", "F" },
            { "double", "D" }
    };

    public static void genMethodHeader(boolean isStatic, String visibility, int indent, String name, String nameSuffix, String args, String ret, StringBuilder out) {
        while (indent-- > 0) {
            out.append(' ');
        }
        out.append(visibility);
        if (isStatic) {
            out.append(" static ");
        } else {
            out.append(' ');
        }
        out.append(ret).append(' ').append(name).append(nameSuffix).append('(').append(args).append(") {\n");
    }

    public static void pushSimpleAssertion(String className, String methodName, String suffix, String expectedValue,StringBuilder out) {
        out.append("        set.addUnitAssertEquals(\"")
        .append(className)
        .append('.')
        .append(methodName)
        .append(suffix)
        .append("\", ")
        .append(className)
        .append("::")
        .append(methodName)
        .append(suffix)
        .append(", ")
        .append(expectedValue)
        .append(");\n");
    }

    public static void pushSimpleAssertionVirtual(String className, String methodName, String suffix, String expectedValue,StringBuilder out) {
        out.append("        set.addUnitAssertEquals(\"")
        .append(className)
        .append('.')
        .append(methodName)
        .append(suffix)
        .append("\", new ")
        .append(className)
        .append("()::")
        .append(methodName)
        .append(suffix)
        .append(", ")
        .append(expectedValue)
        .append(");\n");
    }

    public static void pushLambdaAssertion(String className, String methodName, String suffix, String inputValue, String expectedValue, StringBuilder out) {
        out.append("        set.addUnitAssertEquals(\"")
        .append(className)
        .append('.')
        .append(methodName)
        .append(suffix)
        .append("\", () -> ")
        .append(className)
        .append('.')
        .append(methodName)
        .append(suffix)
        .append('(')
        .append(inputValue)
        .append("), ")
        .append(expectedValue)
        .append(");\n");
    }
}
