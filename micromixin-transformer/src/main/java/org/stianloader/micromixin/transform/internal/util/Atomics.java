package org.stianloader.micromixin.transform.internal.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Atomics {
    @Contract(pure = true)
    public static int getPlain(@NotNull AtomicInteger atomicInt) {
        return atomicInt.get();
    }

    @Contract(pure = false, mutates = "param1")
    public static void setPlain(@NotNull AtomicInteger atomicInt, int value) {
        atomicInt.lazySet(value);
    }
}
