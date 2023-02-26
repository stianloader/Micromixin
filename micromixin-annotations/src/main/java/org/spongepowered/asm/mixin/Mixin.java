package org.spongepowered.asm.mixin;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The {@link Mixin} annotation can be applied on classes to mark them as a mixin.
 * It's presence is however mostly used in order know what classes are targeted by the mixin,
 * as the mixin itself is resolved through the mixin config
 *
 * <p>In order for this annotation to work, either {@link #targets()} or {@link #value()} needs
 * to be defined. If neither are defined, an exception is thrown while parsing the mixin.
 * The micromixin implementation will thus not add the mixin.
 */
@Documented
@Retention(CLASS)
@Target(TYPE)
public @interface Mixin {

    /**
     * The fully qualified names of the target classes.
     * The spongeian implementation will warn if {@link #targets()}
     * is used where {@link #value()} could be used instead,
     * but the Micromixin implementation does not warn in such instances.
     *
     * @return The targeted classes.
     */
    public String[] targets() default { };

    /**
     * The {@link Class} instances of the targeted classes.
     * If the class cannot be referenced (for example because they are package-private),
     * {@link #targets()} should be used instead.
     *
     * @return The targeted classes.
     */
    public Class<?>[] value() default { };

    /**
     * The relative priority of the mixin, which defines the order in which the mixin is applied.
     * The default value can be set through the mixin config.
     *
     * @return The priority of the mixin.
     */
    public int priority() default 1000;
}
