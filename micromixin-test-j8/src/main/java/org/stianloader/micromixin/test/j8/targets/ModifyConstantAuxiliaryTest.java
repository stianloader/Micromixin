package org.stianloader.micromixin.test.j8.targets;

public class ModifyConstantAuxiliaryTest {

    public static Class<?> untargetableConstantVoidImplicit() {
        return void.class;
    }

    public static Class<?> untargetableConstantVoidExplicit() {
        return void.class;
    }

    public static Class<?> untargetableConstantIntImplicit() {
        return int.class;
    }

    public static Class<?> untargetableConstantIntExplicit() {
        return int.class;
    }

    public static Class<?> targetableConstantSelfImplicit() {
        return ModifyConstantAuxiliaryTest.class;
    }

    public static Class<?> targetableConstantSelfExplicit() {
        return ModifyConstantAuxiliaryTest.class;
    }

    public static int slicedConstant0(boolean b) {
        int x = 5;
        if (b) {
            x *= 3;
        } else {
            x *= 12;
        }
        return x;
    }

    public static int slicedConstant1(boolean b) {
        int x = 5;
        if (b) {
            x *= 3;
        } else {
            x *= 12;
        }
        return x;
    }

    public static int captureMultipleImplicit0() {
        int x = 5;
        x *= 3;
        return x;
    }

    public static int captureMultipleExplicit0() {
        int x = 5;
        x *= 3;
        return x;
    }

    public static int captureMultipleExplicit0B() {
        int x = 5;
        x *= 3;
        return x;
    }

    public static int captureMultipleImplicit1() {
        int x = 5;
        x *= 3;
        x *= 3;
        return x;
    }

    public static int captureMultipleExplicit1() {
        int x = 5;
        x *= 3;
        x *= 3;
        return x;
    }

    private int captureIfZ0(boolean v) {
        if (v) {
            return 5;
        } else {
            return 11;
        }
    }

    private int captureIfZ1(boolean v) {
        if (!v) {
            return 5;
        } else {
            return 11;
        }
    }

    private int captureIfI0(int v) {
        if (v == 0) {
            return 5;
        } else {
            return 11;
        }
    }

    private int captureIfI1(int v) {
        if (v != 0) {
            return 5;
        } else {
            return 11;
        }
    }

    private int captureIfI2(int v) {
        if (v == 8) {
            return 5;
        } else {
            return 11;
        }
    }

    private int captureIfI3(int v) {
        if (v != 8) {
            return 5;
        } else {
            return 11;
        }
    }

    public static int captureIfZ0() {
        return new ModifyConstantAuxiliaryTest().captureIfZ0(false) + new ModifyConstantAuxiliaryTest().captureIfZ0(true);
    }

    public static int captureIfZ1() {
        return new ModifyConstantAuxiliaryTest().captureIfZ1(false) + new ModifyConstantAuxiliaryTest().captureIfZ1(true);
    }

    public static int captureIfI0() {
        return new ModifyConstantAuxiliaryTest().captureIfI0(0) + new ModifyConstantAuxiliaryTest().captureIfI0(10);
    }

    public static int captureIfI1() {
        return new ModifyConstantAuxiliaryTest().captureIfI1(1) + new ModifyConstantAuxiliaryTest().captureIfI1(0);
    }

    public static int captureIfI2() {
        return new ModifyConstantAuxiliaryTest().captureIfI2(8) + new ModifyConstantAuxiliaryTest().captureIfI2(0);
    }

    public static int captureIfI3() {
        return new ModifyConstantAuxiliaryTest().captureIfI3(8) + new ModifyConstantAuxiliaryTest().captureIfI3(0);
    }
}
