package de.geolykt.micromixin.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.MixinTransformer;
import de.geolykt.micromixin.SimpleRemapper;
import de.geolykt.micromixin.internal.annotation.MixinAnnotation;
import de.geolykt.micromixin.internal.annotation.MixinInjectAnnotation;
import de.geolykt.micromixin.internal.annotation.MixinModifyArgAnnotation;
import de.geolykt.micromixin.internal.annotation.MixinOverwriteAnnotation;
import de.geolykt.micromixin.internal.annotation.MixinRedirectAnnotation;
import de.geolykt.micromixin.internal.annotation.MixinShadowAnnotation;
import de.geolykt.micromixin.internal.annotation.MixinUniqueAnnotation;
import de.geolykt.micromixin.internal.annotation.VirtualClInitMergeAnnotation;
import de.geolykt.micromixin.internal.annotation.VirtualConstructorMergeAnnotation;
import de.geolykt.micromixin.internal.annotation.mixinsextras.MixinExtrasModifyReturnValueAnnotation;

public class MixinMethodStub implements ClassMemberStub {

    @NotNull
    public final ClassNode owner;
    @NotNull
    public final MethodNode method;
    @NotNull
    public final Collection<MixinAnnotation<MixinMethodStub>> annotations;

    public MixinMethodStub(@NotNull ClassNode owner, @NotNull MethodNode method, @NotNull Collection<MixinAnnotation<MixinMethodStub>> annotations) {
        this.owner = owner;
        this.method = method;
        this.annotations = annotations;
    }

    @NotNull
    public static MixinMethodStub parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) {
        List<MixinAnnotation<MixinMethodStub>> annotations = new ArrayList<MixinAnnotation<MixinMethodStub>>();
        if (method.visibleAnnotations != null) {
            for (AnnotationNode annot : method.visibleAnnotations) {
                if (annot.desc.startsWith("Lorg/spongepowered/asm/")) {
                    if (annot.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")) {
                        annotations.add(MixinInjectAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Overwrite;")) {
                        annotations.add(MixinOverwriteAnnotation.parse(node, method, annot));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;")) {
                        annotations.add(MixinShadowAnnotation.<MixinMethodStub>parse(annot));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Unique;")) {
                        annotations.add(MixinUniqueAnnotation.<MixinMethodStub>parse(annot, transformer.getLogger()));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/injection/Redirect;")) {
                        annotations.add(MixinRedirectAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/injection/ModifyArg;")) {
                        annotations.add(MixinModifyArgAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else {
                        throw new MixinParseException("Unimplemented mixin annotation: " + annot.desc);
                    }
                } else if (annot.desc.startsWith("Lcom/llamalad7/mixinextras/")) {
                    if (annot.desc.equals("Lcom/llamalad7/mixinextras/injector/ModifyReturnValue;")) {
                        annotations.add(MixinExtrasModifyReturnValueAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else {
                        throw new MixinParseException("Unimplemented MixinExtras annotation: " + annot.desc);
                    }
                }
            }
        }
        if (annotations.isEmpty()) {
            if (method.name.equals("<init>") && method.desc.equals("()V")) {
                annotations.add(new VirtualConstructorMergeAnnotation());
            } else if (method.name.equals("<clinit>") && method.desc.equals("()V")) {
                annotations.add(new VirtualClInitMergeAnnotation(method));
            } else {
                annotations.add(MixinOverwriteAnnotation.generateImplicit(node, method));
            }
        }
        return new MixinMethodStub(node, method, Collections.unmodifiableCollection(annotations));
    }

    public void applyTo(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx, @NotNull MixinStub stub, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        for (MixinAnnotation<MixinMethodStub> a : this.annotations) {
            a.apply(target, hctx, stub, this, remapper, sharedBuilder);
        }
    }

    @Override
    @NotNull
    public ClassNode getOwner() {
        return this.owner;
    }

    @Override
    @NotNull
    public String getName() {
        return this.method.name;
    }

    @Override
    @NotNull
    public String getDesc() {
        return this.method.desc;
    }

    @Override
    public void collectMappings(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub stub, @NotNull SimpleRemapper out,
            @NotNull StringBuilder sharedBuilder) {
        for (MixinAnnotation<MixinMethodStub> annotation : this.annotations) {
            annotation.collectMappings(this, target, out, sharedBuilder);
        }
    }

    @Override
    public int getAccess() {
        return this.method.access;
    }
}
