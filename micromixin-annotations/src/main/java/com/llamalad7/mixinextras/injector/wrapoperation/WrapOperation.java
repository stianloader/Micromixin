package com.llamalad7.mixinextras.injector.wrapoperation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;

import com.llamalad7.mixinextras.sugar.Cancellable;

import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * <h3>Abstract</3>
 *
 * The {@link WrapOperation} annotation defines an injector which is capable of
 * wrapping a single instruction and modify it.
 *
 * <h3>WrapOperation versus {@link Redirect}</h3>
 *
 * <p>In a sense, {@link WrapOperation} behaves similar to {@link Redirect},
 * except that the original instruction callsite is preserved and can be used.
 * This also means that {@link WrapOperation} injectors are not mutually exclusive
 * to each other and thus are compatible, as long as the original operation is being called.
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
 * <h3>Signature and visibility modifiers</h3>
 *
 * <p>The {@link WrapOperation} handler MUST
 * declare the same return type (subtypes are not supported) as the return type of the targeted
 * instruction (if the instruction does not return an operand, use {@code void}).
 *
 * <p>The {@link WrapOperation} handler's argument list must be prefixed by the
 * input operands of the targeted instruction - including the receiver operand - in
 * the order they are used in the targeted instruction. The argument list is followed by
 * and argument of type {@link Operation}. The {@link Operation} argument should have the
 * same generic type as the result operand of the targeted instruction. For a method call
 * returning void, use {@link Void} - for a primitive such as {@code int}, use the boxed
 * equivalent (here {@link Integer}). While the generic signature of the {@link Operation}
 * argument is technically not enforced by the mixin transformer, failing to use the correct
 * generic signature can cause runtime issues (specifically, {@link ClassCastException}).
 * All arguments following the {@link Operation} argument are available for argument capture
 * (for example for {@link Cancellable}).
 *
 * <p>If the targeted method is <code>static</code>, the handler MUST be <code>static</code>
 * and <code>private</code>. For non-<code>static</code> targets the access modifiers are
 * not of relevance, except for constructors where the handler must be <code>static</code> when
 * not injecting immediately before the final return via <code>TAIL</code>.
 *
 * <h3>Compatible instructions</h3>
 *
 * Micromixin-transformer's implementation of {@link WrapOperation} may transform any of the following instructions:
 * <ul>
 *  <li>InvokeInterface</li>
 *  <li>InvokeSpecial (except for superconstructor calls)</li>
 *  <li>InvokeStatic</li>
 *  <li>InvokeVirtual</li>
 * </li>
 *
 * <p>However, stock MixinExtras can transform other instructions, too - for example (not exhaustive):
 * <ul>
 *  <li>AAStore</li>
 *  <li>AALoad</li>
 *  <li>AStore</li>
 *  <li>ALoad</li>
 *  <li>BAStore</li>
 *  <li>BALoad</li>
 *  <li>CAStore</li>
 *  <li>CALoad</li>
 *  <li>CheckCast</li>
 *  <li>DAStore</li>
 *  <li>DALoad</li>
 *  <li>DStore</li>
 *  <li>DLoad</li>
 *  <li>IAStore</li>
 *  <li>IALoad</li>
 *  <li>InstanceOf</li>
 *  <li>GetField</li>
 *  <li>GetStatic</li>
 *  <li>PutField</li>
 *  <li>PutStatic</li>
 *  <li>etc. etc.</li>
 * </ul>
 *
 * @since 0.9.0
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface WrapOperation {

    /**
     * The maximum amount of injection points that should be allowed. If the value of this
     * element is below 1 or if the value is below the {@link WrapOperation#require() minimum amount}
     * of allowable injection points then the limit is not being enforced. However,
     * {@link WrapOperation#expect()} has no influence on {@link WrapOperation#allow()}.
     *
     * <p>Furthermore this limit is only valid per target class. That is, if multiple target classes are
     * defined as per {@link Mixin#value()} or {@link Mixin#targets()} then this limit is only applicable
     * for all the injection points in the targeted class. This limitation is caused due to the fact
     * that the targeted classes are not known until they are loaded in by the classloader, at which point
     * all the injection logic occurs.
     *
     * <p>This limit is shared across all methods (as defined by {@link WrapOperation#method()})
     * targeted by the handler within a class.
     *
     * @return The maximum amount targeted of injection points within the target class.
     */
    public int allow() default -1;

    /**
     * The injection points where the injection should occur.
     * If none of the injection points apply no exception is thrown by default (this default can be changed
     * through {@link #require()}), however transformation does not occur (Micromixin still copies the handler into
     * the target class anyways).
     *
     * <p>For {@link WrapOperation} the injection points MUST target one of the following instructions:
     * <ul>
     *  <li><code>INVOKESTATIC</code></li>
     *  <li><code>INVOKESPECIAL</code> (<b>but constructor calls are not supported</b>)</li>
     *  <li><code>INVOKEVIRTUAL</code></li>
     *  <li><code>INVOKEINTERFACE</code></li>
     * </ul>
     * If that is not the case, the class will fail to transform.
     *
     * <p>Note that at this point in time, micromixin-transformer only supports {@link WrapOperation} on MethodInsnNodes,
     * which means that micromixin-transformer supports modifying less operations than stock MixinExtras.
     * In a similar vein, targeting <code>INVOKEDYNAMIC</code> instructions is not supported either.
     *
     * <p>When targeting an instruction, the instruction immediately before must be selected, i.e.
     * using <code>INVOKE</code> injection point (see {@link At#value()}). A <code>INVOKE_ASSIGN</code> injection
     * point is unsupported however (at least at this point), unless {@link Shift#BEFORE} is used.
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
     * <p>MixinExtras does not support target matching via {@link Desc}. It is likely that this feature will never
     * be implemented by MixinExtras as the developer finds that feature superfluous. While the micromixin-transformer
     * does implement that feature regardless, making use of the feature would require a custom annotation library
     * for as long as Mixin implementations based on the spongeian mixin implementation remain viable
     * in the galimulator modding space and the strict interoperability between the implementations is needed.
     *
     * @return The target selectors that define the target method of the handler.
     */
    String[] method();

    /**
     * The minimum amount of amount of injection points. If less injection points are found (as per {@link #at()}).
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
}
