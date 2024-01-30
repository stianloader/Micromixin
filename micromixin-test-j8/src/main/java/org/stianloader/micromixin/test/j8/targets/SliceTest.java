package org.stianloader.micromixin.test.j8.targets;

import org.stianloader.micromixin.test.j8.MutableInt;

public class SliceTest {

    public static class InvalidlyExcludedTailTest {
        public static void target() {
            MutableInt val = MutableInt.valueOf(2);
            val.add(MutableInt.valueOf(2));
            val.add(MutableInt.valueOf(5));
            val.add(MutableInt.valueOf(2));
        }
    }

    public static class AmbigiousSliceTest {
        public static void target() {
            MutableInt val = MutableInt.valueOf(2);
            val.add(MutableInt.valueOf(2));
            val.add(MutableInt.valueOf(5));
            val.add(MutableInt.valueOf(5));
            val.add(MutableInt.valueOf(2));
        }
    }

    public static void sliceTest0Inject() {
        MutableInt val = MutableInt.valueOf(2);
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 4) {
            throw new IllegalStateException("Value != 4, it is: " + val.intValue());
        }
        // Injected: *= 2
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 10) {
            throw new IllegalStateException("Value != 10, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 12) {
            throw new IllegalStateException("Value != 12, it is: " + val.intValue());
        }
    }

    public static void sliceTest0ModifyArg() {
        MutableInt val = MutableInt.valueOf(2);
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 4) {
            throw new IllegalStateException("Value != 4, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2)); // Redirect replaces this call with += square(2)
        if (val.intValue() != 8) {
            throw new IllegalStateException("Value != 8, it is: " + val.intValue());
        }
        int expectedResult = 10; // @Redirect and @ModifyArg can have multiple injection points; this little hack moves the end of the slice without using shifts in @At
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != expectedResult) {
            throw new IllegalStateException("Value != 10, it is: " + val.intValue());
        }
    }

    public static void sliceTest0Redirect() {
        MutableInt val = MutableInt.valueOf(2);
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 4) {
            throw new IllegalStateException("Value != 4, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2)); // Redirect replaces this call with *= 2
        if (val.intValue() != 8) {
            throw new IllegalStateException("Value != 8, it is: " + val.intValue());
        }
        int expectedResult = 10; // @Redirect and @ModifyArg can have multiple injection points; this little hack moves the end of the slice without using shifts in @At
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != expectedResult) {
            throw new IllegalStateException("Value != 10, it is: " + val.intValue());
        }
    }

    public static void sliceTest1ModifyArg() {
        MutableInt val = MutableInt.valueOf(2);
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 4) {
            throw new IllegalStateException("Value != 4, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2)); // Redirect replaces this call with += square(2)
        if (val.intValue() != 8) {
            throw new IllegalStateException("Value != 8, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 12) {
            throw new IllegalStateException("Value != 12, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 14) {
            throw new IllegalStateException("Value != 14, it is: " + val.intValue());
        }
    }

    public static void sliceTest1Redirect() {
        MutableInt val = MutableInt.valueOf(2);
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 4) {
            throw new IllegalStateException("Value != 4, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2)); // Redirect replaces this call with *= 2
        if (val.intValue() != 8) {
            throw new IllegalStateException("Value != 8, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2)); // Redirect also replaces this call with *= 2
        if (val.intValue() != 16) {
            throw new IllegalStateException("Value != 16, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 18) {
            throw new IllegalStateException("Value != 18, it is: " + val.intValue());
        }
    }

    public static int sliceTest2Inject(int iterations) {
        if (iterations < 0) {
            return iterations;
        }
        int accumulated = 0;
        for (int j = 0; j < iterations; j++) {
            accumulated += j;
            if (accumulated > 10) {
                return accumulated; // Injector will multiply it by 2
            }
        }
        return accumulated / 2;
    }

    public static int sliceTest2ModifyReturnValue(int iterations) {
        if (iterations < 0) {
            return iterations;
        }
        int accumulated = 0;
        for (int j = 0; j < iterations; j++) {
            accumulated += j;
            if (accumulated > 10) {
                return accumulated; // Injector will multiply it by 2
            }
        }
        return accumulated / 2;
    }

    public static int sliceTest3ModifyReturnValue(int iterations) {
        if (iterations < 0) {
            return iterations;
        }
        int accumulated = 0;
        for (int j = 0; j < iterations; j++) {
            accumulated += j;
            if (accumulated > 10) {
                return accumulated; // Injector will multiply it by 2
            }
        }
        return accumulated / 2; // Injector will multiple it by 2 afterwards
    }

    public static void sliceTest4InjectA(MutableInt val) {
        // Injected: *= 3
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 8) {
            throw new IllegalStateException("Value != 8, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(5));
        if (val.intValue() != 13) {
            throw new IllegalStateException("Value != 13, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 15) {
            throw new IllegalStateException("Value != 15, it is: " + val.intValue());
        }
    }

    public static MutableInt sliceTest4InjectB() {
        MutableInt val = MutableInt.valueOf(2);
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 4) {
            throw new IllegalStateException("Value != 4, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(5));
        if (val.intValue() != 9) {
            throw new IllegalStateException("Value != 9, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 11) {
            throw new IllegalStateException("Value != 11, it is: " + val.intValue());
        }
        // Injected: *= 2
        return val;
    }

    public static void sliceTest4InjectC() {
        MutableInt val = MutableInt.valueOf(2);
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 4) {
            throw new IllegalStateException("Value != 4, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(5));
        if (val.intValue() != 13) {
            throw new IllegalStateException("Value != 13, it is: " + val.intValue());
        }
        val.add(MutableInt.valueOf(2));
        if (val.intValue() != 15) {
            throw new IllegalStateException("Value != 15, it is: " + val.intValue());
        }
    }

    public static MutableInt sliceTest4InjectD(MutableInt val) {
        val.add(MutableInt.valueOf(2));
        if (val.intValue() > 10) {
            return val;
        }
        val.add(MutableInt.valueOf(5));
        val.add(MutableInt.valueOf(2));
        return val;
    }
}
