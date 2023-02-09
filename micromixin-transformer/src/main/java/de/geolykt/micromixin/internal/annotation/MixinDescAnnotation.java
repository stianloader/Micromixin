package de.geolykt.micromixin.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import de.geolykt.micromixin.internal.MemberDesc;
import de.geolykt.micromixin.internal.MixinParseException;

public class MixinDescAnnotation {

    @NotNull
    public final String value;
    @NotNull
    public final MemberDesc target;

    public MixinDescAnnotation(@NotNull String value, @NotNull MemberDesc target) {
        this.value = value;
        this.target = target;
    }

    @NotNull
    public static MixinDescAnnotation parse(@NotNull ClassNode node, @NotNull String fallbackMethodDesc, @NotNull AnnotationNode atValue) {
        String value = null;
        for (int i = 0; i < atValue.values.size(); i += 2) {
            String name = (String) atValue.values.get(i);
            Object val = atValue.values.get(i + 1);
            if (name.equals("value")) {
                value = (String) val;
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
        // IMPLEMENT owner/args/ret
        MemberDesc target = new MemberDesc(node.name, value, fallbackMethodDesc);
        return new MixinDescAnnotation(value, target);
    }

    @Override
    public String toString() {
        return "MixinDescAnnotation[value = " + value + ", target = " + target + "]";
    }
}
