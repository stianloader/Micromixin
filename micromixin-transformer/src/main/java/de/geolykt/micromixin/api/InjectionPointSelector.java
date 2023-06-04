package de.geolykt.micromixin.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.SimpleRemapper;
import de.geolykt.micromixin.internal.annotation.MixinAtAnnotation;

public abstract class InjectionPointSelector {

    @NotNull
    public final String fullyQualifiedName;

    @NotNull
    public final Set<String> allNames;

    public InjectionPointSelector(@NotNull String fqn, @NotNull Collection<String> allNames) {
        this.fullyQualifiedName = fqn;
        Set<String> names = new HashSet<String>(allNames);
        names.add(fqn);
        this.allNames = Collections.unmodifiableSet(names);
    }

    public InjectionPointSelector(@NotNull String fqn, @NotNull String... aliases) {
        this(fqn, Arrays.asList(aliases));
    }

    /**
     * Obtains the {@link LabelNode LabelNodes} that are before every applicable entrypoint within
     * the provided method as defined by this {@link MixinAtAnnotation}.
     * If no {@link LabelNode} is immediately before the selected instruction(s), the label is created and added
     * to the method's {@link InsnList}. However, as labels do not exist in JVMS-compliant bytecode (instead
     * offsets are used), doing so will not create bloat in the resulting class file.
     *
     * <p>Only "pseudo"-instructions may be between the selected instruction and the label.
     * Pseudo-instructions are instructions where {@link AbstractInsnNode#getOpcode()} returns {@value -1}.
     *
     * @param method The method to find the entrypoints in.
     * @param remapper The remapper instance to make use of. This is used to remap any references of the mixin class to the target class when applying injection point constraints.
     * @param sharedBuilder Shared {@link StringBuilder} instance to reduce {@link StringBuilder} allocations.
     * @return The selected or generated labels that are before the matched instruction(-s).
     */
    @NotNull
    public abstract Collection<LabelNode> getLabels(@NotNull MethodNode method, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder);

    /**
     * Checks whether the injection point can be used in conjunction with the Redirect-annotation.
     * Generally should only be true if this injection point selector can select method instruction nodes.
     *
     * @return True if usable in redirects, false otherwise.
     */
    public abstract boolean supportsRedirect();
}
