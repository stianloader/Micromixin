package de.geolykt.micromixin.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * Wrapper around the central "@Mixin" annotation.
 */
public class MixinHeader {

    @NotNull
    public final Collection<String> targets;
    public final int priority;

    public MixinHeader(@NotNull Collection<String> targets, int priority) {
        this.targets = targets;
        this.priority = priority;
    }

    @NotNull
    public static MixinHeader parse(@NotNull ClassNode node, int defaultPriority) throws MixinParseException {
        AnnotationNode mixinAnnot = null;
        for (AnnotationNode annot : node.invisibleAnnotations) {
            if (annot.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                if (mixinAnnot != null) {
                    throw new MixinParseException("Multiple @Mixin annotations???");
                }
                mixinAnnot = annot;
            }
        }
        if (mixinAnnot == null) {
            throw new MixinParseException("Did not find a @Mixin Annotation in class " + node.name);
        }
        List<String> targets = new ArrayList<>();
        int priority = defaultPriority;
        for (int i = 0; i < mixinAnnot.values.size(); i += 2) {
            String name = (String) mixinAnnot.values.get(i);
            Object value = mixinAnnot.values.get(i + 1);
            if (name.equals("value")) {
                @SuppressWarnings("unchecked")
                List<Type> aev = (List<Type>) value;
                for (Type target : aev) {
                    targets.add(target.getDescriptor());
                }
            } else if (name.equals("targets")) {
                @SuppressWarnings("unchecked")
                List<String> aev = (List<String>) value;
                for (String target : aev) {
                    targets.add('L' + target + ';');
                }
            } else if (name.equals("priority")){
                priority = (Integer) value;
            } else {
                throw new MixinParseException("Unimplemented key in @Mixin: " + name);
            }
        }
        List<String> saneTargets = new ArrayList<>();
        for (String t : targets) {
            if (t.charAt(0) != 'L') {
                throw new MixinParseException("Incorrect mixin target in class " + node.name + ": " + t + " (Note: arrays and primitives are not supported.)");
            }
            // IMPLEMENT verify class (somehow?)
            saneTargets.add(t.substring(1, t.length() - 1));
        }
        return new MixinHeader(Collections.unmodifiableCollection(saneTargets), priority);
    }
}
