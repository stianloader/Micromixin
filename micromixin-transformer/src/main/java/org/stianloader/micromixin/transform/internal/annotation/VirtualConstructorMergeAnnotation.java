package org.stianloader.micromixin.transform.internal.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.MixinVendor;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.util.CodeCopyUtil;
import org.stianloader.micromixin.transform.internal.util.LabelNodeMapper;

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
            this.logger.error(VirtualConstructorMergeAnnotation.class, "Unable to find superconstructor call for {}.{}{}: Constructor will not be merged.", source.owner, source.getName(), source.getDesc());
            return; // Error state, gracefully recover. Likely a constructor that is unconditionally throwing.
        }

        AbstractInsnNode lastInsn = source.method.instructions.getLast();
        if (lastInsn == null) {
            throw new AssertionError(); // Sadly `InsnList#getLast` is not nullable due to empty lists being a thing.
        }

        for (MethodNode m : to.methods) {
            if (m.name.equals("<init>") && (this.matchAny || source.method.desc.equals(m.desc))) {
                MethodInsnNode targetInsn = VirtualConstructorMergeAnnotation.getConstructorInvokeInsn(to, m);
                if (targetInsn.owner.equals(to.superName) || targetInsn.owner.equals(to.name)) {
                    // FIXME Relocate local variables
                    Map<LabelNode, LabelNode> labelMap = new HashMap<LabelNode, LabelNode>();
                    CodeCopyUtil.copyTo(source.method, firstInsn, lastInsn, sourceStub, m, targetInsn, to, remapper, hctx.lineAllocator, labelMap, true, false);
                } else {
                    throw new AssertionError("Superconstructor call does not have the expected owner. Expected: " + to.superName + ", actual " + targetInsn.owner + " for class " + to.name);
                }
            }
        }
    }

    private void applySpongeian(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        if (!this.matchAny) {
            throw new IllegalStateException("The spongeian mixin merging behaviour does not support the 'matchAny' attribute (it is enabled by default and micromixin does not allow disabling it.)");
        }

        AbstractInsnNode firstInsn = VirtualConstructorMergeAnnotation.getConstructorInvokeInsn(source.owner, source.method).getNext();

        if (firstInsn == null) {
            this.logger.error(VirtualConstructorMergeAnnotation.class, "Unable to find superconstructor call for {}.{}{}: Constructor will not be merged.", source.owner, source.getName(), source.getDesc());
            return; // Error state, gracefully recover. Likely a constructor that is unconditionally throwing.
        }

        AbstractInsnNode insn = firstInsn;
        while (insn != null && !(insn instanceof LineNumberNode)) {
            insn = insn.getPrevious();
        }

        if (insn == null) {
            throw new IllegalStateException("Unable to find the LineNumberNode corresponding to the superconstructor call for " + source.owner.name + "." + source.getName() + source.getDesc() + ". The SPONGE constructor merging behaviour does not support merging constructors with this metadata missing.");
        }

        int firstConstructorLine = ((LineNumberNode) insn).line;

        insn = source.method.instructions.getLast();
        while (!(insn instanceof LineNumberNode)) {
            insn = insn.getPrevious();
        }

        int lastConstructorInsn = ((LineNumberNode) insn).line;

        insn = firstInsn.getNext();

        for (MethodNode method : to.methods) {
            if (!method.name.equals("<init>")) {
                continue;
            }

            AbstractInsnNode targetInstruction = VirtualConstructorMergeAnnotation.getConstructorInvokeInsn(to, method);

            InsnList output = new InsnList();
            Map<LabelNode, LabelNode> labelMap = new HashMap<LabelNode, LabelNode>();
            Set<LabelNode> declaredLabels = new HashSet<LabelNode>();
            LabelNode finalLabel = new LabelNode();
            LabelNodeMapper labelMapper = new LabelNodeMapper.LazyDuplicateLabelNodeMapper(labelMap);
            Map<LabelNode, Integer> labelToLineNumber = new HashMap<LabelNode, Integer>();
            boolean copying = true;

            for (insn = firstInsn; insn != null; insn = insn.getNext()) {
                if (insn instanceof LineNumberNode) {
                    labelToLineNumber.put(((LineNumberNode) insn).start, ((LineNumberNode) insn).line);
                }
            }

            for (insn = firstInsn; insn != null; insn = insn.getNext()) {
                if (insn instanceof LabelNode) {
                    Integer line = labelToLineNumber.get(insn);
                    if (line != null) {
                        copying = line <= firstConstructorLine || line > lastConstructorInsn;
                    }
                }
                if (!copying) {
                    continue;
                }

                if (insn.getOpcode() == Opcodes.RETURN) {
                    // TODO detect and remove useless jumps (i.e. strip the last RETURN instruction)
                    output.add(new JumpInsnNode(Opcodes.GOTO, finalLabel));
                    continue;
                } else if (insn instanceof LabelNode) {
                    declaredLabels.add((LabelNode) insn);
                } else if (insn instanceof LineNumberNode) {
                    LineNumberNode inLineNumberNode = (LineNumberNode) insn;
                    output.add(hctx.lineAllocator.reserve(sourceStub.sourceNode, inLineNumberNode, labelMapper.apply(inLineNumberNode.start)));
                    continue;
                }
                // FIXME ensure that local variables are not chopped

                AbstractInsnNode copiedInsn = CodeCopyUtil.duplicateRemap(insn, remapper, labelMapper, sharedBuilder, true);
                if (copiedInsn != null) {
                    output.add(copiedInsn);
                }
            }

            output.add(finalLabel);

            List<TryCatchBlockNode> tryCatchBlocks = source.method.tryCatchBlocks;
            if (tryCatchBlocks != null) {
                for (TryCatchBlockNode node : tryCatchBlocks) {
                    boolean start = labelMap.containsKey(node.start);
                    boolean end = labelMap.containsKey(node.end);
                    boolean handler = labelMap.containsKey(node.handler);
                    if (start != end || end != handler) {
                        throw new AssertionError("Attempted to chop off a try-catch block of " + sourceStub.sourceNode.name + "." + source.getName() + source.getDesc() + ". Start chopped: " + (!start) + ", end chopped: " + (!end) + ", handler chopped: " + (!handler) + ". This is likely a bug in the micromixin library.");
                    } else if (start) {
                        String htype = node.type;
                        if (htype != null) {
                            remapper.remapInternalName(htype, sharedBuilder);
                        }
                        if (method.tryCatchBlocks == null) {
                            method.tryCatchBlocks = new ArrayList<TryCatchBlockNode>();
                        }
                        method.tryCatchBlocks.add(new TryCatchBlockNode(labelMap.get(node.start), labelMap.get(node.end), labelMap.get(node.handler), htype));
                    }
                }
            }

            // Verify integrity of labels
            if (declaredLabels.size() < labelMap.size()) {
                this.logger.info(VirtualConstructorMergeAnnotation.class, "Label map info for method {}.{}{} targetting {}.{}{}", source.owner.name, source.getName(), source.getDesc(), to.name, method.name, method.desc);
                insn = source.method.instructions.getFirst();
                while (insn != null) {
                    if (insn instanceof LabelNode) {
                        this.logger.info(VirtualConstructorMergeAnnotation.class, "LabelNode {} -- {} {} @ {}", insn.hashCode(), labelMap.containsKey(insn), declaredLabels.contains(insn), labelToLineNumber.get(insn));
                    } else if (insn instanceof LineNumberNode) {
                        this.logger.info(VirtualConstructorMergeAnnotation.class, "LineNumberNode {} -- {} @ {}", insn.hashCode(), ((LineNumberNode) insn).line, ((LineNumberNode) insn).start.hashCode());
                    } else {
                        this.logger.info(VirtualConstructorMergeAnnotation.class, "{} {}", insn.getClass().getSimpleName(), insn.hashCode());
                    }
                    if (insn == firstInsn) {
                        this.logger.info(VirtualConstructorMergeAnnotation.class, "^^^ First instruction. Copying starts there");
                    }
                    insn = insn.getNext();
                }
                throw new AssertionError((labelMap.size() - declaredLabels.size()) + " more label(s) were chopped off while copying " + sourceStub.sourceNode.name + "." + source.getName() + source.getDesc() + " to " + to.name + "." + method.name + method.desc + ". This is likely a bug in the micromixin library.");
            }

            method.instructions.insert(targetInstruction, output);
        }
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
