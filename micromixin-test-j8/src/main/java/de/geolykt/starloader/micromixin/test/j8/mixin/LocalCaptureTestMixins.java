package de.geolykt.starloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;

import de.geolykt.starloader.micromixin.test.j8.targets.LocalCaptureTest;

@Mixin(LocalCaptureTest.class)
public class LocalCaptureTestMixins {

    /*
    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalStatic2", ret = int.class),
            locals = LocalCapture.PRINT)
    private static void injectorLocalStatic2(CallbackInfoReturnable<Integer> cir) {
    }

    @Inject(at = @At("RETURN"), target = @Desc(value = "returnLocalInstance2", ret = int.class),
            locals = LocalCapture.PRINT)
    private void injectorLocalInstance2(CallbackInfoReturnable<Integer> cir) {
    }*/
}
