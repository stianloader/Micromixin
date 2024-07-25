package org.stianloader.micromixin.transform.internal.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class that copies functionality from java.util.Objects
 * that is not present on java 6.
 *
 * <p>Also includes other unavailable methods that didn't fit elsewhere.
 *
 * <p>This class may appear as deprecated for external consumers
 * of micromixin-transformer, however for internal use this class can still be safely
 * used. For external use, you probably meant to use the Object class
 * provided by the java runtime - please configure your IDE to ignore
 * the internal packages of micromixin-transformer.
 */
@ApiStatus.Internal
public final class Objects {

    @SuppressWarnings("unchecked")
    public static Throwable addSuppressed(Throwable throwable, List<? extends Throwable> suppressed) {
        StringBuilder builder = new StringBuilder();
        builder.append("Following suppressed exceptions (consider updating your Java version for better details):\n");
        Throwable cause = throwable.getCause();
        List<? extends Throwable> suppressed2 = new ArrayList<Throwable>(suppressed);
        if (cause != null) {
            ((List<Throwable>) suppressed2).add(cause);
            ((List<Throwable>) suppressed2).add(throwable);
        }

        for (Throwable t : suppressed2) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            builder.append("Suppressed:\n");
            builder.append(sw.toString());
        }

        if (cause == null) {
            throwable.initCause(new RuntimeException(builder.toString()));
            return throwable;
        } else {
            RuntimeException ex = new RuntimeException("Altered stacktrace (Your java is out of date. Please update your java!):" + throwable.getMessage(), new RuntimeException(builder.toString()));
            return ex;
        }
    }

    @Contract(pure = true, value = "null, !null -> false; null, null -> true; !null, null -> false")
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

    @NotNull
    @Contract(pure = true, value = "!null -> param1; null -> fail")
    public static <T> T requireNonNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }

    @NotNull
    @Contract(pure = true, value = "!null, _ -> param1; null, _ -> fail")
    public static <T> T requireNonNull(T object, @NotNull String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    @SuppressWarnings("null")
    @NotNull
    @Contract(pure = true, value = "_ -> !null")
    public static String toString(@Nullable Object o) {
        if (o == null) {
            return "null";
        } else {
            return o.toString();
        }
    }

    private Objects() {
        throw new AssertionError();
    }
}
