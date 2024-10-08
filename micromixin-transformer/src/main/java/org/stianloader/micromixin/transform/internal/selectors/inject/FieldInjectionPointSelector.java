package org.stianloader.micromixin.transform.internal.selectors.inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.InjectionPointSelector;
import org.stianloader.micromixin.transform.api.InjectionPointSelectorFactory.InjectionPointSelectorProvider;
import org.stianloader.micromixin.transform.api.InjectionPointConstraint;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.MixinParseException;

public class FieldInjectionPointSelector extends InjectionPointSelector {
    @NotNull
    private static final Set<String> ALL_NAMES = new HashSet<String>(Arrays.asList("org.spongepowered.asm.mixin.injection.points.BeforeFieldAccess", "FIELD"));

    @NotNull
    public static final InjectionPointSelectorProvider PROVIDER = new InjectionPointSelectorProvider() {
        @Override
        @NotNull
        public String getFullyQualifiedName() {
            return "org.spongepowered.asm.mixin.injection.points.BeforeFieldAccess";
        }

        @Override
        @NotNull
        public Set<String> getAllNames() {
            return FieldInjectionPointSelector.ALL_NAMES;
        }

        @Override
        @NotNull
        @ApiStatus.AvailableSince("0.7.0-a20241008")
        @Contract(pure = true)
        public InjectionPointSelector create(@Nullable List<String> args, @NotNull InjectionPointConstraint[] constraints) {
            if (args != null) {
                throw new MixinParseException("Broken mixin: Superfluous discriminator found in @At(\"" + this.getFullyQualifiedName() + "\"). Usage of the 'args' constraints is not applicable to the " + this.getFullyQualifiedName() + " injection point.");
            }
            return new FieldInjectionPointSelector(constraints);
        }
    };

    @NotNull
    private final InjectionPointConstraint[] constraints;

    private FieldInjectionPointSelector(@NotNull InjectionPointConstraint[] constraints) {
        super("org.spongepowered.asm.mixin.injection.points.BeforeFieldAccess", "FIELD");
        this.constraints = constraints;
    }

    @Override
    @Nullable
    public AbstractInsnNode getFirstInsn(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        AbstractInsnNode insn = from == null ? method.instructions.getFirst() : from.getFirstInsn(method, remapper, sharedBuilder);
        AbstractInsnNode guard = to == null ? method.instructions.getLast() : to.getFirstInsn(method, remapper, sharedBuilder);

        for (; insn != null; insn = insn.getNext()) {
            if (insn instanceof FieldInsnNode) {
                filter: {
                    for (InjectionPointConstraint constraint : this.constraints) {
                        if (!constraint.isValid(insn, remapper, sharedBuilder)) {
                            break filter;
                        }
                    }
                    return insn;
                }
            }
            if(insn == guard) {
                break;
            }
        }

        if (insn == null) {
            // This is an error condition. Technically speaking we should attach more data in order to more easily
            // debug this issue, but the current plan is to validate for these kinds of errors ahead of time.
            throw new IllegalStateException("Exhausted instruction list before hitting the last instruction in the slice. This likely points to an invalidly programmed selector as well as insufficent slice validation.");
        }

        return null;
    }

    @Override
    @NotNull
    public Collection<? extends AbstractInsnNode> getMatchedInstructions(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        List<AbstractInsnNode> matched = new ArrayList<AbstractInsnNode>();
        AbstractInsnNode insn = from == null ? method.instructions.getFirst() : from.getFirstInsn(method, remapper, sharedBuilder);
        AbstractInsnNode guard = to == null ? method.instructions.getLast() : to.getFirstInsn(method, remapper, sharedBuilder);

        for (; insn != null; insn = insn.getNext()) {
            if (insn instanceof FieldInsnNode) {
                filter: {
                    for (InjectionPointConstraint constraint : this.constraints) {
                        if (!constraint.isValid(insn, remapper, sharedBuilder)) {
                            break filter;
                        }
                    }
                    matched.add(insn);
                }
            }

            if(insn == guard) {
                break;
            }

            assert insn != null : "Exhausted instruction list"; // This exists to satisfy eclipse. Of course, the error condition will be dealt without relying on assertions in practice.
        }

        if (insn == null) {
            // This is an error condition. Technically speaking we should attach more data in order to more easily
            // debug this issue, but the current plan is to validate for these kinds of errors ahead of time.
            throw new IllegalStateException("Exhausted instruction list before hitting the last instruction in the slice. This likely points to an invalidly programmed selector as well as insufficent slice validation.");
        }

        return matched;
    }

    @Override
    public boolean supportsRedirect() {
        return true;
    }
}
