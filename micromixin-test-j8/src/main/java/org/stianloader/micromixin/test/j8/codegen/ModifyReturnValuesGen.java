package org.stianloader.micromixin.test.j8.codegen;

public class ModifyReturnValuesGen {

    public static final String PACKAGE_MIXIN = "org.stianloader.micromixin.test.j8.mixin.mixinextra";
    public static final String PACKAGE_TARGET = "org.stianloader.micromixin.test.j8.targets.mixinextra";
    public static final String CLASS_NAME = "ModifyReturnValue";
    public static final String TEST_SUFFIX = "Test";
    public static final String MIXIN_SUFFIX = "Mixins";
    public static final String COMPANION_SUFFIX = "CompanionMixins";

    public static StringBuilder generateInjectTarget() {
        StringBuilder builder = new StringBuilder();
        builder.append("/* following code is generated - do not touch directly. */\n");
        builder.append("package " + PACKAGE_TARGET + ";\n\n");
        builder.append("public class " + CLASS_NAME + TEST_SUFFIX + " {\n");

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            CodegenCommon.genMethodHeader(true, "public", 4, "identityOverwrite", entry[1], "", entry[0], builder);
            builder.append("        return ").append(entry[2]).append(";\n");
            builder.append("    }\n\n");
        }

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            CodegenCommon.genMethodHeader(true, "public", 4, "identityPassthrough", entry[1], "", entry[0], builder);
            builder.append("        return ").append(entry[2]).append(";\n");
            builder.append("    }\n\n");
        }

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            CodegenCommon.genMethodHeader(false, "public", 4, "virtualIdentityOverwrite", entry[1], "", entry[0], builder);
            builder.append("        return ").append(entry[2]).append(";\n");
            builder.append("    }\n\n");
        }

        for (String[] entry : CodegenCommon.NUMERICS_TABLE) {
            CodegenCommon.genMethodHeader(true, "public", 4, "mul3add2", entry[1], entry[0] + " val", entry[0], builder);
            builder.append("        return val;\n");
            builder.append("    }\n\n");
        }

        builder.append("}\n");
        return builder;
    }

    public static StringBuilder generateCompanionMixin() {
        StringBuilder builder = new StringBuilder();
        builder.append("/* following code is generated - do not touch directly. */\n");
        builder.append("package " + PACKAGE_MIXIN + ";\n\n");

        builder.append("import org.spongepowered.asm.mixin.Mixin;\n");
        builder.append("import org.spongepowered.asm.mixin.injection.At;\n\n");
        builder.append("import com.llamalad7.mixinextras.injector.ModifyReturnValue;\n\n");
        builder.append("import " + PACKAGE_TARGET + "." + CLASS_NAME + TEST_SUFFIX + ";\n\n");

        builder.append("@Mixin(value = " + CLASS_NAME + TEST_SUFFIX + ".class, priority = 101)\n");
        builder.append("public class " + CLASS_NAME + COMPANION_SUFFIX + " {\n");

        for (String[] entry : CodegenCommon.NUMERICS_TABLE) {
            builder.append("    @ModifyReturnValue(at = @At(\"TAIL\"), method = { \"mul3add2");
            builder.append(entry[1]);
            builder.append("\" })\n");
            CodegenCommon.genMethodHeader(true, "private", 4, "mul3add2", entry[1], entry[0] + " val", entry[0], builder);
            builder.append("        return (").append(entry[0]).append(") (val + 2);\n");
            builder.append("    }\n\n");
        }

        builder.append("}\n");
        return builder;
    }

    public static StringBuilder generateMixin() {
        StringBuilder builder = new StringBuilder();
        builder.append("/* following code is generated - do not touch directly. */\n");
        builder.append("package " + PACKAGE_MIXIN + ";\n\n");

        builder.append("import org.spongepowered.asm.mixin.Mixin;\n");
        builder.append("import org.spongepowered.asm.mixin.injection.At;\n\n");
        builder.append("import com.llamalad7.mixinextras.injector.ModifyReturnValue;\n\n");
        builder.append("import " + PACKAGE_TARGET + "." + CLASS_NAME + TEST_SUFFIX + ";\n\n");

        builder.append("@Mixin(value = " + CLASS_NAME + TEST_SUFFIX + ".class, priority = 100)\n");
        builder.append("public class " + CLASS_NAME + MIXIN_SUFFIX + " {\n");

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            builder.append("    @ModifyReturnValue(at = @At(\"TAIL\"), method = { \"identityOverwrite");
            builder.append(entry[1]);
            builder.append("\" })\n");
            CodegenCommon.genMethodHeader(true, "private", 4, "identityOverwrite", entry[1], entry[0] + " val", entry[0], builder);
            builder.append("        return ").append(entry[3]).append(";\n");
            builder.append("    }\n\n");
        }

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            builder.append("    @ModifyReturnValue(at = @At(\"TAIL\"), method = { \"identityPassthrough");
            builder.append(entry[1]);
            builder.append("\" })\n");
            CodegenCommon.genMethodHeader(true, "private", 4, "identityPassthrough", entry[1], entry[0] + " val", entry[0], builder);
            builder.append("        return val;\n");
            builder.append("    }\n\n");
        }

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            builder.append("    @ModifyReturnValue(at = @At(\"TAIL\"), method = { \"virtualIdentityOverwrite");
            builder.append(entry[1]);
            builder.append("\" })\n");
            CodegenCommon.genMethodHeader(false, "public", 4, "virtualIdentityOverwrite", entry[1], entry[0] + " val", entry[0], builder);
            builder.append("        return ").append(entry[3]).append(";\n");
            builder.append("    }\n\n");
        }

        for (String[] entry : CodegenCommon.NUMERICS_TABLE) {
            builder.append("    @ModifyReturnValue(at = @At(\"TAIL\"), method = { \"mul3add2");
            builder.append(entry[1]);
            builder.append("\" })\n");
            CodegenCommon.genMethodHeader(true, "private", 4, "mul3add2", entry[1], entry[0] + " val", entry[0], builder);
            builder.append("        return (").append(entry[0]).append(") (val * 3);\n");
            builder.append("    }\n\n");
        }

        builder.append("}\n");
        return builder;
    }

    public static StringBuilder generateAssertions() {
        StringBuilder builder = new StringBuilder();
        builder.append("        /* following code is generated - do not touch directly. */\n");
        builder.append("        TestSet set = new TestSet();\n");

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            CodegenCommon.pushSimpleAssertion(CLASS_NAME + TEST_SUFFIX, "identityOverwrite", entry[1], entry[3], builder);
        }

        builder.append("\n");

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            CodegenCommon.pushSimpleAssertion(CLASS_NAME + TEST_SUFFIX, "identityPassthrough", entry[1], entry[2], builder);
        }

        builder.append("\n");

        for (String[] entry : CodegenCommon.IDENTITY_TABLE) {
            CodegenCommon.pushSimpleAssertionVirtual(CLASS_NAME + TEST_SUFFIX, "virtualIdentityOverwrite", entry[1], entry[3], builder);
        }

        builder.append("\n");

        for (String[] entry : CodegenCommon.NUMERICS_TABLE) {
            String input = '(' + entry[0] + ") 1";
            String output = '(' + entry[0] + ") ((" + input + ") * 3 + 2)";
            CodegenCommon.pushLambdaAssertion(CLASS_NAME + TEST_SUFFIX, "mul3add2", entry[1], input, output, builder);
        }

        builder.append("\n");

        builder.append("        LoggerFactory.getLogger(TestHarness.class).info(\"" + CLASS_NAME + TEST_SUFFIX + ":\");\n");
        builder.append("        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));\n");
        return builder;
    }

    public static void main(String[] args) {
        System.out.println(generateInjectTarget());
        System.out.println("\n\n==========================================================================\n\n");
        System.out.println(generateCompanionMixin());
        System.out.println("\n\n==========================================================================\n\n");
        System.out.println(generateMixin());
        System.out.println("\n\n==========================================================================\n\n");
        System.out.println(generateAssertions());
    }
}
