package org.stianloader.micromixin.backports;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IExtensionRegistry;

public class MicromixinBackportsBootstrap {

    public static void init(@NotNull Extensions extensionRegistry) {
        
    }

    public static void init(@NotNull IMixinTransformer transformer) {
        IExtensionRegistry extensionRegistry = transformer.getExtensions();
        if (extensionRegistry == null) {
            throw new IllegalArgumentException("The provided transformer lacks an extension registry.");
        }
        if (!(extensionRegistry instanceof Extensions)) {
            throw new IllegalArgumentException("The given transformer does not have a extension registry known to support mutation. It is of type " + extensionRegistry.getClass().getName());
        }

        MicromixinBackportsBootstrap.init((Extensions) extensionRegistry);
    }

    public static void init() {
        Object transformer = MixinEnvironment.getDefaultEnvironment().getActiveTransformer();
        if (transformer == null) {
            throw new IllegalStateException("Default mixin environment has no active transformer.");
        }
        if (!(transformer instanceof IMixinTransformer)) {
            throw new IllegalStateException("Default mixin environment has a transformer of type " + transformer.getClass().getName() + ". However, a transformer of type " + IMixinTransformer.class.getName() + " was expected.");
        }
        MicromixinBackportsBootstrap.init((IMixinTransformer) transformer);
    }
}
