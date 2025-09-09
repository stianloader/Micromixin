module org.stianloader.micromixin.testneo {
    requires org.stianloader.picoresolve;
    requires org.json;
    requires java.instrument;
    requires static org.stianloader.micromixin.annotations;
    requires static transitive org.jetbrains.annotations;

    exports org.stianloader.micromixin.testneo;
    exports org.stianloader.micromixin.testneo.testenv;
}
