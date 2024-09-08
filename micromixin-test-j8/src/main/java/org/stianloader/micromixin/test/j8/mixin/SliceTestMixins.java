package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.stianloader.micromixin.test.j8.MutableInt;
import org.stianloader.micromixin.test.j8.targets.SliceTest;
import org.stianloader.micromixin.test.j8.targets.SliceTest.AmbigiousSliceTest;
import org.stianloader.micromixin.test.j8.targets.SliceTest.InvalidlyExcludedTailTest;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(SliceTest.class)
public class SliceTestMixins {
    // TODO Also do one with at shifted by one opcode
    // TODO Also do one with named slices (i.e. not only the default slice)
    // TODO Also perhaps multiple slices when working with @Inject?

    @Mixin(AmbigiousSliceTest.class)
    private static class AmbigiousSliceTestMixin {
        @Inject(at = @At(value = "HEAD"),
                target = @Desc(value = "target", ret = void.class),
                slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=5")))
        private static void injector(CallbackInfo ci) { }
    }

    @Mixin(InvalidlyExcludedTailTest.class)
    private static class InvalidlyExcludedTailTestMixin {
        @Inject(at = @At(value = "TAIL"),
                target = @Desc(value = "target", ret = void.class),
                slice = @Slice(to = @At(value = "CONSTANT", args = "intValue=5")))
        private static void injector(CallbackInfo ci) { }
    }

    @Inject(at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "add", args = MutableInt.class, ret = MutableInt.class)),
            target = @Desc(value = "sliceTest0Inject"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=4"), to = @At(value = "CONSTANT", args = "intValue=10")))
    private static void slice0Inject(CallbackInfo ci, MutableInt captured) {
        if (captured.intValue() != 4) {
            throw new IllegalStateException("Captured value expected 4, instead " + captured.intValue());
        }
        captured.mul(2);
    }

    @ModifyArg(at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "add", args = MutableInt.class, ret = MutableInt.class)),
            target = @Desc(value = "sliceTest0ModifyArg"),
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=4"), to = @At(value = "CONSTANT", args = "intValue=10")))
    private static MutableInt slice0ModifyArg(MutableInt arg) {
        return arg.mul(arg);
    }

    @Redirect(at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "add", args = MutableInt.class, ret = MutableInt.class)),
            target = @Desc(value = "sliceTest0Redirect"),
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=4"), to = @At(value = "CONSTANT", args = "intValue=10")))
    private static MutableInt slice0Redirect(MutableInt caller, MutableInt arg) {
        if (caller.intValue() != 4) {
            throw new IllegalStateException("caller value expected 4, instead " + caller.intValue());
        }
        return caller.mul(arg);
    }

    @ModifyArg(at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "add", args = MutableInt.class, ret = MutableInt.class)),
            target = @Desc(value = "sliceTest1ModifyArg"),
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=4"), to = @At(value = "CONSTANT", args = "intValue=12")))
    private static MutableInt slice1ModifyArg(MutableInt arg) {
        return arg.mul(arg);
    }

    @Redirect(at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "add", args = MutableInt.class, ret = MutableInt.class)),
            target = @Desc(value = "sliceTest1Redirect"),
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=4"), to = @At(value = "CONSTANT", args = "intValue=16")))
    private static MutableInt slice1Redirect(MutableInt caller, MutableInt arg) {
        return caller.mul(arg);
    }

    @Inject(at = @At("RETURN"),
            target = @Desc(value = "sliceTest2Inject", args = int.class, ret = int.class),
            cancellable = true,
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=10"), to = @At(value = "CONSTANT", args = "intValue=2")))
    private static void slice2Inject(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() * 2);
    }

    @ModifyReturnValue(at = @At("RETURN"),
            method = "sliceTest2ModifyReturnValue(I)I",
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=10"), to = @At(value = "CONSTANT", args = "intValue=2")))
    private static int slice2ModifyReturnValue(int ret) {
        return ret * 2;
    }

    @ModifyReturnValue(at = @At("RETURN"),
            method = "sliceTest3ModifyReturnValue(I)I",
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=10")))
    private static int slice3ModifyReturnValue(int ret) {
        return ret * 2;
    }

    @Inject(at = @At(value = "HEAD"),
            target = @Desc(value = "sliceTest4InjectA", args = MutableInt.class),
            slice = @Slice(to = @At(value = "CONSTANT", args = "intValue=5")),
            require = 1)
    private static void slice4InjectA(MutableInt i, CallbackInfo ci) {
        if (i.intValue() != 2) {
            throw new IllegalStateException("Captured value expected 2, instead " + i.intValue());
        }
        i.mul(3);
    }

    @Inject(at = @At(value = "TAIL"),
            target = @Desc(value = "sliceTest4InjectB", ret = MutableInt.class),
            locals = LocalCapture.CAPTURE_FAILHARD,
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=5")),
            require = 1)
    private static void slice4InjectB(CallbackInfoReturnable<MutableInt> ci, MutableInt captured) {
        if (captured != ci.getReturnValue()) {
            throw new AssertionError();
        }
        if (captured.intValue() != 11) {
            throw new IllegalStateException("Captured value expected 11, instead " + captured.intValue());
        }
        captured.mul(2);
    }

    @Inject(at = @At(value = "HEAD"),
            target = @Desc(value = "sliceTest4InjectC"),
                    locals = LocalCapture.CAPTURE_FAILHARD,
            slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=5")),
            require = 1)
    private static void slice4InjectC(CallbackInfo ci, MutableInt captured) {
        captured.mul(2);
    }

    @Inject(at = @At(value = "TAIL"),
            target = @Desc(value = "sliceTest4InjectD", args = MutableInt.class, ret = MutableInt.class),
            slice = @Slice(to = @At(value = "CONSTANT", args = "intValue=5")),
            require = 1)
    private static void slice4InjectD(MutableInt i, CallbackInfoReturnable<MutableInt> cir) {
        i.mul(2);
    }

    @Inject(
        at = @At(value = "TAIL", slice = "slice1"),
        target = @Desc(value = "sliceTest5InjectA", args = MutableInt.class, ret = MutableInt.class),
        slice = {
            @Slice(to = @At(value = "CONSTANT", args = "intValue=5", slice = "slice0"), id = "slice1"),
            @Slice(to = @At(value = "CONSTANT", args = "intValue=5"), id = "slice0"),
        },
        require = 1
    )
    private static void slice5InjectA(MutableInt i, CallbackInfoReturnable<MutableInt> cir) {
        i.mul(2);
    }

    @Inject(
        at = @At(value = "RETURN", slice = "slice1"),
        target = @Desc(value = "sliceTest5InjectB", args = MutableInt.class, ret = MutableInt.class),
        slice = {
            @Slice(to = @At(value = "RETURN", slice = "slice0"), id = "slice1"),
            @Slice(to = @At(value = "CONSTANT", args = "intValue=5"), id = "slice0"),
        },
        require = 1
    )
    private static void slice5InjectB(MutableInt i, CallbackInfoReturnable<MutableInt> cir) {
        i.mul(2);
    }

    @Inject(
        at = @At(value = "TAIL", slice = "slice1"),
        target = @Desc(value = "sliceTest5InjectC", args = MutableInt.class, ret = MutableInt.class),
        slice = {
            @Slice(to = @At(value = "RETURN", slice = "slice0"), id = "slice1"),
            @Slice(to = @At(value = "CONSTANT", args = "intValue=5"), id = "slice0"),
        },
        require = 1
    )
    private static void slice5InjectC(MutableInt i, CallbackInfoReturnable<MutableInt> cir) {
        i.mul(2);
    }
}
