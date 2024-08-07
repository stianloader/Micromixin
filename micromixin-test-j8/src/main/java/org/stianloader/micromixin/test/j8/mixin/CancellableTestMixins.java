package org.stianloader.micromixin.test.j8.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stianloader.micromixin.test.j8.MutableInt;
import org.stianloader.micromixin.test.j8.targets.CancellableTest;

import com.llamalad7.mixinextras.sugar.Cancellable;

@Mixin(CancellableTest.class)
public class CancellableTestMixins {
    @Redirect(at = @At(value = "INVOKE", target = "intValue"), method = "redirectStatic", require = 1, expect = 1, allow = 1)
    private static int handleRedirectStatic(@NotNull MutableInt caller, @Cancellable CallbackInfo ci) {
        ci.cancel();
        return 4;
    }
}
