package org.spongepowered.asm.mixin.injection;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@link Constant} annotation marks a single constant value which should be selected.
 * This is used in accord with {@link ModifyConstant}, however the principles of this annotation
 * are shared with {@link At#args()} when using the CONSTANT injection point.
 *
 * <p>Declaring multiple constant values within a single {@link Constant} annotation will
 * not work and will cause the mixin to not apply.
 *
 * <p>Reminder: Micromixin-transformer (and any other transformer for that matter)
 * can see which attributes of an annotation have been set and which have not.
 * More specifically, the javac (TM) compiler does not include the default values of
 * annotation attributes, which allows micromixin-transformer to infer that the absence
 * of an attribute means that it has not been set (which in turn means that the
 * attribute is ignored).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Constant {
    /**
     * Defines the {@link Class} constant values to match.
     *
     * <p>Matches the LDC opcode.
     *
     * <p><b>Does not match GETSTATIC java/lang/Integer.TYPE or similar</b>,
     * which means that int.class or similar cannot be matched using
     * this approach. This means that not primitive classes can be matched
     * as "int.class" is represented using a GETSTATIC instruction in java
     * bytecode. That being said, this behaviour could change in future
     * versions of the spongeian mixin implementation or future versions
     * of micromixin as this flaw is not directly documented in the
     * spongeian documentation of this attribute.
     *
     * @return The {@link Class} constant to match.
     */
    public Class<?> classValue() default void.class;

    /**
     * Specify a double constant value to match
     *
     * <p>Matches opcodes such as LDC or DCONST_0.
     *
     * @return The double constant to match
     */
    public double doubleValue() default 0.0D;

    /**
     * Specify a float constant value to match
     *
     * <p>Matches opcodes such as LDC or FCONST_0.
     *
     * @return The float constant to match
     */
    public float floatValue() default 0.0F;

    /**
     * Specify the integer constant value to match.
     *
     * <p>Matches opcodes such as LDC, BIPUSH or ICONST_0.
     *
     * <p>Due to how types work in java bytecode, this can also match
     * booleans, bytes, shorts and chars, which do not have their own
     * types and are always represented as integers.
     *
     * <p>The spongeian documentation does not explicitly guarantee matching
     * booleans and chars - but the spongeian mixin implementation does
     * permit it anyways and the current behaviour is unlikely to change.
     *
     * <p>Be careful, directives such as {@code x == 0} or {@code x <= 0}
     * and other comparisons with the constant value 0 are compiled to their
     * own dedicated opcodes, which means that this attribute cannot
     * match 0 on it's own. HOWEVER, the spongeian mixin implementation
     * provides tools to capture such constants anyways, but micromixin-transformer
     * does not yet support these facilities.
     *
     * @return The integer constant to match
     */
    public int intValue() default 0;

    /**
     * Specify a long constant value to match
     *
     * <p>Matches opcodes such as LDC or LCONST_0. {@link #longValue()}
     * does not support matching ints, shorts, bytes, etc.
     *
     * @return The long constant to match
     */
    public long longValue() default 0L;

    /**
     * If true, null values are matched.
     *
     * <p>Matches the ACONST_NULL opcode.
     *
     * <p>Setting the attribute to false may result in undefined behaviour.
     * It is rather recommended to plainly remove this attribute instead of
     * defining it to false.
     *
     * <p>Matching null values is rather brittle due to the frequency of the
     * null values. Aside from that the type of a null value is not well defined
     * and must be inferred. This means that micromixin-transformer accepts
     * any object types when paired with {@link ModifyConstant}. However,
     * accepting and returning an object of type {@link Object} is still the
     * recommended approach when capturing null values. That being said,
     * other mixin implementations may behave differently and this property
     * is not well tested.
     *
     * @return True to match null values, false otherwise.
     */
    public boolean nullValue() default false;

    /**
     * The id of the {@link Slice} that should be passed to the injection point to narrow down the amount of
     * resulting matched instructions. This should be the preferred way of selecting one specific instruction
     * from a pool of otherwise similar instructions as this would be the least brittle option - provided the
     * slices are not used carelessly.
     *
     * <p>Note that micromixin-transformer as well as the accompanying documentation only partly implements
     * {@link Slice#id()} as well as {@link Constant#slice()}; significant derivations are to be expected between
     * micromixin-transformer's behaviour (as well as the documented behaviour) and the spongeian transformer
     * behaviour (as well as it's documentation). As such, usage of these attributes ought to be avoided in
     * environments where usage of micromixin-transformer is expected and the spongeian documentation should be
     * consulted before making use of this attribute.
     *
     * @return The id of the {@link Slice} to use, as per {@link Slice#id()}.
     */
    public String slice() default "";

    /**
     * Defines the string constant values to match.
     *
     * <p>Matches the LDC opcode.
     *
     * <p>The string value attribute must contain the entire string to match.
     * Do note that smaller, more frequent, strings could be matched multiple times.
     * If in doubt, you might want to make use of {@link #slice()} to reduce
     * the frequency in which a specific string is captured.
     *
     * @return The string constant to match
     */
    public String stringValue() default "";
}
