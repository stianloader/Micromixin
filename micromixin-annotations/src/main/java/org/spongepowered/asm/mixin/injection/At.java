package org.spongepowered.asm.mixin.injection;

import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * The {@link At} annotation selects one or multiple instructions (depending on the circumstances no instructions
 * can also be matched) based on the parameters it was given to. What the selected instruction mean depends on the context.
 * For {@link Inject#at()} it is the instruction at which the injection should occur.
 */
@Documented
@Retention(CLASS)
public @interface At {

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
