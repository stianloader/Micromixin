package org.stianloader.micromixin.transform.internal.util;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class that copies functionality from java.util.Objects
 * that is not present on java 6.
 *
 * As this is the implementation under Java 9 and above, we can simply delegate the calls back to
 * java.util.Objects, which may have more optimised implementations of the methods, thus
 * aiding JIT.
 */
public final class Objects {

    private Objects() {
        throw new AssertionError();
    }

    public static Throwable addSuppressed(Throwable throwable, List<? extends Throwable> suppressed) {
        suppressed.forEach(throwable::addSuppressed);
        return throwable;
    }

    public static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        return java.util.Objects.equals(o1, o2);
    }

    public static int hashCode(@Nullable Object o) {
        return java.util.Objects.hashCode(o);
    }

    @NotNull
    public static <T> T requireNonNull(T object) {
        return java.util.Objects.requireNonNull(object);
    }

    @NotNull
    public static <T> T requireNonNull(T object, @NotNull String message) {
        return java.util.Objects.requireNonNull(object, message);
    }

    @NotNull
    public static String toString(@Nullable Object o) {
        return java.util.Objects.toString(o);
    }
}
