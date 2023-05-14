package de.geolykt.micromixin.internal.util.commenttable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class StringTableSection implements CommentTableSection {

    @NotNull
    private final List<String> lines;
    private final int maxWidth;

    @SuppressWarnings("null")
    public StringTableSection(@NotNull List<String> lines) {
        this.lines = Collections.unmodifiableList(new ArrayList<String>(lines));
        int maxWidth = 0;
        for (String line : this.lines) {
            maxWidth = Math.max(maxWidth, line.length());
        }
        this.maxWidth = maxWidth;
    }

    @Override
    public int getPreferedWidth() {
        return this.maxWidth;
    }

    @Override
    @NotNull
    public List<String> getLines() {
        return lines;
    }
}
