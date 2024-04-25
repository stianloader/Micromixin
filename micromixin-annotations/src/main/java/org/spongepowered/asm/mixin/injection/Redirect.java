package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.spongepowered.asm.mixin.Mixin;

/**
 * The {@link Redirect} annotation redirects a method call to the mixin implementation.
 * The method on whom the annotation is applied is referenced within the micromixin documentation as the redirect handler.
 *
 * <h2>Handler signature and arguments</h2>
 * The redirect handler must consume all arguments of the redirected method in the order they appear within the
 * redirected method. In case the redirected method is not static, the redirect handler must also consume the instance type
 * of the owner of the redirected method at the first position. If the redirected method returns a value, 
 *
 * <p>The redirect handler must be static regardless of the static-ness of the redirected method. As a logical consequence
 * the redirect handler must also be private.
 * These steps are required as {@link Redirect} does not modify the stack and replaces a single instruction
 * with another single instruction.
 *
  <p>Locals and argument capture is not supported when using {@link Redirect}.
 *
 * <h2>Field redirects</h2>
 * Aside from redirecting methods which is the main use of this annotation, {@link Redirect} is also capable of redirecting
 * read as well as write references to fields - static or not. However, this is not yet supported within micromixin at
 * the point of writing (28th of January 2024). Regardless, the signature of the handler would behave much as if the GETFIELD,
 * GETSTATIC, PUTFIELD or PUTSTATIC were replaced with appropriate getter or setter methods. The integrity of the
 * operand stack must be preserved.
 *
 * <h2>Array access redirects</h2>
 * Furthermore {@link Redirect} is capable of redirecting array accesses via the xALOAD and xASTORE opcode families.
 * Like field access redirects, this is also not yet implemented in micromixin at the point of writing (28th of January 2024).
 *
 * <p>When redirecting read access to an array of type <code>{type}</code>, the signature of the redirect handler is as
 * follows: <code>private static {type} handlerName({type}[] array, int index)</code>. Likewise write access would have
 * following signature: <code>private static void handlerName({type}[] array, int index, {type} element)</code>.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Redirect {

    /**
     * The maximum amount of injection points that should be allowed. If the value of this
     * element is below 1 or if the value is below the {@link Redirect#require() minimum amount}
     * of allowable injection points then the limit is not being enforced. However,
     * {@link Redirect#expect()} has no influence on {@link Redirect#allow()}.
     *
     * <p>Furthermore this limit is only valid per target class. That is, if multiple target classes are
     * defined as per {@link Mixin#value()} or {@link Mixin#targets()} then this limit is only applicable
     * for all the injection points in the targeted class. This limitation is caused due to the fact
     * that the targeted classes are not known until they are loaded in by the classloader, at which point
     * all the injection logic occurs.
     *
     * <p>This limit is shared across all methods (as defined by {@link Redirect#method()} or
     * {@link Redirect#target()}) targeted by the handler within a class.
     *
     * @return The maximum amount targeted of injection points within the target class.
     */
    public int allow() default -1;

    /**
     * The injection point where the injection should occur.
     * If the injection point does not match anything no exception is thrown by default (this default can be changed
     * through {@link #require()}), however transformation does still partially occur (Micromixin still copies the handler
     * into the target class anyways).
     *
     * <p>Unlike {@link Inject}, {@link Redirect}'s {@link #at()} is able to match multiple instructions with a single
     * {@link At}.
     *
     * @return The injection point.
     */
    public At at();

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
     * The targeted method selectors. The amounts of methods that may match and are selected is not bound to
     * any hard value and as such it should be limited by setting attributes such as {@link #require()} or
     * {@link #expect()} as otherwise the injector might accidentally not match anything with no way of knowing
     * what exactly went wrong.
     *
     * <p>The following are all valid formats of explicit target selectors:
     *
     * <ul>
     *  <li><code>targetMethod</code></li>
     *  <li><code>targetMethod(Lcom/example/Argument;)V</code></li>
     *  <li><code>(Lcom/example/Argument;)V</code></li>
     *  <li><code>targetMethod(I)Lcom/example/ReturnValue;</code></li>
     *  <li><code>targetMethod()Z</code></li>
     *  <li><code>Lcom/example/Target;targetMethod(Lcom/example/Argument;)V</code></li>
     *  <li><code>Lcom/example/Target;(Lcom/example/Argument;)V</code></li>
     *  <li><code>Lcom/example/Target;targetMethod(Lcom/example/Argument;) V</code></li>
     *  <li><code>Lcom/example/Target;target Method(Lcom/example/Argument;)V</code></li>
     *  <li><code>Lcom/example/Target;targetMethod(Lcom/exam ple/Argument;)V</code></li>
     * </ul>
     *
     * <p>The parts of the explicit target selector (owner, name, descriptor) must always have the same order,
     * but the individual parts must not necessarily be present.
     *
     * <p>While permissible, it is <strong>strongly discouraged</strong> to make use of whitespace in explicit
     * target selectors. When they are used, the spongeian mixin implementation (and also micromixin) will
     * discard all whitespace characters (tabs included). This is documented behaviour (in both micromixin
     * and sponge's mixin) and is unlikely to change in the future. This discouragement exists as this feature
     * may cause target selectors to be illegible.
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
     * <p>It is rather advisable to use {@link #target()} over {@link #method()}, especially for beginners,
     * since latter provides behaviour that can more easily be anticipated.
     *
     * @return The target selectors that define the target method of the handler.
     */
    public String[] method() default {};

    /**
     * The minimum amount of injection points. If less injection points are found (as per {@link #at()}).
     * an exception is thrown during transformation. The default amount of required injection points can be set
     * by mixin configuration file, but by default that is no minimum amount of required injection points.
     *
     * @return The minimum amount of injection points
     */
    public int require() default -1;

    /**
     * The slice to make use for the injection points defined by {@link #at()}.
     *
     * <p>The id of the slice is ignored as per the spongeian documentation, but this behaviour may not be
     * reflected by micromixin-transformer. It is as such not recommended to depend on this behaviour.
     * See {@link At#slice()} and {@link Slice#id()} for further information.
     *
     * @return The slice used to filter the possible instructions matched by injection points.
     */
    public Slice slice() default @Slice;

    /**
     * The targeted methods.Only one method is picked from the list of provided methods.
     * As such the list should generally only be used to mark method aliases among others.
     *
     * @return The target selectors that define the target method of the handler.
     */
    public Desc[] target() default {};
}
