package org.stianloader.micromixin.api;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stianloader.micromixin.internal.util.Objects;

public class InjectionPointSelectorFactory {

    public static interface InjectionPointSelectorProvider {
        @NotNull
        String getFullyQualifiedName();
        @NotNull
        Set<String> getAllNames();

        @NotNull
        InjectionPointSelector create(@Nullable List<String> args, @Nullable InjectionPointTargetConstraint constraint);
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
