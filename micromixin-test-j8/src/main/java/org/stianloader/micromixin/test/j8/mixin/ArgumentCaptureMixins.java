package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stianloader.micromixin.test.j8.targets.ArgumentCaptureTest;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(ArgumentCaptureTest.class)
public class ArgumentCaptureMixins {

    @ModifyConstant(method = "captureModifyConstantMulti0", constant = @Constant(intValue = 0))
    private int captureModifyConstantMulti0(int originalReturnValue, int capturedArgumentA, int capturedArgumentB) {
        return capturedArgumentA - capturedArgumentB;
    }

    @ModifyConstant(method = "captureModifyConstantMulti1", constant = @Constant(longValue = 0))
    private long captureModifyConstantMulti1(long originalReturnValue, int capturedArgumentA, int capturedArgumentB) {
        return capturedArgumentA - capturedArgumentB;
    }

    @ModifyConstant(method = "captureModifyConstantMulti2", constant = @Constant(intValue = 0))
    private int captureModifyConstantMulti2(int originalReturnValue, long capturedArgumentA, int capturedArgumentB) {
        return (int) (capturedArgumentA - capturedArgumentB);
    }

    @ModifyConstant(method = "captureModifyConstantMulti3", constant = @Constant(intValue = 0))
    private int captureModifyConstantMulti3(int originalReturnValue, int capturedArgumentA, long capturedArgumentB) {
        return (int) (capturedArgumentA - capturedArgumentB);
    }

    @ModifyConstant(method = "captureModifyConstantMulti4", constant = @Constant(intValue = 0))
    private int captureModifyConstantMulti4(int originalReturnValue, long capturedArgumentA, long capturedArgumentB) {
        return (int) (capturedArgumentA - capturedArgumentB);
    }

    @ModifyConstant(method = "captureModifyConstantMulti5", constant = @Constant(longValue  = 0))
    private long captureModifyConstantMulti5(long originalReturnValue, int capturedArgumentA, long capturedArgumentB) {
        return capturedArgumentA - capturedArgumentB;
    }

    @Inject(at = @At("HEAD"), method = "injectCaptureAndAddDoubles(DDD)D", cancellable = true)
    private void injectCaptureAndAddDoubles(double arg0, double arg1, double arg2, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(arg0 + arg1 + arg2);
    }

    @Inject(at = @At("HEAD"), method = "captureNaught")
    private void injectHandler0(CallbackInfo ci) {
        // NOP
    }

    @Inject(at = @At("HEAD"), method = "captureOne")
    private void injectHandler1(int a, CallbackInfo ci) {
        // NOP
    }

    @Inject(at = @At("HEAD"), method = "captureOne")
    private void injectHandler2(int a, CallbackInfo ci) {
        // NOP
    }

    @ModifyConstant(method = "captureModifyConstant", constant = @Constant(intValue = 0))
    private int modifyConstantValue(int originalReturnValue, boolean capturedArgument) {
        if (originalReturnValue != 0) {
            throw new AssertionError("captureModifyConstant == true; expected opposite");
        }
        return capturedArgument ? 1 : 0;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = "captureModifyReturnValue")
    private boolean modifyValue(boolean originalReturnValue, boolean capturedArgument) {
        if (originalReturnValue) {
            throw new AssertionError("originalReturnValue == true; expected opposite");
        }
        return capturedArgument;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = "captureModifyReturnValueMulti0")
    private int modifyValueMulti0(int originalReturnValue, int capturedArgumentA, int capturedArgumentB) {
        return capturedArgumentA - capturedArgumentB;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = "captureModifyReturnValueMulti1")
    private long modifyValueMulti1(long originalReturnValue, int capturedArgumentA, int capturedArgumentB) {
        return capturedArgumentA - capturedArgumentB;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = "captureModifyReturnValueMulti2")
    private int modifyValueMulti2(int originalReturnValue, long capturedArgumentA, int capturedArgumentB) {
        return (int) (capturedArgumentA - capturedArgumentB);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = "captureModifyReturnValueMulti3")
    private int modifyValueMulti3(int originalReturnValue, int capturedArgumentA, long capturedArgumentB) {
        return (int) (capturedArgumentA - capturedArgumentB);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = "captureModifyReturnValueMulti4")
    private int modifyValueMulti4(int originalReturnValue, long capturedArgumentA, long capturedArgumentB) {
        return (int) (capturedArgumentA - capturedArgumentB);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = "captureModifyReturnValueMulti5")
    private long modifyValueMulti5(long originalReturnValue, int capturedArgumentA, long capturedArgumentB) {
        return capturedArgumentA - capturedArgumentB;
    }
}
