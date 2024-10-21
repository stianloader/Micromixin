package org.stianloader.micromixin.transform.internal.annotation;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinStub;

public abstract class MixinAnnotation<T> implements Comparable<MixinAnnotation<T>> {
    public abstract void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub, @NotNull T source, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder);

    public abstract void collectMappings(@NotNull T source, @NotNull HandlerContextHelper hctx, @NotNull ClassNode target, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder);

    @Override
    public int compareTo(MixinAnnotation<T> o) {
        return o.getClass().getName().compareTo(this.getClass().getName());
    }

    @Deprecated // Currently unused
    public boolean isCompatible(Collection<MixinAnnotation<T>> collection) {
        return true;
    }

    /**
     * Validate the mixin annotation against the member the annotation was applied on.
     * This method will be called after all annotations on the member have been parsed, but
     * the owner class may not have been parsed in it's entirety. The main purpose of this
     * validation method is to catch obviously flawed mixin handlers as early as possible.
     *
     * <p>As for larger modded environments this 'as early as possible' may in fact be too early
     * and get drowned out by other log statements, any logging statements made while executing
     * this method should be printed on application too, within reason (i.e.
     * {@link MixinLoggingFacade#info(Class, String, Object...)} calls have no real reason to
     * be repeated).
     *
     * <p>Note that at the point this method is being called, micromixin-transformer has no
     * knowledge of the class the mixin handler targets. If the target class or a member from there
     * is required to be known during validation, such validation should be done within
     * {@link #apply(ClassNode, HandlerContextHelper, MixinStub, Object, SimpleRemapper, StringBuilder)}
     * or {@link #collectMappings(Object, HandlerContextHelper, ClassNode, SimpleRemapper, StringBuilder)}.
     *
     * @param source The mixin handler source member which has this annotation annotated.
     * @param logger The logger to print warnings, errors or informational statements to.
     * @param sharedBuilder A shared {@link StringBuilder} instance that may be freely mutated (note: not thread-safe).
     */
    public void validateMixin(@NotNull T source, @NotNull MixinLoggingFacade logger, @NotNull StringBuilder sharedBuilder) {
        // It's valid by default.
    }
}
