package org.stianloader.micromixin.test.j8.targets;

import org.stianloader.micromixin.test.j8.MutableInt;

public class CancellableTest {

    private static int counter0;

    public static int testRedirectStatic() {
        CancellableTest.redirectStatic();
        return CancellableTest.counter0;
    }

    private static void redirectStatic() {
        CancellableTest.counter0 = 1;
        CancellableTest.counter0 = new MutableInt(2).intValue();
        CancellableTest.counter0 = 3;
    }
}
