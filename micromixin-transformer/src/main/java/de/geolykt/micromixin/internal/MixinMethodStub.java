package de.geolykt.micromixin.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.annotation.MixinAnnotation;
import de.geolykt.micromixin.internal.annotation.MixinInjectAnnotation;
import de.geolykt.micromixin.internal.annotation.MixinOverwriteAnnotation;
import de.geolykt.micromixin.internal.annotation.MixinShadowAnnotation;
import de.geolykt.micromixin.internal.annotation.VirtualClInitMergeAnnotation;
import de.geolykt.micromixin.internal.annotation.VirtualConstructorMergeAnnotation;
import de.geolykt.micromixin.internal.util.Remapper;

public class MixinMethodStub implements ClassMemberStub {
    // IMPLEMENT @Unique

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
    public static MixinMethodStub parse(@NotNull ClassNode node, @NotNull MethodNode method) {
        List<MixinAnnotation<MixinMethodStub>> annotations = new ArrayList<>();
        if (method.visibleAnnotations != null) {
            for (AnnotationNode annot : method.visibleAnnotations) {
                if (annot.desc.startsWith("Lorg/spongepowered/asm/")) {
                    if (annot.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")) {
                        annotations.add(MixinInjectAnnotation.parse(node, method, annot));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Overwrite;")) {
                        annotations.add(MixinOverwriteAnnotation.parse(node, method, annot));
                    } else if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;")) {
                        annotations.add(MixinShadowAnnotation.parse(annot));
                    } else {
                        throw new MixinParseException("Unimplemented mixin annotation: " + annot.desc);
                    }
                }
            }
        }
        if (annotations.isEmpty()) { // FIXME That is the wrong approach once we support @Unique
            if (method.name.equals("<init>") && method.desc.equals("()V")) {
                annotations.add(new VirtualConstructorMergeAnnotation(method));
            } else if (method.name.equals("<clinit>") && method.desc.equals("()V")) {
                annotations.add(new VirtualClInitMergeAnnotation(method));
            } else {
                annotations.add(MixinOverwriteAnnotation.generateImplicit(node, method));
            }
        }
        return new MixinMethodStub(node, method, Collections.unmodifiableCollection(annotations));
    }

    public void applyTo(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx, @NotNull MixinStub stub, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
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
            @NotNull MixinStub stub, @NotNull Remapper out,
            @NotNull StringBuilder sharedBuilder) {
        for (MixinAnnotation<MixinMethodStub> annotation : this.annotations) {
            annotation.collectMappings(this, target, out, sharedBuilder);
        }
    }
}
