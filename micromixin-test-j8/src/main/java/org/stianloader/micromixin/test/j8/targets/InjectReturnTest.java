package org.stianloader.micromixin.test.j8.targets;

public class InjectReturnTest {

    private static int valueArray;
    private static int valueB;
    private static int valueC;
    private static int valueD;
    private static int valueF;
    private static int valueI;
    private static int valueJ;
    private static int valueObject;
    private static int valueS;
    private static int valueV;
    private static int valueZ;

    public static void assertValueArray() {
        InjectReturnTest.callInjectorArray();
        int value = InjectReturnTest.valueArray;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueB() {
        InjectReturnTest.callInjectorB();
        int value = InjectReturnTest.valueB;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueC() {
        InjectReturnTest.callInjectorC();
        int value = InjectReturnTest.valueC;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueD() {
        InjectReturnTest.callInjectorD();
        int value = InjectReturnTest.valueD;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueF() {
        InjectReturnTest.callInjectorF();
        int value = InjectReturnTest.valueF;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueI() {
        InjectReturnTest.callInjectorI();
        int value = InjectReturnTest.valueI;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueJ() {
        InjectReturnTest.callInjectorJ();
        int value = InjectReturnTest.valueJ;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueObject() {
        InjectReturnTest.callInjectorObject();
        int value = InjectReturnTest.valueObject;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueS() {
        InjectReturnTest.callInjectorS();
        int value = InjectReturnTest.valueS;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueV() {
        InjectReturnTest.callInjectorV();
        int value = InjectReturnTest.valueV;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    public static void assertValueZ() {
        InjectReturnTest.callInjectorZ();
        int value = InjectReturnTest.valueZ;
        if (value != 2) {
            throw new AssertionError("Expected value of 2, got " + value);
        }
    }

    private static Object[] callInjectorArray() {
        InjectReturnTest.valueArray = 1;
        return null;
    }

    private static byte callInjectorB() {
        InjectReturnTest.valueB = 1;
        return 0;
    }

    private static char callInjectorC() {
        InjectReturnTest.valueC = 1;
        return 0;
    }

    private static double callInjectorD() {
        InjectReturnTest.valueD = 1;
        return 0;
    }

    private static float callInjectorF() {
        InjectReturnTest.valueF = 1;
        return 0;
    }

    private static int callInjectorI() {
        InjectReturnTest.valueI = 1;
        return 0;
    }

    private static long callInjectorJ() {
        InjectReturnTest.valueJ = 1;
        return 0;
    }

    private static Object callInjectorObject() {
        InjectReturnTest.valueObject = 1;
        return null;
    }

    private static short callInjectorS() {
        InjectReturnTest.valueS = 1;
        return 0;
    }

    private static void callInjectorV() {
        InjectReturnTest.valueV = 1;
        return;
    }

    private static boolean callInjectorZ() {
        InjectReturnTest.valueZ = 1;
        return false;
    }
}
