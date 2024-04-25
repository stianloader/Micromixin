package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.spongepowered.asm.mixin.Mixin;

/**
 * The {@link ModifyArg} annotation allows to apply a function on a single
 * argument used to call a method. More specifically, the mixin implementation
 * will call the argument modifier handler with the original argument value.
 * The handler will then return the modified value with whom the targeted method
 * is called with.
 *
 * <p>A single {@link ModifyArg} handler can only modify a single argument.
 * If multiple arguments are to be modified, multiple handlers are required.
 * Alternatively, different (as of yet unimplemented) annotations can be used,
 * for example ModifyArgs.
 *
 * <h3>Local variable use</h3>
 *
 * <p>The micromxin-transformer implementation tries to avoid usage of local variables
 * when using ModifyArg. However this is not always possible. For the target descriptor
 * (ICDLjava/lang/Object;I)V and an index of 2 the transformer will produce following bytecode:
 *
 * <blockquote><code>
 * ISTORE index0<br>
 * ASTORE index1<br>
 * DUP2_X2<br>
 * POP2<br>
 * ALOAD 0<br>
 * SWAP<br>
 * invokevirtual Clazz.handler(C)C<br>
 * DUP2_X2<br>
 * POP2<br>
 * ALOAD index1<br>
 * ILOAD index0<br>
 * invokeX Clazz.original(ICDLjava/lang/Object;I)V<br>
 * </code></blockquote>
 *
 * <p>While micromixin-transformer uses opcodes such as DUP or POP aggressively, the spongeian implementation
 * will use xSTORE/xLOAD everywhere.
 *
 * <h3>Signature and visibility modifiers</h3>
 *
 * <p>The {@link ModifyArg} handler (also known as the "argument modifier") MUST
 * declare the same return type (subtypes are not supported) as its argument
 * type (supertypes are not supported). If the targeted method is static, the handler MUST be static and private.
 * For non-static targeted methods the handler MUST NOT be static, but otherwise the accessibility
 * modifiers are not of relevance.
 
  <p>Locals and argument capture is not supported when using {@link ModifyArg}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ModifyArg {

    /**
     * The maximum amount of injection points that should be allowed.
     * If the value of this element is below 1 or if the value is below the {@link ModifyArg#require() minimum amount}
     * of allowable injection points then the limit is not being enforced. However, {@link ModifyArg#expect()}
     * has no influence on {@link ModifyArg#allow()}.
     *
     * <p>Furthermore this limit is only valid per target class. That is, if multiple target classes are
     * defined as per {@link Mixin#value()} or {@link Mixin#targets()} then this limit is only applicable
     * for all the injection points in the targeted class. This limitation is caused due to the fact
     * that the targeted classes are not known until they are loaded in by the classloader, at which point
     * all the injection logic occurs.
     *
     * <p>This limit is shared across all methods (as defined by {@link ModifyArg#method()} or {@link ModifyArg#target()})
     * targeted by the handler within a class.
     *
     * @return The maximum amount targeted of injection points within the target class.
     */
    public int allow() default -1;

    /**
     * The injection point where the injection should occur.
     * If none of the injection points apply no exception is thrown by default (this default can be changed
     * through {@link #require()}), however transformation does not occur (Micromixin still copies the handler into
     * the target class anyways though).
     *
     * <p>Note that contrary to other annotations provided by Mixins or MixinExtra, {@link ModifyArg} can only
     * target a single {@link At}, however the spongeian mixin implementation as well as micromixin-transformer
     * allows this single {@link At} to match multiple instructions (as would be the case when using RETURN).
     *
     * <p>The injection points of the {@link ModifyArg} MUST point to MethodInsnNodes. The current micromixin-transformer
     * implementation as such forbids to target INVOKEDYNAMIC instructions with this annotation. Otherwise
     * INVOKEVIRTUAL, INVOKESPECIAL, INVOKEINTERFACE and INVOKESTATIC are all valid targets.
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
     * The ordinal of the argument to capture for the argument modifier handler.
     *
     * <p>The index does not care about the computational type category of the arguments.
     * That is both doubles and ints are both treated as having the size of 1.
     *
     * <p>A value of -1 means that the ordinal is automatically evaluated based on the argument type.
     * Should multiple arguments match, an error is thrown.
     *
     * @return The ordinal of the argument to capture, or -1 to automatically choose the ordinal
     */
    public int index() default -1;

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
