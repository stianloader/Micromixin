package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.stianloader.micromixin.test.j8.MutableInt;
import org.stianloader.micromixin.test.j8.targets.AllowTest;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(AllowTest.class)
public class AllowMixin {

    // The TooFew classes accidentally test 'require', but that is fine. 'allow' is being tested in the TooMany classes
    @Mixin(AllowTest.TooFewInject.class)
    private static class TooFewInject {
        @Inject(target = @Desc(value = "test", args = MutableInt.class), at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "mul", args = MutableInt.class, ret = MutableInt.class)), require = 3, cancellable = true)
        private static void test(MutableInt accumulator, CallbackInfo ci) {
            accumulator.add(1);
        }
    }

    @Mixin(AllowTest.TooFewModifyArg.class)
    private static class TooFewModifyArg {
        @ModifyArg(target = @Desc(value = "test", args = MutableInt.class), at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "mul", args = MutableInt.class, ret = MutableInt.class)), require = 3)
        private static MutableInt test(MutableInt multiplicant) {
            return multiplicant.add(1);
        }
    }

    @Mixin(AllowTest.TooFewModifyConstant.class)
    private static class TooFewModifyConstant {
        @ModifyConstant(target = @Desc(value = "test", args = MutableInt.class), constant = @Constant(intValue = 2), require = 3)
        private static int test(int modificationConstant, MutableInt accumulator) {
            accumulator.add(1);
            return modificationConstant;
        }
    }

    @Mixin(AllowTest.TooFewRedirect.class)
    private static class TooFewRedirect {
        @Redirect(target = @Desc(value = "test", args = MutableInt.class), at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "mul", args = MutableInt.class, ret = MutableInt.class)), require = 3, allow = 8)
        private static MutableInt test(MutableInt accumulator, MutableInt arg) {
            return accumulator.add(1).mul(2);
        }
    }

    // now testing with an allow of 1 (despite targeting 2 instructions):
    @Mixin(AllowTest.TooManyInject.class)
    private static class TooManyInject {
        @Inject(target = @Desc(value = "test", args = MutableInt.class), at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "mul", args = MutableInt.class, ret = MutableInt.class)), allow = 1, cancellable = true)
        private static void test(MutableInt accumulator, CallbackInfo ci) {
            accumulator.add(1);
        }
    }

    @Mixin(AllowTest.TooManyModifyArg.class)
    private static class TooManyModifyArg {
        @ModifyArg(target = @Desc(value = "test", args = MutableInt.class), at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "mul", args = MutableInt.class, ret = MutableInt.class)), allow = 1)
        private static MutableInt test(MutableInt multiplicant) {
            return multiplicant.add(1);
        }
    }

    @Mixin(AllowTest.TooManyModifyConstant.class)
    private static class TooManyModifyConstant {
        @ModifyConstant(target = @Desc(value = "test", args = MutableInt.class), constant = @Constant(intValue = 2), allow = 1)
        private static int test(int modificationConstant, MutableInt accumulator) {
            accumulator.add(1);
            return modificationConstant;
        }
    }

    @Mixin(AllowTest.TooManyRedirect.class)
    private static class TooManyRedirect {
        @Redirect(target = @Desc(value = "test", args = MutableInt.class), at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "mul", args = MutableInt.class, ret = MutableInt.class)), allow = 1)
        private static MutableInt test(MutableInt accumulator, MutableInt arg) {
            return accumulator.add(1).mul(2);
        }
    }

    // negative (i.e. -1)

    @Inject(method = "testNegativeAllowInject", at = @At("RETURN"), allow = -1, cancellable = true)
    private static void testNegativeAllowInject(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @ModifyArg(method = "testNegativeAllowModifyArg", at = @At(value = "INVOKE", desc = @Desc(value = "unaryExpr", args = boolean.class, ret = boolean.class)), allow = -1)
    private static boolean testNegativeAllowModifyArg(boolean in) {
        if (in) {
            throw new AssertionError("in expected false.");
        }
        return true;
    }

    @ModifyConstant(method = "testNegativeAllowModifyConstant", constant = @Constant(intValue = 0), allow = -1)
    private static int testNegativeAllowModifyConstant(int in) {
        if (in != 0) {
            throw new AssertionError("in expected false.");
        }
        return 1;
    }

    @ModifyReturnValue(method = "testNegativeAllowModifyReturnValue", at = @At("RETURN"), allow = -1)
    private static boolean testNegativeAllowModifyReturnValue(boolean in) {
        if (in) {
            throw new AssertionError("in expected false.");
        }
        return true;
    }

    @Redirect(method = "testNegativeAllowRedirect", at = @At(value = "INVOKE", desc = @Desc(value = "unaryExpr", args = boolean.class, ret = boolean.class)), allow = -1)
    private static boolean testNegativeAllowRedirect(boolean in) {
        if (in) {
            throw new AssertionError("in expected false.");
        }
        return true;
    }

    // zero (0)

    @Inject(method = "testZeroAllowInject", at = @At("RETURN"), allow = 0, cancellable = true)
    private static void testZeroAllowInject(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @ModifyArg(method = "testZeroAllowModifyArg", at = @At(value = "INVOKE", desc = @Desc(value = "unaryExpr", args = boolean.class, ret = boolean.class)), allow = 0)
    private static boolean testZeroAllowModifyArg(boolean in) {
        if (in) {
            throw new AssertionError("in expected false.");
        }
        return true;
    }

    @ModifyConstant(method = "testZeroAllowModifyConstant", constant = @Constant(intValue = 0), allow = 0)
    private static int testZeroAllowModifyConstant(int in) {
        if (in != 0) {
            throw new AssertionError("in expected false.");
        }
        return 1;
    }

    @ModifyReturnValue(method = "testZeroAllowModifyReturnValue", at = @At("RETURN"), allow = 0)
    private static boolean testZeroAllowModifyReturnValue(boolean in) {
        if (in) {
            throw new AssertionError("in expected false.");
        }
        return true;
    }

    @Redirect(method = "testZeroAllowRedirect", at = @At(value = "INVOKE", desc = @Desc(value = "unaryExpr", args = boolean.class, ret = boolean.class)), allow = 0)
    private static boolean testZeroAllowRedirect(boolean in) {
        if (in) {
            throw new AssertionError("in expected false.");
        }
        return true;
    }

    // one (1)

    @Inject(method = "testOneAllowInject", at = @At("RETURN"), allow = 1, cancellable = true)
    private static void testOneAllowInject(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @ModifyArg(method = "testOneAllowModifyArg", at = @At(value = "INVOKE", desc = @Desc(value = "unaryExpr", args = boolean.class, ret = boolean.class)), allow = 1)
    private static boolean testOneAllowModifyArg(boolean in) {
        if (in) {
            throw new AssertionError("in expected false.");
        }
        return true;
    }

    @ModifyConstant(method = "testOneAllowModifyConstant", constant = @Constant(intValue = 0), allow = 1)
    private static int testOneAllowModifyConstant(int in) {
        if (in != 0) {
            throw new AssertionError("in expected false.");
        }
        return 1;
    }

    @ModifyReturnValue(method = "testOneAllowModifyReturnValue", at = @At("RETURN"), allow = 1)
    private static boolean testOneAllowModifyReturnValue(boolean in) {
        if (in) {
            throw new AssertionError("in expected false.");
        }
        return true;
    }

    @Redirect(method = "testOneAllowRedirect", at = @At(value = "INVOKE", desc = @Desc(value = "unaryExpr", args = boolean.class, ret = boolean.class)), allow = 1)
    private static boolean testOneAllowRedirect(boolean in) {
        if (in) {
            throw new AssertionError("in expected false.");
        }
        return true;
    }

    // require = 2, allow = 1

    @Inject(target = @Desc(value = "testHigherExpectInject", args = MutableInt.class), at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "mul", args = MutableInt.class, ret = MutableInt.class)), require = 2, allow = 1, cancellable = true)
    private static void testHigherRequireInject(MutableInt accumulator, CallbackInfo ci) {
        accumulator.add(1);
    }

    @ModifyArg(target = @Desc(value = "testHigherExpectModifyArg", args = MutableInt.class), at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "mul", args = MutableInt.class, ret = MutableInt.class)), require = 2, allow = 1)
    private static MutableInt testHigherExpectModifyArg(MutableInt multiplicant) {
        return multiplicant.add(1);
    }

    @ModifyConstant(target = @Desc(value = "testHigherExpectModifyConstant", args = MutableInt.class), constant = @Constant(intValue = 2), require = 2, allow = 1)
    private static int testHigherExpectModifyConstant(int modificationConstant, MutableInt accumulator) {
        accumulator.add(1);
        return modificationConstant;
    }

    @Redirect(target = @Desc(value = "testHigherExpectRedirect", args = MutableInt.class), at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "mul", args = MutableInt.class, ret = MutableInt.class)), require = 2, allow = 1)
    private static MutableInt testHigherExpectRedirect(MutableInt accumulator, MutableInt arg) {
        return accumulator.add(1).mul(2);
    }

    // TODO one handler targets many methods
}
