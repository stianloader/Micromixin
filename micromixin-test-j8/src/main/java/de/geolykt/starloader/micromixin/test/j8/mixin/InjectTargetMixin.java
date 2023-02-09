package de.geolykt.starloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.geolykt.starloader.micromixin.test.j8.InjectTarget;

@Mixin(InjectTarget.class)
public class InjectTargetMixin {

    @Inject(at = @At("TAIL"), target = @Desc(value = "injectedMethodTail"))
    public void injectTail(CallbackInfo ci) {
        System.out.println("DEF");
    }

    @Inject(at = @At("TAIL"), target = @Desc(value = "injectedMethodTail"))
    public void injectHead(CallbackInfo ci) {
        System.out.println("DEF");
    }
}
