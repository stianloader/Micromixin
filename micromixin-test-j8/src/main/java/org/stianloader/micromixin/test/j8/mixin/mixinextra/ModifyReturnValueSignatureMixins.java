package org.stianloader.micromixin.test.j8.mixin.mixinextra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

public class ModifyReturnValueSignatureMixins {

    @Mixin(value = ModifyReturnValueSignatureTest.NoConsumeValue.class)
    private static class NoConsumeValue {
        @ModifyReturnValue(at = @At("TAIL"), method = "getValue")
        private static Object modifyValue() {
            return new Object();
        }
    }

    @Mixin(value = ModifyReturnValueSignatureTest.NoProvideValue.class)
    private static class NoProvideValue {
        @ModifyReturnValue(at = @At("TAIL"), method = "getValue")
        private static void modifyValue(Object o) {
        }
    }

    @Mixin(value = ModifyReturnValueSignatureTest.TargetVoid.class)
    private static class TargetVoid {
        @ModifyReturnValue(at = @At("TAIL"), method = "getValue")
        private static Object modifyValue(Object o) {
            return new Object();
        }
    }

    @Mixin(value = ModifyReturnValueSignatureTest.TargetVoidNoConsume.class)
    private static class TargetVoidNoConsume {
        @ModifyReturnValue(at = @At("TAIL"), method = "getValue")
        private static Object modifyValue() {
            return new Object();
        }
    }

    @Mixin(value = ModifyReturnValueSignatureTest.TargetVoidRun.class)
    private static class TargetVoidRun {
        @ModifyReturnValue(at = @At("TAIL"), method = "getValue")
        private static void modifyValue() {
        }
    }
}
