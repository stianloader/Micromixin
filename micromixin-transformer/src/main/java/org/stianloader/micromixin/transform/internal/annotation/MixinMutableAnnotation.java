package org.stianloader.micromixin.transform.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.*;

import java.util.Collection;

public class MixinMutableAnnotation<T extends ClassMemberStub> extends MixinAnnotation<T> {
    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
                      @NotNull MixinStub sourceStub, @NotNull T source,
                      @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        if (source instanceof MixinFieldStub) {
            for (FieldNode fn : to.fields) {
                if (fn.name.equals(source.getName()) && fn.desc.equals(source.getDesc())) {
                    fn.access &= ~Opcodes.ACC_FINAL;
                    return;
                }
            }
        } else if (source instanceof MixinMethodStub) {
            // TODO
            for (MethodNode mn : to.methods) {
                if (mn.name.equals(source.getName()) && mn.desc.equals(source.getDesc())) {
                    mn.access &= ~Opcodes.ACC_FINAL;
                    return;
                }
            }
        } else {
            throw new UnsupportedOperationException("Implementation of ClassMemberStub: " + source.getClass() + " is not supported");
        }
    }

    // TODO: ask geo about mappings
    @Override
    public void collectMappings(@NotNull T source, @NotNull ClassNode target,
                                @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        // NOOP
    }

    // TODO: Once @Final is added, make this incompatible
    @Override
    public boolean isCompatible(Collection<MixinAnnotation<T>> collection) {
        for (MixinAnnotation<T> annot : collection) {
            if (annot instanceof MixinShadowAnnotation) return true;
        }
        return false;
    }
}
