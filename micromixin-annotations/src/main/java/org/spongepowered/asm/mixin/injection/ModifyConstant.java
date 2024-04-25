package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.spongepowered.asm.mixin.Mixin;

/**
 * The {@link ModifyConstant} annotation allows to apply a function on a single constant
 * after pushing the constant onto the operand stack. More specifically,
 * the mixin implementation will call the constant modifier handler with the original
 * constant value. The handler will then return the modified value which replaces the
 * original value on the operand stack.
 *
 * <h3>Chaining</h3>
 * While the original value can be constant, it is not guaranteed to be so.
 * Micromixin-transformer and the spongeian mixin implementation allow
 * {@link ModifyConstant} to "chain", that is multiple constant modifier handlers can
 * target the same constant and the result of one constant modifier handler will be used
 * as the input of the other. Or, in laymen's terms, the resulting code would look something
 * like follows:
 *
 * <blockquote><code>
 * modifyConstant0(modifyConstant1(CONSTANT))
 * </code></blockquote>
 *
 * In this case, modifyConstant0 has a lower {@link Mixin#priority() priority} than modifyConstant1
 * or applied before modifyConstant1 due to other (usually implementation-specific) reasons. If
 * the ordering of constant modifiers is important, then it is necessary to set the
 * mixin priority as necessary as otherwise the ordering is not guaranteed to stay constant, potentially
 * causing intermittent hard-to-reproduce bugs.
 *
 * <h3>Signature and visibility modifiers</h3>
 *
 * <p>The {@link ModifyConstant} handler (also known as the "constant modifier") MUST
 * declare the same return type (subtypes are not supported) as its argument
 * type (supertypes are not supported). If the targeted method is static, the handler MUST be static and private.
 * For non-static targeted methods the handler MUST NOT be static, but otherwise the accessibility
 * modifiers are not of relevance.
 
  <p>Locals and argument capture is not supported when using {@link ModifyConstant}.
 *
 * <h3>Example use and produced bytecode</h3>
 *
 * Assume the following target method:
 *
 * <blockquote><pre>
 * public static void main() {
 *   System.out.println("Test!");
 * }
 * </pre></blockquote>
 *
 * The above target method can be modified with following handler to instead produce "Hello World.":
 *
 * <blockquote><pre>
 * &#64;ModifyConstant(target = &#64;Desc("main"), constant = &#64;Constant(stringValue = "Test!"))
 * private static String modifyConstant(String originalValue) {
 *     return "Hello World.";
 * }
 * </pre></blockquote>
 *
 * This produces following bytecode:
 *
 * <blockquote><pre>
 * GETSTATIC java/lang/System.out Ljava/io/PrintStream;
 * LDC "Test!"
 * + INVOKESTATIC TargetClass.modifyConstant(Ljava/lang/String;)Ljava/lang/String;
 * INVOKEVIRTUAL java/io/PrintStream.println(Ljava/lang/String;)V
 * </pre></blockquote>
 *
 * which is equivalent to following code:
 *
 * <blockquote><pre>
 * public static void main() {
 *   System.out.println(modifyConstant("Test!"));
 * }
 * </pre></blockquote>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ModifyConstant {

    /**
     * The maximum amount of injection points that should be allowed. If the value of this
     * element is below 1 or if the value is below the {@link ModifyConstant#require() minimum amount}
     * of allowable injection points then the limit is not being enforced. However,
     * {@link ModifyConstant#expect()} has no influence on {@link ModifyConstant#allow()}.
     *
     * <p>Furthermore this limit is only valid per target class. That is, if multiple target classes are
     * defined as per {@link Mixin#value()} or {@link Mixin#targets()} then this limit is only applicable
     * for all the injection points in the targeted class. This limitation is caused due to the fact
     * that the targeted classes are not known until they are loaded in by the classloader, at which point
     * all the injection logic occurs.
     *
     * <p>This limit is shared across all methods (as defined by {@link ModifyConstant#method()} or
     * {@link ModifyConstant#target()}) targeted by the handler within a class.
     *
     * @return The maximum amount targeted of injection points within the target class.
     */
    public int allow() default -1;

    /**
     * The list of constants the constant modifier should target.
     *
     * <p>If multiple constants are provided or if the constant occurs multiple times
     * within the respective {@link Constant#slice() slice}, then all occurrences of
     * all constants are matched.
     *
     * <p>If the list is empty or undefined, then a special injection point selector
     * is used that matches any constant that matches the modifier method's accepted
     * type (be weary when using this feature with null values - this behaviour isn't
     * guaranteed to be stable for null values).
     *
     * <p>This list behaves quite similarly to {@link Inject#at()}
     * and micromixin-transformer's internal representation of this field
     * is mostly identical to {@link Inject#at()}. While doing so greatly
     * reduces expenses in required coding time, it has the unfortunate
     * side effect that micromixin-transformer cannot validate the
     * passed {@link Constant constants}. In other words: It trusts you
     * (the API consumer) to specify valid constants and will fail in unexpected
     * ways if this assumption doesn't hold true. HOWEVER, this behaviour
     * is subject to change and shouldn't be relied upon.
     *
     * @return A list of constants to target
     */
    public Constant[] constant() default {};

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
     * The minimum amount of injection points. If less injection points are found (as per {@link #constant()}).
     * an exception is thrown during transformation. The default amount of required injection points can be set
     * by mixin configuration file, but by default that is no minimum amount of required injection points.
     *
     * @return The minimum amount of injection points
     */
    public int require() default -1;

    /**
     * The available slices used for bisecting the available injection points declared by {@link #constant()}.
     *
     * @return An array of declared slices.
     */
    public Slice[] slice() default {};

    /**
     * The targeted methods.Only one method is picked from the list of provided methods.
     * As such the list should generally only be used to mark method aliases among others.
     *
     * @return The target selectors that define the target method of the handler.
     */
    public Desc[] target() default {};
}
