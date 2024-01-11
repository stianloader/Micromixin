/* following code is generated - do not touch directly. */
package de.geolykt.starloader.micromixin.test.j8.mixin.mixinextra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTest;

@Mixin(value = ModifyReturnValueTest.class, priority = 101)
public class ModifyReturnValueCompanionMixins {
    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2I" })
    private static int mul3add2I (int val) {
        return (int) (val + 2);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2J" })
    private static long mul3add2J (long val) {
        return (long) (val + 2);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2S" })
    private static short mul3add2S (short val) {
        return (short) (val + 2);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2C" })
    private static char mul3add2C (char val) {
        return (char) (val + 2);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2B" })
    private static byte mul3add2B (byte val) {
        return (byte) (val + 2);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2F" })
    private static float mul3add2F (float val) {
        return (float) (val + 2);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2D" })
    private static double mul3add2D (double val) {
        return (double) (val + 2);
    }

}
