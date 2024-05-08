package org.stianloader.micromixin.transform.internal;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        return this.owner.hashCode() ^ this.name.hashCode() ^ this.desc.hashCode();
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
        return this.owner + "." + this.name + " " + this.desc;
    }

    @NotNull
    @Contract(pure = true, value = "_, _, _, _, _ -> new")
    public static MemberDesc remappedMethod(@NotNull String owner, @NotNull String name,
            @NotNull String desc, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        return new MemberDesc(remapper.getRemappedClassName(owner),
                remapper.getRemappedMethodName(owner, name, desc),
                remapper.getRemappedMethodDescriptor(desc, sharedBuilder));
    }

    @NotNull
    @Contract(pure = true, value = "_, _, _, _, _ -> new")
    public static MemberDesc remappedField(@NotNull String owner, @NotNull String name,
            @NotNull String desc, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        return new MemberDesc(remapper.getRemappedClassName(owner),
                remapper.getRemappedFieldName(owner, name, desc),
                remapper.getRemappedFieldDescriptor(desc, sharedBuilder));
    }

    @NotNull
    @Contract(pure = true, value = "null, _ -> fail; _, null -> fail; !null, !null -> new")
    public MemberDesc remap(@NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        if (this.desc.codePointAt(0) == '(') {
            return MemberDesc.remappedMethod(this.owner, this.name, this.desc, remapper, sharedBuilder);
        } else {
            return MemberDesc.remappedField(this.owner, this.name, this.desc, remapper, sharedBuilder);
        }
    }
}
