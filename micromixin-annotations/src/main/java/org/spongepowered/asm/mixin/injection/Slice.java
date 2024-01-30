package org.spongepowered.asm.mixin.injection;

import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * The {@link Slice} annotation denotes a specific area in which an injection point may be placed in.
 * Note that injection points may react to the slice, so slices are much more than a simple filter.
 * More specifically, the HEAD injection point will inject before the first instruction in the slice,
 * while TAIL will inject before the last return instruction within the slice (and will throw an error
 * if there are no return instructions, contrary to other injection points).
 *
 * <p>For most other injection points (such as INVOKE or CONSTANT), a slice can be seen as a simple range filter.
 */
@Documented
@Retention(CLASS)
public @interface Slice {

    /**
     * An injection point that matches the first instruction within the slice.
     *
     * @return The first instruction to match
     */
    public At from() default @At("HEAD");

    /**
     * The identifier of the slice. This attribute is in turn used for {@link At#slice()}.
     *
     * <p>If the attribute is left to the default value (and empty string), then the specified slice
     * is the default slice within the context. As such it will apply to any {@link At}s where the
     * slice id wasn't explicitly defined.
     *
     * <p>Defining multiple slices with the same name within the same context is not permitted (this includes
     * the default slice id <code>""</code> [an empty string]).
     *
     * <p>Note that micromixin-transformer as well as the accompanying documentation only partly implements
     * {@link Slice#id()} as well as {@link At#slice()}; significant derivations are to be expected between
     * micromixin-transformer's behaviour (as well as the documented behaviour) and the spongeian transformer
     * behaviour (as well as it's documentation). As such, usage of these attributes ought to be avoided in
     * environments where usage of micromixin-transformer is expected and the spongeian documentation should be
     * consulted before making use of this attribute.
     *
     * @return The identifier of the slice
     * @see At#slice()
     */
    public String id() default "";

    /**
     * An injection point that matches the last instruction within the slice.
     *
     * @return The last instruction of the slice
     */
    public At to() default @At("TAIL");
}
