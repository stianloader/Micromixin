package de.geolykt.starloader.micromixin.test.j8.targets;

public class LocalCaptureTest {

    public static int returnLocalStatic2() {
        int local0 = -1;
        int local1 = 0;
        int local2 = 1;
        int local3 = 2;
        return local0 * (local1 + local2 + local3);
    }

    public int returnLocalInstance2() {
        int local1 = 0;
        int local2 = 1;
        int local3 = 2;
        return local1 + local2 + local3;
    }
}
