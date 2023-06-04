package de.geolykt.micromixin.internal.selectors.inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.SimpleRemapper;
import de.geolykt.micromixin.api.InjectionPointSelector;
import de.geolykt.micromixin.api.InjectionPointSelectorFactory.InjectionPointSelectorProvider;
import de.geolykt.micromixin.api.InjectionPointTargetConstraint;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.util.ASMUtil;

public class ReturnInjectionPointSelector extends InjectionPointSelector implements InjectionPointSelectorProvider {

    @NotNull
    public static final ReturnInjectionPointSelector INSTANCE = new ReturnInjectionPointSelector();

    private ReturnInjectionPointSelector() {
        super("org.spongepowered.asm.mixin.injection.points.BeforeReturn", "RETURN");
    }

    @Override
    @NotNull
    public Collection<LabelNode> getLabels(@NotNull MethodNode method, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        List<LabelNode> returns = new ArrayList<LabelNode>();
        for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            switch (insn.getOpcode()) {
            case Opcodes.IRETURN:
            case Opcodes.LRETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.ARETURN:
            case Opcodes.RETURN:
                returns.add(ASMUtil.getLabelNodeBefore(insn, method.instructions));
            }
        }
        return returns;
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
