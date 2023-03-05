package de.geolykt.starloader.micromixin.test.j8.targets;

public class LocalPrintingTest {

    public static int returnLocalStatic0() {
        int local0 = -1;
        int local1 = 0;
        int local2 = 1;
        int local3 = 2;
        return local0 * (local1 + local2 + local3);
    }

    public static int returnLocalStaticArg0(int arg0) {
        int local0 = -1;
        int local1 = 0;
        int local2 = 1;
        int local3 = 2;
        return local0 * (local1 + local2 + local3);
    }

    public static int returnLocalStaticArg1(Object arg0) {
        int local0 = -1;
        int local1 = 0;
        int local2 = 1;
        int local3 = 2;
        return local0 * (local1 + local2 + local3);
    }

    public int returnLocalInstance0() {
        int local1 = 0;
        int local2 = 1;
        int local3 = 2;
        return local1 + local2 + local3;
    }

    public int returnLocalInstanceArg0(int arg0) {
        int local1 = 0;
        int local2 = 1;
        int local3 = 2;
        return local1 + local2 + local3;
    }

    public int returnLocalInstanceArg1(Object arg0) {
        int local1 = 0;
        int local2 = 1;
        int local3 = 2;
        return local1 + local2 + local3;
    }
}
