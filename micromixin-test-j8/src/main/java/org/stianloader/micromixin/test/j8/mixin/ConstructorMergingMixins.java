package org.stianloader.micromixin.test.j8.mixin;

import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.stianloader.micromixin.test.j8.MutableInt;
import org.stianloader.micromixin.test.j8.targets.ConstructorMergingTest;

@Mixin(ConstructorMergingTest.class)
public class ConstructorMergingMixins {
    @Shadow
    public int shadowField0 = 4;
    @Shadow
    public int shadowField1;
    @Shadow
    public int shadowField3;
    private Object witness0 = new Object();
    private Object witness1;
    private String witness2;

    public ConstructorMergingMixins(int value) {
        this.shadowField3 = 10;
        this.witness1 = new Object();
        if (Boolean.getBoolean("false")) {
            return;
        } else if (Boolean.getBoolean("true")) {
            throw new AssertionError();
        }
        ((MutableInt) (Object) this).add(1);
        this.shadowField1 = 6;
        this.witness2 = "0b" + Integer.toBinaryString(value);
    }

    public ConstructorMergingMixins(String thingy) {
        // Second constructor, it's there for other reasons
    }

    @Shadow
    public int shadowField2 = 8;
    private String witness3 = "0x" + Integer.toHexString(10).toUpperCase(Locale.ROOT);

    @Overwrite
    public Object getWitness0() {
        return this.witness0;
    }

    @Overwrite
    public Object getWitness1() {
        return this.witness1;
    }

    @Overwrite
    public Object getWitness2() {
        return this.witness2;
    }

    @Overwrite
    public Object getWitness3() {
        return this.witness3;
    }
}
