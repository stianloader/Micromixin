package de.geolykt.starloader.micromixin.test.j8.targets.invalid;

import de.geolykt.starloader.micromixin.test.j8.Blackhole;

public class InjectorStackPosioningTest {

    public static String getBehaviour() {
        return "POPPING";
    }

    public static class IllegalPoison {
        public static String getValue() {
            for (int i = 0; i < 100; i++) {
                if (Boolean.getBoolean(null)) {
                    i -= 80;
                }
                i += 15;
            }
            new Blackhole().dump("Padding, for safety.");
            return "poison popped";
        }
    }
}
