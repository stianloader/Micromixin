module de.geolykt.starloader.micromixin {
    exports org.spongepowered.asm.mixin.injection.callback;
    exports de.geolykt.micromixin;

    requires org.objectweb.asm;
    requires org.objectweb.asm.tree;
    requires org.json;
    requires org.objectweb.asm.commons;
    // Technically we don't need JB Annotations to be present at runtime, but whatever.
    // The technical people among us will know how to deal with such an inconvenience should it be a larger issue
    requires org.jetbrains.annotations;
}
