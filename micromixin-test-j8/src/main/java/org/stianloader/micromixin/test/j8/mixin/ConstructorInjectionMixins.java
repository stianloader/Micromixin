package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Mixin(ConstructorInjectionTest.ModifySuperconstructorArg.class)
    private static class ModifySuperconstructorArg {
        @ModifyArg(method = "<init>", at = @At(value = "INVOKE", desc = @Desc(owner = MutableInt.class, value = "<init>", args = int.class)))
        private static int modifyArg(int arg) {
            return 8;
        }
    }
}
