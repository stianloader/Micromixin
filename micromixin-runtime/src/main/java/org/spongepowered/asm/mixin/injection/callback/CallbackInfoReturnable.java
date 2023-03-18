package org.spongepowered.asm.mixin.injection.callback;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({ "unchecked"})
public class CallbackInfoReturnable<R> extends CallbackInfo {

    @Nullable
    private R returnValue;

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = null;
    }

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable, @Nullable R returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable, byte returnValue) {
        super(name, cancellable);
        this.returnValue = (R) (Byte) returnValue;
    }

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable, char returnValue) {
        super(name, cancellable);
        this.returnValue = (R) (Character) returnValue;
    }

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable, double returnValue) {
        super(name, cancellable);
        this.returnValue = (R) (Double) returnValue;
    }

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable, float returnValue) {
        super(name, cancellable);
        this.returnValue = (R) (Float) returnValue;
    }

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable, int returnValue) {
        super(name, cancellable);
        this.returnValue = (R) (Integer) returnValue;
    }

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable, long returnValue) {
        super(name, cancellable);
        this.returnValue = (R) (Long) returnValue;
    }

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable, short returnValue) {
        super(name, cancellable);
        this.returnValue = (R) (Short) returnValue;
    }

    public CallbackInfoReturnable(@NotNull String name, boolean cancellable, boolean returnValue) {
        super(name, cancellable);
        this.returnValue = (R) (Boolean) returnValue;
    }

    public void setReturnValue(@Nullable R returnValue) throws CancellationException {
        this.cancel();
        this.returnValue = returnValue;
    }

    @Nullable
    public R getReturnValue() {
        return this.returnValue;
    }

    public byte getReturnValueB() {
        Byte value = (Byte) this.returnValue;
        if (value == null) {
            return 0;
        }
        return value;
    }

    public char getReturnValueC() {
        Character value = (Character) this.returnValue;
        if (value == null) {
            return 0;
        }
        return value;
    }

    public double getReturnValueD() {
        Double value = (Double) this.returnValue;
        if (value == null) {
            return 0;
        }
        return value;
    }

    public float getReturnValueF() {
        Float value = (Float) this.returnValue;
        if (value == null) {
            return 0;
        }
        return value;
    }

    public int getReturnValueI() {
        Integer value = (Integer) this.returnValue;
        if (value == null) {
            return 0;
        }
        return value;
    }

    public long getReturnValueJ() {
        Long value = (Long) this.returnValue;
        if (value == null) {
            return 0;
        }
        return value;
    }

    public short getReturnValueS() {
        Short value = (Short) this.returnValue;
        if (value == null) {
            return 0;
        }
        return value;
    }

    public boolean getReturnValueZ() {
        Boolean value = (Boolean) this.returnValue;
        if (value == null) {
            return false;
        }
        return value;
    }
}
