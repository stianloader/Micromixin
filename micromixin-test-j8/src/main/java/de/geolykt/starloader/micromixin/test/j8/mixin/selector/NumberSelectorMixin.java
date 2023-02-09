package de.geolykt.starloader.micromixin.test.j8.mixin.selector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.geolykt.starloader.micromixin.test.j8.selector.NumberSelectorTest;

@Mixin(NumberSelectorTest.class)
public class NumberSelectorMixin {

    @Inject(at = @At(value = "CONSTANT", args = "intValue=2"), target = @Desc(value = "printNumbers"), require = 1)
    private static void target2s(CallbackInfo ci) {
        System.out.println("2!");
    }
}
