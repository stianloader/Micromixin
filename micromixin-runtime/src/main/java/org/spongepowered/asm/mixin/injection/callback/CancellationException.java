package org.spongepowered.asm.mixin.injection.callback;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;

/**
 * @deprecated micromixin-runtime is deprecated for removal. Use micromixin-annotations instead.
 * Reason for this is that mixin annotations have a retention policy of {@link RetentionPolicy#RUNTIME}.
 * In edge cases such as {@link Annotation#annotationType()}, the class needs to present at runtime.
 * This is especially compounded by JPMS not supporting packages being in two or more modules,
 * while micromixin-annotation having the {@code LocalCapture} class in the callback package.
 */
@Deprecated
public class CancellationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CancellationException(String message) {
        super(message);
    }
}
