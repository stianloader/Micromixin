package org.stianloader.micromixin.test.j8.targets.invalid;

import org.stianloader.micromixin.test.j8.Blackhole;

public class InvalidDuplicateSliceTest {
    public static void target() {
        new Blackhole().dump(1);
        new Blackhole().dump(2);
        new Blackhole().dump(3);
        new Blackhole().dump(4);
    }
}
