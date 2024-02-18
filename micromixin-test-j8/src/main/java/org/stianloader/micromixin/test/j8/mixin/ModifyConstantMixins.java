/* following code is generated - do not touch directly. */
package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.stianloader.micromixin.test.j8.targets.ModifyConstantTest;

@Mixin(value = ModifyConstantTest.class)
public class ModifyConstantMixins {
    @ModifyConstant(constant = @Constant(intValue = 0), method = "modifyRetExplicitI")
    private static int modifyRetExplicitI(int val) {
        return -957256832;
    }

    @ModifyConstant(method = "modifyRetImplicitI")
    private static int modifyRetImplicitI(int val) {
        return -957256832;
    }

    @ModifyConstant(constant = @Constant(longValue = 0L), method = "modifyRetExplicitJ")
    private static long modifyRetExplicitJ(long val) {
        return Long.MAX_VALUE;
    }

    @ModifyConstant(method = "modifyRetImplicitJ")
    private static long modifyRetImplicitJ(long val) {
        return Long.MAX_VALUE;
    }

    @ModifyConstant(constant = @Constant(intValue = ((short) 0)), method = "modifyRetExplicitS")
    private static int modifyRetExplicitS(int val) {
        return ((short) -58);
    }

    @ModifyConstant(method = "modifyRetImplicitS")
    private static int modifyRetImplicitS(int val) {
        return ((short) -58);
    }

    @ModifyConstant(constant = @Constant(intValue = '\u0000'), method = "modifyRetExplicitC")
    private static int modifyRetExplicitC(int val) {
        return 'c';
    }

    @ModifyConstant(method = "modifyRetImplicitC")
    private static int modifyRetImplicitC(int val) {
        return 'c';
    }

    @ModifyConstant(constant = @Constant(intValue = ((byte) 0)), method = "modifyRetExplicitB")
    private static int modifyRetExplicitB(int val) {
        return ((byte) 126);
    }

    @ModifyConstant(method = "modifyRetImplicitB")
    private static int modifyRetImplicitB(int val) {
        return ((byte) 126);
    }

    @ModifyConstant(constant = @Constant(intValue = 0), method = "modifyRetExplicitZ")
    private static int modifyRetExplicitZ(int val) {
        return 1;
    }

    @ModifyConstant(method = "modifyRetImplicitZ")
    private static int modifyRetImplicitZ(int val) {
        return 1;
    }

    @ModifyConstant(constant = @Constant(floatValue = 0F), method = "modifyRetExplicitF")
    private static float modifyRetExplicitF(float val) {
        return 1.2F;
    }

    @ModifyConstant(method = "modifyRetImplicitF")
    private static float modifyRetImplicitF(float val) {
        return 1.2F;
    }

    @ModifyConstant(constant = @Constant(doubleValue = 0D), method = "modifyRetExplicitD")
    private static double modifyRetExplicitD(double val) {
        return 2.5D;
    }

    @ModifyConstant(method = "modifyRetImplicitD")
    private static double modifyRetImplicitD(double val) {
        return 2.5D;
    }

    @ModifyConstant(constant = @Constant(stringValue = "Test"), method = "modifyRetExplicitStr")
    private static String modifyRetExplicitStr(String val) {
        return "Test2";
    }

    @ModifyConstant(method = "modifyRetImplicitStr")
    private static String modifyRetImplicitStr(String val) {
        return "Test2";
    }

    @ModifyConstant(constant = @Constant(stringValue = ""), method = "modifyRetExplicitNulStr2")
    private static String modifyRetExplicitNulStr2(String val) {
        return null;
    }

    @ModifyConstant(method = "modifyRetImplicitNulStr2")
    private static String modifyRetImplicitNulStr2(String val) {
        return null;
    }

    @ModifyConstant(constant = @Constant(stringValue = "non-empty"), method = "modifyRetExplicitEmptyStr")
    private static String modifyRetExplicitEmptyStr(String val) {
        return "";
    }

    @ModifyConstant(method = "modifyRetImplicitEmptyStr")
    private static String modifyRetImplicitEmptyStr(String val) {
        return "";
    }

    @ModifyConstant(constant = @Constant(classValue = Object.class), method = "modifyRetExplicitNulClass2")
    private static Class<?> modifyRetExplicitNulClass2(Class<?> val) {
        return null;
    }

    @ModifyConstant(method = "modifyRetImplicitNulClass2")
    private static Class<?> modifyRetImplicitNulClass2(Class<?> val) {
        return null;
    }
}
