package org.stianloader.micromixin.testneo.testenv.mixins;

import java.util.function.IntConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.communication.Signaller;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.targets.LocalCaptureMixinsTarget;

@Mixin(LocalCaptureMixinsTarget.class)
public class LocalCaptureMixins {

    @Mixin(LocalCaptureMixinsTarget.NoArgsCaptureInLocalCapture.class)
    private static class NoArgsCaptureInLocalCapture {
        @Inject(method = { "testArgsNoArgCapture(JDD)V" }, at = { @At("TAIL") }, locals = LocalCapture.CAPTURE_FAILHARD)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestArgsNoArgCapture")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestArgsNoArgCapture", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private void onTestArgsNoArgCapture(CallbackInfo ci, IntConsumer captured) {
            captured.accept(1);
        }
    }

    @Mixin(LocalCaptureMixinsTarget.NoArgsCaptureInLocalCaptureUnderflow.class)
    private static class NoArgsCaptureInLocalCaptureUnderflow {
        @Inject(method = { "testArgsNoArgCaptureUnderflow(JDD)V" }, at = { @At("TAIL") }, locals = LocalCapture.CAPTURE_FAILHARD)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestArgsNoArgCaptureUnderflow")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestArgsNoArgCaptureUnderflow", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private void onTestArgsNoArgCaptureUnderflow(CallbackInfo ci) {
            Signaller.setSignal(1);
        }
    }

    @Mixin(LocalCaptureMixinsTarget.NoArgsOverflow.class)
    private static class NoArgsOverflow {
        @Inject(method = { "testNoArgsOverflow()V" }, at = { @At("TAIL") }, locals = LocalCapture.CAPTURE_FAILHARD)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestNoArgsOverflow")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestNoArgsOverflow", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private void onTestNoArgsOverflow(CallbackInfo ci, IntConsumer capturedA, CallbackInfo cb, Object cc) {
            Signaller.setSignal(1);
        }
    }

    @Inject(method = { "testArgCapture(JDD)V" }, at = { @At("TAIL") }, locals = LocalCapture.CAPTURE_FAILHARD)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestArgCapture")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestArgCapture", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private void onTestArgCapture(long x, double y, double z, CallbackInfo ci, IntConsumer captured) {
        captured.accept(1);
    }

    @Inject(method = { "testArgCaptureUnderflow(JDD)V" }, at = { @At("TAIL") }, locals = LocalCapture.CAPTURE_FAILHARD)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestArgCaptureUnderflow")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestArgCaptureUnderflow", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private void onTestArgCaptureUnderflow(long x, double y, double z, CallbackInfo ci) {
        Signaller.setSignal(1);
    }

    @Inject(method = { "testNoArgs()V" }, at = { @At("TAIL") }, locals = LocalCapture.CAPTURE_FAILHARD)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestNoArgs")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestNoArgs", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private void onTestNoArgs(CallbackInfo ci, IntConsumer captured) {
        captured.accept(1);
    }

    @Inject(method = { "testNoArgsUnderflow()V" }, at = { @At("TAIL") }, locals = LocalCapture.CAPTURE_FAILHARD)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestNoArgsUnderflow")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestNoArgsUnderflow", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private void onTestNoArgsUnderflow(CallbackInfo ci) {
        Signaller.setSignal(1);
    }
}
