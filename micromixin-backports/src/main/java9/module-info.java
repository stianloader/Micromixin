module org.stianloader.micromixin.backports {
    requires org.spongepowered.mixin;
    requires java.base;
    requires static org.jetbrains.annotations;

    exports org.stianloader.micromixin.annotations;
    exports org.stianloader.micromixin.backports;
}
