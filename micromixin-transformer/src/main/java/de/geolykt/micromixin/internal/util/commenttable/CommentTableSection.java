package de.geolykt.micromixin.internal.util.commenttable;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface CommentTableSection {

    public int getPreferedWidth();

    @NotNull
    public List<String> getLines();
}
