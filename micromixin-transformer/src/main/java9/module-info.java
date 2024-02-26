module org.stianloader.micromixin.transform {
    requires transitive org.objectweb.asm;
    requires org.objectweb.asm.commons;
    requires transitive org.objectweb.asm.tree;
    requires org.objectweb.asm.util;
    requires transitive org.json;
    requires org.jetbrains.annotations;

    exports org.stianloader.micromixin.transform;
    exports org.stianloader.micromixin.transform.api;
    exports org.stianloader.micromixin.transform.supertypes;
}
