package org.stianloader.micromixin.test.j8.targets;

import org.stianloader.micromixin.test.j8.MutableInt;

public class ConstructorMergingTest extends MutableInt {
    public int shadowField0 = 3;
    public int shadowField1 = 5;
    public int shadowField2 = 7;
    public int shadowField3 = 9;

    public ConstructorMergingTest(int value) {
        super(value);
        if (Boolean.getBoolean("true")) {
            return;
        } else if (Boolean.getBoolean("false")) {
            return;
        }
    }

    public ConstructorMergingTest(int value, Void otherConstructor) {
        super(value);
        if (Boolean.getBoolean("true")) {
            return;
        } else if (Boolean.getBoolean("false")) {
            return;
        }
    }

    public Object getWitness0() {
        throw new UnsupportedOperationException();
    }

    public Object getWitness1() {
        throw new UnsupportedOperationException();
    }

    public Object getWitness2() {
        throw new UnsupportedOperationException();
    }

    public Object getWitness3() {
        throw new UnsupportedOperationException();
    }
}
