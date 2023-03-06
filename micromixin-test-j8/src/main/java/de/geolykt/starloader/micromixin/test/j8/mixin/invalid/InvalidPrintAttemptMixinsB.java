package de.geolykt.starloader.micromixin.test.j8.mixin.invalid;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import de.geolykt.starloader.micromixin.test.j8.targets.invalid.InvalidPrintAttemptTestB;

@Mixin(InvalidPrintAttemptTestB.class)
public class InvalidPrintAttemptMixinsB {
    @Inject(at = @At("HEAD"), target = @Desc("staticMethod"), locals = LocalCapture.PRINT)
    private void instanceInjector(@NotNull CallbackInfo ci) {
    }
}
