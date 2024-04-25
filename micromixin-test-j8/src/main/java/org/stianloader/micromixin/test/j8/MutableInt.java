package org.stianloader.micromixin.test.j8;

public class MutableInt {

    public static MutableInt valueOf(int val) {
        return new MutableInt(val);
    }

    private int val;

    public MutableInt(int val) {
        this.val = val;
    }

    public MutableInt() {
        this(0);
    }

    public MutableInt add(int val) {
        this.val += val;
        return this;
    }

    public MutableInt add(MutableInt val) {
        this.val += val.val;
        return this;
    }

    public int intValue() {
        return this.val;
    }

    public MutableInt mul(int val) {
        this.val *= val;
        return this;
    }

    public MutableInt mul(MutableInt val) {
        this.val *= val.val;
        return this;
    }

    public MutableInt set(int val) {
        this.val = val;
        return this;
    }
}
