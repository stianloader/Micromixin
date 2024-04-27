package org.spongepowered.asm.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Mutable} may only be applied to {@link Shadow shadowed} fields or on accessor methods.
 *
 * <p>When applied to a field, it removes the {@code final} modifier if the field has it,
 * thus allowing to change the value of the field (mutation). There is no real benefit of using {@link Mutable}
 * over RAS, AW or AT other than the fact that usage of {@link Mutable} does not differ from toolchain
 * to toolchain (e.g. in the stianloader toolchain only RAS is acceptable, where as elsewhere RAS
 * is not in frequent use). As such technically speaking usage of {@link Mutable} is slightly better compared
 * to other approaches, but is not in use throughout the stianloader projects and thus may be more unstable
 * than other annotations implemented by micromixin-transformer.
 */
// TODO: Whenever Accessor or Invoker is added, update this to reflect standard usage fully.
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mutable {
}
