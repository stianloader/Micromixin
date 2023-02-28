package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * {@link Desc} is a metadata annotation to specify a specific class member without
 * the potential of typos.
 *
 * <p>When matching to fields, {@link #args()} is ignored. The descriptor of the field is
 * solely defined by {@link #ret()}.
 *
 * <p>It is generally advisable to fill in all information, as otherwise matching might
 * happen in an unintended manner.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Desc {

    /**
     * The arguments of the matched methods.
     * Ignored when matching fields.
     *
     * @return The method's arguments.
     */
    public Class<?>[] args() default { };

    /**
     * The owner of the member, or the type from which a call is made.
     *
     * @return The owner class
     */
    public Class<?> owner() default void.class;

    /**
     * The return type of the matched member.
     *
     * @return The return type
     */
    public Class<?> ret() default void.class;

    /**
     * The name of the member to match.
     * Matching happens in a case-sensitive manner.
     *
     * @return The name of the member
     */
    public String value();
}
