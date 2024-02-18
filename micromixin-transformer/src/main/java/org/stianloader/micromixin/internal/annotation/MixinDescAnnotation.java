package org.stianloader.micromixin.internal.annotation;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.internal.MemberDesc;
import org.stianloader.micromixin.internal.MixinParseException;

public class MixinDescAnnotation {

    @Nullable
    public final Type owner;
    @NotNull
    public final String value;
    @Nullable
    public final Type[] args;
    @Nullable
    public final Type ret;
    @NotNull
    public final MemberDesc target;

    private MixinDescAnnotation(@Nullable Type owner, @NotNull String value, @Nullable Type[] args, @Nullable Type ret, @NotNull MemberDesc target) {
        this.owner = owner;
        this.value = value;
        this.args = args;
        this.ret = ret;
        this.target = target;
    }

    @NotNull
    public static MixinDescAnnotation parse(@NotNull ClassNode node, @NotNull AnnotationNode atValue) {
        String value = null;
        Type ret = null;
        Type owner = null;
        Type[] args = null;
        for (int i = 0; i < atValue.values.size(); i += 2) {
            String name = (String) atValue.values.get(i);
            Object val = atValue.values.get(i + 1);
            if (name.equals("value")) {
                value = (String) val;
            } else if (name.equals("ret")) {
                ret = (Type) val;
            } else if (name.equals("owner")) {
                owner = (Type) val;
            } else if (name.equals("args")) {
                @SuppressWarnings("unchecked")
                Type[] hack = ((List<Type>) val).toArray(new Type[0]);
                args = hack;
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
        String splicedMethodDesc = "()V";
        if (ret != null) {
            splicedMethodDesc = splicedMethodDesc.substring(0, splicedMethodDesc.lastIndexOf(')') + 1) + ret.getDescriptor();
        }
        if (args != null) {
            String splicedArgs = "(";
            for (Type type : args) {
                splicedArgs += type.getDescriptor();
            }
            splicedMethodDesc = splicedArgs + splicedMethodDesc.substring(splicedMethodDesc.lastIndexOf(')'));
        }
        String ownerName;
        if (owner != null) {
            ownerName = owner.getInternalName();
        } else {
            ownerName = node.name;
        }
        MemberDesc target = new MemberDesc(ownerName, value, splicedMethodDesc);
        return new MixinDescAnnotation(owner, value, args, ret, target);
    }

    @Override
    public String toString() {
        return "MixinDescAnnotation[value = \"" + this.value
                + "\", args = " + Arrays.toString(args)
                + ", ret = \"" + this.ret
                + "\", target = \"" + this.target + "\"]";
    }
}
