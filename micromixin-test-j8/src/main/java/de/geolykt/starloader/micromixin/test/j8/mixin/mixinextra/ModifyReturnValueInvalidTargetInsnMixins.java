package de.geolykt.starloader.micromixin.test.j8.mixin.mixinextra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueInvalidTargetInsnTest;

public class ModifyReturnValueInvalidTargetInsnMixins {

    @Mixin(value = ModifyReturnValueInvalidTargetInsnTest.TargetHead.class)
    private static class TargetHead {
        @ModifyReturnValue(at = @At("HEAD"), method = "getValue")
        private static Object modifyValue(Object o) {
            return new Object();
        }
    }

    @Mixin(value = ModifyReturnValueInvalidTargetInsnTest.TargetReturn.class)
    private static class TargetReturn {
        @ModifyReturnValue(at = @At("RETURN"), method = "getValue")
        private static Object modifyValue(Object o) {
            return new Object();
        }
        @ModifyReturnValue(at = @At("TAIL"), method = "getValue")
        private static Object modifyValue2(Object o) {
            return new Object();
        }
    }
}
