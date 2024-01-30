package org.stianloader.micromixin.internal.selectors.inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.SimpleRemapper;
import org.stianloader.micromixin.api.InjectionPointSelector;
import org.stianloader.micromixin.api.InjectionPointTargetConstraint;
import org.stianloader.micromixin.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.api.InjectionPointSelectorFactory.InjectionPointSelectorProvider;
import org.stianloader.micromixin.internal.MixinParseException;
import org.stianloader.micromixin.internal.annotation.ConstantSelector;
import org.stianloader.micromixin.internal.util.ASMUtil;

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

    private ConstantInjectionPointSelector(@NotNull ConstantSelector constSelector) {
        super("org.spongepowered.asm.mixin.injection.points.BeforeConstant", "CONSTANT");
        this.constSelector = constSelector;
    }

    @Override
    @Nullable
    public LabelNode getFirst(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        AbstractInsnNode insn = from == null ? method.instructions.getFirst() : from.getFirst(method, remapper, sharedBuilder);
        AbstractInsnNode guard = to == null ? method.instructions.getLast() : to.getFirst(method, remapper, sharedBuilder);

        for (; insn != null && insn != guard; insn = insn.getNext()) {
            if (this.constSelector.matchesConstant(insn)) {
                return ASMUtil.getLabelNodeBefore(insn, method.instructions);
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
    public Collection<LabelNode> getLabels(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        List<LabelNode> labels = new ArrayList<LabelNode>();
        AbstractInsnNode insn = from == null ? method.instructions.getFirst() : from.getFirst(method, remapper, sharedBuilder);
        AbstractInsnNode guard = to == null ? method.instructions.getLast() : to.getFirst(method, remapper, sharedBuilder);

        for (; insn != null && insn != guard; insn = insn.getNext()) {
            if (this.constSelector.matchesConstant(insn)) {
                labels.add(ASMUtil.getLabelNodeBefore(insn, method.instructions));
            }
        }

        if (insn == null) {
            // This is an error condition. Technically speaking we should attach more data in order to more easily
            // debug this issue, but the current plan is to validate for these kinds of errors ahead of time.
            throw new IllegalStateException("Exhausted instruction list before hitting the last instruction in the slice. This likely points to an invalidly programmed selector as well as insufficent slice validation.");
        }

        return labels;
    }

    @Override
    public boolean supportsRedirect() {
        return false;
    }
}
