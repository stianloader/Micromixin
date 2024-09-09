package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.stianloader.micromixin.test.j8.MutableInt;
import org.stianloader.micromixin.test.j8.targets.ConstructorInjectionTest;

public class ConstructorInjectionMixins {

    @Mixin(ConstructorInjectionTest.IllegalSuperconstructorRedirect.class)
    private static class IllegalSuperconstructorRedirect {
        @Redirect(method = "<init>", at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "<init>", args = int.class)))
        private static void injectCtor(MutableInt value, int arg) {
            // nothing required
        }
    }

    @Mixin(ConstructorInjectionTest.IllegalSuperconstructorRedirect2.class)
    private static class IllegalSuperconstructorRedirect2 {
        @Redirect(method = "<init>", at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "<init>", args = int.class)))
        private static void injectCtor(int arg) {
            // nothing required
        }
    }

    @Mixin(ConstructorInjectionTest.InjectHeadStatic.class)
    public static class InjectHeadStatic {
        @Inject(method = "<init>", at = @At("HEAD"), allow = 1, require = 1)
        private static void oninit(int captureArg, CallbackInfo ci) {
            if (captureArg != 7) {
                throw new AssertionError("Did not correctly capture host argument.");
            }
            ConstructorInjectionTest.InjectHeadStatic.callerCounter++;
        }
    }

    @Mixin(ConstructorInjectionTest.InjectHeadVirtual.class)
    public static class InjectHeadVirtual {
        @Inject(method = "<init>", at = @At("HEAD"), allow = 1, require = 1)
        private void oninit(int captureArg, CallbackInfo ci) {
            if (captureArg != 7) {
                throw new AssertionError("Did not correctly capture host argument.");
            }
            ConstructorInjectionTest.InjectHeadVirtual.callerCounter++;
            ((MutableInt) (Object) this).mul(3);
        }
    }

    @Mixin(ConstructorInjectionTest.InjectReturnStatic.class)
    public static class InjectReturnStatic {
        @Inject(method = "<init>", at = @At("RETURN"), allow = 1, require = 1)
        private static void oninit(int captureArg, CallbackInfo ci) {
            if (captureArg != 7) {
                throw new AssertionError("Did not correctly capture host argument.");
            }
            ConstructorInjectionTest.InjectReturnStatic.callerCounter++;
        }
    }

    @Mixin(ConstructorInjectionTest.InjectReturnVirtual.class)
    public static class InjectReturnVirtual {
        @Inject(method = "<init>", at = @At("RETURN"), allow = 1, require = 1)
        private void oninit(int captureArg, CallbackInfo ci) {
            if (captureArg != 7) {
                throw new AssertionError("Did not correctly capture host argument.");
            }
            ConstructorInjectionTest.InjectReturnVirtual.callerCounter++;
            ((MutableInt) (Object) this).mul(3);
        }
    }

    @Mixin(ConstructorInjectionTest.InjectTailStatic.class)
    public static class InjectTailStatic {
        @Inject(method = "<init>", at = @At("TAIL"), allow = 1, require = 1)
        private static void oninit(int captureArg, CallbackInfo ci) {
            if (captureArg != 7) {
                throw new AssertionError("Did not correctly capture host argument.");
            }
            ConstructorInjectionTest.InjectTailStatic.callerCounter++;
        }
    }

    @Mixin(ConstructorInjectionTest.InjectTailVirtual.class)
    public static class InjectTailVirtual {
        @Inject(method = "<init>", at = @At("TAIL"), allow = 1, require = 1)
        private void oninit(int captureArg, CallbackInfo ci) {
            if (captureArg != 7) {
                throw new AssertionError("Did not correctly capture host argument.");
            }
            ConstructorInjectionTest.InjectTailVirtual.callerCounter++;
            ((MutableInt) (Object) this).mul(3);
        }
    }

    @Mixin(ConstructorInjectionTest.ModifySuperconstructorArg.class)
    private static class ModifySuperconstructorArg {
        @ModifyArg(method = "<init>", at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "<init>", args = int.class)))
        private static int modifyArg(int arg) {
            return 8;
        }
    }

    @Mixin(ConstructorInjectionTest.RedirectInConstructorStatic.class)
    private static class RedirectInConstructorStatic {
        @Redirect(at = @At(value = "INVOKE", target = "set"), method = "<init>")
        private static MutableInt onSet(ConstructorInjectionTest.RedirectInConstructorStatic caller, int value) {
            return caller.add(value);
        }
    }

    @Mixin(ConstructorInjectionTest.RedirectInConstructorStaticCapture.class)
    private static class RedirectInConstructorStaticCapture {
        @Redirect(at = @At(value = "INVOKE", target = "set"), method = "<init>")
        private static MutableInt onSet(ConstructorInjectionTest.RedirectInConstructorStaticCapture caller, int value, int captured) {
            if (captured != value) {
                throw new AssertionError("Capture and value mismatch");
            }
            return caller.add(value);
        }
    }

    @Mixin(ConstructorInjectionTest.RedirectInConstructorVirtual.class)
    private static class RedirectInConstructorVirtual {
        @Redirect(at = @At(value = "INVOKE", target = "set"), method = "<init>")
        private MutableInt onSet(ConstructorInjectionTest.RedirectInConstructorVirtual caller, int value) {
            return caller.add(value);
        }
    }

    @Mixin(ConstructorInjectionTest.RedirectInConstructorVirtualCapture.class)
    private static class RedirectInConstructorVirtualCapture {
        @Redirect(at = @At(value = "INVOKE", target = "set"), method = "<init>")
        private MutableInt onSet(ConstructorInjectionTest.RedirectInConstructorVirtualCapture caller, int value, int captured) {
            if (captured != value) {
                throw new AssertionError("Capture and value mismatch");
            }
            return caller.add(value);
        }
    }
}
