package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stianloader.micromixin.test.j8.targets.MultiInjectTest;

@Mixin(MultiInjectTest.class)
public class MultiInjectTestMixins {

    @Inject(target = {
            @Desc(value = "injectionPointA0"), @Desc(value = "injectionPointA1"),
            @Desc(value = "injectionPointA2"), @Desc(value = "injectionPointA3")
            }, at = @At("HEAD"))
    private static void injectorA(CallbackInfo ci) {
        // Injector should eagerly match all
        MultiInjectTest.injectionPointACount++;
    }

    @Inject(target = {
            @Desc(value = "injectionPointB0"), @Desc(value = "injectionPointB1"),
            @Desc(value = "injectionPointB2"), @Desc(value = "injectionPointB3")
            }, at = @At("HEAD"),
            cancellable = true)
    private static void injectorB(CallbackInfo ci) {
        ci.cancel();
        MultiInjectTest.injectionPointBCount++;
    }

    @Inject(target = {
            @Desc(value = "injectionPointC0"), @Desc(value = "injectionPointC1"),
            @Desc(value = "injectionPointC2"), @Desc(value = "injectionPointC3")
            }, at = @At("HEAD"),
            cancellable = true)
    private static void injectorC(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(target = {
            @Desc(value = "injectionPointD0"), @Desc(value = "injectionPointD1"),
            @Desc(value = "injectionPointD2"), @Desc(value = "injectionPointD3")
            }, at = @At("TAIL"),
            cancellable = true)
    private static void injectorD(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(target = {
            @Desc(value = "injectionPointE0"), @Desc(value = "injectionPointE1"),
            @Desc(value = "injectionPointE2"), @Desc(value = "injectionPointE3")
            }, at = @At("HEAD"))
    private static void injectorE(CallbackInfo ci) {
        // Injector should only match injectionPointE0 (which has void as it's return type)
        MultiInjectTest.injectionPointECount++;
    }

    @Inject(target = {
            @Desc(value = "injectionPointF0"), @Desc(value = "injectionPointF1"),
            @Desc(value = "injectionPointF2")
            }, at = @At("HEAD"))
    private static double injectorF(CallbackInfo ci) {
        // Injector should only match injectionPointF0 and injectionPointF2 (which have void as their return type)
        MultiInjectTest.injectionPointFCount++;
        return 0;
    }

    @Inject(target = {
            @Desc(value = "injectionPointG0", ret = double.class), @Desc(value = "injectionPointG1", ret = double.class),
            @Desc(value = "injectionPointG2", ret = double.class)
            }, at = @At("TAIL"))
    private static double injectorG(CallbackInfoReturnable<Double> ci) {
        // Injector should only match injectionPointG1 (which has double as it's return type)
        MultiInjectTest.injectionPointGCount++;
        return 0;
    }
}
