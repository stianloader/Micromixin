package org.stianloader.micromixin.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * <h2>Abstract</h2>
 * 
 * The {@link CanonicalOverwrite} is a micromixin-specific derivation of the
 * traditional {@link Overwrite} annotation. This annotation has the same behavioural
 * semantics as {@link Overwrite} in the absence of a remapper or refmaps.
 * However, when a remapper is used, the handler method keeps its
 * &quot;canonical&quot; name, and a new helper method is spawned to keep the
 * semantics of {@link Overwrite}. Hence, this method is in a sense of hybrid
 * of {@link Unique} (which makes a method keep it's name for public methods),
 * and {@link Overwrite}.
 *
 * <p>It may not be applied on non-public or static methods, see the illegal
 * usecases for further details.
 *
 * <h2>Semantic approximation</h2>
 *
 * <em>The semantic approximation is an approximation of how the behaviour of this
 * annotation can be replicated using other annotations, but may not mirror
 * the inner workings of the annotation behind the scenes.</em>
 *
 * <p>In it's purest form, the behaviour of this annotation can be replicated
 * using a default/implicit overwrite + explicit {@link Overwrite} pair.
 * Henceforth you get the following two methods:
 *
 * <blockquote><pre><code>
 * // Note: No annotation here!
 * public void methodName() {
 *     // Method logic goes here.
 *     // This is the &quot;canonical&quot; part of the handler.
 * }
 *
 * &#64;Overwrite
 * public void methodName() {
 *     ((MyInterface) this).methodName();
 * }
 * </code></pre></blockquote>
 *
 * <p>The keen might realize that while this cannot be emulated with a single
 * mixin class, this behaviour can be achieved using two or more mixin classes.
 * This effect might make this annotation redundant, and it may be the only way
 * to replicate the behaviour of {@link CanonicalOverwrite} if the annotation is
 * not guaranteed to be supported under a given environment (please check out the
 * documentation of your environment and dependencies you rely on for hints about
 * this support). That being said, this requires the {@link Overwrite} to be applied
 * before the implicit overwrite. However, as the implicit overwrite does not have
 * strongly defined behaviour, it is possible for this emulation to not work under
 * certain environments. Further, the chance of mistakes is high and the intended
 * behaviour might be obfuscated by the boilerplate, ultimately making maintenance
 * more costly. Henceforth, {@link CanonicalOverwrite} should be used for this
 * exact usecase.
 *
 * <h2>Technical details</h2>
 *
 * <em>This paragraph is mostly specific to the implementation within
 * micromixin-transformer, other implementations may derive slightly from the
 * implementation details. However, the behaviour must be kept the same.
 * This is especially crucial to overrides in conjunction with subclassing.</em>
 *
 * <p>Within the remapper process, the handler method may not be renamed.
 * Instead, only {@link #method()} and {@link #target()} are renamed.
 * If neither {@link #method()} nor {@link #target()} are defined,
 * then {@link #target()} will be set to correspond to the method's
 * current name and descriptor, and the new {@link #target()} value
 * will be remapped from then on. The handler method's name stays
 * constant as established earlier.
 *
 * <h2>Important limitations &amp; words of warning</h2>
 *
 * <em>Usages of this annotation that are either discouraged or environments
 * in which this annotation might show unintended behaviour (though this
 * behaviour not being intended per-se but rather being a byproduct of other
 * constraints).</em>
 *
 * <p>This annotation is unlikely to behave as intended outside of the stianloader
 * toolchain. Especially, the remapping semantics described by this annotation
 * are unlikely to be supported by tiny-remapper (although tiny-remapper could
 * easily be extended to support this annotation) and are completely ignored
 * when making use of the annotation processor and/or refmaps. In general,
 * only micromixin-remapper can be reliably expected to process this annotation.
 *
 * <p>This annotation has the potential of polluting the target class if it is used
 * too frequently for little reason. Use {@link Overwrite} instead whenever applicable.
 * Do note that like {@link Overwrite}, {@link CanonicalOverwrite} completely overwrites
 * the target method. Thus it should not be used when an {@link Overwrite} is inappropriate,
 * that is, {@link CanonicalOverwrite} ought not replace one or more {@link Inject},
 * {@link ModifyArg}, or similar. Further this annotation should not be used as an alternative
 * to {@link Unique}.
 *
 * <h2>Illegal usecases</h2>
 *
 * <em>Usages of this annotation that are invalid and thus will raise either a remap-time
 * failure or a runtime failure. Implementors are encouraged to check against these
 * uses.</em>
 *
 * <p>Methods annotated with {@link CanonicalOverwrite} MUST be <code>public</code>.
 * All other visibility modifiers (such as protected, package-private or private) are considered
 * illegal within the scope of this constraint. This constraint is at its core arbitrary,
 * but is necessary to reduce the possibility of misuse of this annotation, as the ABI
 * (application binary interface) guarantees provided are only relevant when this constraint
 * is applied.
 *
 * <p>A method annotated with {@link CanonicalOverwrite} MUST implement a definition defined
 * by at least one interface implemented in the target class. As a logical consequence,
 * it is invalid to apply this annotation on a <code>static</code> method.
 * This constraint is at its core  arbitrary, but is necessary to reduce the possibility of
 * misuse of this annotation, as the ABI (application binary interface) guarantees provided
 * are only relevant when this constraint is applied.
 *
 * <p>The handler method (that is the method annotated with {@link CanonicalOverwrite}) and the
 * target method (that is the method that should be overwritten) must have the same descriptor.
 * It is not valid for them to differ in the descriptor, which means that the return value and
 * the argument types must match. However, the generic signatures may differ due to generic
 * erasure (but do note that mismatches may be dangerous nonetheless).
 */
@Documented
@Retention(CLASS)
@Target(METHOD)
public @interface CanonicalOverwrite {

    /**
     * The target selector string defining the method that should be overwritten.
     * That is, the selected method will delegate all it's calls to the handler method.
     *
     * <p>This parameter is mutually exclusive with {@link #target()}. Only one of
     * the two may be defined. However, it is also valid for none of the elements
     * to be set, in which case the overwritten method is assumed to have the same
     * descriptor and name as the annotated handler method.
     *
     * <p>Note: Like many other mixin annotations, the transformer can differ between
     * explicitly setting the value to <code>&quot;&quot;</code> and not setting the
     * value at all. This is because javac omits undefined element values and ignores
     * the default value. The default value advertised here is only required due to how
     * optional elements need to be defined in java source code but truth be told
     * there is no such default value in practice.
     *
     * @return The target selector string defining the method to overwrite.
     */
    public String method() default "";

    /**
     * The {@link Desc &#64;Desc} definition referring to the method that should be
     * overwritten.
     * That is, the selected method will delegate all it's calls to the handler method.
     *
     * <p>This parameter is mutually exclusive with {@link #method()}. Only one of
     * the two may be defined. However, it is also valid for none of the elements
     * to be set, in which case the overwritten method is assumed to have the same
     * descriptor and name as the annotated handler method.
     *
     * <p>Like in all other annotations, the default descriptor of the {@link Desc} is
     * implicitly set to <code>void.class</code>. If a non-void method is
     * overwritten, the descriptor <b>must</b> be explicitly set as a logical consequence
     * of this constant (i.e. blind) default. The descriptor of the overwritten method
     * and the descriptor of the target method must be the same, as defined by the
     * illegal usecases of {@link CanonicalOverwrite}.
     *
     * <p>Note: Like many other mixin annotations, the transformer can differ between
     * explicitly setting the value to <code>&#64;Desc(&quot;&quot;)</code> and not setting the
     * value at all. This is because javac omits undefined element values and ignores
     * the default value. The default value advertised here is only required due to how
     * optional elements need to be defined in java source code but truth be told
     * there is no such default value in practice.
     *
     * @return The method to overwrite.
     */
    public Desc target() default @Desc("");
}
