package org.stianloader.micromixin.test.j8.mixin.redirect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.stianloader.micromixin.test.j8.targets.redirect.ErroneousInstructionTargetInvoker;

@Mixin(ErroneousInstructionTargetInvoker.class)
public class ErroneousInstructionTargetMixin {
    // This should fail as @Desc's owner for the `at` is ErroneousInstructionTargetInvoker, but should be `GenericInvokeTarget`.
    @Redirect(target = @Desc(value = "invoke"), at = @At(value = "INVOKE", desc = @Desc("assertCalledStaticVV")), require = 1)
    public void redirect$assertCalledStaticVV() {
        // NOP
    }
}
