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
    private static final MethodHandle MH_LOG_ERROR_THROWABLE;

    @NotNull
    private static final MethodHandle MH_LOG_INFO;

    @NotNull
    private static final MethodHandle MH_LOG_DEBUG_VARARGS;

    @NotNull
    private static final MethodHandle MH_LOG_WARNING;

    @NotNull
    private static final MethodHandle MH_LOG_CL_FAILURES_GET;

    @NotNull
    private static final MethodHandle MH_LOG_CL_FAILURES_SET;

    static {
        try {
            Class<?> loggerFactoryClass = SLF4JLogger.class.getClassLoader().loadClass("org.slf4j.LoggerFactory");
            Class<?> loggerClass = SLF4JLogger.class.getClassLoader().loadClass("org.slf4j.Logger");
            Class<?> clClass = SLF4JLogger.class.getClassLoader().loadClass("net.minestom.server.extras.selfmodification.MinestomRootClassLoader");

            Lookup lookup = MethodHandles.lookup();
            MH_GET_LOGGER = lookup.findStatic(loggerFactoryClass, "getLogger", MethodType.methodType(loggerClass, Class.class));
            MH_LOG_INFO = lookup.findVirtual(loggerClass, "info", MethodType.methodType(void.class, String.class));
            MH_LOG_WARNING = lookup.findVirtual(loggerClass, "warn", MethodType.methodType(void.class, String.class));
            MH_LOG_ERROR = lookup.findVirtual(loggerClass, "error", MethodType.methodType(void.class, String.class));
            MH_LOG_ERROR_THROWABLE = lookup.findVirtual(loggerClass, "error", MethodType.methodType(void.class, String.class, Throwable.class));
            MH_LOG_DEBUG_VARARGS = lookup.findVirtual(loggerClass, "debug", MethodType.methodType(void.class, String.class, Object[].class));

            MethodHandle mhGetCLInstance = lookup.findStatic(clClass, "getInstance", MethodType.methodType(clClass));
            MethodHandle mhCLIsLoggingFailures = lookup.findVirtual(clClass, "isThreadLoggingClassloadingFailures", MethodType.methodType(boolean.class));
            MethodHandle mhCLSetLoggingFailures = lookup.findVirtual(clClass, "setThreadLoggingClassloadingFailures", MethodType.methodType(clClass, boolean.class));

            MH_LOG_CL_FAILURES_GET = MethodHandles.foldArguments(mhCLIsLoggingFailures, mhGetCLInstance);
            MH_LOG_CL_FAILURES_SET = MethodHandles.dropReturn(MethodHandles.foldArguments(mhCLSetLoggingFailures, mhGetCLInstance));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void debug(@NotNull Class<?> clazz, @NotNull String format, @NotNull Object @NotNull... objects) {
        try {
            SLF4JLogger.MH_LOG_DEBUG_VARARGS.invoke(SLF4JLogger.MH_GET_LOGGER.invoke(clazz), format, objects);
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

    public static void error(@NotNull Class<?> clazz, @NotNull String message, @NotNull Throwable t) {
        try {
            SLF4JLogger.MH_LOG_ERROR_THROWABLE.invoke(SLF4JLogger.MH_GET_LOGGER.invoke(clazz), message, t);
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

    public static boolean isThreadLoggingClassloadingFailures() {
        try {
            return (boolean) SLF4JLogger.MH_LOG_CL_FAILURES_GET.invokeExact();
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

    public static void setThreadLoggingClassloadingFailures(boolean v) {
        try {
            SLF4JLogger.MH_LOG_CL_FAILURES_SET.invokeExact(v);
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
