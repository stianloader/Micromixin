package org.stianloader.micromixin.testneo.testenv.mixins;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.targets.WrapOpMixinsTarget;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(WrapOpMixinsTarget.class)
public class WrapOpMixins {

    @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testWrapOpSI()V" }, require = 1, allow = 1)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onSignalTestWrapOpSI")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onSignalTestWrapOpSI", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private static void onSignalTestWrapOpSI(IntConsumer reciever, int arg1, Operation<Void> op) {
        op.call(reciever, 1);
    }

    @WrapOperation(at = { @At(value = "INVOKE", target = "accept") }, method = { "testWrapOpSIO()V" }, require = 1, allow = 1)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onSignalTestWrapOpSIO")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onSignalTestWrapOpSIO", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private static void onSignalTestWrapOpSIO(Consumer<Integer> reciever, Object arg1, Operation<Void> op) {
        op.call(reciever, 1);
    }

    @WrapOperation(at = { @At(value = "INVOKE", target = "setSignal(I)V") }, method = { "testWrapOpSS()V" }, require = 1, allow = 1)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onSignalTestWrapOpSS")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onSignalTestWrapOpSS", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private static void onSignalTestWrapOpSS(int arg0, Operation<Void> op) {
        op.call(1);
    }

    @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testWrapOpVI()V" }, require = 1, allow = 1)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onSignalTestWrapOpVI")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onSignalTestWrapOpVI", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    public void onSignalTestWrapOpVI(IntConsumer reciever, int arg1, Operation<Void> op) {
        op.call(reciever, 1);
    }

    @WrapOperation(at = { @At(value = "INVOKE", target = "accept(Ljava/lang/Object;)V") }, method = { "testWrapOpVIO()V" }, require = 1, allow = 1)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onSignalTestWrapOpVIO")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onSignalTestWrapOpVIO", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    public void onSignalTestWrapOpVIO(Consumer<Integer> reciever, Object arg1, Operation<Void> op) {
        op.call(reciever, 1);
    }

    @WrapOperation(at = { @At(value = "INVOKE", target = "setSignal(I)V") }, method = { "testWrapOpVS()V" }, require = 1, allow = 1)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onSignalTestWrapOpVS")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onSignalTestWrapOpVS", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    public void onSignalTestWrapOpVS(int arg0, Operation<Void> op) {
        op.call(1);
    }
}
