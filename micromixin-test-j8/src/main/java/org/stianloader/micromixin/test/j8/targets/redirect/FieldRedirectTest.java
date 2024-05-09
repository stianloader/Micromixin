package org.stianloader.micromixin.test.j8.targets.redirect;

public class FieldRedirectTest {
    private static byte fieldBS = 0;
    private static char fieldCS = 0;
    private static double fieldDS = 0;
    private static float fieldFS = 0;
    private static int fieldIS = 0;
    private static long fieldJS = 0;
    private static Object fieldLS = null;
    private static short fieldSS = 0;
    private static boolean fieldZS = false;

    public static byte staticRedirectB() {
        return FieldRedirectTest.fieldBS;
    }

    public static char staticRedirectC() {
        return FieldRedirectTest.fieldCS;
    }

    public static double staticRedirectD() {
        return FieldRedirectTest.fieldDS;
    }

    public static float staticRedirectF() {
        return FieldRedirectTest.fieldFS;
    }

    public static int staticRedirectI() {
        return FieldRedirectTest.fieldIS;
    }

    public static long staticRedirectJ() {
        return FieldRedirectTest.fieldJS;
    }

    public static long staticRedirectJI() {
        return new FieldRedirectTest().fieldJ;
    }

    public static Object staticRedirectL() {
        return FieldRedirectTest.fieldLS;
    }

    public static short staticRedirectS() {
        return FieldRedirectTest.fieldSS;
    }

    public static boolean staticRedirectZ() {
        return FieldRedirectTest.fieldZS;
    }

    private byte fieldB = 0;
    private char fieldC = 0;
    private double fieldD = 0;
    private float fieldF = 0;
    private int fieldI = 0;
    private long fieldJ = 0;
    private Object fieldL = null;
    private short fieldS = 0;
    private boolean fieldZ = false;

    public byte redirectB() {
        return this.fieldB;
    }

    public char redirectC() {
        return this.fieldC;
    }

    public double redirectD() {
        return this.fieldD;
    }

    public float redirectF() {
        return this.fieldF;
    }

    public int redirectI() {
        return this.fieldI;
    }

    public long redirectJ() {
        return this.fieldJ;
    }

    public Object redirectL() {
        return this.fieldL;
    }

    public short redirectS() {
        return this.fieldS;
    }

    public boolean redirectZ() {
        return this.fieldZ;
    }
}
