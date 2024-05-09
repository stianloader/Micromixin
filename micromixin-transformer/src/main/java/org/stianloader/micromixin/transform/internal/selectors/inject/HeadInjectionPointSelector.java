package org.stianloader.micromixin.transform.internal.selectors.inject;

import java.util.Collection;
import java.util.Collections;
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
import org.stianloader.micromixin.transform.internal.util.ASMUtil;

public class HeadInjectionPointSelector extends InjectionPointSelector implements InjectionPointSelectorProvider {

    @NotNull
    public static final HeadInjectionPointSelector INSTANCE = new HeadInjectionPointSelector();

    private HeadInjectionPointSelector() {
        super("org.spongepowered.asm.mixin.injection.points.MethodHead", "HEAD");
    }

    @Override
    @Nullable
    public AbstractInsnNode getFirstInsn(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from,
            @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        if (from == null) {
            AbstractInsnNode firstInsn = method.instructions.getFirst();
            if (firstInsn.getOpcode() != -1) {
                return firstInsn;
            }
            return ASMUtil.getNext(method.instructions.getFirst());
        } else {
            return from.getFirstInsn(method, remapper, sharedBuilder);
        }
    }

    @Override
    @NotNull
    public Collection<? extends AbstractInsnNode> getMatchedInstructions(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from,
            @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        return Collections.singletonList(this.getFirstInsn(method, from, to, remapper, sharedBuilder));
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
