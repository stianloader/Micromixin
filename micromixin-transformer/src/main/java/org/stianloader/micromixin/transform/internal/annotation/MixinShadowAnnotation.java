package org.stianloader.micromixin.transform.internal.annotation;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.ClassMemberStub;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinFieldStub;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.util.Objects;

public final class MixinShadowAnnotation<T extends ClassMemberStub> extends MixinAnnotation<T> {
    @NotNull
    private final List<String> aliases;
    private final boolean isMutable;
    @NotNull
    private final MixinLoggingFacade logger;
    @NotNull
    private final String prefix;

    private MixinShadowAnnotation(@NotNull String prefix, @NotNull List<String> aliases, boolean isMutable, @NotNull MixinLoggingFacade logger) {
        this.prefix = prefix;
        this.aliases = aliases;
        this.isMutable = isMutable;
        this.logger = logger;
    }

    @NotNull
    public static <T0 extends ClassMemberStub> MixinShadowAnnotation<T0> parse(@NotNull AnnotationNode annotation, @Nullable List<AnnotationNode> allAnnotations, @NotNull MixinLoggingFacade logger) {
        boolean isMutable = false;
        if (allAnnotations != null) {
            for (AnnotationNode currAnn : allAnnotations) {
                if (currAnn.desc.equals("Lorg/spongepowered/asm/mixin/Mutable;")) {
                    if (currAnn.values != null && !currAnn.values.isEmpty())
                        throw new MixinParseException("The @Mutable annotation does not take any values in");
                    isMutable = true;
                    break;
                }
            }
        }

        String prefix = "shadow$";
        List<String> aliases = Collections.emptyList();

        if (annotation.values == null) {
            return new MixinShadowAnnotation<T0>(prefix, aliases, isMutable, logger);
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

        return new MixinShadowAnnotation<T0>(prefix, aliases, isMutable, logger);
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
        throw new IllegalStateException("Unresolved @Shadow-annotated field: " + source.owner.name + "." + source.field.name + " " + source.field.desc + " (remapped as '" + target.name + "'.'" + name + "':'" + desc + "')");
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
                      @NotNull MixinStub sourceStub, @NotNull T source, @NotNull SimpleRemapper remapper,
                      @NotNull StringBuilder sharedBuilder) {
        if (this.isMutable) {
            if (source instanceof MixinFieldStub) {
                String mappedName = remapper.getRemappedFieldName(source.getOwner().name, source.getName(), source.getDesc());
                String mappedDesc = remapper.getRemappedFieldDescriptor(source.getDesc(), sharedBuilder);
                for (FieldNode fn : to.fields) {
                    if (fn.name.equals(mappedName) && fn.desc.equals(mappedDesc)) {
                        fn.access &= ~Opcodes.ACC_FINAL;
                    }
                }
            } else {
                throw new UnsupportedOperationException("@Mutable is incompatible with @Shadow on methods. @Mutable may only be used in conjunction with accessors.");
            }
        }

        if ((source.getAccess() & Opcodes.ACC_PRIVATE) == 0 && !this.aliases.isEmpty()) {
            this.logger.warn(MixinShadowAnnotation.class, "The @Shadow annotated member {}.{} {} defines an alias for a non-private method. While this behaviour is supported in micromixin-transformer, it isn't supported in the spongeian mixin implementation. This may represent a compatibility hazard. For more information, see the javadocs on aliases for the Shadow annotation. Note that the modifier of your mixin member is not of relevance, just the modifier of the member that is being aliased.", source.getOwner().name, source.getName(), source.getDesc());
        }
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

    @Override
    public void validateMixin(@NotNull T source, @NotNull MixinLoggingFacade logger, @NotNull StringBuilder sharedBuilder) {
        if (source instanceof MixinFieldStub) {
            if (source.getName().startsWith(this.prefix) && !this.prefix.equals("")) {
                logger.warn(MixinShadowAnnotation.class, "The @Shadow-annotated field {}.{} {} defines a prefix. However, the spongeian mixin implementation does not support using @Shadow prefixes on fields. For more information, see the javadocs on prefixes. A possible workaround would be getting rid of the prefix and if necessary use aliases instead.", source.getOwner().name, source.getName(), source.getDesc());
            }
        } else if (source instanceof MixinMethodStub) {
            if ((source.getAccess() & Opcodes.ACC_STATIC) != 0) {
                if (source.getName().startsWith(this.prefix)) {
                    logger.error(MixinShadowAnnotation.class, "The @Shadow annotated method {}.{}{} defines a prefix and is static. However, due to a bug in the spongeian mixin implementation INVOKESTATIC calls will not be redirected to the non-prefixed member you are targetting, effectively causing a crash at runtime. At this point in time micromixin-transformer replicates this issue, but this behaviour is subject to change.", source.getOwner().name, source.getName(), source.getDesc());
                } else if (!this.aliases.isEmpty()) {
                    logger.error(MixinShadowAnnotation.class, "The @Shadow annotated method {}.{}{} defines an alias and is static. However, due to a bug in the spongeian mixin implementation INVOKESTATIC calls will not be redirected to the member you are targetting, effectively causing a crash at runtime. At this point in time micromixin-transformer replicates this issue, but this behaviour is subject to change.", source.getOwner().name, source.getName(), source.getDesc());
                }
            }
        }
    }
}
