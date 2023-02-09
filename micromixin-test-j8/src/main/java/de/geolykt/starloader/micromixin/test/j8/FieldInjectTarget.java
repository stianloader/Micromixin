package de.geolykt.starloader.micromixin.test.j8;

public class FieldInjectTarget {

    public FieldInjectTarget() {
        this("Invoke args constructor first");
        System.out.println("No-args Constructor");
    }

    public FieldInjectTarget(String x) {
        System.err.println(x);
        System.out.println("Args constructor");
    }

    static {
        System.out.println("FieldInjectTarget static-init block invoked");
    }
}
