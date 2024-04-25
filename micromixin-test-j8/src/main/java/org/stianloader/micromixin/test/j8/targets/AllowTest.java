package org.stianloader.micromixin.test.j8.targets;

import org.stianloader.micromixin.test.j8.MutableInt;

public class AllowTest {
    // The TooFewX classes accidentally test expect instead of allow, but I was too lazy to change that and instead copied them.
    // And more tests per tests are always better, am I right?
    public static class TooFewInject {
        public static void test(MutableInt accumulator) {
            // Expected algorithm: Add 1 before every multiplication
            // ((9 + 1) * 2) + 1) * 2 = 42
            //  (10     * 2) + 1) * 2 = 42
            //  (20          + 1) * 2 = 42
            //                21  * 2 = 42
            if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 42) {
                throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
            }
        }
    }

    public static class TooFewModifyArg {
        public static void test(MutableInt accumulator) {
            if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 9 * 3 * 3) {
                throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
            }
        }
    }

    public static class TooFewModifyConstant {
        public static void test(MutableInt accumulator) {
            if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 42) {
                throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
            }
        }
    }

    public static class TooFewRedirect {
        public static void test(MutableInt accumulator) {
            if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 42) {
                throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
            }
        }
    }

    // 'allow' is set to 1 for all of these classes, but they all target more than one return
    public static class TooManyInject {
        public static void test(MutableInt accumulator) {
            if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 42) {
                throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
            }
        }
    }

    public static class TooManyModifyArg {
        public static void test(MutableInt accumulator) {
            if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 9 * 3 * 3) {
                throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
            }
        }
    }

    public static class TooManyModifyConstant {
        public static void test(MutableInt accumulator) {
            if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 42) {
                throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
            }
        }
    }

    public static class TooManyRedirect {
        public static void test(MutableInt accumulator) {
            if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 42) {
                throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
            }
        }
    }

    // negative (i.e. -1) tests

    public static boolean testNegativeAllowInject() {
        return false;
    }

    public static boolean testNegativeAllowModifyArg() {
        return unaryExpr(false);
    }

    public static boolean testNegativeAllowModifyConstant() {
        return false;
    }

    public static boolean testNegativeAllowModifyReturnValue() {
        return false;
    }

    public static boolean testNegativeAllowRedirect() {
        return unaryExpr(false);
    }

    // zero tests

    public static boolean testZeroAllowInject() {
        return false;
    }

    public static boolean testZeroAllowModifyArg() {
        return unaryExpr(false);
    }

    public static boolean testZeroAllowModifyConstant() {
        return false;
    }

    public static boolean testZeroAllowModifyReturnValue() {
        return false;
    }

    public static boolean testZeroAllowRedirect() {
        return unaryExpr(false);
    }

    // one tests

    public static boolean testOneAllowInject() {
        return false;
    }

    public static boolean testOneAllowModifyArg() {
        return unaryExpr(false);
    }

    public static boolean testOneAllowModifyConstant() {
        return false;
    }

    public static boolean testOneAllowModifyReturnValue() {
        return false;
    }

    public static boolean testOneAllowRedirect() {
        return unaryExpr(false);
    }

    // expect = 2, allow = 1

    public static void testHigherExpectInject(MutableInt accumulator) {
        // Expected algorithm: Add 1 before every multiplication
        // ((9 + 1) * 2) + 1) * 2 = 42
        //  (10     * 2) + 1) * 2 = 42
        //  (20          + 1) * 2 = 42
        //                21  * 2 = 42
        if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 42) {
            throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
        }
    }

    public static void testHigherExpectModifyArg(MutableInt accumulator) {
        if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 9 * 3 * 3) {
            throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
        }
    }

    public static void testHigherExpectModifyConstant(MutableInt accumulator) {
        if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 42) {
            throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
        }
    }

    public static void testHigherExpectRedirect(MutableInt accumulator) {
        if (accumulator.set(9).mul(MutableInt.valueOf(2)).mul(MutableInt.valueOf(2)).intValue() != 42) {
            throw new AssertionError("Unexpected accumulator value: " + accumulator.intValue());
        }
    }

    // Utility methods

    private static boolean unaryExpr(boolean v) {
        return v;
    }
}
