package org.stianloader.micromixin.transform.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.annotation.MixinAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinInjectAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinModifyArgAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinModifyConstantAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinModifyVariableAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinOverwriteAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinRedirectAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinShadowAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinUniqueAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.VirtualClInitMergeAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.VirtualConstructorMergeAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.micromixin.MicromixinCanonicalOverwriteAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.mixinsextras.MixinExtrasModifyReturnValueAnnotation;

public class MixinMethodStub implements ClassMemberStub {

    @NotNull
    public static MixinMethodStub parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) {
        List<MixinAnnotation<MixinMethodStub>> annotations = new ArrayList<MixinAnnotation<MixinMethodStub>>();
        if (method.visibleAnnotations != null) {
            for (AnnotationNode annot : method.visibleAnnotations) {
                if (annot.desc.startsWith("Lorg/spongepowered/asm/")) {
                    if (annot.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")) {
                        annotations.add(MixinInjectAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Overwrite;")) {
                        annotations.add(MixinOverwriteAnnotation.parse(node, method, annot, transformer.getLogger()));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;")) {
                        annotations.add(MixinShadowAnnotation.<MixinMethodStub>parse(annot, null, transformer.getLogger()));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Unique;")) {
                        annotations.add(MixinUniqueAnnotation.<MixinMethodStub>parse(annot, transformer.getLogger()));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/injection/Redirect;")) {
                        annotations.add(MixinRedirectAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/injection/ModifyArg;")) {
                        annotations.add(MixinModifyArgAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/injection/ModifyConstant;")) {
                        annotations.add(MixinModifyConstantAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/injection/ModifyVariable;")) {
                        annotations.add(MixinModifyVariableAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else {
                        throw new MixinParseException("Unimplemented mixin annotation: " + annot.desc);
                    }
                } else if (annot.desc.startsWith("Lcom/llamalad7/mixinextras/")) {
                    if (annot.desc.equals("Lcom/llamalad7/mixinextras/injector/ModifyReturnValue;")) {
                        annotations.add(MixinExtrasModifyReturnValueAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else {
                        throw new MixinParseException("Unimplemented MixinExtras annotation: " + annot.desc);
                    }
                } else if (annot.desc.startsWith("Lorg/stianloader/micromixin/annotations/")) {
                    if (annot.desc.equals("Lorg/stianloader/micromixin/annotations/CanonicalOverwrite;")) {
                        annotations.add(MicromixinCanonicalOverwriteAnnotation.parse(node, method, annot, transformer, sharedBuilder));
                    } else {
                        throw new MixinParseException("Unimplemented micromixin annotation: " + annot.desc);
                    }
                }
            }
        }

        if (annotations.isEmpty()) {
            if (method.name.equals("<init>")) {
                annotations.add(new VirtualConstructorMergeAnnotation(null, true, transformer.getLogger()));
            } else if (method.name.equals("<clinit>") && method.desc.equals("()V")) {
                annotations.add(new VirtualClInitMergeAnnotation());
            } else {
                annotations.add(MixinOverwriteAnnotation.generateImplicit(node, method, transformer.getLogger()));
            }
        }

        MixinMethodStub stub = new MixinMethodStub(node, method, Collections.unmodifiableCollection(annotations), transformer.getLogger());
        for (MixinAnnotation<MixinMethodStub> annotation : annotations) {
            annotation.validateMixin(stub, transformer.getLogger(), sharedBuilder);
        }

        return stub;
    }

    @NotNull
    public final Collection<MixinAnnotation<MixinMethodStub>> annotations;
    @NotNull
    public final MixinLoggingFacade logger;
    @NotNull
    public final MethodNode method;
    @NotNull
    public final ClassNode owner;

    public MixinMethodStub(@NotNull ClassNode owner, @NotNull MethodNode method, @NotNull Collection<MixinAnnotation<MixinMethodStub>> annotations, @NotNull MixinLoggingFacade logger) {
        this.owner = owner;
        this.method = method;
        this.annotations = annotations;
        this.logger = logger;
    }

    public void applyTo(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx, @NotNull MixinStub stub, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        for (MixinAnnotation<MixinMethodStub> a : this.annotations) {
            try {
                a.apply(target, hctx, stub, this, remapper, sharedBuilder);
            } catch (RuntimeException e) {
                throw new RuntimeException("Unable to apply annotation '" + a.toString() + "' fom method " + this.owner.name + "." + this.method.name + this.method.desc + " to the target class.", e);
            }
        }
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

    @Override
    @NotNull
    public String getDesc() {
        return this.method.desc;
    }

    @Override
    @NotNull
    public MixinLoggingFacade getLogger() {
        return this.logger;
    }

    @Override
    @NotNull
    public String getName() {
        return this.method.name;
    }

    @Override
    @NotNull
    public ClassNode getOwner() {
        return this.owner;
    }
}
