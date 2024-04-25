package org.spongepowered.asm.mixin.injection;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * The {@link Inject} annotation can be applied to methods that need to be called
 * by the given target method. The point(s) where the target method calls
 * the annotated method are called Injection points and are defined by {@link #at()}.
 * Meanwhile the annotated method is called the handler.
 *
 * <p>If the target method returns a void, the method should have a {@link CallbackInfo} as
 * it's argument. A sample method head would thus look like follows:
 * <blockquote><code>private void handleCallback(CallbackInfo ci)</code></blockquote>
 *
 * <p>For non-void target methods, {@link CallbackInfoReturnable} should be used instead.
 * The generic of that argument should be the same as the return type of the target method.
 * For primitive return types, the wrapper class should be used instead. Thus a method head
 * could look as follows:
 * <blockquote><code>private void handleCallback(CallbackInfoReturnable&lt;String&gt; cir)</code></blockquote>
 *
 * <p>If a {@link CallbackInfo} is used where a {@link CallbackInfoReturnable} is expected (or vice-versa),
 * an exception will be thrown during transformation. As such the transformation does not occur. Similarly if
 * no {@link CallbackInfo} or {@link CallbackInfoReturnable} are present in the arguments of the handler method,
 * an exception is thrown during transformation.
 *
 * <h3>Non-void returning handlers</h3>
 *
 * <p>A handler method should return <code>void</code> at all times. However, the Micromixin implementation supports
 * non-void return types - but they won't have any effect. Instead, they are <code>POP</code>/<code>POP2</code>'d off
 * the operand stack. <b>Be aware that the spongeian Mixin implementation also supports non-void handler methods,
 * but it does not pop the return value off. Depending on the method, that can lead to a {@link VerifyError}
 * once the target method is ran.</b> This behaviour is also "working as intended".
 *
 * <h3>Argument capture</h3>
 *
 * <p>Arguments can be captured irrespective of local capture. Arguments that need to be captured can simply be added
 * in front of the {@link CallbackInfo} or {@link CallbackInfoReturnable}. However, be aware that Micromixin
 * captures arguments rather naively, that is it does not expect local variable reuse. As the local variable table
 * is left ignored by Micromixin (but not the spongeian implementation), a transformer must be applied before the
 * Mixin that untangles the local variables (mostly be rewriting the XLOAD and XSTORE indices).
 *
 * <p>Neither the spongeian nor the Micromixin implementation support argument capture underflows or overflows.
 * As such the captured arguments must exactly be the arguments present in the targeted method. However,
 * this does not apply if the handler does not capture any arguments.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface Inject {

    /**
     * The maximum amount of injection points that should be allowed.
     * If the value of this element is below 1 or if the value is below the {@link Inject#require() minimum amount}
     * of allowable injection points then the limit is not being enforced. However, {@link Inject#expect()}
     * has no influence on {@link Inject#allow()}.
     *
     * <p>Furthermore this limit is only valid per target class. That is, if multiple target classes are
     * defined as per {@link Mixin#value()} or {@link Mixin#targets()} then this limit is only applicable
     * for all the injection points in the targeted class. This limitation is caused due to the fact
     * that the targeted classes are not known until they are loaded in by the classloader, at which point
     * all the injection logic occurs.
     *
     * <p>This limit is shared across all methods (as defined by {@link Inject#method()} or {@link Inject#target()})
     * targeted by the handler within a class.
     *
     * @return The maximum amount targeted of injection points within the target class.
     */
    public int allow() default -1;

    /**
     * The injection points where the injection should occur.
     * If none of the injection points apply no exception is thrown by default (this default can be changed
     * through {@link #require()}), however transformation does still partially occur (Micromixin still copies the handler
     * into the target class anyways).
     *
     * <p>Unlike many other annotations such as {@link Redirect} or {@link ModifyArg}, each {@link At} annotation may at
     * most represent a single instruction - that is if multiple instructions were to match the requirements set by the
     * annotation, only the first matching instruction will be used and the others are discarded.
     *
     * @return The injection points.
     */
    public At[] at();

    /**
     * Defines whether {@link CallbackInfo#cancel()} and {@link CallbackInfoReturnable#cancel()}
     * are valid - or better said: It defines the state of {@link CallbackInfo#isCancellable()}
     * and {@link CallbackInfoReturnable#isCancellable()}.
     *
     * <p>By default all injectors are not cancellable. Setting this to <code>true</code> allows to
     * cancel injectors.
     *
     * <p>Cancelled callbacks will return from the method, for {@link CallbackInfoReturnable}
     * it will return the appropriate (previously set) return value, although the return does not happen
     * until the handler has finished running (i.e. calling cancel does not interrupt the handler).
     *
     * @return True in order for the handler to be able to cancel the method, false otherwise.
     */
    public boolean cancellable() default false;

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
     * Obtains whether and how locals are captured, or whether locals should just be printed.
     * By default, local variables are not captured, as the capture requires analysis of the method. Such analysis can be
     * performance intensive and is not suited in a performance-intensive situation such as classloading.
     * <b>Local capture happens irrespective of the local variable table (LVT) of the method.</b> Instead ASM's
     * Verifier is used to obtain the local variables at any given instruction.
     *
     * <p>The local variables that can be captured can easily be viewed through {@link LocalCapture#PRINT},
     * which prints the local variables that can be captured. However, when doing so the injector is ignored.
     * {@link LocalCapture#PRINT} also prints the recommended method signature with whom all local variables
     * can be captured. Unneeded trailing arguments can be removed (up to the {@link CallbackInfo}/
     * {@link CallbackInfoReturnable}). Leading arguments cannot be removed, nor any arguments in between.
     *
     * <p>Arguments can be captured regardless of the local capture. Captured arguments need to be added before
     * the {@link CallbackInfo} or {@link CallbackInfoReturnable} argument, <b>captured locals need to be added
     * after that argument.</b>
     *
     * <p>Local capture cannot capture arguments - for that argument capture should be used.
     * In medium to highly obfuscated environments, where local variables share the same index as
     * arguments, that can be an issue. In that case another transformer must run before the mixin transformation
     * step. Said transformer must remove collisions with local variables.
     *
     * @return The {@link LocalCapture} type that describes how locals should be captured - if at all.
     */
    public LocalCapture locals() default LocalCapture.NO_CAPTURE;

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
     * The available slices used for bisecting the available injection points declared by {@link #at()}.
     *
     * @return An array of declared slices.
     */
    public Slice[] slice() default {};

    /**
     * The targeted methods. Only one method is picked from the list of provided methods.
     * As such the list should generally only be used to mark method aliases among others.
     *
     * @return The target selectors that define the target method of the handler.
     */
    public Desc[] target() default {};
}
