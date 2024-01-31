package org.stianloader.micromixin.internal.annotation;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.SimpleRemapper;
import org.stianloader.micromixin.internal.ClassMemberStub;
import org.stianloader.micromixin.internal.HandlerContextHelper;
import org.stianloader.micromixin.internal.MixinFieldStub;
import org.stianloader.micromixin.internal.MixinMethodStub;
import org.stianloader.micromixin.internal.MixinParseException;
import org.stianloader.micromixin.internal.MixinStub;
import org.stianloader.micromixin.internal.util.Objects;

public final class MixinShadowAnnotation<T extends ClassMemberStub> extends MixinAnnotation<T> {

    @NotNull
    private final String prefix;
    @NotNull
    private final List<String> aliases;

    private MixinShadowAnnotation(@NotNull String prefix, @NotNull List<String> aliases) {
        this.prefix = prefix;
        this.aliases = aliases;
    }

    @NotNull
    public static <T0 extends ClassMemberStub> MixinShadowAnnotation<T0> parse(@NotNull AnnotationNode annotation) {
        String prefix = "shadow$";
        List<String> aliases = Collections.emptyList();
        if (annotation.values == null) {
            return new MixinShadowAnnotation<T0>(prefix, aliases);
        }
        for (int i = 0; i < annotation.values.size(); i += 2) {
            String name = (String) annotation.values.get(i);
            if (name.equals("aliases")) {
                @SuppressWarnings("all")
                @NotNull
                List<String> temp = (List<String>) annotation.values.get(i + 1);
                aliases = temp;
            } else if (name.equals("prefix")) {
                prefix = (String) annotation.values.get(i + 1);
                if (prefix == null) {
                    throw new MixinParseException("Null prefix for @Shadow annotation");
                }
            } else {
                throw new MixinParseException("Unimplemented option for @Shadow: " + name);
            }
        }
        return new MixinShadowAnnotation<T0>(prefix, aliases);
    }

    private void apply(@NotNull MixinMethodStub source, @NotNull ClassNode target, @NotNull SimpleRemapper out, @NotNull StringBuilder sharedBuilder) {
        String desc = out.getRemappedMethodDescriptor(source.method.desc, sharedBuilder);
        String name = source.method.name;
        if (name.startsWith(this.prefix)) {
            name = name.substring(this.prefix.length());
        }
        // TODO do we need to resolve methods of superclasses/super-interfaces? Also, in which orders are aliases selected?
        for (MethodNode tmethod : target.methods) {
            if (tmethod.name.equals(name) && tmethod.desc.equals(desc)) {
                out.remapMethod(source.owner.name, source.method.desc, source.method.name, name);
                return;
            }
        }
        for (String alias : this.aliases) {
            // Note: aliases are not affected by prefixes.
            for (MethodNode tmethod : target.methods) {
                if (tmethod.name.equals(alias) && tmethod.desc.equals(desc)) {
                    out.remapMethod(source.owner.name, source.method.desc, source.method.name, Objects.requireNonNull(alias, "Null alias"));
                    return;
                }
            }
        }
        throw new IllegalStateException("Unresolved @Shadow-annotated method: " + source.owner.name + "." + source.method.name + source.method.desc);
    }

    private void apply(@NotNull MixinFieldStub source, @NotNull ClassNode target, @NotNull SimpleRemapper out, @NotNull StringBuilder sharedBuilder) {
        String desc = out.getRemappedFieldDescriptor(source.field.desc, sharedBuilder);
        String name = source.field.name;
        if (name.startsWith(this.prefix)) {
            name = name.substring(this.prefix.length());
        }
        // TODO do we need to resolve fields of superclasses? Also, in which orders are aliases selected?
        for (FieldNode tfield : target.fields) {
            if (tfield.name.equals(name) && tfield.desc.equals(desc)) {
                out.remapField(source.owner.name, source.field.desc, source.field.name, name);
                return;
            }
        }
        for (String alias : this.aliases) {
            // Note: aliases are not affected by prefixes.
            for (FieldNode tfield : target.fields) {
                if (tfield.name.equals(alias) && tfield.desc.equals(desc)) {
                    out.remapField(source.owner.name, source.field.desc, source.field.name, Objects.requireNonNull(alias, "Null alias"));
                    return;
                }
            }
        }
        throw new IllegalStateException("Unresolved @Shadow-annotated field: " + source.owner.name + "." + name + " " + source.field.desc + " (remapped as \"" + desc + "\", targetting \"" + target.name + "\")");
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull T source, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        // NOP
    }

    @Override
    public void collectMappings(@NotNull T source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        if (source instanceof MixinMethodStub) {
            apply((MixinMethodStub) source, target, remapper, sharedBuilder);
        } else if (source instanceof MixinFieldStub) {
            apply((MixinFieldStub) source, target, remapper, sharedBuilder);
        } else {
            throw new UnsupportedOperationException("Unknown/Unsupported implementation of ClassMemberStub: " + source.getClass().getName());
        }
    }
}