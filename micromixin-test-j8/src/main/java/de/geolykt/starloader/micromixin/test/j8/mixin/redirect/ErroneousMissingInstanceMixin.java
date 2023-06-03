package de.geolykt.starloader.micromixin.test.j8.mixin.redirect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;

import de.geolykt.starloader.micromixin.test.j8.targets.redirect.ErroneousMissingInstanceInvoker;
import de.geolykt.starloader.micromixin.test.j8.targets.redirect.GenericInvokeTarget;

@Mixin(value = ErroneousMissingInstanceInvoker.class)
public class ErroneousMissingInstanceMixin {
    @Redirect(target = @Desc(value = "invoke", ret = void.class), at = @At(value = "INVOKE", desc = @Desc(value = "return0Instanced", owner = GenericInvokeTarget.class, ret = int.class)), require = 1)
    private static int redirect() {
        return 1;
    }
}
