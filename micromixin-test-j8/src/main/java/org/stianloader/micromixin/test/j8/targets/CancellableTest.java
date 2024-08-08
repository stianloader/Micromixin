package org.stianloader.micromixin.test.j8.targets;

import org.stianloader.micromixin.test.j8.MutableInt;

public class CancellableTest {

    private static int counter0;
    private static int counter1;

    private static void redirectStatic() {
        CancellableTest.counter0 = 1;
        CancellableTest.counter0 = new MutableInt(2).intValue();
        CancellableTest.counter0 = 3;
    }

    private static int redirectStaticReturnable() {
        CancellableTest.counter1 = 1;
        CancellableTest.counter1 = new MutableInt(2).intValue();
        CancellableTest.counter1 = 3;
        return 0;
    }

    public static int testRedirectStatic() {
        CancellableTest.redirectStatic();
        return CancellableTest.counter0;
    }

    public static int testRedirectStaticReturnable() {
        return (CancellableTest.redirectStaticReturnable() << 3) | CancellableTest.counter1;
    }
}
