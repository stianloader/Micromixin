package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stianloader.micromixin.test.j8.targets.InjectionHeadTest;

@Mixin(InjectionHeadTest.class)
public class InjectionHeadTestMixins {

    // TODO implicit injector selection (i.e. injector method's name is the same as the inject target)

    // TODO An injector such as follows will not work:
    /*
    @Inject(target = @Desc(value = "expectNoThrowD"),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowD(CallbackInfo ci) {
        ci.cancel();
    }
    */
    // This is due to mismatching expected vs unexpected arguments.
    // A test is dearly needed as MM most likely does not account for that at the moment
    // (CI injects cannot operate where CIR would be appropriate)
    // The signature of the CIR does not influence selection.

    @Inject(target = @Desc(value = "expectNoThrowB", ret = byte.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowB(CallbackInfoReturnable<Byte> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectNoThrowC", ret = char.class), // TODO The `ret = char.class` part is required!
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowC(CallbackInfoReturnable<Integer> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectNoThrowD", ret = double.class),
            cancellable = true,
            at = @At("HEAD"),
            require = 1)
    private static void injectExpectNoThrowD(CallbackInfoReturnable<Double> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectNoThrowF", ret = float.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowF(CallbackInfoReturnable<Float> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectNoThrowI", ret = int.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowI(CallbackInfoReturnable<Integer> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectNoThrowJ", ret = long.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowJ(CallbackInfoReturnable<Long> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectNoThrowL", ret = Object.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowL(CallbackInfoReturnable<Object> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectNoThrowS", ret = short.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowS(CallbackInfoReturnable<Short> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectNoThrowV"),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowV(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectNoThrowZ", ret = boolean.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectNoThrowZ(CallbackInfoReturnable<Boolean> ci) {
        ci.cancel();
    }

    // ---

    @Inject(target = @Desc(value = "expectThrowB", ret = byte.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectThrowB(CallbackInfoReturnable<Byte> ci) {
        throw new RuntimeException();
    }

    @Inject(target = @Desc(value = "expectThrowC", ret = char.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectThrowC(CallbackInfoReturnable<Integer> ci) {
        throw new RuntimeException();
    }

    @Inject(target = @Desc(value = "expectThrowD", ret = double.class),
            cancellable = true,
            at = @At("HEAD"),
            require = 1)
    private static void injectExpectThrowD(CallbackInfoReturnable<Double> ci) {
        throw new RuntimeException();
    }

    @Inject(target = @Desc(value = "expectThrowF", ret = float.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectThrowF(CallbackInfoReturnable<Float> ci) {
        throw new RuntimeException();
    }

    @Inject(target = @Desc(value = "expectThrowI", ret = int.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectThrowI(CallbackInfoReturnable<Integer> ci) {
        throw new RuntimeException();
    }

    @Inject(target = @Desc(value = "expectThrowJ", ret = long.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectThrowJ(CallbackInfoReturnable<Long> ci) {
        throw new RuntimeException();
    }

    @Inject(target = @Desc(value = "expectThrowL", ret = Object.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectThrowL(CallbackInfoReturnable<Object> ci) {
        throw new RuntimeException();
    }

    @Inject(target = @Desc(value = "expectThrowS", ret = short.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectThrowS(CallbackInfoReturnable<Short> ci) {
        throw new RuntimeException();
    }

    @Inject(target = @Desc(value = "expectThrowV"),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectThrowV(CallbackInfo ci) {
        throw new RuntimeException();
    }

    @Inject(target = @Desc(value = "expectThrowZ", ret = boolean.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectThrowZ(CallbackInfoReturnable<Boolean> ci) {
        throw new RuntimeException();
    }

    // ---

    @Inject(target = @Desc(value = "expectInvalidCancellationB", ret = byte.class),
            at = @At("HEAD"))
    private static void injectExpectInvalidCancellationB(CallbackInfoReturnable<Byte> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectInvalidCancellationC", ret = char.class),
            cancellable = false,
            at = @At("HEAD"))
    private static void injectExpectInvalidCancellationC(CallbackInfoReturnable<Character> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectInvalidCancellationD", ret = double.class),
            cancellable = false,
            at = @At("HEAD"),
            require = 1)
    private static void injectExpectInvalidCancellationD(CallbackInfoReturnable<Double> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectInvalidCancellationF", ret = float.class),
            cancellable = false,
            at = @At("HEAD"))
    private static void injectExpectInvalidCancellationF(CallbackInfoReturnable<Float> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectInvalidCancellationI", ret = int.class),
            cancellable = false,
            at = @At("HEAD"))
    private static void injectExpectInvalidCancellationI(CallbackInfoReturnable<Integer> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectInvalidCancellationJ", ret = long.class),
            cancellable = false,
            at = @At("HEAD"))
    private static void injectExpectInvalidCancellationJ(CallbackInfoReturnable<Long> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectInvalidCancellationL", ret = Object.class),
            cancellable = false,
            at = @At("HEAD"))
    private static void injectExpectInvalidCancellationL(CallbackInfoReturnable<Object> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectInvalidCancellationS", ret = short.class),
            cancellable = false,
            at = @At("HEAD"))
    private static void injectExpectInvalidCancellationS(CallbackInfoReturnable<Short> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectInvalidCancellationV"),
            cancellable = false,
            at = @At("HEAD"))
    private static void injectExpectInvalidCancellationV(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectInvalidCancellationZ", ret = boolean.class),
            cancellable = false,
            at = @At("HEAD"))
    private static void injectExpectInvalidCancellationZ(CallbackInfoReturnable<Boolean> ci) {
        ci.cancel();
    }

    // ---

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationB", ret = byte.class),
            at = @At("HEAD"))
    private static void injectExpectImplicitlyInvalidCancellationB(CallbackInfoReturnable<Byte> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationC", ret = char.class),
            at = @At("HEAD"))
    private static void injectExpectImplicitlyInvalidCancellationC(CallbackInfoReturnable<Character> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationD", ret = double.class),
            at = @At("HEAD"),
            require = 1)
    private static void injectExpectImplicitlyInvalidCancellationD(CallbackInfoReturnable<Double> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationF", ret = float.class),
            at = @At("HEAD"))
    private static void injectExpectImplicitlyInvalidCancellationF(CallbackInfoReturnable<Float> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationI", ret = int.class),
            at = @At("HEAD"))
    private static void injectExpectImplicitlyInvalidCancellationI(CallbackInfoReturnable<Integer> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationJ", ret = long.class),
            at = @At("HEAD"))
    private static void injectExpectImplicitlyInvalidCancellationJ(CallbackInfoReturnable<Long> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationL", ret = Object.class),
            at = @At("HEAD"))
    private static void injectExpectImplicitlyInvalidCancellationL(CallbackInfoReturnable<Object> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationS", ret = short.class),
            at = @At("HEAD"))
    private static void injectExpectImplicitlyInvalidCancellationS(CallbackInfoReturnable<Short> ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationV"),
            at = @At("HEAD"))
    private static void injectExpectImplicitlyInvalidCancellationV(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(target = @Desc(value = "expectImplicitlyInvalidCancellationZ", ret = boolean.class),
            at = @At("HEAD"))
    private static void injectExpectImplicitlyInvalidCancellationZ(CallbackInfoReturnable<Boolean> ci) {
        ci.cancel();
    }

    // ---

    @Inject(target = @Desc(value = "expectReturnNondefaultB", ret = byte.class),
            at = @At("HEAD"))
    private static void injectExpectReturnNondefaultB(CallbackInfoReturnable<Byte> ci) {
        ci.setReturnValue((byte) 1);
    }

    @Inject(target = @Desc(value = "expectReturnNondefaultC", ret = char.class),
            at = @At("HEAD"))
    private static void injectExpectReturnNondefaultC(CallbackInfoReturnable<Character> ci) {
        ci.setReturnValue((char) 1);
    }

    @Inject(target = @Desc(value = "expectReturnNondefaultD", ret = double.class),
            at = @At("HEAD"),
            require = 1)
    private static void injectExpectReturnNondefaultD(CallbackInfoReturnable<Double> ci) {
        ci.setReturnValue(1D);
    }

    @Inject(target = @Desc(value = "expectReturnNondefaultF", ret = float.class),
            at = @At("HEAD"))
    private static void injectExpectReturnNondefaultF(CallbackInfoReturnable<Float> ci) {
        ci.setReturnValue(1F);
    }

    @Inject(target = @Desc(value = "expectReturnNondefaultI", ret = int.class),
            at = @At("HEAD"))
    private static void injectExpectReturnNondefaultI(CallbackInfoReturnable<Integer> ci) {
        ci.setReturnValue(1);
    }

    @Inject(target = @Desc(value = "expectReturnNondefaultJ", ret = long.class),
            at = @At("HEAD"))
    private static void injectExpectReturnNondefaultJ(CallbackInfoReturnable<Long> ci) {
        ci.setReturnValue(1L);
    }

    @Inject(target = @Desc(value = "expectReturnNondefaultL", ret = Object.class),
            at = @At("HEAD"))
    private static void injectExpectReturnNondefaultL(CallbackInfoReturnable<Object> ci) {
        ci.setReturnValue(new Object());
    }

    @Inject(target = @Desc(value = "expectReturnNondefaultS", ret = short.class),
            at = @At("HEAD"))
    private static void injectExpectReturnNondefaultS(CallbackInfoReturnable<Short> ci) {
        ci.setReturnValue((short) 1);
    }

    @Inject(target = @Desc(value = "expectReturnNondefaultZ", ret = boolean.class),
            at = @At("HEAD"))
    private static void injectExpectReturnNondefaultZ(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(true);
    }

    // ---

    @Inject(target = @Desc(value = "expectCancellableReturnNondefaultB", ret = byte.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectCancellableReturnNondefaultB(CallbackInfoReturnable<Byte> ci) {
        ci.setReturnValue((byte) 1);
    }

    @Inject(target = @Desc(value = "expectCancellableReturnNondefaultC", ret = char.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectCancellableReturnNondefaultC(CallbackInfoReturnable<Character> ci) {
        ci.setReturnValue((char) 1);
    }

    @Inject(target = @Desc(value = "expectCancellableReturnNondefaultD", ret = double.class),
            cancellable = true,
            at = @At("HEAD"),
            require = 1)
    private static void injectExpectCancellableReturnNondefaultD(CallbackInfoReturnable<Double> ci) {
        ci.setReturnValue(1D);
    }

    @Inject(target = @Desc(value = "expectCancellableReturnNondefaultF", ret = float.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectCancellableReturnNondefaultF(CallbackInfoReturnable<Float> ci) {
        ci.setReturnValue(1F);
    }

    @Inject(target = @Desc(value = "expectCancellableReturnNondefaultI", ret = int.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectCancellableReturnNondefaultI(CallbackInfoReturnable<Integer> ci) {
        ci.setReturnValue(1);
    }

    @Inject(target = @Desc(value = "expectCancellableReturnNondefaultJ", ret = long.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectCancellableReturnNondefaultJ(CallbackInfoReturnable<Long> ci) {
        ci.setReturnValue(1L);
    }

    @Inject(target = @Desc(value = "expectCancellableReturnNondefaultL", ret = Object.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectCancellableReturnNondefaultL(CallbackInfoReturnable<Object> ci) {
        ci.setReturnValue(new Object());
    }

    @Inject(target = @Desc(value = "expectCancellableReturnNondefaultS", ret = short.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectCancellableReturnNondefaultS(CallbackInfoReturnable<Short> ci) {
        ci.setReturnValue((short) 1);
    }

    @Inject(target = @Desc(value = "expectCancellableReturnNondefaultZ", ret = boolean.class),
            cancellable = true,
            at = @At("HEAD"))
    private static void injectExpectCancellableReturnNondefaultZ(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(true);
    }
}
