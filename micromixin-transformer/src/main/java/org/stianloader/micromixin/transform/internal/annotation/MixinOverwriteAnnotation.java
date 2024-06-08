package org.stianloader.micromixin.transform.internal.annotation;

import java.util.List;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;

public final class MixinOverwriteAnnotation extends AbstractOverlayAnnotation<MixinMethodStub> {

    @Nullable
    private final List<String> aliases;

    @NotNull
    private final MixinLoggingFacade logger;

    /**
     * Nag flag that is set whenever {@link #aliases} are being used to refer to a non-private member:
     * A feature that is not supported in the spongeian mixin implementation. As such, a warning is logged
     * by micromixin-transformer 
     *
     * <p>Needed as {@link #handleCollision(MixinMethodStub, ClassNode, int)} will be invoked twice
     * (once to collect members, the second twice when doing the actual application), but we'd ideally
     * only want to log the warning message once.
     */
    @ApiStatus.Internal
    private boolean naggedInvalidAlias;

    private MixinOverwriteAnnotation(@Nullable List<String> aliases, @NotNull MixinLoggingFacade logger) {
        this.aliases = aliases;
        this.logger = logger;
    }

    @NotNull
    public static MixinOverwriteAnnotation generateImplicit(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull MixinLoggingFacade logger) throws MixinParseException {
        // Only explicitly @Overwrite-annotated method can be public and static at the same time.
        if ((method.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)) == (Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)) {
            throw new MixinParseException("The handler method " + node.name + "." + method.name + method.desc + ", which is implicitly an @Overwrite-annotated method, is public and static. Mixin however does not support both access modifiers existing at the same time. So instead explicitly define it as a @Overwrite-annotated method or use @Unique if the intention to overwrite a method is not there.");
        }
        return new MixinOverwriteAnnotation(null, logger);
    }

    @NotNull
    public static MixinOverwriteAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot, @NotNull MixinLoggingFacade logger) throws MixinParseException {
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
        return new MixinOverwriteAnnotation(aliases, logger);
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

        if ((access & Opcodes.ACC_PRIVATE) == 0 && this.aliases != null && !this.aliases.isEmpty() && !this.naggedInvalidAlias) {
            this.naggedInvalidAlias = true;
            this.logger.warn(MixinShadowAnnotation.class, "The @Overwrite annotated member {}.{} {} defines an alias for a non-private method. While this behaviour is supported in micromixin-transformer, it isn't supported in the spongeian mixin implementation. This may represent a compatibility hazard. For more information, see the javadocs on aliases for the Overwrite annotation. Note that the access modifier of your mixin member is not of relevance, just the modifier of the member that is being aliased.", source.getOwner().name, source.getName(), source.getDesc());
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
