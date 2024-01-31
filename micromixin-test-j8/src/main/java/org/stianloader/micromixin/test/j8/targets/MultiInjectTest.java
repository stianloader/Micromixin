package org.stianloader.micromixin.test.j8.targets;

import org.stianloader.micromixin.test.j8.Blackhole;
import org.stianloader.micromixin.test.j8.MutableInt;

public class MultiInjectTest {

    // injection point A - non-cancellable.
    public static int injectionPointACount = 0;

    public static void injectionPointA0() {
    }
    public static void injectionPointA1() {
    }
    public static void injectionPointA2() {
    }
    public static void injectionPointA3() {
    }
    public static int getInjectionPointACount() {
        return MultiInjectTest.injectionPointACount;
    }

    // injection point B - cancellable and cancelling at head. Furthermore it is incrementing after cancellation

    public static int injectionPointBCount = 0;

    public static void injectionPointB0() {
    }
    public static void injectionPointB1() {
    }
    public static void injectionPointB2() {
    }
    public static void injectionPointB3() {
    }
    public static int getInjectionPointBCount() {
        return MultiInjectTest.injectionPointBCount;
    }

    // injection point C - cancellable and cancelling at HEAD

    public static int injectionPointCCount = 0;

    public static void injectionPointC0() {
        MultiInjectTest.injectionPointCCount++;
    }
    public static void injectionPointC1() {
        MultiInjectTest.injectionPointCCount++;
    }
    public static void injectionPointC2() {
        MultiInjectTest.injectionPointCCount++;
    }
    public static void injectionPointC3() {
        MultiInjectTest.injectionPointCCount++;
    }
    public static int getInjectionPointCCount() {
        return MultiInjectTest.injectionPointCCount;
    }

    // injection point D - cancellable and cancelling at TAIL

    public static int injectionPointDCount = 0;

    public static void injectionPointD0() {
        MultiInjectTest.injectionPointDCount++;
    }
    public static void injectionPointD1() {
        MultiInjectTest.injectionPointDCount++;
    }
    public static void injectionPointD2() {
        MultiInjectTest.injectionPointDCount++;
    }
    public static void injectionPointD3() {
        MultiInjectTest.injectionPointDCount++;
    }
    public static int getInjectionPointDCount() {
        return MultiInjectTest.injectionPointDCount;
    }

    // injection point E - different return types

    public static int injectionPointECount = 0;

    public static void injectionPointE0() {
    }
    public static double injectionPointE1() {
        return 0D;
    }
    public static int injectionPointE2() {
        return 0;
    }
    public static Object injectionPointE3() {
        return null;
    }
    public static int getInjectionPointECount() {
        return MultiInjectTest.injectionPointECount;
    }

    // injection point F - dark magic that shouldn't be supported by Mixins but apparently is due to incoherent error checking
    // TODO Maybe we want to print out warnings? Mixins handles it very strangely and it is apparent that such behaviour
    // will produce issues, however we cannot simply POP/POP2 here to retain backwards compatibility.

    public static int injectionPointFCount = 0;

    public static void injectionPointF0() {
    }
    public static double injectionPointF1() {
        return 0D;
    }
    public static void injectionPointF2() {
        new Blackhole().dump(5D * (25D + injectionPointFCount));
    }
    public static int getInjectionPointFCount() {
        return MultiInjectTest.injectionPointFCount;
    }

    // injection point G - dark magic but with CIR instead of CI

    public static int injectionPointGCount = 0;

    public static void injectionPointG0() {
    }
    public static double injectionPointG1() {
        return 0D;
    }
    public static void injectionPointG2() {
        new Blackhole().dump(5D * (25D + injectionPointGCount));
    }
    public static int getInjectionPointGCount() {
        return MultiInjectTest.injectionPointGCount;
    }

    // injection point H - Regular old injection point tests but with different Annotations
    // H0 = @Inject (CIR)
    // H1 = @ModifyArg
    // H2 = @ModifyReturnValue
    // H3 = @Redirect
    // Expect 2 (if not injecting, 1 would be returned)

    public static int injectionPointH0A() {
        MutableInt mutInt = new MutableInt(0);
        mutInt.add(1);
        return mutInt.intValue();
    }

    public static int injectionPointH0B() {
        MutableInt mutInt = new MutableInt(0);
        mutInt.add(1);
        return mutInt.intValue();
    }

    public static int injectionPointH1A() {
        MutableInt mutInt = new MutableInt(0);
        mutInt.add(1);
        return mutInt.intValue();
    }

    public static int injectionPointH1B() {
        MutableInt mutInt = new MutableInt(0);
        mutInt.add(1);
        return mutInt.intValue();
    }

    public static int injectionPointH2A() {
        MutableInt mutInt = new MutableInt(0);
        mutInt.add(1);
        return mutInt.intValue();
    }

    public static int injectionPointH2B() {
        MutableInt mutInt = new MutableInt(0);
        mutInt.add(1);
        return mutInt.intValue();
    }

    public static int injectionPointH3A() {
        MutableInt mutInt = new MutableInt(0);
        mutInt.add(1);
        return mutInt.intValue();
    }

    public static int injectionPointH3B() {
        MutableInt mutInt = new MutableInt(0);
        mutInt.add(1);
        return mutInt.intValue();
    }
}
