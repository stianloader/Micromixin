package org.stianloader.micromixin.transform.internal;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class DefaultMixinLogger implements MixinLoggingFacade {
    @NotNull
    @Contract(pure = false, mutates = "param1", value = "!null, _, _ -> param1")
    private static StringBuilder appendMessage(@NotNull StringBuilder builder, String message, Object... args) {
        for (int i = 0; i < args.length; i++) {
            int replaceHead = message.indexOf("{}");
            if (replaceHead == -1) {
                builder.append(message);
                message = "";
                if (i == (args.length - 1) && args[i] instanceof Throwable) {
                    builder.append('\n');
                    StringWriter sw = new StringWriter();
                    ((Throwable) args[i]).printStackTrace(new PrintWriter(sw));
                    builder.append(sw.toString());
                } else {
                    builder.append(Objects.toString(args[i]));
                }
            } else {
                builder.append(message.subSequence(0, replaceHead)).append(Objects.toString(args[i]));
                message = message.substring(replaceHead + 2);
            }
        }

        builder.append(message);
        return builder;
    }

    private final boolean debug;

    public DefaultMixinLogger(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void debug(Class<?> clazz, String message, Object... args) {
        if (this.debug) {
            StringBuilder builder = new StringBuilder();
            builder.append("(DEBUG: Micromixin/").append(clazz.getSimpleName()).append("): ");
            System.out.println(DefaultMixinLogger.appendMessage(builder, message, args).toString());
        }
    }

    @Override
    public void error(Class<?> clazz, String message, Object... args) {
        StringBuilder builder = new StringBuilder();
        builder.append("(ERROR: Micromixin/").append(clazz.getSimpleName()).append("): ");
        System.err.println(DefaultMixinLogger.appendMessage(builder, message, args).toString());
    }

    @Override
    public void info(Class<?> clazz, String message, Object... args) {
        StringBuilder builder = new StringBuilder();

        builder.append("(INFO: Micromixin/").append(clazz.getSimpleName()).append("): ");
        System.out.println(DefaultMixinLogger.appendMessage(builder, message, args).toString());
    }

    @Override
    public void warn(Class<?> clazz, String message, Object... args) {
        StringBuilder builder = new StringBuilder();

        builder.append("(WARN: Micromixin/").append(clazz.getSimpleName()).append("): ");
        System.out.println(DefaultMixinLogger.appendMessage(builder, message, args).toString());
    }
}
