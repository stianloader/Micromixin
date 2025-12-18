package org.stianloader.micromixin.testneo.testenv.targets;

import org.spongepowered.asm.mixin.Overwrite;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;

public class OverwriteMixinsTarget {

    @ExpectedAnnotations(value = {Overwrite.class, AssertMemberName.class}, capability = "COPY_ANNOTATIONS")
    public static void runTests() {
        throw new IllegalStateException();
    }

}
