module org.stianloader.micromixin.annotations {
    requires static org.jetbrains.annotations;

    exports org.spongepowered.asm.mixin.injection.callback;
    exports org.spongepowered.asm.mixin.injection;
    exports org.spongepowered.asm.mixin;
    exports org.stianloader.micromixin.annotations;

    exports com.llamalad7.mixinextras.injector;
    exports com.llamalad7.mixinextras.injector.wrapoperation;
    exports com.llamalad7.mixinextras.sugar;
}
