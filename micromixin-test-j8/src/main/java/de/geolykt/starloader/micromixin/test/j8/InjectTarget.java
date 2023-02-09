package de.geolykt.starloader.micromixin.test.j8;

public class InjectTarget {

    public void injectedMethodTail() {
        System.out.println("ABC");
    }

    public void injectedMethodHead() {
        System.out.println("DEF");
    }
}
