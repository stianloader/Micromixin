package de.geolykt.micromixin.internal.annotation;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import de.geolykt.micromixin.internal.HandlerContextHelper;
import de.geolykt.micromixin.internal.MixinStub;
import de.geolykt.micromixin.internal.util.Remapper;

public interface MixinAnnotation<T> extends Comparable<MixinAnnotation<T>> {
    void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub, @NotNull T source, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder);
    @Override
    default int compareTo(MixinAnnotation<T> o) {
        return o.getClass().getName().compareTo(this.getClass().getName());
    }
    default boolean isCompatible(Collection<MixinAnnotation<T>> collection) {
        return true;
    }
    void collectMappings(@NotNull T source, @NotNull ClassNode target, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder);
}
