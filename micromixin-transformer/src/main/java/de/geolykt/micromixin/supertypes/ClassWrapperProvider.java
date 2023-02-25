package de.geolykt.micromixin.supertypes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ClassWrapperProvider {

    // TODO request with modularity attachment
    @Nullable
    ClassWrapper provide(@NotNull String name, @NotNull ClassWrapperPool pool);
}
