package de.geolykt.starloader.micromixin.test.j8.codegen;

import java.io.IOException;

public class ModifyArgGen {

    public static final String PACKAGE_MIXIN = "de.geolykt.starloader.micromixin.test.j8.mixin";
    public static final String PACKAGE_TARGET = "de.geolykt.starloader.micromixin.test.j8.targets";
    public static final String CLASS_NAME = "ModifyArg";
    public static final String TEST_SUFFIX = "Test";
    public static final String MIXIN_SUFFIX = "Mixins";

    public static StringBuilder genMixins() {
        StringBuilder builder = new StringBuilder();
        builder.append("/* following code is generated - do not touch directly. */\n");
        builder.append("package " + PACKAGE_MIXIN + ";\n\n");

        builder.append("import org.spongepowered.asm.mixin.Mixin;\n");
        builder.append("import org.spongepowered.asm.mixin.injection.At;\n\n");
        builder.append("import org.spongepowered.asm.mixin.injection.ModifyArg;\n\n");
        builder.append("import " + PACKAGE_TARGET + "." + CLASS_NAME + TEST_SUFFIX + ";\n\n");

        builder.append("@Mixin(value = " + CLASS_NAME + TEST_SUFFIX + ".class, priority = 100)\n");
        builder.append("public class " + CLASS_NAME + MIXIN_SUFFIX + " {\n");

        for (int argcount = 1; argcount < 5; argcount++) {
            String[][] entries = new String[argcount][];

            int requiredIds = CodegenCommon.IDENTITY_TABLE_MINIMAL.length;
            for (int i = 1; i < argcount; i++) {
                requiredIds *= CodegenCommon.IDENTITY_TABLE_MINIMAL.length;
            }

            for (int id = 0; id < requiredIds; id++) {
                int i = id;
                for (int j = argcount; j > 0;) {
                    entries[--j] = CodegenCommon.IDENTITY_TABLE_MINIMAL[i % CodegenCommon.IDENTITY_TABLE_MINIMAL.length];
                    i /= CodegenCommon.IDENTITY_TABLE_MINIMAL.length;
                }

                String[] autoindexEntry = null;

                if (argcount == 1) {
                    autoindexEntry = entries[0];
                } else {
                    outerLoop:
                    for (i = 0; i < argcount; i++) {
                        for (int j = i + 1; j < argcount; j++) {
                            if (entries[i] == entries[j]) {
                                break outerLoop;
                            }
                        }
                        autoindexEntry = entries[i];
                    }
                }

                int index;
                String[] choosenEntry;
                if (autoindexEntry == null) {
                    index = id % argcount;
                    choosenEntry = entries[index];
                } else {
                    index = id % (argcount + 1);
                    if (index == argcount) {
                        choosenEntry = autoindexEntry;
                    } else {
                        choosenEntry = entries[index];
                    }
                }

                builder.append("    @ModifyArg(method = \"testConcatDirect");
                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }
                builder.append("\", at = @At(value = \"INVOKE\", target = \"concat");
                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }
                builder.append("\")");
                if (index != argcount) {
                    builder.append(", index = ").append(index);
                }
                builder.append(")\n    private static ").append(choosenEntry[0]).append(" onConcatDirect");
                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }
                builder.append("(")
                .append(choosenEntry[0])
                .append(" var) {\n        return ")
                .append(choosenEntry[3])
                .append(";\n    }\n\n");

                builder.append("    @ModifyArg(method = \"testConcatLVT");
                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }
                builder.append("\", at = @At(value = \"INVOKE\", target = \"concat");
                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }
                builder.append("\")");
                if (index != argcount) {
                    builder.append(", index = ").append(index);
                }
                builder.append(")\n    private static ").append(choosenEntry[0]).append(" onConcatLVT");
                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }
                builder.append("(")
                .append(choosenEntry[0])
                .append(" var) {\n        return ")
                .append(choosenEntry[2])
                .append(";\n    }\n\n");
            }
        }

        builder.append("}\n");
        return builder;
    }

    public static StringBuilder genTests() {
        StringBuilder builder = new StringBuilder();
        builder.append("/* following code is generated - do not touch directly. */\n");
        builder.append("package " + PACKAGE_TARGET + ";\n\n");
        builder.append("public class " + CLASS_NAME + TEST_SUFFIX + " {\n");

        for (int argcount = 1; argcount < 5; argcount++) {
            String[][] entries = new String[argcount][];

            int requiredIds = CodegenCommon.IDENTITY_TABLE_MINIMAL.length;
            for (int i = 1; i < argcount; i++) {
                requiredIds *= CodegenCommon.IDENTITY_TABLE_MINIMAL.length;
            }

            for (int id = 0; id < requiredIds; id++) {
                int i = id;
                for (int j = argcount; j > 0;) {
                    entries[--j] = CodegenCommon.IDENTITY_TABLE_MINIMAL[i % CodegenCommon.IDENTITY_TABLE_MINIMAL.length];
                    i /= CodegenCommon.IDENTITY_TABLE_MINIMAL.length;
                }

                builder.append("    private static String concat");
                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }
                builder.append("(");
                i = 0;
                for (String[] entry : entries) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    builder.append(entry[0]).append(" var").append(i++);
                }
                builder.append(") {\n        return ");
                for (i = 0; i < argcount; i++) {
                    if (i != 0) {
                        builder.append(" + ");
                    }
                    builder.append("java.util.Objects.toString(var").append(i).append(")");
                }
                builder.append(";\n    }\n\n");

                builder.append("    public static String testConcatDirect");
                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }
                builder.append("() {\n        return concat");
                i = 0;
                for (String[] entry : entries) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    builder.append(entry[1]);
                }
                builder.append('(');
                for (i = 0; i < argcount; i++) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    builder.append(entries[i][2]);
                }
                builder.append(");\n    }\n\n");

                builder.append("    public static String testConcatLVT");
                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }
                builder.append("() {\n        ");
                for (i = 0; i < argcount; i++) {
                    builder.append(entries[i][0]).append(" var").append(i).append(" = ").append(entries[i][3]).append(";\n        ");
                }
                builder.append("return concat");
                i = 0;
                for (String[] entry : entries) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    builder.append(entry[1]);
                }
                builder.append('(');
                for (i = 0; i < argcount; i++) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    builder.append("var").append(i);
                }
                builder.append(");\n    }\n\n");
            }
        }

        builder.append("}\n");
        return builder;
    }

    public static StringBuilder genAssertions() {
        StringBuilder builder = new StringBuilder();
        builder.append("        /* following code is generated - do not touch directly. */\n");
        builder.append("        TestSet set = new TestSet();\n");

        builder.append("\n");

        for (int argcount = 1; argcount < 5; argcount++) {
            String[][] entries = new String[argcount][];

            int requiredIds = CodegenCommon.IDENTITY_TABLE_MINIMAL.length;
            for (int i = 1; i < argcount; i++) {
                requiredIds *= CodegenCommon.IDENTITY_TABLE_MINIMAL.length;
            }

            for (int id = 0; id < requiredIds; id++) {
                int i = id;
                for (int j = argcount; j > 0;) {
                    entries[--j] = CodegenCommon.IDENTITY_TABLE_MINIMAL[i % CodegenCommon.IDENTITY_TABLE_MINIMAL.length];
                    i /= CodegenCommon.IDENTITY_TABLE_MINIMAL.length;
                }

                String[] autoindexEntry = null;
                int autoindex = -1;

                if (argcount == 1) {
                    autoindexEntry = entries[0];
                    autoindex = 0;
                } else {
                    outerLoop:
                    for (i = 0; i < argcount; i++) {
                        for (int j = i + 1; j < argcount; j++) {
                            if (entries[i] == entries[j]) {
                                break outerLoop;
                            }
                        }
                        autoindex = i;
                        autoindexEntry = entries[i];
                    }
                }

                int index;
                String[] choosenEntry;
                if (autoindexEntry == null) {
                    index = id % argcount;
                    choosenEntry = entries[index];
                } else {
                    index = id % (argcount + 1);
                    if (index == argcount) {
                        choosenEntry = autoindexEntry;
                        index = autoindex;
                    } else {
                        choosenEntry = entries[index];
                    }
                }

                builder.append("        set.addUnitAssertEquals(\"" + CLASS_NAME + TEST_SUFFIX + ".testConcatDirect");

                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }

                builder.append("\", " + CLASS_NAME + TEST_SUFFIX + "::testConcatDirect");

                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }

                builder.append(", \"");

                for (i = 0; i < argcount; i++) {
                    if (i == index) {
                        builder.append(choosenEntry[5]);
                    } else {
                        builder.append(entries[i][4]);
                    }
                }

                builder.append("\");\n");

                builder.append("        set.addUnitAssertEquals(\"" + CLASS_NAME + TEST_SUFFIX + ".testConcatLVT");

                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }

                builder.append("\", " + CLASS_NAME + TEST_SUFFIX + "::testConcatLVT");

                for (String[] entry : entries) {
                    builder.append(entry[1]);
                }

                builder.append(", \"");

                for (i = 0; i < argcount; i++) {
                    if (i == index) {
                        builder.append(choosenEntry[4]);
                    } else {
                        builder.append(entries[i][5]);
                    }
                }

                builder.append("\");\n");
            }
        }

        builder.append("\n");

        builder.append("        LoggerFactory.getLogger(TestHarness.class).info(\"" + CLASS_NAME + TEST_SUFFIX + ":\");\n");
        builder.append("        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));\n");
        return builder;
    }

    public static void main(String[] args) throws IOException {
        int read = System.in.read() - '0';
        switch (read) {
        case 0:
            System.out.print(genAssertions().toString());
            break;
        case 1:
            System.out.print(genMixins().toString());
            break;
        case 2:
            System.out.print(genTests().toString());
            break;
        }
    }
}
