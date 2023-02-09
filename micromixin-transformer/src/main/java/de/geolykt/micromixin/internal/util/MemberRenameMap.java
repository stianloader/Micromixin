package de.geolykt.micromixin.internal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.geolykt.micromixin.internal.MemberDesc;

public class MemberRenameMap {

    private final Map<MemberDesc, String> renames = new HashMap<>();

    public MemberRenameMap() {
    }

    public void clear() {
        renames.clear();
    }

    @Nullable
    public String get(@NotNull String owner, @NotNull String descriptor, @NotNull String oldName) {
        return renames.get(new MemberDesc(owner, oldName, descriptor));
    }

    @SuppressWarnings("null")
    @NotNull
    public String getOrDefault(@NotNull String owner, @NotNull String descriptor, @NotNull String oldName, @NotNull String defaultValue) {
        return renames.getOrDefault(new MemberDesc(owner, oldName, descriptor), defaultValue);
    }

    @SuppressWarnings("null")
    @NotNull
    public String optGet(@NotNull String owner, @NotNull String descriptor, @NotNull String oldName) {
        return renames.getOrDefault(new MemberDesc(owner, oldName, descriptor), oldName);
    }

    public void put(@NotNull String owner, @NotNull String descriptor, @NotNull String name, @NotNull String newName) {
        MemberDesc ref = new MemberDesc(owner, descriptor, name);
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
        other.renames.forEach(this.renames::putIfAbsent);
    }
}
