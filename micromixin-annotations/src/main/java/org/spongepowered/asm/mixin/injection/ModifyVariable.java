package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.spongepowered.asm.mixin.Mixin;

/**
 * The {@link ModifyVariable} annotation allows to apply a function on a single
 * local variable at an arbitrary point during execution. More specifically,
 * the mixin implementation will call the variable modifier handler with the
 * original value of the local variable. The return value of the handler is
 * then used to set the value of the local variable.
 * The point in which the function is applied is dictated through {@link ModifyVariable#at()}.
 *
 * <p>As method arguments are also local variables, {@link ModifyVariable} can be
 * used to modify <em>incoming</em> (that is those of the injected method) arguments.
 * In order to modify <em>outgoing</em> arguments (that is arguments that are
 * passed to methods invoked by the injected method), {@link ModifyArg} should be used.
 *
 * <h3>Signature and visibility modifiers</h3>
 *
 * <p>The {@link ModifyVariable} handler (also known as the "variable modifier") MUST
 * declare the same return type (subtypes are not supported) as the type of the variable
 * it modifies (supertypes are not supported). If the targeted method is <code>static</code>,
 * the handler MUST be <code>static</code> and <code>private</code>. For non-<code>static</code>
 * targets the access modifiers are not of relevance.
 *
 * <p>Locals capture is not supported when using {@link ModifyVariable}.
 * However, arguments may be captured by appending them to the list of arguments
 * of the handler method.
 *
 * <h3>Effects on the target method</h3>
 *
 * <p>This injector annotation, once applied will produce code similar to follows:
 *
 * <blockquote><code>
 * ACONST null
 * ASTORE 0
 * ALOAD 0
 * INVOKESTATIC TargetClass.modifyVariableHandler(Ljava/lang/Object;)V
 * ASTORE 0
 * </code></blockquote>
 *
 * which corresponds to following source code:
 *
 * <blockquote><code>
 * Object variable = null;
 * variable = modifyVariableHandler(variable);
 * </code></blockquote>
 *
 * The variable modifier method would look a bit like follows:
 * <blockquote><code>
 * &#64;ModifyVariable()
 * private static Object modifyVariableHandler(Object original) {
 *   return original;
 * }
 * </code></blockquote>
 *
 * <h3>Other notes and potential pitfalls</h3>
 *
 * <p>A single {@link ModifyVariable} can modify only a single variable, but can modify it multiple times
 * (as per it's {@link #at()}).
 *
 * <p>{@link ModifyVariable} supports an injection point selector that is strictly
 * unique to {@link ModifyVariable}: <code>LOAD</code> and <code>STORE</code>. These injection
 * point selectors target the <code>xLOAD</code> or <code>xSTORE</code> instructions of the
 * selected local variable (mind you that {@link ModifyVariable} can only select a single
 * variable at a single time, so it is not possible to select ALL <code>xLOAD</code> or
 * <code>xSTORE</code> instructions using <code>LOAD</code> and <code>STORE</code>). These injection
 * point selectors can be used by setting the appropriate {@link At#value()} to either
 * <code>LOAD</code> or <code>STORE</code>.
 *
 * <p><b>Unlike any other injection point selectors, <code>STORE</code> selects the instruction after
 * the <code>xSTORE</code> instruction.</b> Please be aware of this when using an {@link At#value()}
 * of STORE. This behaviour exists as otherwise modifications to the variable will be immediately overwritten,
 * even in cases of <code>variable = variable * 4;</code>. Note that <code>LOAD</code> behaves like most other
 * injection point selectors and targets the instruction before the <code>xLOAD</code> instruction(-s).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ModifyVariable {

    /**
     * The maximum amount of injection points that should be allowed.
     * If the value of this element is below 1 or if the value is below the {@link ModifyVariable#require() minimum amount}
     * of allowable injection points then the limit is not being enforced. However, {@link ModifyVariable#expect()}
     * has no influence on {@link ModifyVariable#allow()}.
     *
     * <p>Furthermore this limit is only valid per target class. That is, if multiple target classes are
     * defined as per {@link Mixin#value()} or {@link Mixin#targets()} then this limit is only applicable
     * for all the injection points in the targeted class. This limitation is caused due to the fact
     * that the targeted classes are not known until they are loaded in by the classloader, at which point
     * all the injection logic occurs.
     *
     * <p>This limit is shared across all methods (as defined by {@link ModifyVariable#method()} or
     * {@link ModifyVariable#target()}) targeted by the handler within a class.
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
     * <p>The injection points of the {@link ModifyVariable} can target any arbitrary instruction, however
     * attempting to target an instruction that lies outside the lifetime of the local variable may have
     * adverse effects and may cause the resulting class file to fail to validate or even cause
     * transformation failures.
     *
     * <p>{@link ModifyVariable} supports an injection point selector that is strictly
     * unique to {@link ModifyVariable}: <code>LOAD</code> and <code>STORE</code>. These injection
     * point selectors target the <code>xLOAD</code> or <code>xSTORE</code> instructions of the
     * selected local variable (mind you that {@link ModifyVariable} can only select a single
     * variable at a single time, so it is not possible to select ALL <code>xLOAD</code> or
     * <code>xSTORE</code> instructions using <code>LOAD</code> and <code>STORE</code>). These injection
     * point selectors can be used by setting the appropriate {@link At#value()} to either
     * <code>LOAD</code> or <code>STORE</code>.
     *
     * <p><b>Unlike any other injection point selectors, <code>STORE</code> selects the instruction after
     * the <code>xSTORE</code> instruction.</b> Please be aware of this when using an {@link At#value()}
     * of STORE. This behaviour exists as otherwise modifications to the variable will be immediately overwritten,
     * even in cases of <code>variable = variable * 4;</code>. Note that <code>LOAD</code> behaves like most other
     * injection point selectors and targets the instruction before the <code>xLOAD</code> instruction(-s).
     *
     * @return The injection point targeted by this {@link ModifyVariable} handler.
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
     * The names of the local variable that is to be modified by the {@link ModifyVariable}-annotated
     * variable modifier.
     *
     * <p>This property only takes effect if no ordinal or index is defined (ordinals and indices are not
     * supported in micromixin-transformer as of 2024-05-14, nor are they present in micromixin-annotations).
     * Further, this property requires the presence of the <em>LVT</em> (local variable table) and is as such
     * unlikely to be present in obfuscated environments where either the local variable table is outright
     * missing or contains bogus values (in minecraft, the local variable table contains bogus names,
     * where as in galimulator it is stripped). Using decompiler-provided names will <em>not</em> work
     * in these circumstances.
     *
     * <p>When providing multiple names, only one will be matched. Neither micromixin-transformer nor the
     * spongeian mixin implementation make any guarantees which name will be picked if multiple names were
     * to exist.
     *
     * <p>Micromixin-transformer may not properly handle cases where local variables are defined multiple
     * times under the same name (this is commonly the case for the counter variable of for loops), please
     * exercise caution in this circumstance.
     *
     * <p>Both micromixin-transformer and the spongeian implementation support implicit local variable
     * selection, meaning that this property is solely a filter to narrow down the amounts of instructions
     * that is spit out by the {@link #at() injection point selector}. The type of the local variable
     * and that of the handler method is of course another filter.
     *
     * @return The possible names of the selected variable.
     */
    public String[] name() default {};

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
     * The targeted methods. Only one method is picked from the list of provided methods.
     * As such the list should generally only be used to mark method aliases among others.
     *
     * @return The target selectors that define the target method of the handler.
     */
    public Desc[] target() default {};
}
