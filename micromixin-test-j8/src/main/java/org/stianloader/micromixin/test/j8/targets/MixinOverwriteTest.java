package org.stianloader.micromixin.test.j8.targets;

public class MixinOverwriteTest {

    // Unfortunately the official Mixin implementation refuses public-static mixins (at least to some degree), so we have to do these
    // workarounds.
    public static int acc$explicitlyOverwrittenMethodAliasedMultiA() {
        return MixinOverwriteTest.explicitlyOverwrittenMethodAliasedMultiA();
    }

    public static int acc$explicitlyOverwrittenMethodAliasedMultiB() {
        return MixinOverwriteTest.explicitlyOverwrittenMethodAliasedMultiB();
    }

    public static double acc$explicitlyOverwrittenMethodAliasedMultiInvalid() {
        return MixinOverwriteTest.explicitlyOverwrittenMethodAliasedMultiInvalid();
    }

    public static int acc$explicitlyOverwrittenMethodAliasedPriority() {
        return MixinOverwriteTest.explicitlyOverwrittenMethodAliasedPriority();
    }

    public static int acc$explicitlyOverwrittenMethodAliasedPriorityAlias() {
        return MixinOverwriteTest.explicitlyOverwrittenMethodAliasedPriorityAlias();
    }

    public static int acc$implicitlyOverwrittenMethod() {
        return MixinOverwriteTest.implicitlyOverwrittenMethod();
    }

    public static int acc$implicitlyOverwrittenMethodDescMismatch() {
        return MixinOverwriteTest.implicitlyOverwrittenMethodDescMismatch();
    }

    public static int explicitlyOverwrittenMethod() {
        return 0;
    }

    private static int explicitlyOverwrittenMethodAliasedMultiA() {
        return 0;
    }

    private static int explicitlyOverwrittenMethodAliasedMultiB() {
        return 0;
    }

    private static double explicitlyOverwrittenMethodAliasedMultiInvalid() {
        // Invalid due to descriptor mismatch
        return 2;
    }

    private static int explicitlyOverwrittenMethodAliasedPriority() {
        return 4;
    }

    private static int explicitlyOverwrittenMethodAliasedPriorityAlias() {
        return 2;
    }

    public static int explicitlyProtectedMethod() {
        // Protected as in the Mixin uses @Unique
        return 1;
    }

    private static int implicitlyOverwrittenMethod() {
        return 0;
    }

    private static int implicitlyOverwrittenMethodDescMismatch() {
        // The mixin should declare a method "implicitlyOverwrittenMethodDescMismatch" with a long return type,
        // which should not influence this method
        return 1;
    }
}
