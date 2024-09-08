package org.stianloader.micromixin.transform.internal.selectors.inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.InjectionPointSelector;
import org.stianloader.micromixin.transform.api.InjectionPointSelectorFactory.InjectionPointSelectorProvider;
import org.stianloader.micromixin.transform.api.InjectionPointTargetConstraint;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.annotation.ConstantSelector;

public class ConstantInjectionPointSelector extends InjectionPointSelector {
    @NotNull
    private static final Set<String> ALL_NAMES = new HashSet<String>(Arrays.asList("org.spongepowered.asm.mixin.injection.points.BeforeConstant", "CONSTANT"));

    @NotNull
    public static final InjectionPointSelectorProvider PROVIDER = new InjectionPointSelectorProvider() {
        @Override
        @NotNull
        public String getFullyQualifiedName() {
            return "org.spongepowered.asm.mixin.injection.points.BeforeConstant";
        }

        @Override
        @NotNull
        public Set<String> getAllNames() {
            return ConstantInjectionPointSelector.ALL_NAMES;
        }

        @Override
        @NotNull
        public InjectionPointSelector create(@Nullable List<String> args, @Nullable InjectionPointTargetConstraint constraint) {
            if (constraint != null) {
                throw new MixinParseException("Broken mixin: Superfluous discriminator found in @At(\"" + this.getFullyQualifiedName() + "\"). Usage of the 'target' or 'desc' constraints is not applicable to the CONSTANT injection point.");
            }
            if (args == null) {
                throw new MixinParseException("Broken mixin: No constant discriminator could be found in @At(\"" + this.getFullyQualifiedName() + "\") args");
            }
            return new ConstantInjectionPointSelector(ConstantSelector.parse(args));
        }
    };

    @NotNull
    private final ConstantSelector constSelector;

    public ConstantInjectionPointSelector(@NotNull ConstantSelector constSelector) {
        super("org.spongepowered.asm.mixin.injection.points.BeforeConstant", "CONSTANT");
        this.constSelector = constSelector;
    }

    @Override
    @Nullable
    public AbstractInsnNode getFirstInsn(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        AbstractInsnNode insn = from == null ? method.instructions.getFirst() : from.getFirstInsn(method, remapper, sharedBuilder);
        AbstractInsnNode guard = to == null ? method.instructions.getLast() : to.getFirstInsn(method, remapper, sharedBuilder);

        for (; insn != null; insn = insn.getNext()) {
            if (this.constSelector.matchesConstant(insn)) {
                return insn;
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
            if (this.constSelector.matchesConstant(insn)) {
                matched.add(insn);
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

        return matched;
    }

    @Override
    public boolean supportsRedirect() {
        return false;
    }
}
