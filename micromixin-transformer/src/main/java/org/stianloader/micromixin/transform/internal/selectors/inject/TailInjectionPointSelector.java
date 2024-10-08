package org.stianloader.micromixin.transform.internal.selectors.inject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.InjectionPointSelector;
import org.stianloader.micromixin.transform.api.InjectionPointSelectorFactory.InjectionPointSelectorProvider;
import org.stianloader.micromixin.transform.api.InjectionPointConstraint;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;

public class TailInjectionPointSelector extends InjectionPointSelector implements InjectionPointSelectorProvider {

    @NotNull
    public static final TailInjectionPointSelector INSTANCE = new TailInjectionPointSelector();

    private TailInjectionPointSelector() {
        super("org.spongepowered.asm.mixin.injection.points.BeforeFinalReturn", "TAIL");
    }

    @Override
    @Nullable
    public AbstractInsnNode getFirstInsn(@NotNull MethodNode method, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        AbstractInsnNode guard = from == null ? method.instructions.getFirst() : from.getFirstInsn(method, remapper, sharedBuilder);
        AbstractInsnNode insn = to == null ? method.instructions.getLast() : to.getFirstInsn(method, remapper, sharedBuilder);

        for (; insn != null; insn = insn.getPrevious()) {
            if (ASMUtil.isReturn(insn.getOpcode())) {
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

        // For one reason or another, BeforeFinalReturn throws an exception if there are no xRETURN instructions
        // under the spongeian mixin implementation.
        // As such, micromixin-transformer will mirror this behaviour for the time being.
        throw new IllegalStateException("Injection point selector TAIL (also known as " + this.getFullyQualifiedName() + ") was given a slice where within it no RETURN opcode was present. Please validate your slices, they are likely to be wrong.");
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
    @ApiStatus.AvailableSince("0.7.0-a20241008")
    @Contract(pure = true)
    public InjectionPointSelector create(@Nullable List<String> args, @NotNull InjectionPointConstraint[] constraints) {
        if (constraints.length != 0) {
            throw new MixinParseException("Broken mixin: Superfluous discriminator found in @At(\"" + this.getFullyQualifiedName() + "\"). Usage of the 'target' or 'desc' constraints is not applicable to the CONSTANT injection point.");
        }
        if (args != null) {
            throw new MixinParseException("Broken mixin: Superfluous discriminator found in @At(\"" + this.getFullyQualifiedName() + "\"). Usage of the 'args' constraints is not applicable to the " + this.getFullyQualifiedName() + " injection point.");
        }
        return this;
    }
}
