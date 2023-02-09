package de.geolykt.micromixin.internal;

import org.jetbrains.annotations.NotNull;

import de.geolykt.micromixin.internal.util.Remapper;

public class MemberDesc {

    @NotNull
    public final String owner;
    @NotNull
    public final String name;
    @NotNull
    public final String desc;

    public MemberDesc(@NotNull String owner, @NotNull String name, @NotNull String desc) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public int hashCode() {
        return owner.hashCode() ^ name.hashCode() ^ desc.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MemberDesc)) {
            return false;
        }
        MemberDesc other = (MemberDesc) obj;
        return this.owner.equals(other.owner) && this.name.equals(other.name) && this.desc.equals(other.desc);
    }

    @Override
    public String toString() {
        return owner + "." + name + " " + desc;
    }

    @NotNull
    public static MemberDesc remappedMethod(@NotNull String owner, @NotNull String name,
            @NotNull String desc, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        return new MemberDesc(remapper.getRemappedClassName(name),
                remapper.methodRenames.optGet(owner, desc, name),
                remapper.getRemappedMethodDescriptor(desc, sharedBuilder));
    }

    @NotNull
    public static MemberDesc remappedField(@NotNull String owner, @NotNull String name,
            @NotNull String desc, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        return new MemberDesc(remapper.getRemappedClassName(name),
                remapper.fieldRenames.optGet(owner, desc, name),
                remapper.getRemappedFieldDescriptor(desc, sharedBuilder));
    }
}
