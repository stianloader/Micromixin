package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.stianloader.micromixin.test.j8.targets.ModifyVariableTest;

@Mixin(ModifyVariableTest.class)
public class ModifyVariableMixins {

    @Mixin(ModifyVariableTest.InvalidExtraneousArg.class)
    private static class InvalidExtraneousArg {
        @ModifyVariable(method = "modifySD", at = @At(value = "TAIL", shift = Shift.BEFORE))
        private static double modifySD(double original, boolean arg) {
            return 1;
        }
    }

    @ModifyVariable(method = "modifyNamed", at = @At(value = "TAIL", shift = Shift.BEFORE), name = "b")
    private static int modifyNamed(int original) {
        if (original != 2) {
            throw new AssertionError("original expected == 2; Got " + original);
        }

        return 1;
    }

    @ModifyVariable(method = "modifyNamedMulti", at = @At(value = "STORE"), name = {"a", "b"})
    private static int modifyNamedMulti(int original) {
        if (original != 0) {
            throw new AssertionError("original expected == 0; got " + original);
        }
        return 1;
    }

    @ModifyVariable(target = @Desc(value = "modifySD", ret = double.class), at = @At(value = "TAIL", shift = Shift.BEFORE))
    private static double modifySD(double original) {
        return 1;
    }

    @ModifyVariable(method = "modifySD(Z)D", at = @At(value = "TAIL", shift = Shift.BEFORE))
    private static double modifySD(double original, boolean arg) {
        return 1;
    }

    @ModifyVariable(target = @Desc(value = "modifySDNoShift", ret = double.class), at = @At(value = "TAIL"))
    private static double modifySDNoShift(double original) {
        return 1;
    }

    @ModifyVariable(target = @Desc(value = "modifySF", ret = float.class), at = @At(value = "TAIL", shift = Shift.BEFORE))
    private static float modifySF(float original) {
        return 1;
    }

    @ModifyVariable(method = "modifySF(Z)F", at = @At(value = "TAIL", shift = Shift.BEFORE))
    private static float modifySF(float original, boolean arg) {
        return 1;
    }

    @ModifyVariable(target = @Desc(value = "modifySI", ret = int.class), at = @At(value = "TAIL", shift = Shift.BEFORE))
    private static int modifySI(int original) {
        return 1;
    }

    @ModifyVariable(method = "modifySI(Z)I", at = @At(value = "TAIL", shift = Shift.BEFORE))
    private static int modifySI(int original, boolean arg) {
        return 1;
    }

    @ModifyVariable(target = @Desc(value = "modifySJ", ret = long.class), at = @At(value = "TAIL", shift = Shift.BEFORE))
    private static long modifySJ(long original) {
        return 1;
    }

    @ModifyVariable(method = "modifySJ(Z)J", at = @At(value = "TAIL", shift = Shift.BEFORE))
    private static long modifySJ(long original, boolean arg) {
        return 1;
    }

    @ModifyVariable(target = @Desc(value = "modifyVD", ret = double.class), at = @At(value = "TAIL", shift = Shift.BEFORE))
    private double modifyVD(double original) {
        return 1;
    }

    @ModifyVariable(method = "modifyVD(Z)D", at = @At(value = "TAIL", shift = Shift.BEFORE))
    private double modifyVD(double original, boolean arg) {
        return 1;
    }

    @ModifyVariable(target = @Desc(value = "modifyVF", ret = float.class), at = @At(value = "TAIL", shift = Shift.BEFORE))
    private float modifyVF(float original) {
        return 1;
    }

    @ModifyVariable(method = "modifyVF(Z)F", at = @At(value = "TAIL", shift = Shift.BEFORE))
    private float modifyVF(float original, boolean arg) {
        return 1;
    }

    @ModifyVariable(target = @Desc(value = "modifyVI", ret = int.class), at = @At(value = "TAIL", shift = Shift.BEFORE))
    private int modifyVI(int original) {
        return 1;
    }

    @ModifyVariable(method = "modifyVI(Z)I", at = @At(value = "TAIL", shift = Shift.BEFORE))
    private int modifyVI(int original, boolean arg) {
        return 1;
    }

    @ModifyVariable(target = @Desc(value = "modifyVJ", ret = long.class), at = @At(value = "TAIL", shift = Shift.BEFORE))
    private long modifyVJ(long original) {
        return 1;
    }

    @ModifyVariable(method = "modifyVJ(Z)J", at = @At(value = "TAIL", shift = Shift.BEFORE))
    private long modifyVJ(long original, boolean arg) {
        return 1;
    }
}
