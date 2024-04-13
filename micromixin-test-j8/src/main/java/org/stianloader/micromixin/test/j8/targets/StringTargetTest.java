package org.stianloader.micromixin.test.j8.targets;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

public class StringTargetTest {

    private static class DetchedReturnDescriptorTarget {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }
    }

    private static class DotMixinTarget {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }
    }

    private static class FullDotMixinTarget {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }
    }

    private static class FullSlashMixinTarget {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }
    }

    private static class SlashDotMixinTarget {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }
    }

    private static class SlashMixinTarget {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }
    }

    private static class WhitespaceDescriptorTarget0 {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }

        // This one is just there to make eclipse shut up about the whole unused thing
        // (while making sure the compiler plays no smart plays here - I don't want to risk newer compilers breaking anything)
        @ScheduledForRemoval
        private static void throwError(long x) {
            // This method plainly exists so that mixins is tempted to target into it (if it were, it would fail to apply)
        }
    }

    private static class WhitespaceDescriptorTarget1 {
        private static void throwError(int i, int j) {
            throw new AssertionError("Class transformation did not apply");
        }

        // This one is just there to make eclipse shut up about the whole unused thing
        // (while making sure the compiler plays no smart plays here - I don't want to risk newer compilers breaking anything)
        @ScheduledForRemoval
        private static void throwError(long x) {
            // This method plainly exists so that mixins is tempted to target into it (if it were, it would fail to apply)
        }
    }

    private static class WhitespaceDescriptorTarget2 {
        private static void throwError(Object o) {
            throw new AssertionError("Class transformation did not apply");
        }

        // This one is just there to make eclipse shut up about the whole unused thing
        // (while making sure the compiler plays no smart plays here - I don't want to risk newer compilers breaking anything)
        @ScheduledForRemoval
        private static void throwError(long x) {
            // This method plainly exists so that mixins is tempted to target into it (if it were, it would fail to apply)
        }
    }

    private static class WhitespaceDescriptorTarget3 {
        private static void throwError(Object o) {
            throw new AssertionError("Class transformation did not apply");
        }

        // This one is just there to make eclipse shut up about the whole unused thing
        // (while making sure the compiler plays no smart plays here - I don't want to risk newer compilers breaking anything)
        @ScheduledForRemoval
        private static void throwError(long x) {
            // This method plainly exists so that mixins is tempted to target into it (if it were, it would fail to apply)
        }
    }

    private static class WhitespaceDescriptorTarget4 {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }

        // This one is just there to make eclipse shut up about the whole unused thing
        // (while making sure the compiler plays no smart plays here - I don't want to risk newer compilers breaking anything)
        @ScheduledForRemoval
        private static void throwError(long x) {
            // This method plainly exists so that mixins is tempted to target into it (if it were, it would fail to apply)
        }
    }

    private static class WhitespaceMixinTarget {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }
    }

    private static class WhitespaceNameTarget {
        private static void throwError() {
            throw new AssertionError("Class transformation did not apply");
        }

        // This one is just there to make eclipse shut up about the whole unused thing
        // (while making sure the compiler plays no smart plays here - I don't want to risk newer compilers breaking anything)
        @ScheduledForRemoval
        private static void throwError(long x) {
            // This method plainly exists so that mixins is tempted to target into it (if it were, it would fail to apply)
        }
    }

    public static void loadDetchedReturnDescriptorTarget() {
        DetchedReturnDescriptorTarget.throwError();
    }

    public static void loadDotMixinTarget() {
        DotMixinTarget.throwError();
    }

    public static void loadFullDotMixinTarget() {
        FullDotMixinTarget.throwError();
    }

    public static void loadFullSlashMixinTarget() {
        FullSlashMixinTarget.throwError();
    }

    public static void loadSlashDotMixinTarget() {
        SlashDotMixinTarget.throwError();
    }

    public static void loadSlashMixinTarget() {
        SlashMixinTarget.throwError();
    }

    public static void loadWhitespaceDescriptorTarget0() {
        WhitespaceDescriptorTarget0.throwError();
    }

    public static void loadWhitespaceDescriptorTarget1() {
        WhitespaceDescriptorTarget1.throwError(0, 0);
    }

    public static void loadWhitespaceDescriptorTarget2() {
        WhitespaceDescriptorTarget2.throwError(null);
    }

    public static void loadWhitespaceDescriptorTarget3() {
        WhitespaceDescriptorTarget3.throwError(null);
    }

    public static void loadWhitespaceDescriptorTarget4() {
        WhitespaceDescriptorTarget4.throwError();
    }

    public static void loadWhitespaceMixinTarget() {
        WhitespaceMixinTarget.throwError();
    }

    public static void loadWhitespaceNameTarget() {
        WhitespaceNameTarget.throwError();
    }
}
