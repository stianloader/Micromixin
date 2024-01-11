package de.geolykt.starloader.micromixin.test.j8.mixin.mixinextra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueVisibilityTest;

class ModifyReturnValueVisbilityMixins {
    // ModifyReturnValue handlers must be private if they are also statics
    @Mixin(value = ModifyReturnValueVisibilityTest.VisibilityPublic.class)
    private static class VisibilityPublic {
        @ModifyReturnValue(at = @At("RETURN"), method = "getValue")
        public static Object modifyValue(Object o) {
            return new Object();
        }
    }

    @Mixin(value = ModifyReturnValueVisibilityTest.VisibilityPrivate.class)
    private static class VisibilityPrivate {
        @ModifyReturnValue(at = @At("RETURN"), method = "getValue")
        private static Object modifyValue(Object o) {
            return new Object();
        }
    }

    @Mixin(value = ModifyReturnValueVisibilityTest.VisibilityPackageProtected.class)
    private static class VisibilityPackageProtected {
        @ModifyReturnValue(at = @At("RETURN"), method = "getValue")
        static Object modifyValue(Object o) {
            return new Object();
        }
    }

    @Mixin(value = ModifyReturnValueVisibilityTest.VisibilityProtected.class)
    private static class VisibilityProtected {
        @ModifyReturnValue(at = @At("RETURN"), method = "getValue")
        protected static Object modifyValue(Object o) {
            return new Object();
        }
    }
}
