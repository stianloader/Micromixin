package org.spongepowered.asm.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Mutable} can only be applied to {@link Shadow}ed fields or methods.
 * <p>
 * When applied to a field, it removes the {@code final} modifier if the field is marked with it, thus making the field mutable.
 */
// TODO: Whenever Accessor or Invoker is added, update this to reflect standard usage fully.
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mutable {
}
