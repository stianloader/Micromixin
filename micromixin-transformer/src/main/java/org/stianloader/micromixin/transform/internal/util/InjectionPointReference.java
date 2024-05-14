package org.stianloader.micromixin.transform.internal.util;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;

public class InjectionPointReference {
    @NotNull
    public final AbstractInsnNode unshiftedInstruction;
    @NotNull
    public final AbstractInsnNode shiftedInstruction;
    @NotNull
    public final MethodNode targetedMethod;
    @NotNull
    public final SlicedInjectionPointSelector atSelector;

    InjectionPointReference(@NotNull AbstractInsnNode unshifted,
            @NotNull AbstractInsnNode shifted,
            @NotNull MethodNode target,
            @NotNull SlicedInjectionPointSelector atSelector) {
        this.unshiftedInstruction = unshifted;
        this.shiftedInstruction = shifted;
        this.targetedMethod = target;
        this.atSelector = atSelector;
    }
}
