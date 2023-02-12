package de.geolykt.micromixin.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import de.geolykt.micromixin.internal.MixinFieldStub;
import de.geolykt.micromixin.internal.util.Remapper;

public class VirtualFieldOverlayAnnotation extends AbstractOverlayAnnotation<MixinFieldStub> {

    @Override
    public boolean allowOverwrite(@NotNull MixinFieldStub source, @NotNull ClassNode target) {
        return false;
    }

    @Override
    @NotNull
    public String getDesiredName(@NotNull MixinFieldStub source, @NotNull ClassNode target, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        return source.getName();
    }

    @Override
    protected void handleAlreadyExisting(@NotNull MixinFieldStub source, @NotNull ClassNode target) {
        // NOP
    }
}
