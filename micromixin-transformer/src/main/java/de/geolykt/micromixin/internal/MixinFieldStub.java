package de.geolykt.micromixin.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import de.geolykt.micromixin.internal.annotation.MixinShadowAnnotation;
import de.geolykt.micromixin.internal.util.Remapper;

public class MixinFieldStub implements ClassMemberStub {

    @NotNull
    public final ClassNode owner;
    @NotNull
    public final FieldNode field;
    @Nullable
    public final MixinShadowAnnotation<MixinFieldStub> shadow;

    private MixinFieldStub(@NotNull ClassNode owner, @NotNull FieldNode field, @Nullable MixinShadowAnnotation<MixinFieldStub> shadow) {
        this.owner = owner;
        this.field = field;
        this.shadow = shadow;
    }

    @NotNull
    public static MixinFieldStub parse(@NotNull ClassNode owner, @NotNull FieldNode field) {
        MixinShadowAnnotation<MixinFieldStub> shadow = null;
        if (field.visibleAnnotations != null) {
            for (AnnotationNode annot : field.visibleAnnotations) {
                if (annot.desc.startsWith("Lorg/spongepowered/asm/")) {
                    if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Shadow;")) {
                        shadow = MixinShadowAnnotation.parse(annot);
                    } else {
                        throw new MixinParseException("Unimplemented mixin annotation: " + annot.desc);
                    }
                }
            }
        }
        return new MixinFieldStub(owner, field, shadow);
    }

    @NotNull
    private MemberDesc resolve(@NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        return MemberDesc.remappedField(owner.name, field.name, field.desc, remapper, sharedBuilder);
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
            @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        MemberDesc resolved = resolve(remapper, sharedBuilder);
        FieldNode lookupField = null;
        for (FieldNode field : target.fields) {
            if (resolved.name.equals(field.name) && resolved.desc.equals(field.desc)) {
                lookupField = field;
                break;
            }
        }
        if (lookupField == null && this.shadow == null) {
            FieldNode field = new FieldNode(this.field.access, resolved.name, resolved.desc, null, this.field.value);
            target.fields.add(field);
        }
    }

    @Override
    public void collectMappings(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub stub, @NotNull Remapper out,
            @NotNull StringBuilder sharedBuilder) {
        MixinShadowAnnotation<MixinFieldStub> shadow = this.shadow;
        if (shadow != null) {
            shadow.collectMappings(this, target, out, sharedBuilder);
        }
    }
}
