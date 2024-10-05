package org.stianloader.micromixin.transform.internal.util.smap;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class FutureSection extends AbstractSMAPSection {
    private final char otherChar;
    @NotNull
    private final List<String> futureInfos;

    public FutureSection(char otherChar, @NotNull List<String> futureInfos) {
        this.otherChar = otherChar;
        this.futureInfos = Objects.requireNonNull(futureInfos, "Argument 'futureInfos' may not be null");
    }

    @Override
    @NotNull
    public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
        sharedBuilder.append('*').append(this.otherChar).append('\r');
        for (String futureInfo : this.futureInfos) {
            sharedBuilder.append(futureInfo).append('\r');
        }
        return sharedBuilder;
    }
}
