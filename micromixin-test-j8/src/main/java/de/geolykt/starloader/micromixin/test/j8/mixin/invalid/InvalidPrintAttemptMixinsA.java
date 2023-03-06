package de.geolykt.starloader.micromixin.test.j8.mixin.invalid;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import de.geolykt.starloader.micromixin.test.j8.targets.invalid.InvalidPrintAttemptTestA;

@Mixin(InvalidPrintAttemptTestA.class)
public class InvalidPrintAttemptMixinsA {
    @Inject(at = @At("HEAD"), target = @Desc("instanceMethod"), locals = LocalCapture.PRINT)
    private static void staticInjector(@NotNull CallbackInfo ci) {
    }
}
