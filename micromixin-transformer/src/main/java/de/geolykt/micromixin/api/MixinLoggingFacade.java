package de.geolykt.micromixin.api;

/**
 * There comes a time where any library must log something.
 * Be it for debug purposes, error handling or simple warnings
 * for things that were dealt with in a sub-standard fashion.
 *
 * <p>Ordinarily we'd use SLF4J and call it a day, however in
 * our case that'd be a bit difficult given that micromixin-transformer
 * supports runtimes as low as Java 6. SLF4J 1.7.X would suffice
 * as it supports Java 5+ - but from the other side it'd be difficult
 * for the Java 9+ users who use JPMS, where usage of SLF4J 2.0.X
 * might be 
 *
 * <p>In order to appeal to everyone the simple decision of simply
 * allowing the API consumer to choose what they want was made.
 * By default logging will happen entirely via {@link System#out}
 * or {@link System#err}.
 *
 * <p>This facade should support "standard" SLF4J placeholders via "{}".
 * Not all arguments may map to a placeholder. They should simply be appended
 * to the end of the message. Leftover "{}" placeholders need to be
 * kept as-is. Implementations of this interface are free to assume
 * that "{}" never need to be escaped and that likewise no indexing
 * occurs (that "{2}" should never happen). If the last argument is a
 * {@link Throwable}, it's stacktrace should be logged.
 */
public interface MixinLoggingFacade {
    void error(Class<?> clazz, String message, Object... args);
    void info(Class<?> clazz, String message, Object... args);
    void warn(Class<?> clazz, String message, Object... args);
}
