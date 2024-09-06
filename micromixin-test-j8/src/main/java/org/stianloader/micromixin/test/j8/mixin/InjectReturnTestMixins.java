package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stianloader.micromixin.test.j8.targets.InjectReturnTest;

/**
 * Regression tests for https://github.com/stianloader/Micromixin/pull/4
 */
@Mixin(InjectReturnTest.class)
public class InjectReturnTestMixins {
    @Shadow
    private static int valueArray;
    @Shadow
    private static int valueB;
    @Shadow
    private static int valueC;
    @Shadow
    private static int valueD;
    @Shadow
    private static int valueF;
    @Shadow
    private static int valueI;
    @Shadow
    private static int valueJ;
    @Shadow
    private static int valueObject;
    @Shadow
    private static int valueS;
    @Shadow
    private static int valueV;
    @Shadow
    private static int valueZ;

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorArray", ret = Object[].class))
    private static void injectStaticArray(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueArray = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorB", ret = byte.class))
    private static void injectStaticB(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueB = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorC", ret = char.class))
    private static void injectStaticC(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueC = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorD", ret = double.class))
    private static void injectStaticD(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueD = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorF", ret = float.class))
    private static void injectStaticF(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueF = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorI", ret = int.class))
    private static void injectStaticI(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueI = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorJ", ret = long.class))
    private static void injectStaticJ(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueJ = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorObject", ret = Object.class))
    private static void injectStaticObject(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueObject = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorS", ret = short.class))
    private static void injectStaticS(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueS = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorV", ret = void.class))
    private static void injectStaticV(CallbackInfo ci) {
        InjectReturnTestMixins.valueV = 2;
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "callInjectorZ", ret = boolean.class))
    private static void injectStaticZ(CallbackInfoReturnable<?> ci) {
        InjectReturnTestMixins.valueZ = 2;
    }
}
