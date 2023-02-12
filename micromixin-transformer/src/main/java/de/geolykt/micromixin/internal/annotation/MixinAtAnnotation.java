package de.geolykt.micromixin.internal.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.MixinParseException;

public class MixinAtAnnotation {

    @NotNull
    public final String value;
    @Nullable
    public final List<String> args;
    @Nullable
    public final ConstantSelector constantSelector;

    public MixinAtAnnotation(@NotNull String value, @Nullable List<String> args, @Nullable ConstantSelector constantSelector) {
        this.value = value;
        this.args = args;
        this.constantSelector = constantSelector;
    }

    @NotNull
    public static MixinAtAnnotation parse(@NotNull AnnotationNode atValue) throws MixinParseException {
        String value = null;
        List<String> args = null;
        ConstantSelector constantSelector = null;
        for (int i = 0; i < atValue.values.size(); i += 2) {
            String name = (String) atValue.values.get(i);
            Object val = atValue.values.get(i + 1);
            if (name.equals("value")) {
                value = ((String) val).toUpperCase(Locale.ROOT);
            } else if (name.equals("args")) {
                @SuppressWarnings("all")
                List<String> temp = (List<String>) val; // Temporary variable required to suppress all warnings caused by this "dangerous" cast
                args = temp;
            } else {
                throw new MixinParseException("Unimplemented key in @At: " + name);
            }
        }
        if (value == null) {
            throw new MixinParseException("The required field \"value\" is missing.");
        }
        // IMPLEMENT Rename FQN values to aliases (since the other way around would be ugly)
        if (args != null) {
            if (value.equals("CONSTANT")) {
                constantSelector = ConstantSelector.parse(args);
            }
            args = Collections.unmodifiableList(args);
        } else {
            // Verify that at-values that require arguments have arguments
            if (value.equals("CONSTANT")) {
                throw new MixinParseException("Broken mixin: No constant discriminator could be found in @At(\"" + value + "\") args");
            }
        }
        return new MixinAtAnnotation(value, args, constantSelector);
    }

    @NotNull
    private static LabelNode getNodeBefore(@NotNull AbstractInsnNode insn, @NotNull InsnList merge) {
        AbstractInsnNode lookbehind = insn.getPrevious();
        while (lookbehind.getOpcode() == -1 && !(lookbehind instanceof LabelNode)) {
            lookbehind = lookbehind.getPrevious();
            if (lookbehind == null) {
                // reached beginning of list (insn was first instruction - unlikely but possible with stuff like constants being used in an INVOKESTATIC context)
                LabelNode l = new LabelNode();
                merge.insert(l);
                return l;
            }
        }
        if (lookbehind instanceof LabelNode) {
            return (LabelNode) lookbehind;
        }
        LabelNode l = new LabelNode();
        merge.insertBefore(insn, l);
        return l;
    }

    @NotNull
    public Collection<LabelNode> getLabels(@NotNull MethodNode method) {
        // IMPLEMENT Hide mixin injector calls. The main part would be done through annotations (see the comment on CallbackInfo-chaining in MixinInjectAnnotation)
        switch (this.value) {
        case "RETURN": { // BeforeReturn
            List<LabelNode> returns = new ArrayList<>();
            for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                switch (insn.getOpcode()) {
                case Opcodes.IRETURN:
                case Opcodes.LRETURN:
                case Opcodes.FRETURN:
                case Opcodes.DRETURN:
                case Opcodes.ARETURN:
                case Opcodes.RETURN:
                    returns.add(getNodeBefore(insn, method.instructions));
                }
            }
            return returns;
        }
        case "TAIL": {// BeforeLastReturn
            for (AbstractInsnNode insn = method.instructions.getLast(); insn != null; insn = insn.getPrevious()) {
                switch (insn.getOpcode()) {
                case Opcodes.IRETURN:
                case Opcodes.LRETURN:
                case Opcodes.FRETURN:
                case Opcodes.DRETURN:
                case Opcodes.ARETURN:
                case Opcodes.RETURN:
                    return Collections.singletonList(getNodeBefore(insn, method.instructions));
                }
            }
        }
        case "CONSTANT": {// BeforeConstant
            ConstantSelector selector = constantSelector;
            if (selector == null) {
                throw new InternalError();
            }
            List<@NotNull AbstractInsnNode> selected = new ArrayList<>();
            for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (selector.matchesConstant(insn)) {
                    selected.add(insn);
                }
            }
            // IMPLEMENT fetch ordinal here
            List<LabelNode> labels = new ArrayList<>();
            for (AbstractInsnNode insn : selected) {
                labels.add(getNodeBefore(insn, method.instructions));
            }
            return labels;
        }
        case "HEAD": {
            for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (insn.getOpcode() != -1) {
                    LabelNode temp = new LabelNode();
                    method.instructions.insertBefore(temp, insn);
                    return Collections.<@NotNull LabelNode>singletonList(temp);
                } else if (insn instanceof LabelNode) {
                    @SuppressWarnings("null")
                    LabelNode temp = (@NotNull LabelNode) insn; // I don't quite understand why that hack is necessary, but whatever floats your boat...
                    return Collections.<@NotNull LabelNode>singletonList(temp);
                }
            }
            // There are no instructions in the list
            LabelNode temp = new LabelNode();
            method.instructions.insert(temp);
            return Collections.<@NotNull LabelNode>singletonList(temp);
        }
        default:
            throw new MixinParseException("Unimplemented @At-value: " + this.value);
        }
    }

    public boolean supportsConstructors() {
        return value.equals("TAIL");
    }
}
