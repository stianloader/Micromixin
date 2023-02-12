package de.geolykt.micromixin.internal.annotation;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.MixinMethodStub;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.util.Remapper;

public final class MixinOverwriteAnnotation extends AbstractOverlayAnnotation<MixinMethodStub> {

    @Nullable
    private final List<String> aliases;

    private MixinOverwriteAnnotation(@Nullable List<String> aliases) {
        this.aliases = aliases;
    }

    @NotNull
    public static MixinOverwriteAnnotation generateImplicit(@NotNull ClassNode node, @NotNull MethodNode method) {
        return new MixinOverwriteAnnotation(null);
    }

    @NotNull
    public static MixinOverwriteAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot) {
        List<String> aliases = null;
        if (annot.values != null) {
            for (int i = 0; i < annot.values.size(); i += 2) {
                String name = (String) annot.values.get(i);
                Object val = annot.values.get(i + 1);
                if (name.equals("aliases")) {
                    @SuppressWarnings("unchecked")
                    List<String> aval = (List<String>) val;
                    aliases = aval;
                } else {
                    throw new MixinParseException("Unimplemented key in @Overwrite: " + name);
                }
            }
        }
        return new MixinOverwriteAnnotation(aliases);
    }

    @Override
    public boolean allowOverwrite(@NotNull MixinMethodStub source, @NotNull ClassNode target) {
        return true;
    }

    @Override
    @NotNull
    public String getDesiredName(@NotNull MixinMethodStub source, @NotNull ClassNode target, @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        List<String> aliases = this.aliases;
        if (aliases == null) {
            return source.getName();
        }
        String desiredDesc = remapper.getRemappedMethodDescriptor(source.getDesc(), sharedBuilder);
        for (MethodNode method : target.methods) {
            if (!method.desc.equals(desiredDesc)) {
                continue;
            }
            for (String alias : aliases) {
                if (alias.equals(method.name)) {
                    return alias;
                }
            }
        }
        throw new IllegalStateException("Unable to find the method defined by the aliases specified by the @Overwrite-annotation on " + source.getOwner().name + "." + source.getName() + source.getDesc() + ". The targetted method should be within " + target.name);
    }
}
