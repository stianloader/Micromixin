package org.stianloader.micromixin.test.j8.mixin.mixinextra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTypingTest;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(ModifyReturnValueTypingTest.class)
public class ModifyReturnValueTypingMixin {

    @Mixin(ModifyReturnValueTypingTest.ConsumeSubclass.class)
    private static class ConsumeSubclass {
        @ModifyReturnValue(method = "getValue", at = @At("TAIL"))
        private static Object modifyValue(String originalValue) {
            return originalValue;
        }
    }

    @Mixin(ModifyReturnValueTypingTest.ProvideSuperclass.class)
    private static class ProvideSuperclass {
        @ModifyReturnValue(method = "getValue", at = @At("TAIL"))
        private static String modifyValue(Object originalValue) {
            return "new value";
        }
    }

    @Mixin(ModifyReturnValueTypingTest.ProvideSubclass.class)
    private static class ProvideSubclass {
        @ModifyReturnValue(method = "getValue", at = @At("TAIL"))
        private static String modifyValue(Object originalValue) {
            return "new value";
        }
    }

    @Mixin(ModifyReturnValueTypingTest.ConsumeSuperclass.class)
    private static class ConsumeSuperclass {
        @ModifyReturnValue(method = "getValue", at = @At("TAIL"))
        private static String modifyValue(Object originalValue) {
            return "new value";
        }
    }

}
