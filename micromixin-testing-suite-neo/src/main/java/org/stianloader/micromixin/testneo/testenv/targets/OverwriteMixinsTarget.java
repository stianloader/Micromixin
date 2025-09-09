package org.stianloader.micromixin.testneo.testenv.targets;

import org.spongepowered.asm.mixin.Overwrite;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;

public class OverwriteMixinsTarget {

    @ExpectedAnnotations({Overwrite.class, AssertMemberName.class})
    public static void runTests() {
        throw new IllegalStateException();
    }

}
