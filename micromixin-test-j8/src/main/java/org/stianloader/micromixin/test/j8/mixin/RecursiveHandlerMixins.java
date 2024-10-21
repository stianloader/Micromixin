package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.stianloader.micromixin.test.j8.targets.RecursiveHandlerTest;

@Mixin(RecursiveHandlerTest.class)
public class RecursiveHandlerMixins {
    @Unique
    private static final ThreadLocal<Boolean> CALLED_RECURSIVELY = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Redirect(at = @At(value = "INVOKE", desc = @Desc("method0")), method = "handlerRedirectVirtual")
    private void handlerRedirectVirtual(RecursiveHandlerTest reciever) {
        Boolean bool = RecursiveHandlerMixins.CALLED_RECURSIVELY.get();
        if (!bool) {
            RecursiveHandlerMixins.CALLED_RECURSIVELY.set(true);
            this.handlerRedirectVirtual(reciever);
            RecursiveHandlerMixins.CALLED_RECURSIVELY.set(false);
        }
    }
}
