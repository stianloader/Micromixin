package org.stianloader.micromixin.test.j8.targets;

public class ModifyVariableTest {

    public static class InvalidExtraneousArg {
        public static double modifySD() {
            double i = 0;
            return i;
        }

        public static double modifySD(boolean condition) {
            double i = 0;
            if (!condition) {
                return 0;
            }
            return i;
        }
    }

    public static int modifyNamed() {
        int a = 0;
        int b = 2;
        int c = 3;

        b += a * c;

        return b;
    }

    public static int modifyNamedMulti() {
        int a = 0;
        int b = 0;

        return a + b;
    }

    public static double modifySD() {
        double i = 0;
        return i;
    }

    public static double modifySD(boolean condition) {
        double i = 0;
        if (!condition) {
            return 0;
        }
        return i;
    }

    public static double modifySDNoShift() {
        double i = 0;
        return i;
    }

    public static float modifySF() {
        float i = 0;
        return i;
    }

    public static float modifySF(boolean condition) {
        float i = 0;
        if (!condition) {
            return 0;
        }
        return i;
    }

    public static int modifySI() {
        int i = 0;
        return i;
    }

    public static int modifySI(boolean condition) {
        int i = 0;
        if (!condition) {
            return 0;
        }
        return i;
    }

    public static long modifySJ() {
        long i = 0;
        return i;
    }

    public static long modifySJ(boolean condition) {
        long i = 0;
        if (!condition) {
            return 0;
        }
        return i;
    }

    public double modifyVD() {
        double i = 0;
        return i;
    }

    public double modifyVD(boolean condition) {
        double i = 0;
        if (!condition) {
            return 0;
        }
        return i;
    }

    public float modifyVF() {
        float i = 0;
        return i;
    }

    public float modifyVF(boolean condition) {
        float i = 0;
        if (!condition) {
            return 0;
        }
        return i;
    }

    public int modifyVI() {
        int i = 0;
        return i;
    }

    public int modifyVI(boolean condition) {
        int i = 0;
        if (!condition) {
            return 0;
        }
        return i;
    }

    public long modifyVJ() {
        long i = 0;
        return i;
    }

    public long modifyVJ(boolean condition) {
        long i = 0;
        if (!condition) {
            return 0;
        }
        return i;
    }
}
