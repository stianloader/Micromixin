package de.geolykt.micromixin.internal.annotation;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import de.geolykt.micromixin.internal.HandlerContextHelper;
import de.geolykt.micromixin.internal.MixinStub;
import de.geolykt.micromixin.internal.util.Remapper;

public abstract class MixinAnnotation<T> implements Comparable<MixinAnnotation<T>> {
    public abstract void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub, @NotNull T source, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder);

    @Override
    public int compareTo(MixinAnnotation<T> o) {
        return o.getClass().getName().compareTo(this.getClass().getName());
    }

    public boolean isCompatible(Collection<MixinAnnotation<T>> collection) {
        return true;
    }

    public abstract void collectMappings(@NotNull T source, @NotNull ClassNode target, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder);
}
