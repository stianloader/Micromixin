package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class StringTargetTestMixins {

    @Mixin(targets = "org/stianloader/micromixin/test/j8/targets/StringTargetTest$DetchedReturnDescriptorTarget")
    private static class DetchedReturnDescriptorTarget {
        @Inject(at = @At("HEAD"), method = "throwError() V", cancellable = true)
        private static void onThrowError(CallbackInfo ci) {
            ci.cancel();
        }
    }

    @Mixin(targets = "org.stianloader.micromixin.test.j8.targets.StringTargetTest$DotMixinTarget")
    private static class DotMixinTarget {
        @Overwrite
        private static void throwError() {
            // NOP
        }
    }

    @Mixin(targets = "org.stianloader.micromixin.test.j8.targets.StringTargetTest.FullDotMixinTarget")
    private static class FullDotMixinTarget {
        @Overwrite
        private static void throwError() {
            // NOP
        }
    }

    @Mixin(targets = "org/stianloader/micromixin/test/j8/targets/StringTargetTest/FullSlashMixinTarget")
    private static class FullSlashMixinTarget {
        @Overwrite
        private static void throwError() {
            // NOP
        }
    }

    @Mixin(targets = "org/stianloader.micromixin/test.j8/targets.StringTargetTest$SlashDotMixinTarget")
    private static class SlashDotMixinTarget {
        @Overwrite
        private static void throwError() {
            // NOP
        }
    }

    @Mixin(targets = "org/stianloader/micromixin/test/j8/targets/StringTargetTest$SlashMixinTarget")
    private static class SlashMixinTarget {
        @Overwrite
        private static void throwError() {
            // NOP
        }
    }

    @Mixin(targets = "org/stianloader/micromixin/test/j8/targets/StringTargetTest$WhitespaceDescriptorTarget0")
    private static class WhitespaceDescriptorTarget0 {
        @Inject(at = @At("HEAD"), method = "throwError( )V", cancellable = true, allow = 1)
        private static void onThrowError(CallbackInfo ci) {
            ci.cancel();
        }
    }

    @Mixin(targets = "org/stianloader/micromixin/test/j8/targets/StringTargetTest$WhitespaceDescriptorTarget1")
    private static class WhitespaceDescriptorTarget1 {
        @Inject(at = @At("HEAD"), method = "throwError(I I)V", cancellable = true, allow = 1)
        private static void onThrowError(CallbackInfo ci) {
            ci.cancel();
        }
    }

    @Mixin(targets = "org/stianloader/micromixin/test/j8/targets/StringTargetTest$WhitespaceDescriptorTarget2")
    private static class WhitespaceDescriptorTarget2 {
        @Inject(at = @At("HEAD"), method = "throwError(L java/lang/Object;)V", cancellable = true, allow = 1)
        private static void onThrowError(CallbackInfo ci) {
            ci.cancel();
        }
    }

    @Mixin(targets = "org/stianloader/micromixin/test/j8/targets/StringTargetTest$WhitespaceDescriptorTarget3")
    private static class WhitespaceDescriptorTarget3 {
        @Inject(at = @At("HEAD"), method = "throwError(Ljava/lan g/Object;)V", cancellable = true, allow = 1)
        private static void onThrowError(CallbackInfo ci) {
            ci.cancel();
        }
    }

    @Mixin(targets = "org/stianloader/micromixin/test/j8/targets/StringTargetTest$WhitespaceDescriptorTarget4")
    private static class WhitespaceDescriptorTarget4 {
        @Inject(at = @At("HEAD"), method = "throwError()\tV", cancellable = true, allow = 1)
        private static void onThrowError(CallbackInfo ci) {
            ci.cancel();
        }
    }

    @Mixin(targets = "org.stianloader.micromixin.test.j8.targets.StringTargetTest$Whitespac eMixinTarget")
    private static class WhitespaceMixinTarget {
        @Overwrite
        private static void throwError() {
            // NOP
        }
    }

    @Mixin(targets = "org/stianloader/micromixin/test/j8/targets/StringTargetTest$WhitespaceNameTarget")
    private static class WhitespaceNameTarget {
        // At least it is consistent
        @Inject(at = @At("HEAD"), method = "thro wError()V", cancellable = true, allow = 1)
        private static void onThrowError(CallbackInfo ci) {
            ci.cancel();
        }
    }
}
