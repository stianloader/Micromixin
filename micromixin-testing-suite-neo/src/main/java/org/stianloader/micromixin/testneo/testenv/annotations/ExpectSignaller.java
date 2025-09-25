package org.stianloader.micromixin.testneo.testenv.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jetbrains.annotations.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExpectSignaller {
    int signalValue();
    @NotNull InvokeArgument @NotNull [] args() default {};
    @NotNull InvokeArgument @NotNull [] constructorArgs() default {};
}
