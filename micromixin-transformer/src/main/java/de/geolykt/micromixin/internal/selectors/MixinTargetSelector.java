package de.geolykt.micromixin.internal.selectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.MixinStub;

public interface MixinTargetSelector {

    @Nullable
    MethodNode selectMethod(@NotNull ClassNode within, @NotNull MixinStub source);
}
