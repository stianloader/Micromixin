package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stianloader.micromixin.test.j8.MutableInt;
import org.stianloader.micromixin.test.j8.targets.InjectionPointTest;

@Mixin(InjectionPointTest.class)
public class InjectionPointMixins {
    @Inject(method = "targetBeforeNewDescString", at = @At(value = "NEW", target = "(I)Lorg/stianloader/micromixin/test/j8/MutableInt;"))
    private static void targetBeforeNewDescString(MutableInt capturedArg, CallbackInfoReturnable<Integer> cir) {
        capturedArg.add(1);
    }

    @Inject(method = "targetBeforeNewFQNString", at = @At(value = "NEW", target = "Lorg/stianloader/micromixin/test/j8/MutableInt; <init>(I)Lorg/stianloader/micromixin/test/j8/MutableInt;"))
    private static void targetBeforeNewFQNString(MutableInt capturedArg, CallbackInfoReturnable<Integer> cir) {
        capturedArg.add(1);
    }

    @Inject(method = "targetBeforeNewOwnerDescString", at = @At(value = "NEW", target = "Lorg/stianloader/micromixin/test/j8/MutableInt;"))
    private static void targetBeforeNewOwnerDescString(MutableInt capturedArg, CallbackInfoReturnable<Integer> cir) {
        capturedArg.add(1);
    }

    @Inject(method = "targetBeforeNewOwnerDotDescString", at = @At(value = "NEW", target = "Lorg.stianloader.micromixin.test.j8.MutableInt;"))
    private static void targetBeforeNewOwnerDotDescString(MutableInt capturedArg, CallbackInfoReturnable<Integer> cir) {
        capturedArg.add(1);
    }

    @Inject(method = "targetBeforeNewOwnerDotString", at = @At(value = "NEW", target = "org.stianloader.micromixin.test.j8.MutableInt"))
    private static void targetBeforeNewOwnerDotString(MutableInt capturedArg, CallbackInfoReturnable<Integer> cir) {
        capturedArg.add(1);
    }

    @Inject(method = "targetBeforeNewOwnerString", at = @At(value = "NEW", target = "org/stianloader/micromixin/test/j8/MutableInt"))
    private static void targetBeforeNewOwnerString(MutableInt capturedArg, CallbackInfoReturnable<Integer> cir) {
        capturedArg.add(1);
    }
}
