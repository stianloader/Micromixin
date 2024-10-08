package org.stianloader.micromixin.transform.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class InjectionPointSelector {

    @NotNull
    public final String fullyQualifiedName;

    @NotNull
    public final Set<String> allNames;

    public InjectionPointSelector(@NotNull String fqn, @NotNull Collection<String> allNames) {
        this.fullyQualifiedName = fqn;
        Set<String> names = new HashSet<String>(allNames);
        names.add(fqn);
        this.allNames = Collections.unmodifiableSet(names);
    }

    @SuppressWarnings("null")
    public InjectionPointSelector(@NotNull String fqn, @NotNull String... aliases) {
        this(fqn, Arrays.asList(aliases));
    }

    /**
     * Obtains the first {@link AbstractInsnNode} that corresponds to the first applicable entrypoint within
     * the provided method as defined by this {@link InjectionPointSelector}. The {@link AbstractInsnNode} may
     * not be virtual, that is it may not have an {@link AbstractInsnNode#getOpcode() opcode} value of -1.
     *
     * <p>Implementations of this method are trusted to not go out of bounds when it comes to the slices.
     * Failure to do so could have nasty consequences for user behaviour as micromixin-transformer or any
     * other caller is not guaranteed to verify the location of the matched instruction (but inversely it is not
     * guaranteed that such as check won't be introduced in the future).
     *
     * @param method The method to find the entrypoints in.
     * @param from The {@link InjectionPointSelector} that represents the start of the slice where the injection point should be selected from. May be null to represent HEAD (start of method).
     * @param to The {@link InjectionPointSelector} that represents the end of the slice where the injection point should be selected from. May be null to represent TAIL (end of method).
     * @param remapper The remapper instance to make use of. This is used to remap any references of the mixin class to the target class when applying injection point constraints.
     * @param sharedBuilder Shared {@link StringBuilder} instance to reduce {@link StringBuilder} allocations.
     * @return The first matched instruction, or null if no instructions match.
     */
    @Nullable
    public abstract AbstractInsnNode getFirstInsn(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder);

    /**
     * Obtains the {@link AbstractInsnNode AbstractInsnNodes} that correspond to every applicable entrypoint within
     * the provided method as defined by this {@link InjectionPointSelector}.
     *
     * <p>Implementations of this method are trusted to not go out of bounds when it comes to the slices.
     * Failure to do so could have nasty consequences for user behaviour as micromixin-transformer or any
     * other caller is not guaranteed to verify the location of the matched instructions (but inversely it is not
     * guaranteed that such as check won't be introduced in the future).
     *
     * @param method The method to find the entrypoints in.
     * @param from The {@link InjectionPointSelector} that represents the start of the slice where the injection point should be selected from. May be null to represent HEAD (start of method).
     * @param to The {@link InjectionPointSelector} that represents the end of the slice where the injection point should be selected from. May be null to represent TAIL (end of method).
     * @param remapper The remapper instance to make use of. This is used to remap any references of the mixin class to the target class when applying injection point constraints.
     * @param sharedBuilder Shared {@link StringBuilder} instance to reduce {@link StringBuilder} allocations.
     * @return The selected instruction nodes that correspond to this entry point.
     */
    @NotNull
    public abstract Collection<? extends AbstractInsnNode> getMatchedInstructions(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder);

    /**
     * <b>WARNING WARNING WARNING</b> The method was deprecated for removal in the 0.6.X lifecycle and
     * will get removed at an unspecified point in time (this might fall within the 0.7.X lifecycle!).
     *  This method is no longer considered to be part of the public API.
     *
     * <p>Checks whether the injection point can be used in conjunction with the Redirect-annotation.
     * Generally should only be true if this injection point selector can select method instruction nodes.
     *
     * @return True if usable in redirects, false otherwise.
     * @deprecated Redirect in theory (not implemented in micromixin-transformer as of now) also supports redirecting
     * PUTFIELD/GETFIELD, PUTSTATIC/GETStATIC, xALOAD and the ARRAYLENGTH instructions. Added with the introduction of
     * slices which make the semantics of the matched instruction type less predictable at first glance, this method
     * has little sense going forward.
     */
    @Deprecated
    @ScheduledForRemoval
    @ApiStatus.OverrideOnly
    public boolean supportsRedirect() {
        return true;
    }
}
