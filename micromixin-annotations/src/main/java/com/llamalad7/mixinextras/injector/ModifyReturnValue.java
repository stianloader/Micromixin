package com.llamalad7.mixinextras.injector;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The {@link ModifyReturnValue} annotation allows to apply a function on the
 * return value of a method. More specifically, the mixin implementation will
 * call the modify return value handler with the original return value and
 * return the actual return value.
 *
 * <h3>Availability</h3>
 *
 * <p>This annotation is not available as-is on the spongeian mixin implementation
 * and is part of Llamalad7's MixinExtras. Please refer to your launcher
 * documentation for more details. Recent release of SLL's launcher-sponge
 * variant should come with MixinExtras already bundled by default.
 * However, as MixinExtras is widely used in the minecraft scene it is very
 * likely that your loader supports it, provided you are either using SLL or
 * a loader written for minecraft (such as neoforge or fabric).
 *
 * <h3>ModifyReturnValue versus Inject</h3>
 *
 * <p>Usage of {@link ModifyReturnValue} should be preferred over other
 * annotations such as {@link Inject} for it's supposed capability of being
 * easily and safely chained. In truth we do not see if that is really the case
 * under the micromixin-transformer implementation. Usage of
 *
 * <blockquote><code>
 * &#64;Inject(...)
 * private void handleCallback(CallbackInfoReturnable&lt;T&gt; cir) {
 *      cir.setReturnValue(apply(cir.getReturnValue()));
 * }
 * </code></blockquote>
 *
 * can and should be replaced with
 *
 * <blockquote><code>
 * &#64;ModifyReturnValue(...)
 * private T handleCallback(T original) {
 *      return apply(original);
 * }
 * </code></blockquote>
 *
 * <p>Aside from the aforementioned improvements this annotations does not
 * (as seen above) allocate a {@link CallbackInfoReturnable} and is as such more performant.
 * It should as such be used in hot code whenever appropriate.
 * While the assumption that the CallbackInfoReturnable allocation will get
 * inlined or otherwise optimized in the JVM may be true, some JVMs might
 * not follow this and will show a notable performance gain when using
 * this annotation over {@link Inject}.
 *
 * <h3>Signature and visibility modifiers</h3>
 *
 * <p>The {@link ModifyReturnValue} handler (also known as the "return value modifier") MUST
 * declare the same return type (subtypes are not supported) as the return type of the targeted
 * method. If the targeted method is static, the handler MUST be static and private.
 * For non-static targeted methods the handler MUST NOT be static, but otherwise the accessibility
 * modifiers are not of relevance.
 *
 * <p>The handler should furthermore also have the original return type (supertypes are not supported)
 * as it's argument.
 
  <p>Locals and argument capture is not supported when using {@link ModifyReturnValue}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface ModifyReturnValue {

    /**
     * The injection points where the injection should occur.
     * If none of the injection points apply no exception is thrown by default (this default can be changed
     * through {@link #require()}), however transformation does not occur (Micromixin still copies the handler into
     * the target class anyways).
     *
     * <p>For {@link ModifyReturnValue} the injection points MUST target return instructions. If that is not the
     * case, the class will fail to transform.
     *
     * @return The injection points.
     */
    public At[] at();

    /**
     * The expected amount of injection points. This behaves similar to {@link #require()}, however
     * while {@link #require()} will cause a class file transformation failure, {@link #expect()}
     * is a weaker form of it. Under the spongeian implementation, this attribute behaves like
     * {@link #require()} if and only if the appropriate debug flags are activated. The micromixin transformer
     * will meanwhile "just" unconditionally write a warning to the logger.
     *
     * <p>This attribute should be used to identify potentially outdated injectors.
     *
     * @return The expected amount of injection points
     */
    public int expect() default -1;

    /**
     * The targeted method selectors. Only one method is picked from the list of provided methods.
     * As such the list should generally only be used to mark method aliases among others.
     * The following are all valid formats of explicit target selectors:
     *
     * <ul>
     *  <li><code>targetMethod</code></li>
     *  <li><code>targetMethod(Lcom/example/Argument;)V</code></li>
     *  <li><code>(Lcom/example/Argument;)V</code></li>
     *  <li><code>targetMethod(I)Lcom/example/ReturnValue;</code></li>
     *  <li><code>targetMethod()Z</code></li>
     *  <li><code>Lcom/example/Target;targetMethod(Lcom/example/Argument;)V</code></li>
     *  <li><code>Lcom/example/Target;(Lcom/example/Argument;)V</code></li>
     * </ul>
     *
     * <p>The parts of the explicit target selector (owner, name, descriptor) must always have the same order,
     * but the individual parts must not necessarily be present.
     *
     * <p>It is generally recommended to not be lazy when it comes to explicit selectors,
     * the more information is provided the better. Information that is not supplied is comparable
     * to a wildcard - the first matching method will be targeted, even if nonsense.
     * It is especially not recommended to discard the method name, even if that is theoretically valid.
     *
     * <p>The spongeian implementation also supports schemes other than the explicit selectors.
     * However the Micromixin implementation only supports explicit selectors as documented above.
     * Where as the spongeian implementation supports quantifiers in explicit selectors,
     * Micromixin does not support them (yet). As such, quantifiers are not included in the documentation.
     *
     * <p>MixinExtra does not support target matching via {@link Desc}. It is likely that this feature will never
     * be implemented by MixinExtra as the developer finds that feature superfluous. As such, the micromixin
     * transformer is unlikely to implement that feature either, at least for as long as Mixin implementations
     * based on the spongeian mixin implementation remain viable in the galimulator modding space.
     *
     * @return The target selectors that define the target method of the handler.
     */
    public String[] method();

    /**
     * The minimum amount of amount of injection points. If less injection points are found (as per {@link #at()}).
     * an exception is thrown during transformation. The default amount of required injection points can be set
     * by mixin configuration file, but by default that is no minimum amount of required injection points.
     *
     * @return The minimum amount of injection points
     */
    public int require() default -1;
}
