package de.geolykt.starloader.micromixin.test.j8.targets;

public class InjectorRemapTest {

    @SuppressWarnings("unused")
    private static int aliasedReturnStatic() {
        return -1;
    }

    @SuppressWarnings("unused")
    private int aliasedReturnInstance() {
        return -1;
    }

    @SuppressWarnings("unused")
    private static void aliasedVoidStatic() {
        throw new AssertionError("Method needs to be overwritten");
    }

    @SuppressWarnings("unused")
    private void aliasedVoidInstance() {
        throw new AssertionError("Method needs to be overwritten");
    }

    public int runInjectorInstance0() {
        return 0;
    }

    public int runInjectorInstance1() {
        return 0;
    }

    public int runInjectorInstance2() {
        return 0;
    }

    public int runInjectorInstance3() {
        return 0;
    }

    public int runInjectorInstance4() {
        return 0;
    }

    public int runInjectorInstance5() {
        return 0;
    }

    public int runInjectorInstance6() {
        return 0;
    }

    public int runInjectorInstance7() {
        return 0;
    }

    public int runInjectorInstance8() {
        return 0;
    }

    public int runInjectorInstance9() {
        return 0;
    }

    public static int runInjectorStatic0() {
        return 0;
    }

    public static int runInjectorStatic1() {
        return 0;
    }

    public static int runInjectorStatic2() {
        return 0;
    }

    public static int runInjectorStatic3() {
        return 0;
    }

    public static int runInjectorStatic4() {
        return 0;
    }

    public static int runInjectorStatic5() {
        return 0;
    }

    public static int runInjectorStatic6() {
        return 0;
    }

    public static int runInjectorStatic7() {
        return 0;
    }

    public static int runInjectorStatic8() {
        return 0;
    }

    public static int runInjectorStatic9() {
        return 0;
    }

    // ---

    public void runInjectorInstanceV0() {
        throw new AssertionError("Expected cancelling @Inject.");
    }

    public void runInjectorInstanceV1() {
        throw new AssertionError("Expected cancelling @Inject.");
    }

    public void runInjectorInstanceV2() {
        throw new AssertionError("Expected cancelling @Inject.");
    }

    public void runInjectorInstanceV3() {
        throw new AssertionError("Expected cancelling @Inject.");
    }

    public static void runInjectorStaticV0() {
        throw new AssertionError("Expected cancelling @Inject.");
    }

    public static void runInjectorStaticV1() {
        throw new AssertionError("Expected cancelling @Inject.");
    }

    public static void runInjectorStaticV2() {
        throw new AssertionError("Expected cancelling @Inject.");
    }

    public static void runInjectorStaticV3() {
        throw new AssertionError("Expected cancelling @Inject.");
    }
}
