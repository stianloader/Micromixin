package org.stianloader.micromixin.backports;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IExtensionRegistry;

public class MicromixinBackportsBootstrap {

    private static boolean globalInit = false;

    private static void globalInit() {
        if (MicromixinBackportsBootstrap.globalInit) {
            throw new IllegalStateException("Already globally initialized. (Calling this method in the wrong thread?)");
        }

        InjectionInfo.register(CanonicalOverwriteInjectionInfo.class);
        MicromixinBackportsBootstrap.globalInit = true;

        /*
        Field f;
        try {
            f = InjectionInfo.class.getDeclaredField("registry");
            f.setAccessible(true);
            Map<?, ?> registry = (Map<?, ?>) f.get(null);
            System.out.println("Registry contents:");

            for (Map.Entry<?, ?> e : registry.entrySet()) {
                Field f1 = e.getValue().getClass().getDeclaredField("annotationType");
                Field f2 = e.getValue().getClass().getDeclaredField("injectorType");
                Field f3 = e.getValue().getClass().getDeclaredField("annotationDesc");
                Field f4 = e.getValue().getClass().getDeclaredField("prefix");
                f1.setAccessible(true);
                f2.setAccessible(true);
                f3.setAccessible(true);
                f4.setAccessible(true);

                System.out.println("  - " + e.getKey() + ": " + f1.get(e.getValue()) + "; " + f2.get(e.getValue()) + "; " + f3.get(e.getValue()) + "; " + f4.get(e.getValue()) + "; ");
            }

            System.out.println("Registered annotations: "+ InjectionInfo.getRegisteredAnnotations());
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

    }

    public static void init(@NotNull Extensions extensionRegistry) {
        if (!MicromixinBackportsBootstrap.globalInit) {
            MicromixinBackportsBootstrap.globalInit();
        }
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
