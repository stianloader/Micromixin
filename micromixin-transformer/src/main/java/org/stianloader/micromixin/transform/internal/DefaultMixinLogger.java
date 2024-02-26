package org.stianloader.micromixin.transform.internal;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class DefaultMixinLogger implements MixinLoggingFacade {

    @Override
    public void error(Class<?> clazz, String message, Object... args) {
        StringBuilder builder = new StringBuilder();

        builder.append("(ERROR: Micromixin/").append(clazz.getSimpleName()).append("): ");

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

        System.err.println(builder.append(message).toString());
    }

    @Override
    public void info(Class<?> clazz, String message, Object... args) {
        StringBuilder builder = new StringBuilder();

        builder.append("(INFO: Micromixin/").append(clazz.getSimpleName()).append("): ");

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

        System.out.println(builder.append(message).toString());
    }

    @Override
    public void warn(Class<?> clazz, String message, Object... args) {
        StringBuilder builder = new StringBuilder();

        builder.append("(WARN: Micromixin/").append(clazz.getSimpleName()).append("): ");

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

        System.out.println(builder.append(message).toString());
    }
}
