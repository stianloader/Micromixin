package de.geolykt.micromixin.internal.selectors.inject;

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

import de.geolykt.micromixin.SimpleRemapper;
import de.geolykt.micromixin.api.InjectionPointSelector;
import de.geolykt.micromixin.api.InjectionPointSelectorFactory.InjectionPointSelectorProvider;
import de.geolykt.micromixin.api.InjectionPointTargetConstraint;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.util.ASMUtil;

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
    @NotNull
    public Collection<LabelNode> getLabels(@NotNull MethodNode method, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        // IMPLEMENT fetch ordinal
        List<LabelNode> labels = new ArrayList<LabelNode>();
        for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn instanceof MethodInsnNode && this.constraint.isValid(insn, remapper, sharedBuilder)) {
                labels.add(ASMUtil.getLabelNodeBefore(insn, method.instructions));
            }
        }
        return labels;
    }

    @Override
    public boolean supportsRedirect() {
        return true;
    }
}
