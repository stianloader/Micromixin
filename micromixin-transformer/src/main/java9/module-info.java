module de.geolykt.starloader.micromixin.transform {
    requires transitive org.objectweb.asm;
    requires org.objectweb.asm.commons;
    requires transitive org.objectweb.asm.tree;
    requires org.objectweb.asm.util;
    requires transitive org.json;
    requires org.jetbrains.annotations;

    exports de.geolykt.micromixin;
    exports de.geolykt.micromixin.api;
    exports de.geolykt.micromixin.supertypes;
}
