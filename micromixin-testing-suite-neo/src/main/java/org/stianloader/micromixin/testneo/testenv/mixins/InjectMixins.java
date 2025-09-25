package org.stianloader.micromixin.testneo.testenv.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.communication.Signaller;
import org.stianloader.micromixin.testneo.testenv.targets.InjectMixinsTarget;

@Mixin(InjectMixinsTarget.class)
public class InjectMixins {
    @Inject(method = { "invoke" }, at = { @At("HEAD") })
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onInvoke")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onInvoke", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private void onInvoke(long x, double y, double z, CallbackInfo ci) {
        Signaller.setSignal((int) (x - y - z));
    }
}
