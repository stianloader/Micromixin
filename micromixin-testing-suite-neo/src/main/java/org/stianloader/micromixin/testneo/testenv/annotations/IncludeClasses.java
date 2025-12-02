package org.stianloader.micromixin.testneo.testenv.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jetbrains.annotations.NotNull;

public class IncludeClasses {

    private IncludeClasses() {}

    /**
     * As part of testing what is possible in micromixin one has to also test what is not possible
     * in mixin. Or in other words, one has to also test failing cases. Many of these failing cases
     * produce error during the class transformation pipeline that have to be handled exceptionally
     * for these failing cases, while also forwarded for "ordinary" tests.
     *
     * <p>Cannot be fetched using reflection!
     */
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.TYPE)
    public @interface IncludeFailingClass {
        @NotNull
        Class<?> @NotNull[] value();
    }
}
