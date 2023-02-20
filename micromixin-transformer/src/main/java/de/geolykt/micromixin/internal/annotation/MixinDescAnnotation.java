package de.geolykt.micromixin.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import de.geolykt.micromixin.internal.MemberDesc;
import de.geolykt.micromixin.internal.MixinParseException;

public class MixinDescAnnotation {

    @Nullable
    public final Type owner;
    @NotNull
    public final String value;
    @Nullable
    public final Type ret;
    @NotNull
    public final MemberDesc target;

    private MixinDescAnnotation(@Nullable Type owner, @NotNull String value, @Nullable Type ret, @NotNull MemberDesc target) {
        this.owner = owner;
        this.value = value;
        this.ret = ret;
        this.target = target;
    }

    @NotNull
    public static MixinDescAnnotation parse(@NotNull ClassNode node, @NotNull String fallbackMethodDesc, @NotNull AnnotationNode atValue) {
        String value = null;
        Type ret = null;
        Type owner = null;
        for (int i = 0; i < atValue.values.size(); i += 2) {
            String name = (String) atValue.values.get(i);
            Object val = atValue.values.get(i + 1);
            if (name.equals("value")) {
                value = (String) val;
            } else if (name.equals("ret")) {
                ret = (Type) val;
            } else if (name.equals("owner")) {
                owner = (Type) val;
            } else {
                throw new MixinParseException("Unimplemented key in @Desc: " + name);
            }
        }
        if (value == null) {
            throw new MixinParseException("The required field \"value\" is missing.");
        }
        if (value.contains("(") || value.contains(".")) { // Better be safe than sorry
            // The spongeian mixin implementation doesn't complain about this case, but it also doesn't work as intended in that case
            throw new MixinParseException("[(.] present in \"value\"");
        }
        // IMPLEMENT args
        String splicedMethodDesc = fallbackMethodDesc;
        if (ret != null) {
            splicedMethodDesc = splicedMethodDesc.substring(0, splicedMethodDesc.lastIndexOf(')') + 1) + ret.getDescriptor();
        }
        String ownerName;
        if (owner != null) {
            ownerName = owner.getInternalName();
        } else {
            ownerName = node.name;
        }
        MemberDesc target = new MemberDesc(ownerName, value, splicedMethodDesc);
        return new MixinDescAnnotation(owner, value, ret, target);
    }

    @Override
    public String toString() {
        return "MixinDescAnnotation[value = \"" + this.value
                + "\", ret = \"" + this.ret
                + "\", target = \"" + this.target + "\"]";
    }
}
