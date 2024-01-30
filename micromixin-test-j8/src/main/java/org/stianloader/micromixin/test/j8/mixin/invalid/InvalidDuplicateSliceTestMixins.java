package org.stianloader.micromixin.test.j8.mixin.invalid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stianloader.micromixin.test.j8.targets.invalid.InvalidDuplicateSliceTest;

@Mixin(InvalidDuplicateSliceTest.class)
public class InvalidDuplicateSliceTestMixins {
    @Inject(at = @At("HEAD"),
            target = @Desc(value = "target", args = {}, ret = void.class),
            slice = {
                    @Slice(from = @At(value = "CONSTANT", args = "intValue=1"), to = @At(value = "CONSTANT", args = "intValue=3")),
                    @Slice(from = @At(value = "CONSTANT", args = "intValue=2"), to = @At(value = "CONSTANT", args = "intValue=4"))
            })
    private static void slice4Inject(CallbackInfo ci) {
        // Do nothing
    }
}
