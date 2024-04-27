package org.stianloader.micromixin.transform.internal;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.SimpleRemapper;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;

public interface ClassMemberStub {

    @NotNull
    ClassNode getOwner();

    @NotNull
    String getName();

    @NotNull
    String getDesc();

    @NotNull
    MixinLoggingFacade getLogger();

    int getAccess();

    void applyTo(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx, @NotNull MixinStub stub, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder);

    // Is such a system viable if we need to remap descriptors beforehand too?
    // That obviously is just a single example, but it could be a bigger pain for other reasons that are similar to that one
    void collectMappings(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx, @NotNull MixinStub stub, @NotNull SimpleRemapper out, @NotNull StringBuilder sharedBuilder);
}
