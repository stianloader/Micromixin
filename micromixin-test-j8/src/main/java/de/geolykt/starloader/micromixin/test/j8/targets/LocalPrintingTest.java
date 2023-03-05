package de.geolykt.starloader.micromixin.test.j8.targets;

public class LocalPrintingTest {

    public static int returnLocalStatic0() {
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
}
