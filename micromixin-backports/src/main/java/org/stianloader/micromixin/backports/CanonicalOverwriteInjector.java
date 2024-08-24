package org.stianloader.micromixin.backports;

import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes.InjectionNode;
import org.spongepowered.asm.mixin.injection.struct.Target;

public class CanonicalOverwriteInjector extends Injector {

    public CanonicalOverwriteInjector(CanonicalOverwriteInjectionInfo info) {
        super(info, "@CanonicalOverwrite");
    }

    @Override
    protected void inject(Target target, InjectionNode node) {
        throw new UnsupportedOperationException("This annotation is not not yet fully implemented.");
    }
}
