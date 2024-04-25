package org.stianloader.micromixin.transform.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.stianloader.micromixin.transform.MixinTransformer;
import org.stianloader.micromixin.transform.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.annotation.MixinAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinShadowAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinUniqueAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.VirtualFieldOverlayAnnotation;

public class MixinFieldStub implements ClassMemberStub {

    @NotNull
    public final ClassNode owner;
    @NotNull
    public final FieldNode field;
    @NotNull
    public final Collection<MixinAnnotation<MixinFieldStub>> annotations;

    private MixinFieldStub(@NotNull ClassNode owner, @NotNull FieldNode field, @NotNull Collection<MixinAnnotation<MixinFieldStub>> annotations) {
        this.owner = owner;
        this.field = field;
        this.annotations = annotations;
    }

    @NotNull
    public static MixinFieldStub parse(@NotNull ClassNode owner, @NotNull FieldNode field, @NotNull MixinTransformer<?> transformer) {
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
        // TODO Implicit field overwrite/overlay!
        return new MixinFieldStub(owner, field, Collections.unmodifiableCollection(annotations));
    }

    @Override
    @NotNull
    public ClassNode getOwner() {
        return this.owner;
    }

    @Override
    @NotNull
    public String getName() {
        return this.field.name;
    }

    @Override
    @NotNull
    public String getDesc() {
        return this.field.desc;
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
}
