package org.stianloader.micromixin.test.j8.mixin.invalid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.stianloader.micromixin.test.j8.targets.invalid.InvalidOverwriteAliasTest;

@Mixin(InvalidOverwriteAliasTest.class)
public class InvalidOverwriteAliasTestMixins {

    @Overwrite(aliases = "aliasedMethod")
    private void overwrite() {
    }
}
