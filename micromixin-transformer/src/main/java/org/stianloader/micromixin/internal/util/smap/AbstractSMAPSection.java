package org.stianloader.micromixin.internal.util.smap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSMAPSection {
    @Override
    public String toString() {
        return this.pushContents(new StringBuilder()).toString();
    }

    @NotNull
    @Contract(mutates = "param1", pure = false, value = "!null -> param1; null -> fail")
    public abstract StringBuilder pushContents(@NotNull StringBuilder sharedBuilder);
}
