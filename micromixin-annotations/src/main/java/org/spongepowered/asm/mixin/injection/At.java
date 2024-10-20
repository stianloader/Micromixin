package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@link At} annotation selects one or multiple instructions (depending on the circumstances no instructions
 * can also be matched) based on the parameters it was given to. What the selected instruction mean depends on the context.
 * For {@link Inject#at()} it is the instruction at which the injection should occur.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface At {

    /**
     * An enumeration defining the available modes of shifting as consumed by {@link At#shift()}.
     */
    public enum Shift {
        /**
         * No shift. The instruction defined by the injection point selector of the {@link At}
         * will be used as-is.
         */
        NONE,

        /**
         * Use the instruction that is immediately before the instruction selected by the injection
         * point selector defined by {@link At#value()} (with the corresponding injection point
         * constraints).
         */
        BEFORE,

        /**
         * Use the instruction that is immediately after the instruction selected by the injection
         * point selector defined by {@link At#value()} (with the corresponding injection point
         * constraints).
         */
        AFTER,

        /**
         * Use an instruction that is immediately before n instructions after the instruction
         * selected by the injection point selector defined by {@link At#value()} (with the corresponding
         * injection point constraints) if {@link At#by()} is a positive value, otherwise
         * select the instruction immediately before n instructions before the instruction selected
         * by the injection point selector, with n being the absolute value of {@link At#by()}.
         *
         * <p>Simplified, using this {@link Shift} mode results in arbitrary shifts by n instructions
         * (for n &gt; 0, the shift is forwards while for n &lt; 0 the shift is backwards. For n = 0
         * no shift occurs). This n is defined using {@link At#by()} (default value for that property is
         * 0).
         *
         * <p>Although permissible when using micromixin at this point in time, other mixin implementations
         * (and potentially future iterations of micromixin) may produce warnings or application failures
         * when using {@link Shift#BY} in combination with a {@link At#by()} that is a very large value.
         * As a rule of thumb, an {@link At#by()} smaller than -3 or larger than 3 are to be avoided.
         * Reason for this is that {@link At#by()} is rather blind, enabling the possibility of writing
         * injection points that are more unstable than they actually need to be.
         */
        BY;
    }

    /**
     * A list of arguments that can be consumed by the injection point provider.
     * These arguments are used if {@link #target()} and {@link #desc()} either do not apply
     * or are not descriptive enough.
     *
     * <p>The probably most common use of this attribute is for use in the CONSTANT
     * injection point. Examples of the syntax of this injection point are follows:
     * <ul>
     *  <li><code>nullValue=true</code></li>
     *  <li><code>intValue=8</code></li>
     *  <li><code>stringValue=name</code></li>
     * </ul>
     * Please be aware that the above examples may be a bit erroneous, but should still work for
     * micromixin. However, it has not yet been investigated as to why that is the case.
     * More specifically, micromixin-transformer supports the same keys as defined by the attributes
     * of the {@link Constant} annotation.
     *
     * <p>The exact syntax of the arguments differ from injection point provider to injection
     * point provider. The provider is defined via {@link #value()}. Consult the manual
     * of your injection point for further details.
     *
     * @return The arguments to pass to the injection point provider for further discrimination.
     */
    public String[] args() default { };

    /**
     * Define by how many instructions the selection should be shifted by.
     *
     * <p>The exact effects are explained in {@link Shift#BY} but simplified,
     * using {@link Shift#BY} for {@link At#shift()} results in arbitrary shifts by n
     * instructions (for n &gt; 0, the shift is forwards while for n &lt; 0 the shift is backwards.
     * For n = 0 no shift occurs). This n is defined using {@link At#by()} (default value for this
     * property is 0).
     *
     * <p>Although permissible when using micromixin at this point in time, other mixin implementations
     * (and potentially future iterations of micromixin) may produce warnings or application failures
     * when using {@link Shift#BY} in combination with a {@link At#by()} that is a very large value.
     * As a rule of thumb, an {@link At#by()} smaller than -3 or larger than 3 are to be avoided.
     * Reason for this is that {@link At#by()} is rather blind, enabling the possibility of writing
     * injection points that are more unstable than they actually need to be.
     *
     * <p><b>This annotation element has no effect if {@link At#shift()} is not set to {@link Shift#BY}.</b>
     * It is undefined whether it is an error to define an {@link At#by()} when {@link At#shift()} is not
     * {@link Shift#BY}.
     *
     * @return The amounts of instructions to shift the selection by.
     * @see Shift#BY
     */
    public int by() default 0;

    /**
     * The targeted signature. Usage of {@link Desc} may result in better compile-time error
     * and sanity checking, however depending on your tooling the difference between {@link #desc()} and
     * {@link #target()} may be indistinguishable in this regard.
     *
     * <p><b>Warning: Fabric's tiny-remapper does not support remapping this annotation. Proceed with caution.
     * </b> For more information, <a href="https://github.com/FabricMC/tiny-remapper/pull/109">see the PR that
     * introduced Desc remapping in tiny-remapper</a>.
     *
     * @return The constraint to use as a {@link Desc} annotation.
     * @see Inject#target()
     * @see At#target()
     */
    public Desc desc() default @Desc("");

    /**
     * At times the injection point selector defined by {@link #value()} points to an instruction adjacent
     * to the instruction that should actually be targeted, yet in some circumstances it can be infeasible
     * or even impossible to target the intended instruction directly. In this case, the instruction targeted
     * by the injection point selector defined by {@link #value()} can be "shifted" by a limited amount.
     *
     * <p>This annotation element only modifies the selection of this {@link At}, but will not mutate the
     * originally selected instruction, nor will it modify or move any other instruction. This annotation element
     * is primarily used to inject code after an injection point selector, e.g. after an <code>INVOKEVIRTUAL</code>
     * instruction matched by <code>INVOKE</code>.
     *
     * <p>By default, the instruction is not shifted. This corresponds to the behaviour of {@link Shift#NONE}.
     *
     * <p>It is not defined whether "virtual" instructions (for example LineInsnNodes, FrameInsnNodes
     * and LabelNodes) are counted towards the shift or whether they are ignored.
     * This behaviour may be subject to change depending on the behaviour of other mixin implementations
     * as well as the (un-)intuitive semantics of such counting. The spongeian mixin documentation
     * implicitly leaves this as an implementation detail.
     *
     * @return The type of shift to perform.
     */
    public Shift shift() default Shift.NONE;

    /**
     * The id of the {@link Slice} that should be passed to the injection point to narrow down the amount of
     * resulting matched instructions. This should be the preferred way of selecting one specific instruction
     * from a pool of otherwise similar instructions as this would be the least brittle option - provided the
     * slices are not used carelessly.
     *
     * <p>Note that micromixin-transformer as well as the accompanying documentation only partly implements
     * {@link Slice#id()} as well as {@link At#slice()}; significant derivations are to be expected between
     * micromixin-transformer's behaviour (as well as the documented behaviour) and the spongeian transformer
     * behaviour (as well as it's documentation). As such, usage of these attributes ought to be avoided in
     * environments where usage of micromixin-transformer is expected and the spongeian documentation should be
     * consulted before making use of this attribute.
     *
     * @return The id of the {@link Slice} to use, as per {@link Slice#id()}.
     */
    public String slice() default "";

    /**
     * <p>The target attribute is a discriminator with whom the chosen instructions can be
     * constrained with more specific accuracy if the injection point isn't descriptive enough
     * as-is. 
     * 
     * <p>This attribute should match to a method for <code>INVOKE</code>, <code>INVOKE_ASSIGN</code>
     * and <code>INVOKE_STRING</code>. It should match to a field for <code>FIELD</code> and match to
     * a class/descriptor (can be an array) for <code>NEW</code>.
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
     * <p>Warning: Depending on your platform and tooling, not specifying a fully qualified reference
     * may cause errors. On SLL there will be no issues at runtime (with either micromixin or the spongeian
     * mixin implementation), however other platforms and tooling may not be so considerate.
     * Especially remappers (which are recommended if not necessary in obfuscated environments) may struggle
     * with incompletely qualified targets.
     *
     * <p>Hint: Usage of {@link #desc()} should be preferred over {@link #target()} if possible.
     * However, beware that doing so may not be viable depending on your platform. Please
     * heed the advice provided in the documentation of {@link #desc()} and the documentation
     * of your platform and other tooling.
     *
     * @return A target selector to use as a constraint.
     * @see Inject#method()
     */
    public String target() default "";

    /**
     * Defines the type of instructions that should be matched.
     * The following values are all valid types:
     * <ul>
     *  <li><code>HEAD</code> - Before the first instruction</li>
     *  <li><code>RETURN</code> - Before all RETURN (including ARETURN, DRETURN, etc.) instructions</li>
     *  <li><code>TAIL</code> - Before the last RETURN (including ARETURN, DRETURN, etc.) instruction</li>
     *  <li><code>INVOKE</code> - Before all matching INVOKESTATIC, INVOKEVIRTUAL or INVOKESPECIAL instructions</li>
     *  <li><code>INVOKE_ASSIGN</code> - After all matching INVOKESTATIC, INVOKEVIRTUAL or INVOKESPECIAL instructions, where the return value is STORE'd immediately</li>
     *  <li><code>FIELD</code> - Before all matching GETFIELD, PUTFIELD, GETSTATIC or PUTSTATIC instructions</li>
     *  <li><code>NEW</code> - Before all matching NEW instructions</li>
     *  <li><code>INVOKE_STRING</code> - Like INVOKE, but instruction must only take in a string and return void. Furthermore, the instruction must be preceded by an LDC which is checked for a match.</li>
     *  <li><code>JUMP</code> - Before all matching jumping instructions (IFEQ, IFNE, IFLT, GOTO, JSR, etc. - if opcode is -1 it matches all)</li>
     *  <li><code>CONSTANT</code> - Before all matching constant instructions (ICONST_0, BIPUSH, SIPUSH, LDC, etc.)</li>
     * </ul>
     *
     * <p>The spongeian implementation supports fully qualified names
     * for custom injection points, however the micromixin implementation
     * does not support such behaviour.
     *
     * <p>Most injection points require additional arguments in order to resolve to an instruction.
     * These arguments are supplied alongside this argument and are to be specified in the {@link At} annotation.
     *
     * @return The type of instructions to match
     */
    public String value();
}
