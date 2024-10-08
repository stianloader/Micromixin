package org.stianloader.micromixin.transform.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.internal.selectors.inject.ReturnInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.TailInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;

public class SlicedInjectionPointSelector {

    @Nullable
    private final SlicedInjectionPointSelector from;

    /**
     * The offset (also commonly referred to as "shift") that the sliced injection point
     * selector has compared to the target provided by the origin {@link InjectionPointSelector}.
     *
     * <p>This offset is counted in "real" instructions. A positive value means that
     * the injection point is {@link #offset} instructions after the instruction
     * initially targeted by the {@link #selector}. A negative value means that the
     * instruction of the {@link SlicedInjectionPointSelector} is shifted before the
     * instruction of {@link #selector}. An offset of 0 means no shift occurs. Real
     * instructions are instructions where {@link AbstractInsnNode#getOpcode()} is
     * a value above 0.
     *
     * <p>The offset does not have any effect on the slices at this point in time,
     * but this behaviour could change in the future if deemed necessary.
     * Note: The offset of the slices themselves do still matter.
     */
    private final int offset;

    @NotNull
    private final InjectionPointSelector selector;
    @Nullable
    private final SlicedInjectionPointSelector to;

    /**
     * Whether this {@link SlicedInjectionPointSelector} allows use in an unsafe context,
     * that is whether it can match instructions that when they are transformed unsafe
     * behaviour could be the result therein.
     *
     * <p>Unsafe injection points generally refer to injection points targeting
     * instructions within the constructor before the final {@link Opcodes#RETURN RETURN} instruction,
     * meaning that only the <code>TAIL</code> injection point can safely target instructions
     * in a constructor. All other injection points must set the unsafe flag in order
     * to correctly target a constructor.
     */
    private final boolean unsafe;

    public SlicedInjectionPointSelector(@NotNull InjectionPointSelector selector, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to, int shift, boolean unsafe) {
        this.selector = selector;
        this.from = from;
        this.to = to;
        this.offset = shift;
        this.unsafe = unsafe;
    }

    /**
     * Obtains the first {@link AbstractInsnNode} that corresponds to the first applicable entrypoint within
     * the provided method as defined by this {@link InjectionPointSelector}. The {@link AbstractInsnNode} may
     * not be virtual, that is it may not have an {@link AbstractInsnNode#getOpcode() opcode} value of -1.
     *
     * <p>Implementations of this method are trusted to not go out of bounds when it comes to the slices.
     * Failure to do so could have nasty consequences for user behaviour as micromixin-transformer or any
     * other caller is not guaranteed to verify the location of the matched instruction (but inversely it is not
     * guaranteed that such as check won't be introduced in the future).
     *
     * @param method The method to find the entrypoints in.
     * @param remapper The remapper instance to make use of. This is used to remap any references of the mixin class to the target class when applying injection point constraints.
     * @param sharedBuilder Shared {@link StringBuilder} instance to reduce {@link StringBuilder} allocations.
     * @return The first matched instruction, or null if no instructions match.
     */
    @Nullable
    public AbstractInsnNode getFirstInsn(@NotNull MethodNode method, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        AbstractInsnNode insn = this.selector.getFirstInsn(method, this.from, this.to, remapper, sharedBuilder);
        if (this.offset == 0) {
            return insn;
        } else {
            return ASMUtil.shiftInsn(insn, this.offset);
        }
    }

    /**
     * Obtains the {@link AbstractInsnNode AbstractInsnNodes} that correspond to every applicable entrypoint within
     * the provided method as defined by this {@link SlicedInjectionPointSelector}.
     *
     * <p>Implementations of this method are trusted to not go out of bounds when it comes to the slices.
     * Failure to do so could have nasty consequences for user behaviour as micromixin-transformer or any
     * other caller is not guaranteed to verify the location of the matched instructions (but inversely it is not
     * guaranteed that such as check won't be introduced in the future).
     *
     * @param method The method to find the entrypoints in.
     * @param remapper The remapper instance to make use of. This is used to remap any references of the mixin class to the target class when applying injection point constraints.
     * @param sharedBuilder Shared {@link StringBuilder} instance to reduce {@link StringBuilder} allocations.
     * @return The selected instruction nodes that correspond to this entry point.
     */
    @NotNull
    public Collection<? extends AbstractInsnNode> getMatchedInstructions(@NotNull MethodNode method, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        Collection<? extends AbstractInsnNode> nodes = this.selector.getMatchedInstructions(method, this.from, this.to, remapper, sharedBuilder);
        if (this.offset == 0 || nodes.isEmpty()) {
            return nodes;
        }
        List<AbstractInsnNode> shiftedInsns = new ArrayList<AbstractInsnNode>();
        for (AbstractInsnNode insn : nodes) {
            shiftedInsns.add(ASMUtil.shiftInsn(insn, this.offset));
        }
        return shiftedInsns;
    }

    /**
     * The offset (also commonly referred to as "shift") that the sliced injection point
     * selector has compared to the target provided by the origin {@link InjectionPointSelector}.
     *
     * <p>This offset is counted in "real" instructions. A positive value means that
     * the injection point is {@link #offset} instructions after the instruction
     * initially targeted by the {@link #selector}. A negative value means that the
     * instruction of the {@link SlicedInjectionPointSelector} is shifted before the
     * instruction of {@link #selector}. An offset of 0 means no shift occurs. Real
     * instructions are instructions where {@link AbstractInsnNode#getOpcode()} is
     * a value above 0.
     *
     * <p>The offset does not have any effect on the slices at this point in time,
     * but this behaviour could change in the future if deemed necessary.
     * Note: The offset of the slices themselves do still matter.
     *
     * @return The shift of this {@link SlicedInjectionPointSelector} from the
     * underlying {@link SlicedInjectionPointSelector#selector injection point selector}
     * @since 0.6.0
     */
    @Contract(pure = true)
    public int getOffset() {
        return this.offset;
    }

    /**
     * Obtains the fully qualified name of the selector that is backing this {@link SlicedInjectionPointSelector}
     * as per {@link InjectionPointSelector#fullyQualifiedName}.
     *
     * <p>Note that the fully qualified name of the selectors that mark the {@link #from} and {@link #to} selectors
     * are considered irrelevant. Further, {@link #getOffset()} and {@link #isUnsafe()} are ignored
     * in the fully qualified name. As such, the returned string is insufficient of clearly denoting
     * which &#64;At annotation this {@link SlicedInjectionPointSelector} belongs to.
     *
     * @return The {@link InjectionPointSelector#fullyQualifiedName fully qualified name} of the injection point
     * selector used by this instance.
     * @since 0.6.0
     */
    @NotNull
    @Contract(pure = true)
    public String getQualifiedSelectorName() {
        return this.selector.fullyQualifiedName;
    }

    /**
     * Get whether this {@link SlicedInjectionPointSelector} allows use in an unsafe context,
     * that is whether it can match instructions that when they are transformed unsafe
     * behaviour could be the result therein.
     *
     * <p>Unsafe injection points generally refer to injection points targeting
     * instructions within the constructor before the final {@link Opcodes#RETURN RETURN} instruction,
     * meaning that only the <code>TAIL</code> injection point can safely target instructions
     * in a constructor while also capturing the <code>this</code> instance. All other injection
     * points must set the unsafe flag in order to correctly target a constructor and capture
     * the 'this' instance while doing so.
     *
     * <p>If a injection point selector targets an unsafe instruction while not being
     * allowed to do so via {@link #isUnsafe()}, then an error will be emitted.
     *
     * @return True if unsafe targets are permitted, false otherwise.
     * @since 0.6.0
     */
    @Contract(pure = true)
    public boolean isUnsafe() {
        return this.unsafe;
    }

    /**
     * Whether to support the usage of implicitly capturing the local variable '<code>this</code>'
     * while injecting into a constructor. In other terms, if this method returns
     * <code>false</code>, then the handler method for this annotation must be <code>static</code>
     * if the handler method injects into the <code>&lt;init;gt;</code> method (i.e. the constructor).
     *
     * <p>If this method returns true, then the handler method can still be <code>static</code>,
     * but it being so is not required.
     *
     * <p>The main reason this behaviour exists is because partially initialized instances shouldn't
     * be leaked and in some cases (e.g. when injecting before the superconstructor) plainly cannot be
     * leaked.
     *
     * <p>By default, the final {@link Opcodes#RETURN RETURN} instruction is assumed to be the only
     * point in time where an object is fully initialized, and henceforth only the <code>TAIL</code>
     * injection point will be treated as a safe injection point. However, this method may still
     * return <code>true</code> if {@link #isUnsafe() the selector's unsafe flag} is set.
     *
     * @return True to allow non-static handlers targeting constructors, false otherwise.
     * @since 0.6.2
     */
    public boolean supportsInstanceCaptureInConstructors() {
        return this.unsafe || this.selector == TailInjectionPointSelector.INSTANCE || this.selector == ReturnInjectionPointSelector.INSTANCE;
    }

    /**
     * <b>WARNING WARNING WARNING</b> The method was deprecated for removal in the 0.6.X lifecycle and
     * will get removed at an unspecified point in time (this might fall within the 0.7.X lifecycle!).
     * This method is no longer considered to be part of the public API.
     *
     * <p>Checks whether the injection point can be used in conjunction with the Redirect-annotation.
     * Generally should only be true if this injection point selector can select method instruction nodes.
     *
     * @return True if usable in redirects, false otherwise.
     * @deprecated Redirect in theory (not implemented in micromixin-transformer as of now) also supports redirecting
     * PUTFIELD/GETFIELD, PUTSTATIC/GETStATIC, xALOAD and the ARRAYLENGTH instructions. Added with the introduction of
     * slices which make the semantics of the matched instruction type less predictable at first glance, this method
     * has little sense going forward.
     */
    @Deprecated
    @ScheduledForRemoval
    @ApiStatus.Internal
    public boolean supportsRedirect() {
        return this.selector.supportsRedirect();
    }

    @Override
    @NotNull
    public String toString() {
        return "SlicedInjectionPointSelector[" + this.selector.fullyQualifiedName + ", from = " + this.from + ", to = " + this.to + ", off = " + this.offset + ", unsafe = " + this.unsafe + "]";
    }

    /**
     * Verify the integrity of a sliced injection point selector.
     * At the core this method is responsible for ensuring that the {@link #getFrom() from}
     * and the {@link #getTo() to} injection points exist and are in a valid order
     * - that is {@link #getTo() to} may not match an instruction before {@link #getFrom() from}.
     *
     * <p>If the {@link SlicedInjectionPointSelector} is not valid, then an exception is thrown
     * with adequate information that ensures that the root cause can be properly traced back or otherwise
     * debugged adequately.
     *
     * @param path A LIFO {@link Queue} storing the path to this {@link SlicedInjectionPointSelector}, used to produce the debug message in case of a crash
     * @param method The {@link MethodNode} that should be probed by the {@link SlicedInjectionPointSelector}.
     * @param remapper A {@link SimpleRemapper} instance. This is used to remap any references of the mixin class to the target class when applying injection point constraints.
     * @param sharedBuilder Shared {@link StringBuilder} instance to reduce {@link StringBuilder} allocations.
     * @throws IllegalStateException If the validation fails.
     */
    public void verifySlices(@NotNull Queue<String> path, @NotNull MethodNode method, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        SlicedInjectionPointSelector from = this.from;
        if (from != null) {
            path.add("from");
            from.verifySlices(path, method, remapper, sharedBuilder);
            path.remove();
        }
        SlicedInjectionPointSelector to = this.to;
        if (to != null) {
            path.add("to");
            to.verifySlices(path, method, remapper, sharedBuilder);
            path.remove();
        }

        try {
            AbstractInsnNode fromInsn = method.instructions.getFirst();
            AbstractInsnNode toInsn = method.instructions.getLast();
            if (from != null) {
                fromInsn = from.getFirstInsn(method, remapper, sharedBuilder);
            }
            if (to != null) {
                toInsn = to.getFirstInsn(method, remapper, sharedBuilder);
            }

            // Check whether from and to even exist
            if (fromInsn == null) {
                throw new IllegalStateException("Slice does not apply: 'from' is a null instruction (wrong target method?). From slice: " + this.from);
            }
            if (toInsn == null) {
                throw new IllegalStateException("Slice does not apply: 'to' is a null instruction (wrong target method?). To slice: " + this.to);
            }

            // Check whether to is before from.
            for (AbstractInsnNode insn = toInsn.getNext(); insn != null; insn = insn.getNext()) {
                if (insn == fromInsn) {
                    throw new IllegalStateException("Invalid slice ordering: 'from' is after 'to' - check whether the target method is correct and whether the slices are indeed correct");
                }
            }

            // test against bugs in the getFirstInsn method itself
            this.getFirstInsn(method, remapper, sharedBuilder);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Invalid injection point selector at path: " + path + " while targetting method " + method.name + method.desc, e);
        }
    }
}
