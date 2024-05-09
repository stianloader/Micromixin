package org.stianloader.micromixin.transform.internal.annotation;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.internal.MemberDesc;
import org.stianloader.micromixin.transform.internal.MixinParseException;

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
    public final MemberDesc targetMethod;
    @NotNull
    public final MemberDesc targetField;

    private MixinDescAnnotation(@Nullable Type owner, @NotNull String value, @Nullable Type[] args, @Nullable Type ret, @NotNull MemberDesc targetMethod, @NotNull MemberDesc targetField) {
        this.owner = owner;
        this.value = value;
        this.args = args;
        this.ret = ret;
        this.targetMethod = targetMethod;
        this.targetField = targetField;
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
        MemberDesc targetMethod = new MemberDesc(ownerName, value, splicedMethodDesc);
        MemberDesc targetField = new MemberDesc(ownerName, value, ret == null ? "V" : ret.getDescriptor());
        return new MixinDescAnnotation(owner, value, args, ret, targetMethod, targetField);
    }

    @Override
    public String toString() {
        return "MixinDescAnnotation[value = \"" + this.value
                + "\", args = " + Arrays.toString(args)
                + ", ret = \"" + this.ret
                + ", targetField = \"" + this.targetField
                + "\", targetMethod = \"" + this.targetMethod + "\"]";
    }
}
