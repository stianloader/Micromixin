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
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.SimpleRemapper;
import org.stianloader.micromixin.transform.api.InjectionPointSelector;
import org.stianloader.micromixin.transform.api.InjectionPointTargetConstraint;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.api.InjectionPointSelectorFactory.InjectionPointSelectorProvider;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;

public class InvokeInjectionPointSelector extends InjectionPointSelector {
    @NotNull
    private static final Set<String> ALL_NAMES = new HashSet<String>(Arrays.asList("org.spongepowered.asm.mixin.injection.points.BeforeInvoke", "INVOKE"));

    @NotNull
    public static final InjectionPointSelectorProvider PROVIDER = new InjectionPointSelectorProvider() {
        @Override
        @NotNull
        public String getFullyQualifiedName() {
            return "org.spongepowered.asm.mixin.injection.points.BeforeInvoke";
        }

        @Override
        @NotNull
        public Set<String> getAllNames() {
            return InvokeInjectionPointSelector.ALL_NAMES;
        }

        @Override
        @NotNull
        public InjectionPointSelector create(@Nullable List<String> args, @Nullable InjectionPointTargetConstraint constraint) {
            if (constraint == null) {
                throw new MixinParseException("Broken mixin: No descriptor or type discriminator found in @At(\"" + this.getFullyQualifiedName() + "\"). Usage of the 'target' or 'desc' constraints is required in the INVOKE injection point.");
            }
            if (args != null) {
                throw new MixinParseException("Broken mixin: Superfluous discriminator found in @At(\"" + this.getFullyQualifiedName() + "\"). Usage of the 'args' constraints is not applicable to the " + this.getFullyQualifiedName() + " injection point.");
            }
            return new InvokeInjectionPointSelector(constraint);
        }
    };

    @NotNull
    private final InjectionPointTargetConstraint constraint;

    private InvokeInjectionPointSelector(@NotNull InjectionPointTargetConstraint constraint) {
        super("org.spongepowered.asm.mixin.injection.points.BeforeInvoke", "INVOKE");
        this.constraint = constraint;
    }

    @Override
    @Nullable
    public LabelNode getFirst(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from,
            @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        AbstractInsnNode insn = from == null ? method.instructions.getFirst() : from.getFirst(method, remapper, sharedBuilder);
        AbstractInsnNode guard = to == null ? method.instructions.getLast() : to.getAfterSelected(method, remapper, sharedBuilder);

        for (; insn != null && insn != guard; insn = insn.getNext()) {
            if (insn instanceof MethodInsnNode && this.constraint.isValid(insn, remapper, sharedBuilder)) {
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
    public Collection<LabelNode> getLabels(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from,
            @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        List<LabelNode> labels = new ArrayList<LabelNode>();
        AbstractInsnNode insn = from == null ? method.instructions.getFirst() : from.getFirst(method, remapper, sharedBuilder);
        AbstractInsnNode guard = to == null ? method.instructions.getLast() : to.getAfterSelected(method, remapper, sharedBuilder);

        for (; insn != null && insn != guard; insn = insn.getNext()) {
            if (insn instanceof MethodInsnNode && this.constraint.isValid(insn, remapper, sharedBuilder)) {
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
        return true;
    }
}
