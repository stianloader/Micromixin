package org.stianloader.micromixin.transform.internal.util.smap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class EndSection extends AbstractSMAPSection {

    @NotNull
    public static final EndSection INSTANCE = new EndSection();

    private EndSection() { }

    @Override
    @NotNull
    @Contract(mutates = "param1", pure = false, value = "!null -> param1; null -> fail")
    public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
        sharedBuilder.append("*E\r");
        return sharedBuilder;
    }
}
