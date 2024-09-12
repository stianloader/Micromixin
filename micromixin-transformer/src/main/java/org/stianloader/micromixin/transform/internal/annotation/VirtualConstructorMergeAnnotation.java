package org.stianloader.micromixin.transform.internal.annotation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.MixinVendor;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.util.CodeCopyUtil;

public class VirtualConstructorMergeAnnotation extends MixinAnnotation<MixinMethodStub> {

    @NotNull
    private static MethodInsnNode getConstructorInvokeInsn(@NotNull ClassNode node, @NotNull MethodNode source) {
        AbstractInsnNode insn = source.instructions.getFirst();

        if (insn == null) {
            throw new IllegalStateException("Constructor " + node.name + "." + source.name + source.desc + " is empty. Did you accidentally use ClassReader.SKIP_CODE?");
        }

        int newDepth = 0;
        while (insn != null) {
            if (insn.getOpcode() != Opcodes.INVOKESPECIAL) {
                if (insn.getOpcode() == Opcodes.NEW &&
                        (((TypeInsnNode) insn).desc.equals(node.superName) || ((TypeInsnNode) insn).desc.equals(node.name))) {
                    newDepth++;
                }
                insn = insn.getNext();
                continue;
            }
            if (((MethodInsnNode) insn).name.equals("<init>") && (((MethodInsnNode) insn).owner.equals(node.superName) || ((MethodInsnNode) insn).owner.equals(node.name))) {
                AbstractInsnNode prev = insn.getPrevious();
                while (prev != null && prev.getOpcode() == -1) {
                    prev = prev.getPrevious();
                }
                if (prev == null) {
                    throw new IllegalStateException("The first instruction of a mixin is a constructor invocation. This does not make much sense!");
                }
                if (newDepth != 0) {
                    newDepth--;
                    insn = insn.getNext();
                    continue;
                }
                break;
            }
            insn = insn.getNext();
        }

        if (insn == null) {
            throw new NullPointerException("Instructions exhausted for " + node.name + "." + source.name + source.desc + ", depth: " + newDepth + "; Overall instructions count: " + source.instructions.size());
        } else {
            return (MethodInsnNode) insn;
        }
    }

    @NotNull
    private final MixinLoggingFacade logger;
    private final boolean matchAny;
    @Nullable
    private final MixinVendor vendorCompatibility; // Null = infer compatibility (nullable for logging purposes)

    public VirtualConstructorMergeAnnotation(@Nullable MixinVendor vendorCompat, boolean matchAny, @NotNull MixinLoggingFacade logger) {
        this.vendorCompatibility = vendorCompat;
        this.matchAny = matchAny;
        this.logger = logger;
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        MixinVendor primaryBehaviour = this.validate0(source, logger, sharedBuilder, true);

        if (primaryBehaviour == MixinVendor.MICROMIXIN) {
            this.applyMicromixin(to, hctx, sourceStub, source, remapper, sharedBuilder);
        } else if (primaryBehaviour == MixinVendor.SPONGE) {
            this.applySpongeian(to, hctx, sourceStub, source, remapper, sharedBuilder);
        } else {
            throw new AssertionError("Unknown, unsupported or otherwise incorrectly implemented vendor: " + primaryBehaviour);
        }
    }

    private void applyMicromixin(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        if (this.matchAny && !source.method.desc.equals("()V")) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + source.method.name + source.method.desc + ". Expected no-args constructor when used in matchAny mode!");
        }

        AbstractInsnNode firstInsn = VirtualConstructorMergeAnnotation.getConstructorInvokeInsn(source.owner, source.method).getNext();

        if (firstInsn == null) {
            return; // Nothing to merge
        }

        AbstractInsnNode lastInsn = source.method.instructions.getLast();
        if (lastInsn == null) {
            throw new AssertionError(); // Sadly `InsnList#getLast` is not nullable due to empty lists being a thing.
        }

        for (MethodNode m : to.methods) {
            if (m.name.equals("<init>") && (this.matchAny || source.method.desc.equals(m.desc))) {
                MethodInsnNode targetInsn = VirtualConstructorMergeAnnotation.getConstructorInvokeInsn(to, m);
                if (targetInsn.owner.equals(to.superName)) {
                    // FIXME Relocate local variables
                    CodeCopyUtil.copyTo(source.method, firstInsn, lastInsn, sourceStub, m, targetInsn, to, remapper, hctx.lineAllocator, true, false);
                } else {
                    throw new AssertionError("Superconstructor call does not have the expected owner.");
                }
            }
        }
    }

    private void applySpongeian(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        // TODO Implement
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        // NOP
    }

    @NotNull
    private MixinVendor validate0(@NotNull MixinMethodStub source, @NotNull MixinLoggingFacade logger, @NotNull StringBuilder sharedBuilder, boolean fatalErrors) {
        if (this.vendorCompatibility == MixinVendor.MICROMIXIN) {
            return MixinVendor.MICROMIXIN;
        }

        AbstractInsnNode insn = VirtualConstructorMergeAnnotation.getConstructorInvokeInsn(source.getOwner(), source.method);
        while (insn != null && !(insn instanceof LineNumberNode)) {
            insn = insn.getPrevious();
        }

        if (insn == null) {
            if (this.vendorCompatibility == null) {
                if (source.getDesc().equals("()V")) {
                    logger.warn(VirtualConstructorMergeAnnotation.class, "Mixin constructor {}.<init>()V does not explicitly specify a vendor compatibility level. As it is a no-args constructor, MICROMIXIN compatibility was inferred. That being said, future versions of micromixin-transformer may use SPONGE compatibility, which requires to know the LineNumberNode of the superconstructor call. However, it could not be obtained, making this mixin potentially incompatible with future versions of micromixin-transformer.", source.getOwner().name);
                    return MixinVendor.MICROMIXIN;
                } else {
                    String message = "Mixin constructor" + source.getOwner().name + ".<init>" + source.getDesc() + " is using inferred SPONGE compatibility, which requires LineNumberNodes to be known. The LineNumberNode of the superconstructor could not be ascertained. Potential fixes: Explicitly define MICROMIXIN constructor merging behaviour or keep/generate debug information for constructors of mixin classes.";
                    if (fatalErrors) {
                        throw new IllegalStateException("Invalid mixin: " + message);
                    } else {
                        logger.error(VirtualConstructorMergeAnnotation.class, message);
                    }
                    return MixinVendor.SPONGE;
                }
            } else if (this.vendorCompatibility == MixinVendor.SPONGE) {
                String message = "Mixin constructor" + source.getOwner().name + ".<init>" + source.getDesc() + " is using explicitly defined SPONGE compatibility, which requires LineNumberNodes to be known. The LineNumberNode of the superconstructor could not be ascertained. Please read the micromixin documentation on differences between constructor merging implementations and consult the spongeian mixin documentation (ahem, source code) accordingly.";
                if (fatalErrors) {
                    throw new IllegalStateException("Invalid mixin: " + message);
                } else {
                    logger.error(VirtualConstructorMergeAnnotation.class, message);
                }
                return MixinVendor.SPONGE;
            } else {
                throw new AssertionError("Unknown or incorrectly implemented vendor compatibility implementation: " + this.vendorCompatibility);
            }
        }

        if (this.vendorCompatibility == MixinVendor.SPONGE) {
            // Explicit compatibility checks will halt here.
            return MixinVendor.SPONGE;
        }

        int firstLine = ((LineNumberNode) insn).line;
        // Obtain the LineNumberNode of the final return opcode
        insn = source.method.instructions.getLast();
        while (!(insn instanceof LineNumberNode)) {
            insn = insn.getPrevious();
        }
        int lastLine = ((LineNumberNode) insn).line;

        while (insn != null) {
            if (insn instanceof LineNumberNode) {
                int lineNumber = ((LineNumberNode) insn).line;
                if (lineNumber > firstLine && lineNumber < lastLine) {
                    logger.warn(VirtualConstructorMergeAnnotation.class, "The mixin constructor {}.<init>{} has at least one instruction which points to a line within the constructor ({}). The spongeian mixin implementation does not merge these instructions, potentially causing unintended side effects or even introducing differences with different mixin implementations. Consult the manual of your respective mixin implementations for further guidance.", source.getOwner().name, source.getDesc(), lineNumber);
                    break;
                }
            }
            insn = insn.getPrevious();
        }

        return source.getDesc().equals("()V") ? MixinVendor.MICROMIXIN : MixinVendor.SPONGE;
    }

    @Override
    public void validateMixin(@NotNull MixinMethodStub source, @NotNull MixinLoggingFacade logger, @NotNull StringBuilder sharedBuilder) {
        this.validate0(source, logger, sharedBuilder, false);
    }
}
