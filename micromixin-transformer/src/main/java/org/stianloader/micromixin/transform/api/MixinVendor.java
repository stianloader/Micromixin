package org.stianloader.micromixin.transform.api;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.AvailableSince;
import org.jetbrains.annotations.Contract;

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
 * <p>Note: The {@link Enum#ordinal()} of this class is not considered part of public
 * ABI. Different versions (even minor versions) might have different ordinals.
 * As such, the ordinal of this enumeration should never be used for serialisation,
 * unless it is known ahead of time that the library version is the same between
 * encoder and decoder.
 *
 * @since 0.6.5-a20240912
 */
@AvailableSince(value = "0.6.5-a20240912")
public enum MixinVendor {
    /**
     * The Mixin implementation maintained by FabricMC,
     * this version is a fork of Sponge's Mixin implementation defined by {@link #SPONGE}.
     *
     * <p>Like with {@link #SPONGE}, micromixin-transformer treats MixinExtras to be
     * a part of the mixin implementation itself, even though in reality that
     * is not the case. That being said, most mixin runtimes will have both the core
     * mixin engine as well as MixinExtras available. Even when the environment
     * does not provide MixinExtras out of the box, it usually is possible to add MixinExtras
     * manually as a plugin (that being said, micromixin-transformer does not support
     * plugins at all as of writing).
     *
     * @since 0.9.0-a20260503
     */
    @AvailableSince(value = "0.9.0-a20260503")
    FABRIC,

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
     * @see #FABRIC The fabric vendor mode is more standard as it is more internally
     * consistent and has fewer bugs. Meanwhile, sponge's implementation has been
     * left unmaintained for a longer amount of time, making it potentially unattractive
     * to consumers as reported bugs are unlikely to get fixed within reasonable time,
     * if at all.
     */
    @AvailableSince(value = "0.6.5-a20240912")
    SPONGE;

    /**
     * Check whether the provided {@link MixinVendor} instance is a
     * mixin implementation derived from the spongeian mixin implementation.
     *
     * <p>Specifically, this method returns {@code true} for {@link MixinVendor#FABRIC}
     * and {@link MixinVendor#SPONGE}, and returns {@code false} for more esoteric implementations
     * such as {@link MixinVendor#MICROMIXIN}.
     *
     * <p>If the provided instance is {@code null}, then {@code false} shall be returned.
     * This method exists as a convenience method 
     *
     * @param vendor The {@link MixinVendor} instance to test against. May be {@code null}.
     * @return {@code true} if the mixin vendor is sponge-derived, {@code false} if otherwise.
     * @since 0.9.0-a20260503
     */
    @AvailableSince(value = "0.9.0-a20260503")
    @Contract(pure = true, value = "null -> false; !null -> _")
    public static boolean isSpongeLike(@Nullable MixinVendor vendor) {
        return vendor == MixinVendor.FABRIC || vendor == MixinVendor.SPONGE;
    }

    /**
     * Check whether the current {@link MixinVendor} instance ({@code this}) is a
     * mixin implementation derived from the spongeian mixin implementation.
     *
     * <p>Specifically, this method returns {@code true} for {@link MixinVendor#FABRIC}
     * and {@link MixinVendor#SPONGE}, and returns {@code false} for more esoteric implementations
     * such as {@link MixinVendor#MICROMIXIN}.
     *
     * @return {@code true} if the mixin vendor is sponge-derived, {@code false} if otherwise.
     * @since 0.9.0-a20260503
     */
    @AvailableSince(value = "0.9.0-a20260503")
    public boolean isSpongeLike() {
        return MixinVendor.isSpongeLike(this);
    }
}
