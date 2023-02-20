package de.geolykt.micromixin.internal.util;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.micromixin.internal.MemberDesc;

/**
 * A MemberRenameMap stores the remapper state for class member renames.
 * Targets include methods or fields.
 *
 * This class is different between the java releases as Java 6 does not support
 * Map#getOrDefault or Map#putIfAbsent. To improve (potential) performance in the
 * time-critical remapping process, Map#getOrDefault and Map#putIfAbsent are being
 * used starting from Java 9 (earlier is not supported due to multi-release having
 * been introduced with Java 9).
 */
public class MemberRenameMap {

    private final Map<MemberDesc, String> renames = new HashMap<MemberDesc, String>();

    public MemberRenameMap() {
    }

    public void clear() {
        renames.clear();
    }

    @Nullable
    public String get(@NotNull String owner, @NotNull String descriptor, @NotNull String oldName) {
        return renames.get(new MemberDesc(owner, oldName, descriptor));
    }

    @NotNull
    public String getOrDefault(@NotNull String owner, @NotNull String descriptor, @NotNull String oldName, @NotNull String defaultValue) {
        String o = renames.get(new MemberDesc(owner, oldName, descriptor));
        if (o == null) {
            return defaultValue;
        }
        return o;
    }

    @SuppressWarnings("null")
    @NotNull
    public String optGet(@NotNull String owner, @NotNull String descriptor, @NotNull String oldName) {
        String o = renames.get(new MemberDesc(owner, oldName, descriptor));
        if (o == null) {
            return oldName;
        }
        return o;
    }

    public void put(@NotNull String owner, @NotNull String descriptor, @NotNull String name, @NotNull String newName) {
        MemberDesc ref = new MemberDesc(owner, name, descriptor);
        String oldMapping = renames.get(ref);
        if (oldMapping == null) {
            renames.put(ref, Objects.requireNonNull(newName, "newName cannot be null."));
        } else if (!oldMapping.equals(newName)) {
            throw new IllegalStateException("Overriding member rename for member " + ref.toString());
        }
    }

    public void remove(@NotNull String owner, @NotNull String desc, @NotNull String name) {
        renames.remove(new MemberDesc(owner, name, desc));
    }

    public int size() {
        return renames.size();
    }

    public void putAllIfAbsent(MemberRenameMap other) {
        for (Map.Entry<MemberDesc, String> entry : other.renames.entrySet()) {
            if (!this.renames.containsKey(entry.getKey())) {
                this.renames.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
