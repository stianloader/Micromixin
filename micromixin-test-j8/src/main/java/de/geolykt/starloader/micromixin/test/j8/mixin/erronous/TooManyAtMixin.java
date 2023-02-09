package de.geolykt.starloader.micromixin.test.j8.mixin.erronous;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.geolykt.starloader.micromixin.test.j8.erronous.TooManyAtReturn;

@Mixin(TooManyAtReturn.class)
public class TooManyAtMixin {

    @Inject(at = @At("RETURN"), method = {"target"})
    private static void injectReturn(CallbackInfo ci) {
        System.out.println("Hello there!");
    }
}
