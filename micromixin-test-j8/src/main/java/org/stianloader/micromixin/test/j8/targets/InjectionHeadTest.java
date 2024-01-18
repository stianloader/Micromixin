package org.stianloader.micromixin.test.j8.targets;

public class InjectionHeadTest {

    public static byte expectNoThrowB() {
        throw new AssertionError();
    }

    public static char expectNoThrowC() {
        throw new AssertionError();
    }

    public static double expectNoThrowD() {
        throw new AssertionError();
    }

    public static float expectNoThrowF() {
        throw new AssertionError();
    }

    public static int expectNoThrowI() {
        throw new AssertionError();
    }

    public static long expectNoThrowJ() {
        throw new AssertionError();
    }

    public static Object expectNoThrowL() {
        throw new AssertionError();
    }

    public static short expectNoThrowS() {
        throw new AssertionError();
    }

    public static void expectNoThrowV() {
        // CI should return without throw
        throw new AssertionError();
    }

    public static boolean expectNoThrowZ() {
        throw new AssertionError();
    }

    public static byte expectThrowB() {
        return 0;
    }

    public static char expectThrowC() {
        return 0;
    }

    public static double expectThrowD() {
        return 0;
    }

    public static float expectThrowF() {
        return 0;
    }

    public static int expectThrowI() {
        return 0;
    }

    public static long expectThrowJ() {
        return 0;
    }

    public static Object expectThrowL() {
        return null;
    }

    public static short expectThrowS() {
        return 0;
    }

    public static void expectThrowV() {
        // NOP; CI should throw
        return;
    }

    public static boolean expectThrowZ() {
        return false;
    }

    // ---

    public static byte expectInvalidCancellationB() {
        return 0;
    }

    public static char expectInvalidCancellationC() {
        return 0;
    }

    public static double expectInvalidCancellationD() {
        return 0;
    }

    public static float expectInvalidCancellationF() {
        return 0;
    }

    public static int expectInvalidCancellationI() {
        return 0;
    }

    public static long expectInvalidCancellationJ() {
        return 0;
    }

    public static Object expectInvalidCancellationL() {
        return null;
    }

    public static short expectInvalidCancellationS() {
        return 0;
    }

    public static void expectInvalidCancellationV() {
        return;
    }

    public static boolean expectInvalidCancellationZ() {
        return false;
    }

    // ---

    public static byte expectImplicitlyInvalidCancellationB() {
        return 0;
    }

    public static char expectImplicitlyInvalidCancellationC() {
        return 0;
    }

    public static double expectImplicitlyInvalidCancellationD() {
        return 0;
    }

    public static float expectImplicitlyInvalidCancellationF() {
        return 0;
    }

    public static int expectImplicitlyInvalidCancellationI() {
        return 0;
    }

    public static long expectImplicitlyInvalidCancellationJ() {
        return 0;
    }

    public static Object expectImplicitlyInvalidCancellationL() {
        return null;
    }

    public static short expectImplicitlyInvalidCancellationS() {
        return 0;
    }

    public static void expectImplicitlyInvalidCancellationV() {
        return;
    }

    public static boolean expectImplicitlyInvalidCancellationZ() {
        return false;
    }

    // ---

    public static byte expectReturnNondefaultB() {
        return 0;
    }

    public static char expectReturnNondefaultC() {
        return 0;
    }

    public static double expectReturnNondefaultD() {
        return 0;
    }

    public static float expectReturnNondefaultF() {
        return 0;
    }

    public static int expectReturnNondefaultI() {
        return 0;
    }

    public static long expectReturnNondefaultJ() {
        return 0;
    }

    public static Object expectReturnNondefaultL() {
        return null;
    }

    public static short expectReturnNondefaultS() {
        return 0;
    }

    public static boolean expectReturnNondefaultZ() {
        return false;
    }

    // ---

    public static byte expectCancellableReturnNondefaultB() {
        return 0;
    }

    public static char expectCancellableReturnNondefaultC() {
        return 0;
    }

    public static double expectCancellableReturnNondefaultD() {
        return 0;
    }

    public static float expectCancellableReturnNondefaultF() {
        return 0;
    }

    public static int expectCancellableReturnNondefaultI() {
        return 0;
    }

    public static long expectCancellableReturnNondefaultJ() {
        return 0;
    }

    public static Object expectCancellableReturnNondefaultL() {
        return null;
    }

    public static short expectCancellableReturnNondefaultS() {
        return 0;
    }

    public static boolean expectCancellableReturnNondefaultZ() {
        return false;
    }
}
