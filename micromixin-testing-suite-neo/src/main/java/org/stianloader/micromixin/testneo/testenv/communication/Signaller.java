package org.stianloader.micromixin.testneo.testenv.communication;

import java.util.OptionalInt;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

public class Signaller {
    private static final ThreadLocal<Integer> SIGNAL_VALUE = new ThreadLocal<>();

    /**
     * Destructively read the signal set by the {@link #setSignal(int)} method
     * on this current thread.
     *
     * @return The signal return value, or an empty {@link OptionalInt} if no signal value was set
     * since the last time {@link #readSignal()} was called.
     */
    @Contract(pure = false)
    @CheckReturnValue
    public static OptionalInt readSignal() {
        Integer v = Signaller.SIGNAL_VALUE.get();
        Signaller.SIGNAL_VALUE.set(null);
        return v == null ? OptionalInt.empty() : OptionalInt.of(v.intValue());
    }

    @Contract(pure = false)
    public static void setSignal(int value) {
        Signaller.SIGNAL_VALUE.set(value);
    }

    @Contract(pure = false)
    public static void resetSignal() {
        Signaller.SIGNAL_VALUE.set(null);
    }
}
