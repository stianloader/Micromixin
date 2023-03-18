module de.geolykt.starloader.micromixin.transform {
    requires org.objectweb.asm;
    requires org.objectweb.asm.commons;
    requires org.objectweb.asm.tree;
    requires org.objectweb.asm.util;
    requires org.json;
    requires org.jetbrains.annotations;

    exports de.geolykt.micromixin;
    exports de.geolykt.micromixin.supertypes;
}
