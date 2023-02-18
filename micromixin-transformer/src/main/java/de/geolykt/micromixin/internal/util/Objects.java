package de.geolykt.micromixin.internal.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class that copies functionality from java.util.Objects
 * that is not present on java 6.
 */
public final class Objects {

    private Objects() {
        throw new AssertionError();
    }

    @NotNull
    public static <T> T requireNonNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }

    @NotNull
    public static <T> T requireNonNull(T object, @NotNull String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static boolean equals(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else if (o2 == null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }

    public static int hashCode(@Nullable Object o) {
        if (o == null) {
            return 0;
        }
        return o.hashCode();
    }
}
