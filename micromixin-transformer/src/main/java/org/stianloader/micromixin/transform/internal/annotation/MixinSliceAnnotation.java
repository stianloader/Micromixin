package org.stianloader.micromixin.transform.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.InjectionPointSelectorFactory;
import org.stianloader.micromixin.transform.internal.MixinParseException;

public class MixinSliceAnnotation {

    @NotNull
    public final String id;

    @NotNull
    public final MixinAtAnnotation from;

    @NotNull
    public final MixinAtAnnotation to;

    public MixinSliceAnnotation(@NotNull String id, @NotNull MixinAtAnnotation from, @NotNull MixinAtAnnotation to) {
        this.id = id;
        this.from = from;
        this.to = to;
    }

    @NotNull
    public static MixinSliceAnnotation parse(@NotNull ClassNode mixinSource, @NotNull AnnotationNode sliceNode, @NotNull InjectionPointSelectorFactory factory) throws MixinParseException {
        String id = "";
        MixinAtAnnotation from = null;
        MixinAtAnnotation to = null;

        for (int i = 0; i < sliceNode.values.size(); i += 2) {
            String name = (String) sliceNode.values.get(i);
            Object val = sliceNode.values.get(i + 1);
            assert val != null;

            if (name.equals("id")) {
                id = (String) val;
            } else if (name.equals("from")) {
                from = MixinAtAnnotation.parse(mixinSource, (AnnotationNode) val, factory);
            } else if (name.equals("to")) {
                to = MixinAtAnnotation.parse(mixinSource, (AnnotationNode) val, factory);
            } else {
                throw new MixinParseException("Unimplemented key in @Slice: " + name);
            }
        }

        if (from == null) {
            if (to == null) {
                throw new MixinParseException("Invalid @Slice within mixin " + mixinSource.name + ": Both 'from' and 'to' attributes of the slice are undefined, however at least one of these attributes must be defined!");
            }
            from = MixinAtAnnotation.HEAD;
        } else if (to == null) {
            to = MixinAtAnnotation.TAIL;
        }

        return new MixinSliceAnnotation(id, from, to);
    }
}
