package org.stianloader.micromixin.test.j8.mixin.redirect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.stianloader.micromixin.test.j8.targets.redirect.FieldRedirectTest;

@Mixin(FieldRedirectTest.class)
public class FieldRedirectTestMixins {
    @Redirect(method = "staticRedirectB", at = @At(value = "FIELD", target = "fieldBS"))
    private static byte staticRedirectB() {
        return 1;
    }

    @Redirect(method = "staticRedirectC", at = @At(value = "FIELD", target = "fieldCS"))
    private static char staticRedirectC() {
        return 1;
    }

    @Redirect(method = "staticRedirectD", at = @At(value = "FIELD", target = "fieldDS"))
    private static double staticRedirectD() {
        return 1;
    }

    @Redirect(method = "staticRedirectF", at = @At(value = "FIELD", target = "fieldFS"))
    private static float staticRedirectF() {
        return 1;
    }

    @Redirect(method = "staticRedirectI", at = @At(value = "FIELD", target = "fieldIS"))
    private static int staticRedirectI() {
        return 1;
    }

    @Redirect(method = "staticRedirectJ", at = @At(value = "FIELD", target = "fieldJS"))
    private static long staticRedirectJ() {
        return 1;
    }

    @Redirect(method = "staticRedirectL", at = @At(value = "FIELD", target = "fieldLS"))
    private static Object staticRedirectL() {
        return Boolean.TRUE;
    }

    @Redirect(method = "staticRedirectS", at = @At(value = "FIELD", target = "fieldSS"))
    private static short staticRedirectS() {
        return 1;
    }

    @Redirect(method = "staticRedirectZ", at = @At(value = "FIELD", target = "fieldZS"))
    private static boolean staticRedirectZ() {
        return true;
    }

    @Redirect(method = "redirectB", at = @At(value = "FIELD", target = "fieldB"))
    public byte redirectB(FieldRedirectTest instance) {
        return 1;
    }

    @Redirect(method = "redirectC", at = @At(value = "FIELD", target = "fieldC"))
    public char redirectC(FieldRedirectTest instance) {
        return 1;
    }

    @Redirect(method = "redirectD", at = @At(value = "FIELD", target = "fieldD"))
    public double redirectD(FieldRedirectTest instance) {
        return 1;
    }

    @Redirect(method = "redirectF", at = @At(value = "FIELD", target = "fieldF"))
    public float redirectF(FieldRedirectTest instance) {
        return 1;
    }

    @Redirect(method = "redirectI", at = @At(value = "FIELD", target = "fieldI"))
    public int redirectI(FieldRedirectTest instance) {
        return 1;
    }

    @Redirect(method = "redirectJ", at = @At(value = "FIELD", target = "fieldJ"))
    public long redirectJ(FieldRedirectTest instance) {
        return 1;
    }

    @Redirect(method = "redirectL", at = @At(value = "FIELD", target = "fieldL"))
    public Object redirectL(FieldRedirectTest instance) {
        return Boolean.TRUE;
    }

    @Redirect(method = "redirectS", at = @At(value = "FIELD", target = "fieldS"))
    public short redirectS(FieldRedirectTest instance) {
        return 1;
    }

    @Redirect(method = "redirectZ", at = @At(value = "FIELD", target = "fieldZ"))
    public boolean redirectZ(FieldRedirectTest instance) {
        return true;
    }
}
