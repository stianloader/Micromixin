package de.geolykt.micromixin.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.util.DescString;

class AnnotationUtil {

    static final String CALLBACK_INFO_NAME = "org/spongepowered/asm/mixin/injection/callback/CallbackInfo";
    static final String CALLBACK_INFO_RETURNABLE_NAME = "org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable";
    static final int CI_LEN = CALLBACK_INFO_NAME.length();
    static final int CIR_LEN = CALLBACK_INFO_RETURNABLE_NAME.length();

    @NotNull
    static String getTargetDesc(@NotNull MethodNode method, boolean stripCallbackInfo) {
        String desc = method.desc;
        if (!stripCallbackInfo) {
            return desc;
        }
        DescString dstring = new DescString(desc);
        String last = null;
        while (dstring.hasNext()) {
            last = dstring.nextType();
        }
        int idx0 = desc.lastIndexOf(')');
        if (("L" + CALLBACK_INFO_NAME + ";").equals(last)) {
            return desc.substring(0, idx0 - 2 - CI_LEN) + desc.substring(idx0);
        } else if (("L" + CALLBACK_INFO_RETURNABLE_NAME + ";").equals(last)) {
            return desc.substring(0, idx0 - 2 - CIR_LEN) + desc.substring(idx0);
        } else {
            throw new MixinParseException("Method " + method.name + desc + " should have a CallbackInfo or a CallbackInfoReturnable as it's last argument. But it does not.");
        }
    }

    static boolean hasMethod(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        // TODO Also check supers? (and interfaces too?)
        for (MethodNode method : node.methods) {
            if (method.name.equals(name) && method.desc.equals(desc)) {
                return true;
            }
        }
        return false;
    }

    static boolean hasField(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        // TODO Also check supers?
        for (FieldNode field : node.fields) {
            if (field.name.equals(name) && field.desc.equals(desc)) {
                return true;
            }
        }
        return false;
    }
}
