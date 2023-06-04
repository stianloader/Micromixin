package de.geolykt.micromixin.internal.annotation;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.SimpleRemapper;
import de.geolykt.micromixin.internal.MixinMethodStub;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.util.ASMUtil;

public final class MixinOverwriteAnnotation extends AbstractOverlayAnnotation<MixinMethodStub> {

    @Nullable
    private final List<String> aliases;

    private MixinOverwriteAnnotation(@Nullable List<String> aliases) {
        this.aliases = aliases;
    }

    @NotNull
    public static MixinOverwriteAnnotation generateImplicit(@NotNull ClassNode node, @NotNull MethodNode method) throws MixinParseException {
        // Only explicitly @Overwrite-annotated method can be public and static at the same time.
        if ((method.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)) == (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)) {
            throw new MixinParseException("The handler method " + node.name + "." + method.name + method.desc + ", which is implicitly an @Overwrite-annotated method, is public and static. Mixin however does not support both access modifiers existing at the same time. So instead explicitly define it as a @Overwrite-annotated method or use @Unique if the intention to overwrite a method is not there.");
        }
        return new MixinOverwriteAnnotation(null);
    }

    @NotNull
    public static MixinOverwriteAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot) throws MixinParseException {
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
    public boolean handleCollision(@NotNull MixinMethodStub source, @NotNull ClassNode target, int access) {

        // Mixin does not support overwriting into public static methods when using aliases.
        // It's a very strange limitation, but whatever
        if ((access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)) == (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC) && this.aliases != null) {
            throw new IllegalStateException("The handler method " + source.owner.name + "." + source.getName() + source.getDesc() + " targets a method in " + target.name + ". As the handler specified aliases and the targetted method is public and static, an overwrite is no possible. Instead explicitly use @Overwrite for the target or manually transform that method through the plattform's supported transformation API.");
        }

        // Mixin does not allow intermingling between static and non-static methods, understandably so.
        if ((source.getAccess() & Opcodes.ACC_STATIC) == 0) {
            if ((access & Opcodes.ACC_STATIC) != 0) {
                // source = not static, access = static
                throw new IllegalStateException("The handler method " + source.owner.name + "." + source.getName() + source.getDesc() + ", which targets a member in " + target.name + ", is not static, but the targetted member is static. Consider making the handler static (should it be possible without dealing with other limitations of @Overwrite).");
            }
        } else if ((access & Opcodes.ACC_STATIC) == 0) {
            // source = static, access = not static
            throw new IllegalStateException("The handler method " + source.owner.name + "." + source.getName() + source.getDesc() + ", which targets a member in " + target.name + ", is static, but the targetted member is not static. Consider making the handler not static or readjust the targetted method.");
        }

        // Don't allow for access modification reduction
        if (ASMUtil.hasReducedAccess(access, source.getAccess())) {
            throw new IllegalStateException("The handler method " + source.owner.name + "." + source.getName() + source.getDesc() + ", which targets a member in " + target.name + ", has a lesser access modifier than the method it targets. Try to keep the access flags the same across both methods.");
        }
        return true;
    }

    @Override
    @NotNull
    public String getDesiredName(@NotNull MixinMethodStub source, @NotNull ClassNode target, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        List<String> aliases = this.aliases;
        if (aliases == null) {
            return source.getName();
        }
        String desiredDesc = remapper.getRemappedMethodDescriptor(source.getDesc(), sharedBuilder);
        for (MethodNode method : target.methods) {
            if (!method.desc.equals(desiredDesc)) {
                continue;
            }
            if (method.name.equals(source.getName())) {
                return source.getName();
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
