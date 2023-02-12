package de.geolykt.micromixin.internal;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import de.geolykt.micromixin.internal.util.Remapper;

public interface ClassMemberStub {

    @NotNull
    ClassNode getOwner();

    @NotNull
    String getName();

    @NotNull
    String getDesc();

    int getAccess();

    void applyTo(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx, @NotNull MixinStub stub, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder);

    // Is such a system viable if we need to remap descriptors beforehand too?
    // That obviously is just a single example, but it could be a bigger pain for other reasons that are similar to that one
    void collectMappings(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx, @NotNull MixinStub stub, @NotNull Remapper out, @NotNull StringBuilder sharedBuilder);
}
