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
    @Mixin(InjectMixinsTarget.WrongCallbackInfoClass.class)
    private static class WrongCallbackInfoClass {
        @Inject(method = "wrongCallbackInfoClass", at = @At("TAIL"))
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onWrongCallbackInfoClass")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "onWrongCallbackInfoClass", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private void onWrongCallbackInfoClass(CallbackInfo ci) {
            Signaller.setSignal(1);
        }
    }

    @Inject(method = { "invoke(JDD)V" }, at = { @At("HEAD") })
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onInvoke")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onInvoke", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private void onInvoke(long x, double y, double z, CallbackInfo ci) {
        Signaller.setSignal((int) (x - y - z));
    }

    @Inject(method = { "testJIII(JIII)V" }, at = { @At("HEAD") })
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestJIII")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestJIII", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private void onTestJIII(long x, int y, int z, int a, CallbackInfo ci) {
        Signaller.setSignal((int) (x - y - z));
    }
}
