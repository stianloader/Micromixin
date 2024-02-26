package org.stianloader.micromixin.transform.internal.util.commenttable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class KeyValueTableSection implements CommentTableSection {

    private int keyLength;
    private int valueLength;
    private final Map<String, String> pairs = new LinkedHashMap<String, String>();

    @NotNull
    @Contract(mutates = "this", pure = false, value = "null, _ -> fail; _, null -> fail; !null, !null -> this")
    public KeyValueTableSection add(@NotNull String key, @NotNull String value) {
        keyLength = Math.max(keyLength, key.length());
        valueLength = Math.max(valueLength, value.length());
        pairs.put(key, value);
        return this;
    }

    @Override
    public int getPreferedWidth() {
        return keyLength + valueLength + 3;
    }

    @Override
    @NotNull
    public List<String> getLines() {
        StringBuilder builder = new StringBuilder(this.getPreferedWidth());
        List<String> lines = new ArrayList<String>();
        for (Map.Entry<String, String> pair : this.pairs.entrySet()) {
            builder.setLength(0);
            String key = pair.getKey();
            int count = keyLength - key.length();
            while (count-- != 0) {
                builder.append(' ');
            }
            builder.append(key).append(" : ").append(pair.getValue());
            lines.add(builder.toString());
        }
        return lines;
    }
}
