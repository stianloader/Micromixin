package org.stianloader.micromixin.transform.internal.util.smap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class StratumSection extends AbstractSMAPSection {
    @NotNull
    private final String stratumId;

    public StratumSection(@NotNull String stratumId) {
        this.stratumId = stratumId;
    }

    @Contract(pure = true)
    public String getStrataId() {
        return this.stratumId;
    }

    @NotNull
    @Contract(mutates = "param1", pure = false, value = "!null -> param1; null -> fail")
    public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
        sharedBuilder.append("*S ").append(this.stratumId).appendCodePoint('\r');
        return sharedBuilder;
    }
}
