package org.stianloader.micromixin.testneo.testenv.mixins;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.communication.Signaller;
import org.stianloader.micromixin.testneo.testenv.targets.WrapOpMixinsTarget;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;

@Mixin(WrapOpMixinsTarget.class)
public class WrapOpMixins {

    @Mixin(WrapOpMixinsTarget.WrapOpCancellable.class)
    private static class WrapOpCancellable {
        @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testConditionallyUsedCI()V" }, require = 3, allow = 3)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "testConditionallyUsedCI")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCI", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private static void testConditionallyUsedCI(IntConsumer reciever, int arg1, Operation<Void> op, @Cancellable CallbackInfo ci) {
            if (arg1 == 3) {
                ci.cancel();
            }

            op.call(reciever, arg1 + 1);
        }

        @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testConditionallyUsedCIWithArgI(I)V" }, require = 2, allow = 2)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "testConditionallyUsedCIWithArgI")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgI", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private static void testConditionallyUsedCIWithArgI(IntConsumer reciever, int arg1, Operation<Void> op, int captured, @Cancellable CallbackInfo ci) {
            if (captured == 3) {
                ci.cancel();
            }

            op.call(reciever, arg1 + 1);
        }

        @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testConditionallyUsedCIWithArgJ(J)V" }, require = 2, allow = 2)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "testConditionallyUsedCIWithArgJ")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgJ", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private static void testConditionallyUsedCIWithArgJ(IntConsumer reciever, int arg1, Operation<Void> op, long captured, @Cancellable CallbackInfo ci) {
            if (captured == 3) {
                ci.cancel();
            }

            op.call(reciever, arg1 + 1);
        }

        @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testConditionallyUsedCIWithArgJ2(JJ)V" }, require = 1, allow = 1)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "testConditionallyUsedCIWithArgJ2")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgJ2", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private static void testConditionallyUsedCIWithArgJ2(IntConsumer reciever, int arg1, Operation<Void> op, long captured, long captured2, @Cancellable CallbackInfo ci) {
            if (captured == captured2) {
                ci.cancel();
            }

            op.call(reciever, arg1 + 1);
        }

        @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testConditionallyUsedCIWithArgJ3(JJ)V" }, require = 1, allow = 1)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "testConditionallyUsedCIWithArgJ3")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgJ3", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private static void testConditionallyUsedCIWithArgJ3(IntConsumer reciever, int arg1, Operation<Void> op, long captured, long captured2, @Cancellable CallbackInfo ci) {
            if (captured == captured2) {
                Signaller.setSignal(2);
                ci.cancel();
                return;
            }

            op.call(reciever, arg1 + 8);
        }

        @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testUnusedCI()V" }, require = 1, allow = 1)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "testUnusedCI")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testUnusedCI", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private static void testUnusedCI(IntConsumer reciever, int arg1, Operation<Void> op, @Cancellable CallbackInfo ci) {
            op.call(reciever, 1);
        }

        @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testUsedCI()V" }, require = 2, allow = 2)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "testUsedCI")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testUsedCI", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private static void testUsedCI(IntConsumer reciever, int arg1, Operation<Void> op, @Cancellable CallbackInfo ci) {
            op.call(reciever, arg1 * 2);
            ci.cancel();
        }
    }

    @Mixin(WrapOpMixinsTarget.WrapOpCancellableNonTrailing.class)
    private static class WrapOpCancellableNonTrailing {
        @WrapOperation(at = { @At(value = "INVOKE", target = "accept(I)V") }, method = { "testConditionallyUsedCIWithArgJ(J)V" }, require = 2, allow = 2)
        @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "testConditionallyUsedCIWithArgJ")
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgJ", negate = true)
        @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
        private static void testConditionallyUsedCIWithArgJ(IntConsumer reciever, int arg1, Operation<Void> op, @Cancellable CallbackInfo ci, long captured) {
            if (captured == 3) {
                ci.cancel();
            }

            op.call(reciever, arg1 + 1);
        }
    }

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

    @WrapOperation(at = { @At(value = "INVOKE", target = "Ljava/util/Arrays;equals([SII[SII)Z") }, method = { "testLargeArgCount()V" }, require = 1, allow = 1)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestLargeArgCount")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestLargeArgCount", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private static boolean onTestLargeArgCount(short[] a, int b, int c, short[] d, int e, int f, Operation<Boolean> operation) {
        return !operation.call(a, b, c, d, e, f);
    }

    @WrapOperation(at = { @At(value = "INVOKE", target = "Ljava/util/Arrays;equals([SII[SII)Z") }, method = { "testLargeArgCountVirt()V" }, require = 1, allow = 1)
    @AssertMemberName(constraint = AssertConstraint.CONTAINS, value = "onTestLargeArgCountVirt")
    @AssertMemberName(constraint = AssertConstraint.IS, value = "onTestLargeArgCountVirt", negate = true)
    @ExpectedAnnotations({ ExpectedAnnotations.class, AssertMemberNames.class })
    private boolean onTestLargeArgCountVirt(short[] a, int b, int c, short[] d, int e, int f, Operation<Boolean> operation) {
        return !operation.call(a, b, c, d, e, f);
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
