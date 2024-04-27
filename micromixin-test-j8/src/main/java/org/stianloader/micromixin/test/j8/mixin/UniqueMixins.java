package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.stianloader.micromixin.test.j8.targets.UniqueTest;

@Mixin(UniqueTest.class)
public class UniqueMixins {

    @Unique
    public boolean isUniqueAbsorbed() {
        return false;
    }

    @Unique
    public boolean uniqueStays() {
        return true;
    }

    @Unique
    private boolean isUniqueRenamedPrivate() {
        return false;
    }

    @Unique
    public boolean forwardIsUniqueRenamedPrivate() {
        return this.isUniqueRenamedPrivate();
    }

    @Unique
    private boolean isUniqueRenamedPrivatePublicTarget() {
        return false;
    }

    @Unique
    public boolean forwardIsUniqueRenamedPrivatePublicTarget() {
        return this.isUniqueRenamedPrivatePublicTarget();
    }
}
