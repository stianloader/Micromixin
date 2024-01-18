package org.stianloader.micromixin.internal.util.commenttable;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TabularTableSection implements CommentTableSection {

    @NotNull
    private final List<String[]> rows = new ArrayList<String[]>();
    private int maxColumns;

    @NotNull
    @Contract(mutates = "this", pure = false, value = "null -> fail; !null -> this")
    public TabularTableSection addRow(@NotNull String... rowEntries) {
        this.maxColumns = Math.max(this.maxColumns, rowEntries.length);
        this.rows.add(rowEntries);
        return this;
    }

    @Contract(pure = true)
    public int getColumnWidth(int columnId) {
        int width = 0;
        for (String[] row : this.rows) {
            if (columnId < row.length) {
                width = Math.max(width, row[columnId].length());
            }
        }
        return width;
    }

    @Contract(pure = true)
    public int getColumnCount() {
        return this.maxColumns;
    }

    @NotNull
    @Contract(pure = true, value = "-> new")
    public int[] getColumnWidths() {
        int[] widths = new int[this.maxColumns];
        for (String[] row : this.rows) {
            for (int i = 0; i < row.length; i++) {
                widths[i] = Math.max(widths[i], row[i].length());
            }
        }
        return widths;
    }

    @Contract(pure = true)
    public int getRowCount() {
        return this.rows.size();
    }

    @Contract(pure = true, value = "null -> fail; !null -> _")
    private int getPreferedWidth(@NotNull int[] widths) {
        int width = 0;
        for (int columnWidth : widths) {
            width += columnWidth + 1;
        }
        if (width != 0) {
            width--;
        }
        return width;
    }

    @Override
    @Contract(pure = true)
    public int getPreferedWidth() {
        return this.getPreferedWidth(this.getColumnWidths());
    }

    @Override
    @NotNull
    @Contract(pure = true, value = "-> new")
    public List<String> getLines() {
        int[] cwidths = this.getColumnWidths();
        StringBuilder builder = new StringBuilder(this.getPreferedWidth(cwidths) + 1);
        List<String> lines = new ArrayList<String>();
        for (String[] row : this.rows) {
            builder.setLength(0);
            for (int i = 0; i < row.length; i++) {
                String cell = row[i];
                builder.append(cell);
                int count = cwidths[i] - cell.length();
                while (count-- != 0) {
                    // TODO Sometimes we need to left-adjust instead of right-adjust.
                    // Why the spongeian mixin implementation does it is beyond me though.
                    builder.append(' ');
                }
                builder.append(' ');
            }
            if (row.length != 0) {
                builder.setLength(builder.length() - 1);
            }
            lines.add(builder.toString());
        }
        return lines;
    }
}
