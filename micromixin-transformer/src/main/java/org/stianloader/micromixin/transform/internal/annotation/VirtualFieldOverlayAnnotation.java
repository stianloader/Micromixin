package org.stianloader.micromixin.transform.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.MixinFieldStub;

public class VirtualFieldOverlayAnnotation extends AbstractOverlayAnnotation<MixinFieldStub> {

    public VirtualFieldOverlayAnnotation(@NotNull MixinLoggingFacade logger) {
        super(logger);
    }

    @Override
    @NotNull
    public String getDesiredName(@NotNull MixinFieldStub source, @NotNull ClassNode target, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        return source.getName();
    }

    @Override
    public boolean handleCollision(@NotNull MixinFieldStub source, @NotNull ClassNode target, int collidedAccess) {
        // TODO handle differences in access flags, if applicable
        return false;
    }
}
