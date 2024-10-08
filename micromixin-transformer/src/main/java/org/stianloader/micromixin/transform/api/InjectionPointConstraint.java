package org.stianloader.micromixin.transform.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * A constraint that is applied on every injection point before it can be considered a valid injection point.
 * Corresponds to the <code>@At(target = [...])</code> or <code>@At(desc = [...])</code> values,
 * as well as other similar discriminating filters.
 * As such, this constraint is blind to the ordinal position of the injection point.
 *
 * @since 0.7.0-a20241008
 */
@ApiStatus.AvailableSince(value = "0.7.0-a20241008")
public interface InjectionPointConstraint {
    /**
     * Check whether the specified instruction is applicable to this constraint.
     *
     * @param insn The instruction to check.
     * @param remapper The remapper instance to make use of. This is used to remap any references of the mixin class to the target class when applying injection point constraints.
     * @param sharedBuilder Shared {@link StringBuilder} instance to reduce {@link StringBuilder} allocations.
     * @return True if valid, false otherwise
     * @since 0.7.0-a20241008
     */
    @ApiStatus.AvailableSince(value = "0.7.0-a20241008")
    boolean isValid(@NotNull AbstractInsnNode insn, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder);
}
