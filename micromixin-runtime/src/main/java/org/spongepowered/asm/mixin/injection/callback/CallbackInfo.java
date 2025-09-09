package org.spongepowered.asm.mixin.injection.callback;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;

import org.jetbrains.annotations.NotNull;

/**
 * @deprecated micromixin-runtime is deprecated for removal. Use micromixin-annotations instead.
 * Reason for this is that mixin annotations have a retention policy of {@link RetentionPolicy#RUNTIME}.
 * In edge cases such as {@link Annotation#annotationType()}, the class needs to present at runtime.
 * This is especially compounded by JPMS not supporting packages being in two or more modules,
 * while micromixin-annotation having the {@code LocalCapture} class in the callback package.
 */
@Deprecated
public class CallbackInfo {

    @NotNull
    private final String name;
    private final boolean cancellable;
    private boolean cancelled;

    public CallbackInfo(@NotNull String name, boolean cancellable) {
        this.name = name;
        this.cancellable = cancellable;
    }

    @NotNull
    public String getId() {
        return this.name;
    }

    public void cancel() throws CancellationException {
        if (!this.cancellable) {
            throw new CancellationException("The call " + this.name + " is not cancellable.");
        }

        this.cancelled = true;
    }

    public final boolean isCancellable() {
        return this.cancellable;
    }

    public final boolean isCancelled() {
        return this.cancelled;
    }
}
