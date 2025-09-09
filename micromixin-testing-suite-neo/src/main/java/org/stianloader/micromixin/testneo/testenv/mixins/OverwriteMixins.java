package org.stianloader.micromixin.testneo.testenv.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.targets.OverwriteMixinsTarget;

@Mixin(OverwriteMixinsTarget.class)
public class OverwriteMixins {

    @Overwrite
    @ExpectedAnnotations({Overwrite.class, AssertMemberName.class})
    @AssertMemberName(constraint = AssertConstraint.IS, value = "runTests")
    public static void runTests() {
        System.out.println("Hello from transformed " + OverwriteMixins.class.getName() + " CL " + OverwriteMixins.class.getClassLoader());
    }
}
