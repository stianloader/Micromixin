package org.spongepowered.asm.mixin;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link Unique} annotation guarantees, when applied to a class member, that the member it was
 * applied on does not cause any modifications to class members of the target classes.
 *
 * <p>When using micromixin-transformer, fields will always get renamed to a unique identifier, however
 * this behaviour may be subject to change. If you wish to change the behaviour, please reach out to the
 * micromixin-transformer maintainers.
 *
 * <p>When applied on methods, then the annotated method will be overlaid under the semantics of the
 * Intrinsic annotation, that is it will overlay the method with the same name, but when a collision
 * occurs, the method will instead be discarded (with the same semantics as {@link Shadow}). <b>HOWEVER,</b>
 * if the annotated method is <code>private</code> or <code>protected</code>, then in case of a collision
 * the annotated method will be renamed using a uniquely generated prefix.
 */
// TODO If @Intrinsic is implemented, update link to Intrinsic
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
public @interface Unique {
    /**
     * If the member that was annotated with {@link Unique} is a public method and a collision occurred
     * which caused the member to be absorbed (using the same semantics as {@link Shadow}), then
     * a warning will be printed to the log unless {@link Unique#silent()} is set to <code>true</code>.
     *
     * @return True for the annotation to be silent, false otherwise.
     */
    public boolean silent() default false;
}
