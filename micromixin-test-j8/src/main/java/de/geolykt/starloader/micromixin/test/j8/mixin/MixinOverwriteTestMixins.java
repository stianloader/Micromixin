package de.geolykt.starloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import de.geolykt.starloader.micromixin.test.j8.targets.MixinOverwriteTest;

@Mixin(value = MixinOverwriteTest.class)
public class MixinOverwriteTestMixins {

    @SuppressWarnings("unused")
    private static int implicitlyOverwrittenMethod() {
        return 1;
    }

    @SuppressWarnings("unused")
    private static long implicitlyOverwrittenMethodDescMismatch() {
        return 0;
    }

    @Overwrite
    public static int explicitlyOverwrittenMethod() {
        return 1;
    }

    @Unique
    public static int explicitlyProtectedMethod() {
        return 0;
    }

    @Overwrite(aliases = {"explicitlyOverwrittenMethodAliasedMultiInvalid", "explicitlyOverwrittenMethodAliasedMultiA", "explicitlyOverwrittenMethodAliasedMultiB"})
    private static int explicitlyOverwrittenMethodAliasedMulti() {
        return 1;
    }

    @Overwrite(aliases = {"explicitlyOverwrittenMethodAliasedPriorityAlias"})
    public static int explicitlyOverwrittenMethodAliasedPriority() {
        return 1;
    }
}
