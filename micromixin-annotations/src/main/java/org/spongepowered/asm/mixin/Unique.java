package org.spongepowered.asm.mixin;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field or method in a mixin that is annotated with {@link Unique} will never overwrite a member
 * of the target class. This means that the name of the method or field is renamed should a collision occur.
 *
 * <p>This includes any members added by mixins that were applied before, but does not include any
 * members that were added after the mixin.
 *
 * <p>However for reasons that are beyond me, the spongeian implementation imposes following restrictions:
 * <ul>
 *  <li>The method is discarded if it is public and collides with a member in the target class. Setting {@link Unique#silent()} to true
 *  will cause the method to overwrite the target instead.</li>
 * </ul>
 * Micromixin reproduces these restrictions as best as it can, however due to the obscure nature of the restrictions,
 * the reproduction may have issues.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
public @interface Unique {

    /**
     * If the member that was annotated with {@link Unique} is a public method, a warning is sent to the log.
     * The official Mixin implementation does not seem to follow that logic, but Micromixin does.
     *
     * <p>Regardless, setting silent to {@value true} will suppress that log. Furthermore it will explicitly avoid discarding
     * the method if it collides with a member. That behaviour is not present if {@link Unique#silent()} is left empty
     * or {@value false}.
     *
     * @return True for the annotation to be silent, false otherwise.
     */
    public boolean silent() default false;
}
