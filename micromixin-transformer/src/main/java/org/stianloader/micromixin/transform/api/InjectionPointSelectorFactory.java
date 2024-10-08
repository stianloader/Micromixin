package org.stianloader.micromixin.transform.api;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.MethodInsnNode;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class InjectionPointSelectorFactory {

    public static interface InjectionPointSelectorProvider {
        @NotNull
        String getFullyQualifiedName();
        @NotNull
        Set<String> getAllNames();

        /**
         * Create an {@link InjectionPointSelector} that selects the instructions
         * with the provided constraints. The selector is optionally further constrained by the arguments
         * defined in the <code>&#64;At</code> annotation.
         *
         * @param args The arguments defined in the <code>&#64;At</code> annotation, null if no arguments were defined. May be empty.
         * @param constraints The constraints defined in the <code>&#64;At</code> annotation. Any instruction matched by the selector must match all constraints.
         * These constraints can limit the selected opcodes, constrain on {@link MethodInsnNode#name} and more.
         * @return The selector using the requested semantics.
         * @since 0.7.0-a20241008
         */
        @NotNull
        @ApiStatus.AvailableSince("0.7.0-a20241008")
        @Contract(pure = true)
        InjectionPointSelector create(@Nullable List<String> args, @NotNull InjectionPointConstraint[] constraints);
    }

    @NotNull
    private Map<String, InjectionPointSelectorProvider> providers = new HashMap<String, InjectionPointSelectorProvider>();

    @NotNull
    @Contract(mutates = "this", pure = false, value = "null -> fail; !null -> this")
    public InjectionPointSelectorFactory register(@NotNull InjectionPointSelectorProvider provider) {
        if (!provider.getAllNames().contains(Objects.requireNonNull(provider, "provider may not be null").getFullyQualifiedName())) {
            throw new IllegalArgumentException("The fully qualified name of the injection point must be present in the list of all names for said injection point.");
        }
        for (String name : provider.getAllNames()) {
            this.providers.put(name.toUpperCase(Locale.ROOT), provider);
        }
        return this;
    }

    @Nullable
    @Contract(pure = true)
    public InjectionPointSelectorProvider getOpt(@NotNull String name) {
        return this.providers.get(name);
    }

    @NotNull
    @Contract(pure = true)
    public InjectionPointSelectorProvider get(@NotNull String name) {
        InjectionPointSelectorProvider selector = this.providers.get(name);
        if (selector != null) {
            return selector;
        }
        throw new IllegalStateException("No injection point selector provider found for name \"" + name + "\"");
    }
}
