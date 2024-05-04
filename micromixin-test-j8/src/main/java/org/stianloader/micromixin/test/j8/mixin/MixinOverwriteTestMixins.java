package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.stianloader.micromixin.test.j8.targets.MixinOverwriteTest;
import org.stianloader.micromixin.test.j8.targets.invalid.InvalidIntrinsic;

@Mixin(value = MixinOverwriteTest.class)
public class MixinOverwriteTestMixins {

    @Mixin(value = InvalidIntrinsic.InvalidIntrinsicNoInterface.class)
    private static class InvalidIntrinsicNoInterface {
        @Intrinsic
        public boolean intrinsicMethodDiscarded() {
            return false;
        }
    }

    @Mixin(value = InvalidIntrinsic.InvalidIntrinsicNoInterfacePrefixed.class)
    private static class InvalidIntrinsicNoInterfacePrefixed {
        @Intrinsic
        @Shadow(prefix = "soft$")
        public boolean soft$intrinsicMethodDiscarded() {
            return false;
        }

        @Intrinsic
        @Shadow(prefix = "soft$")
        public boolean soft$intrinsicMethodApplied() {
            return true;
        }
    }

    @Overwrite
    public static int explicitlyOverwrittenMethod() {
        return 1;
    }

    @Overwrite(aliases = {"explicitlyOverwrittenMethodAliasedMultiInvalid", "explicitlyOverwrittenMethodAliasedMultiA", "explicitlyOverwrittenMethodAliasedMultiB"})
    private static int explicitlyOverwrittenMethodAliasedMulti() {
        return 1;
    }

    @Overwrite(aliases = {"explicitlyOverwrittenMethodAliasedPriorityAlias"})
    public static int explicitlyOverwrittenMethodAliasedPriority() {
        return 1;
    }

    @Unique
    public static int explicitlyProtectedMethod() {
        return 0;
    }

    @SuppressWarnings("unused")
    private static int implicitlyOverwrittenMethod() {
        return 1;
    }

    @SuppressWarnings("unused")
    private static long implicitlyOverwrittenMethodDescMismatch() {
        return 0;
    }
}
