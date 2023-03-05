package de.geolykt.starloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest;

@Mixin(LocalPrintingTest.class)
public class LocalPrintingTestMixins {

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalStatic0", ret = int.class),
            locals = LocalCapture.PRINT)
    private static void injectorLocalStatic0(CallbackInfoReturnable<Integer> cir) {
        // NOP
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalStaticArg0", ret = int.class, args = int.class),
            locals = LocalCapture.PRINT)
    private static void injectorLocalStaticArg0A(CallbackInfoReturnable<Integer> cir) {
        // NOP
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalStaticArg0", ret = int.class, args = int.class),
            locals = LocalCapture.PRINT)
    private static void injectorLocalStaticArg0B(int arg, CallbackInfoReturnable<Integer> cir) {
        // NOP
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalStaticArg1", ret = int.class, args = Object.class),
            locals = LocalCapture.PRINT)
    private static void injectorLocalStaticArg1A(CallbackInfoReturnable<Integer> cir) {
        // NOP
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalStaticArg1", ret = int.class, args = Object.class),
            locals = LocalCapture.PRINT)
    private static void injectorLocalStaticArg1B(Object arg, CallbackInfoReturnable<Integer> cir) {
        // NOP
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalInstance0", ret = int.class),
            locals = LocalCapture.PRINT)
    private void injectorLocalInstance0(CallbackInfoReturnable<Integer> cir) {
        // NOP
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalInstanceArg0", ret = int.class, args = int.class),
            locals = LocalCapture.PRINT)
    private void injectorLocalInstanceArg0A(CallbackInfoReturnable<Integer> cir) {
        // NOP
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalInstanceArg0", ret = int.class, args = int.class),
            locals = LocalCapture.PRINT)
    private void injectorLocalInstanceArg0B(int arg, CallbackInfoReturnable<Integer> cir) {
        // NOP
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalInstanceArg1", ret = int.class, args = Object.class),
            locals = LocalCapture.PRINT)
    private void injectorLocalInstanceArg1A(CallbackInfoReturnable<Integer> cir) {
        // NOP
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalInstanceArg1", ret = int.class, args = Object.class),
            locals = LocalCapture.PRINT)
    private void injectorLocalInstanceArg1B(Object arg, CallbackInfoReturnable<Integer> cir) {
        // NOP
    }
}
