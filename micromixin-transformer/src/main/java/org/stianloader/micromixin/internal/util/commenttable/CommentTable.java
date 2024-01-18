package org.stianloader.micromixin.internal.util.commenttable;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.stianloader.micromixin.internal.util.Objects;

public class CommentTable {

    @NotNull
    private static final String EOL_STRING = " */" + System.getProperty("line.separator");

    @NotNull
    private static final String EXTRA_EOL_STRING = "**/" + System.getProperty("line.separator");

    private static final int EXTRA_STRING_LEN = 3 + EXTRA_EOL_STRING.length();

    @NotNull
    private final List<CommentTableSection> sections = new ArrayList<CommentTableSection>();

    @NotNull
    @Contract(mutates = "this", pure = false, value = "null -> fail; !null -> this")
    public CommentTable addSection(@NotNull CommentTableSection sect) {
        sections.add(Objects.requireNonNull(sect));
        return this;
    }

    @NotNull
    public String toString() {
        int maxWidth = 0;
        for (CommentTableSection sect : this.sections) {
            maxWidth = Math.max(maxWidth, sect.getPreferedWidth());
        }
        StringBuilder builder = new StringBuilder();
        StringBuilder sectionSeparator = new StringBuilder(maxWidth + EXTRA_STRING_LEN);
        sectionSeparator.append("/**");
        for (int i = 0; i < maxWidth; i++) {
            sectionSeparator.append('*');
        }
        sectionSeparator.append(EXTRA_EOL_STRING);
        for (CommentTableSection sect : this.sections) {
            builder.append(sectionSeparator);
            for (String line : sect.getLines()) {
                int count = maxWidth - line.length();
                if (count < 0) {
                    throw new IllegalStateException("The reported prefered width of the section of instance " + sect.getClass() + " is smaller than the actual longest line! Reported: " + sect.getPreferedWidth() + "; Actual: " + line.length());
                }
                builder.append("/* ").append(line);
                while (count-- != 0) {
                    builder.append(' ');
                }
                builder.append(EOL_STRING);
            }
        }
        builder.append(sectionSeparator);
        return builder.toString();
    }
}
