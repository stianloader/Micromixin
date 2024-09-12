package org.stianloader.micromixin.transform.api;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

/**
 * An enumeration storing vendors of a mixin transformation API, with whom
 * micromixin-transformer can try to establish compatibility in selected subsystems.
 *
 * <p>Keep note that micromixin-transformer cannot establish compatibility with future
 * releases, nor will it guarantee compatibility with past releases.
 * Further, please be aware that micromixin-transformer cannot replicate the behaviour
 * of every implementation up to the smallest detail. Doing so would simply take too
 * long to implement. Henceforth, when relying on a vendor compatibility, users are
 * advised to test it in all environments where usage is expected (which may include
 * micromixin-transformer as well as other implementations, such as the spongeian mixin
 * implementation).
 *
 * @since 0.6.5-a20240912
 */
@AvailableSince(value = "0.6.5-a20240912")
public enum MixinVendor {

    /**
     * The micromixin suite, developed under the stianloader banner.
     * This specifically means behaviour that is specific to micromixin-transformer,
     * this only includes behaviour that was written intentionally different compared
     * to mainstream mixin implementations as well as behaviour unintentionally differing
     * to the mainstream mixin implementations, but these differences being deemed to
     * be useful or intuitive. Accidental differences deemed useless or even negative
     * are almost guaranteed to be removed at some point and this entry does not
     * exist to reactivate those differences.
     *
     * <p>Micromixin is a mixin implementation that was written from scratch,
     * it is unlikely to support the full breadth of mixin. Micromixin supports
     * some MixinExtras features.
     *
     * @since 0.6.5-a20240912
     */
    @AvailableSince(value = "0.6.5-a20240912")
    MICROMIXIN,

    /**
     * The Mixin implementation by SpongePowered (or short, Sponge). This is
     * deemed to original mixin implementation and is used by miromixin-transformer
     * as the reference implementation. This means that micromixin-transformer
     * tries to apply the behaviour of Sponge's mixin implementation as the default
     * implementation.
     *
     * <p>While from a technical perspective the spongeian implementation does
     * not support MixinExtras features, micromixin-transformer treats MixinExtras
     * as part of the spongeian implementation as in almost all cases both systems
     * will be used alongside each other (even though MixinExtras can be used alongside
     * the Fabric mixin implementation).
     *
     * @since 0.6.5-a20240911
     */
    @AvailableSince(value = "0.6.5-a20240912")
    SPONGE;
}
