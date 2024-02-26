package org.stianloader.micromixin.transform.internal.selectors.inject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.SimpleRemapper;
import org.stianloader.micromixin.transform.api.InjectionPointSelector;
import org.stianloader.micromixin.transform.api.InjectionPointTargetConstraint;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.api.InjectionPointSelectorFactory.InjectionPointSelectorProvider;
import org.stianloader.micromixin.transform.internal.MixinParseException;

public class HeadInjectionPointSelector extends InjectionPointSelector implements InjectionPointSelectorProvider {

    @NotNull
    public static final HeadInjectionPointSelector INSTANCE = new HeadInjectionPointSelector();

    private HeadInjectionPointSelector() {
        super("org.spongepowered.asm.mixin.injection.points.MethodHead", "HEAD");
    }

    @Override
    @Nullable
    public LabelNode getFirst(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from,
            @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        for (AbstractInsnNode insn = from == null ? method.instructions.getFirst() : from.getFirst(method, remapper, sharedBuilder); insn != null; insn = insn.getNext()) {
            if (insn.getOpcode() != -1) {
                LabelNode temp = new LabelNode();
                method.instructions.insertBefore(temp, insn);
                return temp;
            } else if (insn instanceof LabelNode) {
                @SuppressWarnings("null")
                LabelNode temp = (LabelNode) insn; // I don't quite understand why that hack is necessary, but whatever floats your boat...
                return temp;
            }
        }
        // There are no instructions in the list
        LabelNode temp = new LabelNode();
        method.instructions.insert(temp);
        return temp;
    }

    @Override
    @NotNull
    public Collection<LabelNode> getLabels(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from,
            @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        return Collections.singletonList(this.getFirst(method, from, to, remapper, sharedBuilder));
    }

    @Override
    public boolean supportsRedirect() {
        return false;
    }

    @Override
    @NotNull
    public String getFullyQualifiedName() {
        return this.fullyQualifiedName;
    }

    @Override
    @NotNull
    public Set<String> getAllNames() {
        return this.allNames;
    }

    @Override
    @NotNull
    public InjectionPointSelector create(@Nullable List<String> args, @Nullable InjectionPointTargetConstraint constraint) {
        if (constraint != null) {
            throw new MixinParseException("Broken mixin: Superfluous discriminator found in @At(\"" + this.getFullyQualifiedName() + "\"). Usage of the 'target' or 'desc' constraints is not applicable to the " + this.getFullyQualifiedName() + " injection point.");
        }
        if (args != null) {
            throw new MixinParseException("Broken mixin: Superfluous discriminator found in @At(\"" + this.getFullyQualifiedName() + "\"). Usage of the 'args' constraints is not applicable to the " + this.getFullyQualifiedName() + " injection point.");
        }
        return this;
    }
}
