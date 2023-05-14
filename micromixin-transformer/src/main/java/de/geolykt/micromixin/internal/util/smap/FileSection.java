package de.geolykt.micromixin.internal.util.smap;

import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileSection extends AbstractSMAPSection {

    public static class FileSectionEntry {
        private final int sourceId;
        @NotNull
        private final String sourceName;
        @Nullable
        private final String sourcePath;

        public FileSectionEntry(int sourceId, @NotNull String sourceName, @Nullable String sourcePath) {
            this.sourceId = sourceId;
            this.sourceName = sourceName;
            this.sourcePath = sourcePath;
        }

        @NotNull
        @Contract(mutates = "param1", pure = false, value = "!null -> param1; null -> fail")
        public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
            if (this.sourcePath == null) {
                sharedBuilder.append(this.sourceId).appendCodePoint(' ').append(this.sourceName);
            } else {
                sharedBuilder.append("+ ").append(this.sourceId).appendCodePoint(' ').append(this.sourceName)
                    .appendCodePoint('\r').append(this.sourcePath);
            }
            sharedBuilder.appendCodePoint('\r');
            return sharedBuilder;
        }
    }

    private final List<FileSectionEntry> entries;

    public FileSection(List<FileSectionEntry> entries) {
        this.entries = entries;
    }

    @Override
    @NotNull
    @Contract(mutates = "param1", pure = false, value = "!null -> param1; null -> fail")
    public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
        sharedBuilder.append("*F\r");
        for (FileSectionEntry entry : this.entries) {
            entry.pushContents(sharedBuilder);
        }
        return sharedBuilder;
    }
}
