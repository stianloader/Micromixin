package de.geolykt.starloader.micromixin.test.j8.erronous;

public class TooManyAtReturn {

    public static void target(String[] args) {
        if (args.length == 0) {
            System.out.println("HI!");
            return;
        } else if (Boolean.valueOf(true)) {
            System.out.println("Not quite hello!");
            return;
        }
        if (Boolean.valueOf(false)) {
            System.out.println("Oh no! that shouldn't happen");
        }
    }
}
