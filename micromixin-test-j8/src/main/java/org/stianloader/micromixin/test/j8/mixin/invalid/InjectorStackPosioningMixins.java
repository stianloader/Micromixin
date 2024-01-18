package org.stianloader.micromixin.test.j8.mixin.invalid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stianloader.micromixin.test.j8.targets.invalid.InjectorStackPosioningTest;

@Mixin(InjectorStackPosioningTest.class)
public class InjectorStackPosioningMixins {

    @Inject(at = @At("TAIL"), target = @Desc(value = "getBehaviour", ret = String.class), cancellable = true)
    private static String setBehaviour(CallbackInfoReturnable<String> cir) {
        return "POISONING";
    }

    @Mixin(InjectorStackPosioningTest.IllegalPoison.class)
    private static class IllegalPoison {
        @Inject(at = {
                @At(value = "CONSTANT", args = "intValue=15"),
                @At(value = "CONSTANT", args = "nullValue=true"),
                @At(value = "HEAD")
        }, target = @Desc(value = "getValue", ret = String.class), cancellable = true)
        private static String poison(CallbackInfoReturnable<String> cir) {
            return "the poisoned apple";
        }
    }
}
