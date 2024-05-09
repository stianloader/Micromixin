package org.stianloader.micromixin.transform.api;

import java.util.Collection;
import java.util.Queue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.internal.selectors.inject.TailInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;

public class SlicedInjectionPointSelector {

    @NotNull
    private final InjectionPointSelector selector;
    @Nullable
    private final SlicedInjectionPointSelector from;
    @Nullable
    private final SlicedInjectionPointSelector to;

    public SlicedInjectionPointSelector(@NotNull InjectionPointSelector selector, @Nullable SlicedInjectionPointSelector from, @Nullable SlicedInjectionPointSelector to) {
        this.selector = selector;
        this.from = from;
        this.to = to;
    }

    /**
     * Obtains the instruction immediately after the next instruction.
     *
     * <p>Handle with care, improper use may lead to unexpected behaviour with frames or jumps!
     * The method is mainly intended to be used in order to easily check whether an instruction lies between
     * two other instructions.
     *
     * @param method The method to find the entrypoints in.
     * @param remapper The remapper instance to make use of. This is used to remap any references of the mixin class to the target class when applying injection point constraints.
     * @param sharedBuilder Shared {@link StringBuilder} instance to reduce {@link StringBuilder} allocations.
     * @return Instruction immediately after the next instruction.
     */
    @Nullable
    public AbstractInsnNode getAfterSelected(@NotNull MethodNode method, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        AbstractInsnNode first = this.getFirstInsn(method, remapper, sharedBuilder);
        if (first == null) {
            return null;
        }
        AbstractInsnNode selected = ASMUtil.afterInstruction(first);
        AbstractInsnNode afterSelected = selected.getNext();
        if (afterSelected == null) {
            return selected;
        } else {
            return afterSelected;
        }
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
        return this.selector.getFirstInsn(method, this.from, this.to, remapper, sharedBuilder);
    }

    @Nullable
    public SlicedInjectionPointSelector getFrom() {
        return this.from;
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
        return this.selector.getMatchedInstructions(method, this.from, this.to, remapper, sharedBuilder);
    }

    @NotNull
    public InjectionPointSelector getSelector() {
        return this.selector;
    }

    @Nullable
    public SlicedInjectionPointSelector getTo() {
        return this.to;
    }

    @Deprecated
    public boolean supportsConstructors() {
        // FIXME This is completely bogus behaviour!
        return this.selector == TailInjectionPointSelector.INSTANCE;
    }

    /**
     * Checks whether the injection point can be used in conjunction with the Redirect-annotation.
     * Generally should only be true if this injection point selector can select method instruction nodes.
     *
     * @return True if usable in redirects, false otherwise.
     * @deprecated Redirect in theory (not implemented in micromixin-transformer as of now) also supports redirecting
     * PUTFIELD/GETFIELD, PUTSTATIC/GETStATIC, xALOAD and the ARRAYLENGTH instructions. Added with the introduction of
     * slices which make the semantics of the matched instruction type less predictable at first glance, this method
     * has little sense going forward.
     */
    @Deprecated
    public boolean supportsRedirect() {
        return this.selector.supportsRedirect();
    }

    @Override
    @NotNull
    public String toString() {
        return "SlicedInjectionPointSelector[" + this.selector.fullyQualifiedName + ", from = " + this.from + ", to = " + this.to + "]";
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
