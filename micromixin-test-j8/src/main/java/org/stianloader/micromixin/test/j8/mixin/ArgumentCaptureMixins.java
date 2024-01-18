package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stianloader.micromixin.test.j8.targets.ArgumentCaptureTest;

@Mixin(ArgumentCaptureTest.class)
public class ArgumentCaptureMixins {

    @Inject(at = @At("HEAD"), method = "captureNaught")
    private void injectHandler0(CallbackInfo ci) {
        // NOP
    }

    @Inject(at = @At("HEAD"), method = "captureOne")
    private void injectHandler1(int a, CallbackInfo ci) {
        // NOP
    }
}
