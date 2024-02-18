package org.stianloader.micromixin.internal.annotation;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.stianloader.micromixin.internal.MixinParseException;
import org.stianloader.micromixin.internal.selectors.constant.ClassConstantSelector;
import org.stianloader.micromixin.internal.selectors.constant.DoubleConstantSelector;
import org.stianloader.micromixin.internal.selectors.constant.FloatConstantSelector;
import org.stianloader.micromixin.internal.selectors.constant.IntConstantSelector;
import org.stianloader.micromixin.internal.selectors.constant.LongConstantSelector;
import org.stianloader.micromixin.internal.selectors.constant.NullConstantSelector;
import org.stianloader.micromixin.internal.selectors.constant.StringConstantSelector;

public abstract class ConstantSelector {

    @NotNull
    public static ConstantSelector parse(@NotNull List<String> args) {
        for (String s : args) {
            int equalIndex = s.indexOf('=');
            if (equalIndex == -1) {
                continue;
            }
            String key = s.substring(0, equalIndex);
            String value = s.substring(equalIndex + 1);
            if (key.equals("nullValue") && value.equals("true")) {
                return NullConstantSelector.INSTANCE;
            } else if (key.equals("intValue")) {
                return new IntConstantSelector(Integer.parseInt(value));
            } else if (key.equals("stringValue")) {
                return new StringConstantSelector(value);
            } else if (key.equals("classValue")) {
                return new ClassConstantSelector(value);
            } else if (key.equals("doubleValue")) {
                return new DoubleConstantSelector(Double.parseDouble(value));
            } else if (key.equals("floatValue")) {
                return new FloatConstantSelector(Float.parseFloat(value));
            } else if (key.equals("longValue")) {
                return new LongConstantSelector(Long.parseLong(value));
            }
        }
        throw new MixinParseException("Cannot find any constant values in @At(\"CONSTANT\") args. An example would be @At(value = \"CONSTANT\", args = {\"intValue=5\"}). Note: Whitespaces are not allowed between either side of the equals.");
    }

    public abstract boolean matchesConstant(@NotNull AbstractInsnNode insn);
}
