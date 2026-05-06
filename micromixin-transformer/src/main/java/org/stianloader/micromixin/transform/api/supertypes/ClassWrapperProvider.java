package org.stianloader.micromixin.transform.api.supertypes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ClassWrapperProvider {

    // TODO request with modularity attachment (probably not possible due to how locals capture works - this task would be more involved)
    @Nullable
    ClassWrapper provide(@NotNull String name, @NotNull ClassWrapperPool pool);
}
