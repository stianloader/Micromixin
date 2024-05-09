package org.stianloader.micromixin.transform.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.annotation.MixinAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinShadowAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinUniqueAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.VirtualFieldOverlayAnnotation;

public class MixinFieldStub implements ClassMemberStub {

    @NotNull
    public static MixinFieldStub parse(@NotNull ClassNode owner, @NotNull FieldNode field, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) {
        List<MixinAnnotation<MixinFieldStub>> annotations = new ArrayList<MixinAnnotation<MixinFieldStub>>();
        if (field.visibleAnnotations != null) {
            for (AnnotationNode annot : field.visibleAnnotations) {
                if (annot.desc.startsWith("Lorg/spongepowered/asm/")) {
                    if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;")) {
                        annotations.add(MixinShadowAnnotation.<MixinFieldStub>parse(annot, field.visibleAnnotations));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Unique;")) {
                        annotations.add(MixinUniqueAnnotation.<MixinFieldStub>parse(annot, transformer.getLogger()));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Mutable;")) {
                        // NOP (parsing handled in @Shadow)
                    } else {
                        throw new MixinParseException("Unimplemented mixin annotation: " + annot.desc);
                    }
                } else if (annot.desc.startsWith("Lcom/llamalad7/mixinextras/")) {
                    throw new MixinParseException("Unimplemented MixinExtras annotation: " + annot.desc);
                }
            }
        }
        if (annotations.isEmpty()) {
            annotations.add(new VirtualFieldOverlayAnnotation());
        }

        MixinFieldStub stub = new MixinFieldStub(owner, field, Collections.unmodifiableCollection(annotations), transformer.getLogger());
        for (MixinAnnotation<MixinFieldStub> annotation : annotations) {
            annotation.validateMixin(stub, transformer.getLogger(), sharedBuilder);
        }
        return stub;
    }

    @NotNull
    public final Collection<MixinAnnotation<MixinFieldStub>> annotations;
    @NotNull
    public final FieldNode field;
    @NotNull
    public final MixinLoggingFacade logger;

    @NotNull
    public final ClassNode owner;

    private MixinFieldStub(@NotNull ClassNode owner, @NotNull FieldNode field, @NotNull Collection<MixinAnnotation<MixinFieldStub>> annotations, @NotNull MixinLoggingFacade logger) {
        this.owner = owner;
        this.field = field;
        this.annotations = annotations;
        this.logger = logger;
    }

    @Override
    public void applyTo(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx, @NotNull MixinStub source,
                        @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        for (MixinAnnotation<MixinFieldStub> a : this.annotations) {
            a.apply(target, hctx, source, this, remapper, sharedBuilder);
        }
    }

    @Override
    public void collectMappings(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx,
                                @NotNull MixinStub stub, @NotNull SimpleRemapper out,
                                @NotNull StringBuilder sharedBuilder) {
        for (MixinAnnotation<MixinFieldStub> annotation : this.annotations) {
            annotation.collectMappings(this, target, out, sharedBuilder);
        }
    }

    @Override
    public int getAccess() {
        return this.field.access;
    }

    @Override
    @NotNull
    public String getDesc() {
        return this.field.desc;
    }

    @Override
    @NotNull
    public MixinLoggingFacade getLogger() {
        return this.logger;
    }

    @Override
    @NotNull
    public String getName() {
        return this.field.name;
    }

    @Override
    @NotNull
    public ClassNode getOwner() {
        return this.owner;
    }
}
