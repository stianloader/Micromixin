package org.stianloader.micromixin.test.j8.targets;

import org.stianloader.micromixin.test.j8.MutableInt;

public class InjectionPointTest {
    public int targetBeforeNewDescString(MutableInt arg) {
        new MutableInt();
        new MutableInt(1);
        return arg.intValue();
    }

    public int targetBeforeNewFQNString(MutableInt arg) {
        new MutableInt();
        new MutableInt(1);
        return arg.intValue();
    }

    public int targetBeforeNewOwnerDescString(MutableInt arg) {
        new MutableInt();
        new MutableInt(1);
        return arg.intValue();
    }

    public int targetBeforeNewOwnerDotDescString(MutableInt arg) {
        new MutableInt();
        new MutableInt(1);
        return arg.intValue();
    }

    public int targetBeforeNewOwnerDotString(MutableInt arg) {
        new MutableInt();
        new MutableInt(1);
        return arg.intValue();
    }

    public int targetBeforeNewOwnerString(MutableInt arg) {
        new MutableInt();
        new MutableInt(1);
        return arg.intValue();
    }
}
