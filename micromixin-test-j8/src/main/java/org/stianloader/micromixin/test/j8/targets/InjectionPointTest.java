package org.stianloader.micromixin.test.j8.targets;

import org.stianloader.micromixin.test.j8.MutableInt;

public class InjectionPointTest {

    public static class FieldInjectDescInt {
        private int fieldSource = 0;

        public int computeFieldSum() {
            return this.fieldSource + this.fieldSource;
        }
    }

    public static class FieldInjectDescObject {
        private Object fieldSource = Integer.valueOf(0);

        public int computeFieldSum() {
            return ((Integer) this.fieldSource).intValue() + ((Integer) this.fieldSource).intValue();
        }
    }

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
