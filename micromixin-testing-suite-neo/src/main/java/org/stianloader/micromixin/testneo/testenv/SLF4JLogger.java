package org.stianloader.micromixin.testneo.testenv;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

import org.jetbrains.annotations.NotNull;

public class SLF4JLogger {

    @NotNull
    private static final MethodHandle MH_GET_LOGGER;

    @NotNull
    private static final MethodHandle MH_LOG_ERROR;

    @NotNull
    private static final MethodHandle MH_LOG_INFO;

    @NotNull
    private static final MethodHandle MH_LOG_WARNING;

    static {
        try {
            Class<?> loggerFactoryClass = SLF4JLogger.class.getClassLoader().loadClass("org.slf4j.LoggerFactory");
            Class<?> loggerClass = SLF4JLogger.class.getClassLoader().loadClass("org.slf4j.Logger");

            Lookup lookup = MethodHandles.lookup();
            MH_GET_LOGGER = lookup.findStatic(loggerFactoryClass, "getLogger", MethodType.methodType(loggerClass, Class.class));
            MH_LOG_INFO = lookup.findVirtual(loggerClass, "info", MethodType.methodType(void.class, String.class));
            MH_LOG_WARNING = lookup.findVirtual(loggerClass, "warn", MethodType.methodType(void.class, String.class));
            MH_LOG_ERROR = lookup.findVirtual(loggerClass, "error", MethodType.methodType(void.class, String.class));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void error(@NotNull Class<?> clazz, @NotNull String message) {
        try {
            SLF4JLogger.MH_LOG_ERROR.invoke(SLF4JLogger.MH_GET_LOGGER.invoke(clazz), message);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else if (e instanceof IOException) {
                throw new UncheckedIOException((IOException) e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static void info(@NotNull Class<?> clazz, @NotNull String message) {
        try {
            SLF4JLogger.MH_LOG_INFO.invoke(SLF4JLogger.MH_GET_LOGGER.invoke(clazz), message);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else if (e instanceof IOException) {
                throw new UncheckedIOException((IOException) e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static void warn(@NotNull Class<?> clazz, @NotNull String message) {
        try {
            SLF4JLogger.MH_LOG_WARNING.invoke(SLF4JLogger.MH_GET_LOGGER.invoke(clazz), message);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else if (e instanceof IOException) {
                throw new UncheckedIOException((IOException) e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
