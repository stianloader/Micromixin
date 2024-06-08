package org.spongepowered.asm.mixin;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link Shadow} annotation can be put on methods and fields that are present in the target
 * class without overwriting them. This allows to reference methods and fields without much fuzz.
 *
 * <p>If the member that is annotated with this annotation does not collide with a member of
 * the target class, an exception will be thrown during transformation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
public @interface Shadow {

    /**
     * <p><b>The spongeian implementation (and therefore the standard Micromixin implementation) does not support
     * prefixes on static methods.</b> This behaviour is subject to change in future versions of micromixin-transformer.</p>
     *
     * <p>A prefix that can be added to the name of the member.
     * The name of the member is not necessarily required to start with the prefix though.
     * But if the prefix is present, it will be removed allowing the member to match
     * with the proper name.
     *
     * <p>The main reason to use prefixes is to avoid issues that can be caused with methods
     * having the same name. The JVMS allows the existence of multiple methods with the same arguments and the same
     * name, as long as the return type differs. However the JLS does not allow the existence of such methods.
     *
     * <p>While micromixin-transformer and micromixin-remapper permits it, the spongeian mixin implementation
     * do not allow the usage of prefixes on fields. Doing so will cause the transformer to refuse apply
     * under the spongeian implementation. Under micromixin, a warning is printed out instead, indicating that
     * there are compatibility concerns that arise from such usage of prefixes. According to the spongeian
     * documentation, the core point of prefixes have no application on fields. Why exactly that is the case is a bit
     * beyond me as it is very well possible for fields to have the same name but different descriptors.
     * In any case, use of {@link #aliases()} <em>may</em> be an appropriate workaround.
     *
     * @return The prefix to use
     */
    public String prefix() default "shadow$";

    /**
     * <p><b>The spongeian implementation (and therefore the standard Micromixin implementation) does not support
     * aliases on static methods.</b> This behaviour is subject to change in future versions of micromixin-transformer.</p>
     *
     * The aliases of the method. Only one alias is selected from the given list,
     * if none match an exception is thrown during transformation. The actual name of the method
     * is discarded and will not play a role once aliases are defined.
     *
     * <p>Aliases are not affected by {@link #prefix() the prefix}.
     *
     * <p>The spongeian Mixin specification advises against the usage of aliases.
     * According to them aliases should only be used if the name of the target
     * has changed. (The behaviour of aliases does not allow much more usecases
     * anyways)
     *
     * <p>Unlike prefixes, aliases are completely acceptable when the <code>&#64;Shadow</code>-Annotation
     * targets a field in both micromixin and standard mixin.
     *
     * <p>In the spongeian mixin implementation, <b>aliases are not supported on non-private members</b>
     * (for this, the access modifier of the target member is being used, not the one of the member in the mixin
     * class). The cited reason for this is that other mixins could add fields that would match the alias and thus
     * invalidate caches. It is unclear why this is an issue in the spongeian implementation, but for compatibility
     * reasons it is recommended to ensure that shadowed members with aliases are private.
     * While micromixin-transformer  and micromixin-remapper support the usage of aliases on non-private members,
     * it will log a warning at application time when doing so and may in the future refuse to apply such mixins.
     * Proceed with care.
     *
     * @return The aliases to use.
     */
    public String[] aliases() default { }; 
}
