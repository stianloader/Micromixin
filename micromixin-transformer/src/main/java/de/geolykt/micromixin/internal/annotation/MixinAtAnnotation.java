package de.geolykt.micromixin.internal.annotation;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.SimpleRemapper;
import de.geolykt.micromixin.api.InjectionPointSelector;
import de.geolykt.micromixin.api.InjectionPointSelectorFactory;
import de.geolykt.micromixin.api.InjectionPointTargetConstraint;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.selectors.DescSelector;
import de.geolykt.micromixin.internal.selectors.StringSelector;
import de.geolykt.micromixin.internal.selectors.inject.TailInjectionPointSelector;
import de.geolykt.micromixin.internal.util.Objects;

public class MixinAtAnnotation {

    @NotNull
    public final String value;
    @NotNull
    public final InjectionPointSelector injectionPointSelector;

    public MixinAtAnnotation(@NotNull String value, @NotNull InjectionPointSelector selector) {
        this.value = value;
        this.injectionPointSelector = selector;
    }

    @NotNull
    public static MixinAtAnnotation parse(@NotNull ClassNode mixinSource, @NotNull AnnotationNode atValue, @NotNull InjectionPointSelectorFactory factory) throws MixinParseException {
        String value = null;
        List<String> args = null;
        InjectionPointTargetConstraint constraint = null;
        for (int i = 0; i < atValue.values.size(); i += 2) {
            String name = (String) atValue.values.get(i);
            Object val = atValue.values.get(i + 1);
            if (name.equals("value")) {
                value = ((String) val).toUpperCase(Locale.ROOT);
            } else if (name.equals("args")) {
                @SuppressWarnings("all")
                List<String> temp = (List<String>) val; // Temporary variable required to suppress all warnings caused by this "dangerous" cast
                args = temp;
            } else if (name.equals("target")) {
                constraint = new StringSelector(((String) Objects.requireNonNull(val)));
            } else if (name.equals("desc")) {
                constraint = new DescSelector(MixinDescAnnotation.parse(mixinSource, "()V", Objects.requireNonNull((AnnotationNode) val)));
            } else {
                throw new MixinParseException("Unimplemented key in @At: " + name);
            }
        }
        if (value == null) {
            throw new MixinParseException("The required field \"value\" is missing.");
        }
        return new MixinAtAnnotation(value, factory.get(value).create(args, constraint));
    }

    /**
     * Obtains the {@link LabelNode LabelNodes} that are before every applicable entrypoint within
     * the provided method as defined by this {@link MixinAtAnnotation}.
     * If no {@link LabelNode} is immediately before the selected instruction(s), the label is created and added
     * to the method's {@link InsnList}. However, as labels do not exist in JVMS-compliant bytecode (instead
     * offsets are used), doing so will not create bloat in the resulting class file.
     *
     * <p>Only "pseudo"-instructions may be between the selected instruction and the label.
     * Pseudo-instructions are instructions where {@link AbstractInsnNode#getOpcode()} returns -1.
     *
     * @param method The method to find the entrypoints in.
     * @param remapper The remapper instance to make use of. This is used to remap any references of the mixin class to the target class when applying injection point constraints.
     * @param sharedBuilder Shared {@link StringBuilder} instance to reduce {@link StringBuilder} allocations.
     * @return The selected or generated labels that are before the matched instruction(-s).
     */
    @NotNull
    public Collection<LabelNode> getLabels(@NotNull MethodNode method, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        // IMPLEMENT Hide mixin injector calls. The main part would be done through annotations (see the comment on CallbackInfo-chaining in MixinInjectAnnotation)
        // Also be aware that @At is used in much more than just @Inject, but also @Redirect among others.
        return this.injectionPointSelector.getLabels(method, remapper, sharedBuilder);
    }

    public boolean supportsConstructors() {
        return this.injectionPointSelector == TailInjectionPointSelector.INSTANCE;
    }

    public boolean supportsRedirect() {
        return this.injectionPointSelector.supportsRedirect();
    }
}
