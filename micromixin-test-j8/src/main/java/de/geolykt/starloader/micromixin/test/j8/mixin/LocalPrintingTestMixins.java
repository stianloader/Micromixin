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

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalInstance0", ret = int.class),
            locals = LocalCapture.PRINT)
    private void injectorLocalInstance0(CallbackInfoReturnable<Integer> cir) {
        // NOP
    }
}
