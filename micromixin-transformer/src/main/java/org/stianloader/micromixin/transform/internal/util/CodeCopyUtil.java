package org.stianloader.micromixin.transform.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.util.smap.MultiplexLineNumberAllocator;

public class CodeCopyUtil {

    @NotNull
    private static final String remapDesc(String desc, @NotNull ClassNode a, @NotNull ClassNode b) {
        return desc.replace("L" + a.name + ";", "L" + b.name + ";");
    }

    public static void copyTo(@NotNull MethodNode source, @NotNull MixinStub sourceStub, @NotNull MethodNode copyTarget,
            @NotNull AbstractInsnNode endInInsn, @NotNull ClassNode targetOwner, @NotNull SimpleRemapper remapper,
            @NotNull MultiplexLineNumberAllocator lineAllocator) {
        AbstractInsnNode copySourceStart = source.instructions.getFirst();
        if (copySourceStart == null) {
            throw new IllegalStateException("Source instruction list is empty!");
        }
        AbstractInsnNode copyTargetStart = copyTarget.instructions.getLast();
        while (copyTargetStart != null && copyTargetStart.getOpcode() == -1) {
            copyTargetStart = copyTargetStart.getPrevious();
        }
        if (copyTargetStart == null) {
            throw new IllegalStateException("Target instruction list is empty!");
        }
        if (copyTargetStart.getOpcode() != Opcodes.IRETURN
                && copyTargetStart.getOpcode() != Opcodes.LRETURN
                && copyTargetStart.getOpcode() != Opcodes.FRETURN
                && copyTargetStart.getOpcode() != Opcodes.DRETURN
                && copyTargetStart.getOpcode() != Opcodes.ARETURN
                && copyTargetStart.getOpcode() != Opcodes.RETURN) {
            throw new IllegalStateException("Invalid copy target: " + targetOwner.name + "." + copyTarget.name + copyTarget.desc + ": Last instruction should be a XRETURN opcode.");
        }
        copyTo(source, copySourceStart, endInInsn, sourceStub, copyTarget, copyTargetStart, targetOwner, remapper, lineAllocator, true, false);
    }

    @NotNull
    public static MethodNode copyHandler(@NotNull MethodNode source, @NotNull MixinStub sourceStub,
            final @NotNull ClassNode target, @NotNull String handlerName, @NotNull SimpleRemapper remapper,
            @NotNull MultiplexLineNumberAllocator lineAllocator) {
        // WARNING: This method is what many would call to be "bugged". That is intended!
        // To those wondering, this method does not properly remap INVOKESTATIC methods because
        // the official (Sponge) mixin implementation does not properly remap them either.
        // I am not really satisfied with reproducing that bug, but what can I do there?
        // The tests would only need to run in micromixin presence then, which I mean can be arranged
        // but still feels odd.
        final ClassNode sourceClass = sourceStub.sourceNode;
        MethodNode handler = new MethodNode();
        handler.name = handlerName;
        handler.desc = CodeCopyUtil.remapDesc(source.desc, sourceClass, target);
        handler.exceptions = source.exceptions; // TODO same here? Possible.
        handler.access = source.access;
        AbstractInsnNode sourceStartInsn = source.instructions.getFirst();
        AbstractInsnNode sourceEndInsn = source.instructions.getLast();
        if (sourceStartInsn == null || sourceEndInsn == null) {
            throw new IllegalStateException("The source method is empty!");
        }
        AbstractInsnNode prevOutInsn = new LabelNode();
        handler.instructions.add(prevOutInsn);
        CodeCopyUtil.copyTo(source, sourceStartInsn, sourceEndInsn, sourceStub, handler, prevOutInsn, target, remapper, lineAllocator, false, true);
        target.methods.add(handler);
        return handler;
    }

    private static Object duplicateRemapBSMArg(final Object[] bsmArgs, final int index, final @NotNull StringBuilder sharedStringBuilder,
            final @NotNull SimpleRemapper remapper) {
        Object bsmArg = bsmArgs[index];
        if (bsmArg instanceof Type) {
            Type type = (Type) bsmArg;
            sharedStringBuilder.setLength(0);

            if (type.getSort() == Type.METHOD) {
                if (remapper.remapSignature(type.getDescriptor(), sharedStringBuilder)) {
                    return Type.getMethodType(sharedStringBuilder.toString());
                }
            } else if (type.getSort() == Type.OBJECT) {
                String oldVal = type.getInternalName();
                String remappedVal = remapper.remapInternalName(oldVal, sharedStringBuilder);
                if (oldVal != remappedVal) { // Instance comparison intended
                    return Type.getObjectType(remappedVal);
                }
            } else {
                throw new IllegalArgumentException("Unexpected bsm arg Type sort. Sort = " + type.getSort() + "; type = " + type);
            }
            return bsmArg; // Type is immutable
        } else if (bsmArg instanceof Handle) {
            Handle handle = (Handle) bsmArg;
            String oldName = handle.getName();
            String hOwner = handle.getOwner();
            String newOwner = remapper.getRemappedClassNameFast(hOwner);
            String newName = remapper.getRemappedMethodName(hOwner, oldName, handle.getDesc());
            boolean modified = oldName != newName;
            if (newOwner != null) {
                hOwner = newOwner;
                modified = true;
            }
            String desc = handle.getDesc();
            sharedStringBuilder.setLength(0);
            if (remapper.remapSignature(desc, sharedStringBuilder)) {
                desc = sharedStringBuilder.toString();
                modified = true;
            }
            if (modified) {
                return new Handle(handle.getTag(), hOwner, newName, desc, handle.isInterface());
            }
            return bsmArg; // Fortunately, Handles are immutable
        } else if (bsmArg instanceof String) {
            return bsmArg;
        } else {
            throw new IllegalArgumentException("Unexpected bsm arg class at index " + index + " for " + Arrays.toString(bsmArgs) + ". Class is " + bsmArg.getClass().getName());
        }
    }

    @Nullable
    public static AbstractInsnNode duplicateRemap(@NotNull AbstractInsnNode in,
            @NotNull SimpleRemapper remapper, @NotNull LabelNodeMapper labelNodeMapper, @NotNull StringBuilder sharedBuilder,
            boolean invalidInvokestaticRemapping) {
        switch (in.getType()) {
        case AbstractInsnNode.INSN:
            return new InsnNode(in.getOpcode());
        case AbstractInsnNode.INT_INSN:
            return new IntInsnNode(in.getOpcode(), ((IntInsnNode) in).operand);
        case AbstractInsnNode.VAR_INSN:
            return new VarInsnNode(in.getOpcode(), ((VarInsnNode) in).var);
        case AbstractInsnNode.TYPE_INSN:
            return new TypeInsnNode(in.getOpcode(), remapper.remapInternalName(((TypeInsnNode) in).desc, sharedBuilder));
        case AbstractInsnNode.FIELD_INSN: {
            FieldInsnNode fieldInsn = (FieldInsnNode) in;
            return new FieldInsnNode(fieldInsn.getOpcode(),
                    remapper.remapInternalName(fieldInsn.owner, sharedBuilder),
                    remapper.getRemappedFieldName(fieldInsn.owner, fieldInsn.name, fieldInsn.desc),
                    remapper.getRemappedFieldDescriptor(fieldInsn.desc, sharedBuilder));
        }
        case AbstractInsnNode.METHOD_INSN: {
            MethodInsnNode methodInsn = (MethodInsnNode) in;
            if (invalidInvokestaticRemapping && methodInsn.getOpcode() == Opcodes.INVOKESTATIC) {
                // Yes, I'm being serious there.
                // If this bothers you too much, don't hesitate to notify me so I can work out a solution.
                // Otherwise you could work around a solution.
                return new MethodInsnNode(methodInsn.getOpcode(),
                        remapper.remapInternalName(methodInsn.owner, sharedBuilder),
                        methodInsn.name,
                        remapper.getRemappedMethodDescriptor(methodInsn.desc, sharedBuilder),
                        methodInsn.itf);
            }
            return new MethodInsnNode(methodInsn.getOpcode(),
                    remapper.remapInternalName(methodInsn.owner, sharedBuilder),
                    remapper.getRemappedMethodName(methodInsn.owner, methodInsn.name, methodInsn.desc),
                    remapper.getRemappedMethodDescriptor(methodInsn.desc, sharedBuilder),
                    methodInsn.itf);
        }
        case AbstractInsnNode.INVOKE_DYNAMIC_INSN: {
            InvokeDynamicInsnNode indyInsn = (InvokeDynamicInsnNode) in;
            Object[] bsmArgs = indyInsn.bsmArgs;
            int arglen = bsmArgs.length;
            Object[] bsmArgsCopy = new Object[arglen];
            for (int i = 0; i < arglen; i++) {
                bsmArgsCopy[i] = duplicateRemapBSMArg(bsmArgs, i, sharedBuilder, remapper);
            }
            String desc = indyInsn.desc;
            sharedBuilder.setLength(0);
            if (remapper.remapSignature(indyInsn.desc, sharedBuilder)) {
                desc = sharedBuilder.toString();
            }
            return new InvokeDynamicInsnNode(indyInsn.name, desc, indyInsn.bsm, bsmArgsCopy);
        }
        case AbstractInsnNode.JUMP_INSN:
            return new JumpInsnNode(in.getOpcode(), labelNodeMapper.apply(((JumpInsnNode) in).label));
        case AbstractInsnNode.LABEL:
            return labelNodeMapper.apply((LabelNode) in);
        case AbstractInsnNode.LDC_INSN:
            if (((LdcInsnNode) in).cst instanceof Type) {
                return new LdcInsnNode(Type.getType(remapper.remapSingleDesc(((Type) ((LdcInsnNode) in).cst).getDescriptor(), sharedBuilder)));
            } else {
                return new LdcInsnNode(((LdcInsnNode) in).cst);
            }
        case AbstractInsnNode.IINC_INSN:
            return new IincInsnNode(((IincInsnNode) in).var, ((IincInsnNode) in).incr);
        case AbstractInsnNode.TABLESWITCH_INSN: {
            TableSwitchInsnNode tableSwitchInsn = (TableSwitchInsnNode) in;
            LabelNode defaultLabel = labelNodeMapper.apply(tableSwitchInsn.dflt);
            LabelNode[] handlerLabels = new LabelNode[tableSwitchInsn.labels.size()];
            int i = 0;
            for (LabelNode label : tableSwitchInsn.labels) {
                handlerLabels[i++] = labelNodeMapper.apply(label);
            }
            return new TableSwitchInsnNode(tableSwitchInsn.min, tableSwitchInsn.max, defaultLabel, handlerLabels);
        }
        case AbstractInsnNode.LOOKUPSWITCH_INSN: {
            LookupSwitchInsnNode lookupSwitchInsn = (LookupSwitchInsnNode) in;
            LabelNode defaultLabel = labelNodeMapper.apply(lookupSwitchInsn.dflt);
            LabelNode[] handlerLabels = new LabelNode[lookupSwitchInsn.labels.size()];
            int i = 0;
            for (LabelNode label : lookupSwitchInsn.labels) {
                handlerLabels[i++] = labelNodeMapper.apply(label);
            }
            i = 0;
            int[] keys = new int[lookupSwitchInsn.keys.size()];
            for (Integer key : lookupSwitchInsn.keys) {
                keys[i++] = key;
            }
            return new LookupSwitchInsnNode(defaultLabel, keys, handlerLabels);
        }
        case AbstractInsnNode.MULTIANEWARRAY_INSN:
            return new MultiANewArrayInsnNode(remapper.remapSingleDesc(((MultiANewArrayInsnNode) in).desc, sharedBuilder), ((MultiANewArrayInsnNode) in).dims);
        case AbstractInsnNode.FRAME:
            // Not strictly needed
            return null;
        case AbstractInsnNode.LINE:
            // Note: Generally the MultiplexLineNumberAllocator#reserve method is more appropriate
            LineNumberNode lnn = (LineNumberNode) in;
            return new LineNumberNode(lnn.line, labelNodeMapper.apply(lnn.start));
        default:
            throw new IllegalStateException("Unsupported instruction type: " + in.getType() + " (insn of class " + in.getClass()+ "); Opcode: " + in.getOpcode());
        }
    }

    public static void copyTo(@NotNull MethodNode source, @NotNull AbstractInsnNode startInInsn, @NotNull AbstractInsnNode endInInsn, @NotNull MixinStub sourceStub,
            @NotNull MethodNode output, @NotNull AbstractInsnNode previousOutInsn, @NotNull ClassNode targetClass, @NotNull SimpleRemapper remapper,
            @NotNull MultiplexLineNumberAllocator lineAllocator) {
        CodeCopyUtil.copyTo(source, startInInsn, endInInsn, sourceStub, output, previousOutInsn, targetClass, remapper, lineAllocator, false, false);
    }

    public static void copyTo(@NotNull MethodNode source, @NotNull AbstractInsnNode startInInsn, @NotNull AbstractInsnNode endInInsn, @NotNull MixinStub sourceStub,
            @NotNull MethodNode output, @NotNull AbstractInsnNode previousOutInsn, @NotNull ClassNode targetClass, @NotNull SimpleRemapper remapper,
            @NotNull MultiplexLineNumberAllocator lineAllocator, boolean transformReturnToJump, boolean invalidInvokestaticRemapping) {
        AbstractInsnNode inInsn = startInInsn.getPrevious();
        Objects.requireNonNull(endInInsn, "endInInsn must not be null");
        InsnList copiedInstructions = new InsnList();
        final Map<LabelNode, LabelNode> labelMap = new HashMap<LabelNode, LabelNode>();
        Set<LabelNode> declaredLabels = new HashSet<LabelNode>();
        StringBuilder sharedBuilder = new StringBuilder();
        LabelNode endLabel = new LabelNode();
        LabelNodeMapper labelMapper = new LabelNodeMapper.LazyDuplicateLabelNodeMapper(labelMap);
        do {
            if (inInsn == null) {
                inInsn = startInInsn;
            } else {
                inInsn = inInsn.getNext();
                if (inInsn == null) {
                    throw new IllegalStateException("Instructions exhausted without reaching endInInsn!");
                }
            }
            if (inInsn.getOpcode() == Opcodes.RETURN && transformReturnToJump) {
                // TODO detect and remove useless jumps (i.e. strip the last RETURN instruction)
                copiedInstructions.add(new JumpInsnNode(Opcodes.GOTO, endLabel));
                continue;
            } else if (inInsn instanceof LabelNode) {
                declaredLabels.add((LabelNode) inInsn);
            } else if (inInsn instanceof LineNumberNode) {
                LineNumberNode inLineNumberNode = (LineNumberNode) inInsn;
                copiedInstructions.add(lineAllocator.reserve(sourceStub.sourceNode, inLineNumberNode, labelMapper.apply(inLineNumberNode.start)));
                continue;
            }
            AbstractInsnNode insn = CodeCopyUtil.duplicateRemap(inInsn, remapper, labelMapper, sharedBuilder, invalidInvokestaticRemapping);
            if (insn != null) {
                copiedInstructions.add(insn);
            }
        } while (inInsn != endInInsn);

        List<TryCatchBlockNode> tryCatchBlocks = source.tryCatchBlocks;
        if (tryCatchBlocks != null) {
            for (TryCatchBlockNode node : tryCatchBlocks) {
                boolean start = labelMap.containsKey(node.start);
                boolean end = labelMap.containsKey(node.end);
                boolean handler = labelMap.containsKey(node.handler);
                if (start != end || end != handler) {
                    throw new AssertionError("Attempted to chop off a try-catch block of " + sourceStub.sourceNode.name + "." + source.name + source.desc + ". Start chopped: " + (!start) + ", end chopped: " + (!end) + ", handler chopped: " + (!handler) + ". This is likely a bug in the micromixin library.");
                } else if (start) {
                    String htype = node.type;
                    if (htype != null) {
                        remapper.remapInternalName(htype, sharedBuilder);
                    }
                    if (output.tryCatchBlocks == null) {
                        output.tryCatchBlocks = new ArrayList<TryCatchBlockNode>();
                    }
                    output.tryCatchBlocks.add(new TryCatchBlockNode(labelMap.get(node.start), labelMap.get(node.end), labelMap.get(node.handler), htype));
                }
            }
        }

        // Verify integrity of labels
        if (declaredLabels.size() < labelMap.size()) {
            throw new AssertionError((labelMap.size() - declaredLabels.size()) + " more label(s) were chopped of while copying " + sourceStub.sourceNode.name + "." + source.name + source.desc+ " to " + targetClass.name + "." + output.name + output.desc + ". This is likely a bug in the micromixin library.");
        }

        if (transformReturnToJump) {
            copiedInstructions.add(endLabel);
        }
        output.instructions.insert(previousOutInsn, copiedInstructions);
    }
}
