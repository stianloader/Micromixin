package org.stianloader.micromixin.testneo.testenv.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InvokeArgument {
    Class<?> type();
    int intValue() default 0;
    boolean booleanValue() default false;
    double doubleValue() default 0;
    short shortValue() default 0;
    char charValue() default 0;
    byte byteValue() default 0;
    long longValue() default 0;
    float floatValue() default 0;
    boolean nullValue() default false;
    InvokeStaticMethod methodValue() default @InvokeStaticMethod(name = "", owner = void.class, returnType = void.class);
}
