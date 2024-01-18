package org.stianloader.micromixin.test.j8.targets.redirect;

public class GenericInvokeTarget {

    public void assertCalledIV(int i) {
        throw new AssertionError("assertCalledIV");
    }

    public void assertCalledVV() {
        throw new AssertionError("assertCalledVV");
    }

    public static void assertCalledStaticIV(int i) {
        throw new AssertionError("assertCalledStaticIV");
    }

    public static void assertCalledStaticVV() {
        throw new AssertionError("assertCalledStaticVV");
    }

    public static int return0Static() {
        return 0;
    }

    public int return0Instanced() {
        return 0;
    }

    public static Object returnNullStatic() {
        return null;
    }

    public Object returnNullInstanced() {
        return null;
    }
}
