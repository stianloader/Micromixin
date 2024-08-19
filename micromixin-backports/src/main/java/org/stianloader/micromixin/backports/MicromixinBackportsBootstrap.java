package org.stianloader.micromixin.backports;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

public class MicromixinBackportsBootstrap {

    public static void init(@NotNull IMixinTransformer transformer) {
        // Do nothing, for now (all annotation as purely compile-time syntax sugar for now)
    }

    public static void init() {
        Object transformer = MixinEnvironment.getDefaultEnvironment().getActiveTransformer();
        if (transformer == null) {
            throw new IllegalStateException("Default mixin environment has no active transformer.");
        }
        if (!(transformer instanceof IMixinTransformer)) {
            throw new IllegalStateException("Default mixin environment has a transformer of type " + transformer.getClass().getTypeName() + ". However, a transformer of type " + IMixinTransformer.class.getTypeName() + " was expected.");
        }
        MicromixinBackportsBootstrap.init((IMixinTransformer) transformer);
    }
}
