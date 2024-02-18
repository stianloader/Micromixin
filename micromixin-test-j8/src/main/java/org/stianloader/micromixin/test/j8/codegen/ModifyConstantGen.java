package org.stianloader.micromixin.test.j8.codegen;

import java.io.IOException;

public class ModifyConstantGen {
    public static final String PACKAGE_MIXIN = "org.stianloader.micromixin.test.j8.mixin";
    public static final String PACKAGE_TARGET = "org.stianloader.micromixin.test.j8.targets";
    public static final String CLASS_NAME = "ModifyConstant";
    public static final String TEST_SUFFIX = "Test";
    public static final String MIXIN_SUFFIX = "Mixins";

    public static StringBuilder genMixins() {
        StringBuilder builder = new StringBuilder();
        builder.append("/* following code is generated - do not touch directly. */\n");
        builder.append("package " + PACKAGE_MIXIN + ";\n\n");

        builder.append("import org.spongepowered.asm.mixin.Mixin;\n");
        builder.append("import org.spongepowered.asm.mixin.injection.Constant;\n");
        builder.append("import org.spongepowered.asm.mixin.injection.ModifyConstant;\n\n");
        builder.append("import " + PACKAGE_TARGET + "." + CLASS_NAME + TEST_SUFFIX + ";\n\n");

        builder.append("@Mixin(value = " + CLASS_NAME + TEST_SUFFIX + ".class)\n");
        builder.append("public class " + CLASS_NAME + MIXIN_SUFFIX + " {\n");

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            if ((entry[4] == null && entry[0] != "boolean")
                    || entry[4] == "nullValue"
                    || entry[2] == "void.class") {
                continue;
            }

            String inType = entry[0];
            String outValue = entry[3];

            if (entry[0] == "boolean") {
                inType = "int";
                outValue = entry[3].equals("true") ? "1" : "0";
            } else if (entry[4].equals("intValue")) {
                inType = "int";
            }

            builder.append("    @ModifyConstant(constant = @Constant(");
            if (entry[4] != null) {
                builder.append(entry[4]).append(" = ");
                if (entry[4].equals("nullValue")) {
                    builder.append("true");
                } else {
                    builder.append(entry[2]);
                }
            } else {
                // Compilers are expected to treat true as 1 and false as 0.
                // However, nothing verifies that this is actually the case.
                // Nevertheless, this is the best option we have
                // See: https://github.com/SpongePowered/Mixin/issues/503
                builder.append("intValue = ").append(entry[2].equals("true") ? 1 : 0);
            }
            builder.append("), method = \"modifyRetExplicit").append(entry[1]).append("\")\n");
            CodegenCommon.genMethodHeader(true, "private", 4, "modifyRetExplicit", entry[1], inType + " val", inType, builder);
            builder.append("        return ").append(outValue).append(";\n");
            builder.append("    }\n\n");

            builder.append("    @ModifyConstant(method = \"modifyRetImplicit").append(entry[1]).append("\")\n");
            CodegenCommon.genMethodHeader(true, "private", 4, "modifyRetImplicit", entry[1], inType + " val", inType, builder);
            builder.append("        return ").append(outValue).append(";\n");
            builder.append("    }\n\n");
        }

        builder.setLength(builder.length() - 1);
        builder.append("}\n");
        return builder;
    }

    public static StringBuilder genTests() {
        StringBuilder builder = new StringBuilder();
        builder.append("/* following code is generated - do not touch directly. */\n");
        builder.append("package " + PACKAGE_TARGET + ";\n\n");
        builder.append("public class " + CLASS_NAME + TEST_SUFFIX + " {\n");

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            if ((entry[4] == null && entry[0] != "boolean")
                    || entry[4] == "nullValue"
                    || entry[2] == "void.class") {
                continue;
            }

            CodegenCommon.genMethodHeader(true, "public", 4, "modifyRetExplicit", entry[1], "", entry[0], builder);
            builder.append("        return ").append(entry[2]).append(";\n");
            builder.append("    }\n\n");

            CodegenCommon.genMethodHeader(true, "public", 4, "modifyRetImplicit", entry[1], "", entry[0], builder);
            builder.append("        return ").append(entry[2]).append(";\n");
            builder.append("    }\n\n");
        }

        builder.setLength(builder.length() - 1);
        builder.append("}\n");
        return builder;
    }

    public static StringBuilder genAssertions() {
        StringBuilder builder = new StringBuilder();
        builder.append("        /* following code is generated - do not touch directly. */\n");
        builder.append("        TestSet set = new TestSet();\n");

        builder.append("\n");

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            if ((entry[4] == null && entry[0] != "boolean")
                    || entry[4] == "nullValue"
                    || entry[2] == "void.class") {
                continue;
            }

            CodegenCommon.pushSimpleAssertion(CLASS_NAME + TEST_SUFFIX, "modifyRetExplicit", entry[1], entry[3], builder);
            CodegenCommon.pushSimpleAssertion(CLASS_NAME + TEST_SUFFIX, "modifyRetImplicit", entry[1], entry[3], builder);
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
