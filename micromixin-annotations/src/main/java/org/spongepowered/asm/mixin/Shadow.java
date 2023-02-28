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
     * prefixes on static methods. That is "working as intended" and the bug does not exist - we of course know better.</b></p>
     *
     * A prefix that can be added to the name of the member.
     * The name of the member is not necessarily required to start with the prefix though.
     * But if the prefix is present, it will be removed allowing the member to match
     * with the proper name.
     *
     * <p>The main reason to use prefixes is to avoid issues that can be caused with methods
     * having the same name. The JVMS allows the existence of multiple methods with the same arguments and the same
     * name, as long as the return type differs. However the JLS does not allow the existence of such methods.
     *
     * @return The prefix to use
     */
    public String prefix() default "shadow$";

    /**
     * <p><b>The spongeian implementation (and therefore the standard Micromixin implementation) does not support
     * aliases on static methods. That is "working as intended" and the bug does not exist - we of course know better.</b></p>
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
     * @return The aliases to use.
     */
    public String[] aliases() default { }; 
}
