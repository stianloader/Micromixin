package org.stianloader.micromixin.transform.internal.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableAnnotationNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.selectors.MixinTargetSelector;

@ApiStatus.Internal
public class ASMUtil {

    public static final String CALLBACK_INFO_DESC = "L" + ASMUtil.CALLBACK_INFO_NAME + ";";
    public static final String CALLBACK_INFO_NAME = "org/spongepowered/asm/mixin/injection/callback/CallbackInfo";
    public static final String CALLBACK_INFO_RETURNABLE_DESC = "L" + ASMUtil.CALLBACK_INFO_RETURNABLE_NAME + ";";
    public static final String CALLBACK_INFO_RETURNABLE_NAME = "org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable";
    public static final int CI_LEN = ASMUtil.CALLBACK_INFO_NAME.length();
    public static final int CIR_LEN = ASMUtil.CALLBACK_INFO_RETURNABLE_NAME.length();
    public static final String COMMON_CI_INSTANCE_ANNOT_DESC = "Lorg/stianloader/micromixin/internal/CommonCIInstance;";
    public static final String ROLL_ANNOT_DESC = "Lorg/stianloader/micromixin/internal/Roll;";
    public static final String UNROLL_ANNOT_DESC = "Lorg/stianloader/micromixin/internal/Unroll;";

    @NotNull
    public static AbstractInsnNode afterInstruction(@NotNull AbstractInsnNode insn) {
        AbstractInsnNode next = insn.getNext();
        while (next != null && next.getOpcode() == -1) {
            next = next.getNext();
        }
        if (next == null) {
            return insn;
        }
        return next;
    }

    @Nullable
    private static <T extends AnnotationNode> List<T> copyAnnotationList(@Nullable List<T> annotations) {
        if (annotations == null) {
            return null;
        }

        List<T> copy = new ArrayList<T>();
        for (AnnotationNode annotation : annotations) {
            AnnotationNode clonedAnnotation;
            if (annotation.getClass() == AnnotationNode.class) {
                clonedAnnotation = new AnnotationNode(annotation.desc);
            } else if (annotation.getClass() == TypeAnnotationNode.class) {
                TypeAnnotationNode typeAnnot = (TypeAnnotationNode) annotation;
                clonedAnnotation = new TypeAnnotationNode(typeAnnot.typeRef, typeAnnot.typePath, typeAnnot.desc);
            } else {
                // TODO this doesn't support LocalVariableAnnotationNode.
                // That being said, LVT annotations are a bit more finicky. It would need a bit more involvement.
                throw new IllegalStateException("Unuspported annotation class: " + annotation.getClass());
            }
            annotation.accept(clonedAnnotation);
            @SuppressWarnings("unchecked")
            T clonedAnnotationCast = (T) clonedAnnotation;
            copy.add(clonedAnnotationCast);
        }

        return copy;
    }

    /**
     * Copy all visible and invisible annotations from a method to a different method,
     * excluding {@link LocalVariableAnnotationNode}s which will not be copied due to their
     * reliance on {@link LabelNode}.
     *
     * <p>This method creates independently operating copies. That is, if the annotations in
     * {@code from} where to be modified after invoking this method, the annotations in
     * {@code to} will not be affected.
     *
     * <p>This method will copy even when an annotation shouldn't be present more than once.
     * Neither will this method honour Java 8's {@code Repeatable.value()}.
     *
     * @param from The {@link MethodNode} to copy the annotations from.
     * @param to The {@link MethodNode} to copy the annotations to.
     * @since 0.8.0
     */
    public static void copyAnnotations(@NotNull MethodNode from, @NotNull MethodNode to) {
        to.invisibleAnnotations = ASMUtil.mergeAnnotationLists(to.invisibleAnnotations, from.invisibleAnnotations);
        to.invisibleTypeAnnotations = ASMUtil.mergeAnnotationLists(to.invisibleTypeAnnotations, from.invisibleTypeAnnotations);
        List<AnnotationNode>[] invisibleParameterAnnotationsA = to.invisibleParameterAnnotations;
        List<AnnotationNode>[] invisibleParameterAnnotationsB = from.invisibleParameterAnnotations;
        if (invisibleParameterAnnotationsA == null && invisibleParameterAnnotationsB != null) {
            @SuppressWarnings("unchecked")
            List<AnnotationNode>[] copy = new List[invisibleParameterAnnotationsB.length];
            for (int i = copy.length; i-- != 0;) {
                copy[i] = ASMUtil.copyAnnotationList(invisibleParameterAnnotationsB[i]);
            }
            to.invisibleParameterAnnotations = copy;
        } else if (invisibleParameterAnnotationsB == null && invisibleParameterAnnotationsA != null) {
            @SuppressWarnings("unchecked")
            List<AnnotationNode>[] copy = new List[invisibleParameterAnnotationsA.length];
            for (int i = copy.length; i-- != 0;) {
                copy[i] = ASMUtil.copyAnnotationList(invisibleParameterAnnotationsA[i]);
            }
            to.invisibleParameterAnnotations = copy;
        } else if (invisibleParameterAnnotationsA != null && invisibleParameterAnnotationsB != null) {
            if (invisibleParameterAnnotationsA.length != invisibleParameterAnnotationsB.length) {
                throw new IllegalStateException("invisibleParameterAnnotations length mismatch");
            }
            @SuppressWarnings("unchecked")
            List<AnnotationNode>[] copy = new List[invisibleParameterAnnotationsA.length];
            for (int i = copy.length; i-- != 0;) {
                copy[i] = ASMUtil.mergeAnnotationLists(invisibleParameterAnnotationsA[i], invisibleParameterAnnotationsB[i]);
            }
            to.invisibleParameterAnnotations = copy;
        }

        to.visibleAnnotations = ASMUtil.mergeAnnotationLists(to.visibleAnnotations, from.visibleAnnotations);
        to.visibleTypeAnnotations = ASMUtil.mergeAnnotationLists(to.visibleTypeAnnotations, from.visibleTypeAnnotations);
        List<AnnotationNode>[] visibleParameterAnnotationsA = to.visibleParameterAnnotations;
        List<AnnotationNode>[] visibleParameterAnnotationsB = from.visibleParameterAnnotations;
        if (visibleParameterAnnotationsA == null && visibleParameterAnnotationsB != null) {
            @SuppressWarnings("unchecked")
            List<AnnotationNode>[] copy = new List[visibleParameterAnnotationsB.length];
            for (int i = copy.length; i-- != 0;) {
                copy[i] = ASMUtil.copyAnnotationList(visibleParameterAnnotationsB[i]);
            }
            to.visibleParameterAnnotations = copy;
        } else if (visibleParameterAnnotationsB == null && visibleParameterAnnotationsA != null) {
            @SuppressWarnings("unchecked")
            List<AnnotationNode>[] copy = new List[visibleParameterAnnotationsA.length];
            for (int i = copy.length; i-- != 0;) {
                copy[i] = ASMUtil.copyAnnotationList(visibleParameterAnnotationsA[i]);
            }
            to.visibleParameterAnnotations = copy;
        } else if (visibleParameterAnnotationsA != null && visibleParameterAnnotationsB != null) {
            if (visibleParameterAnnotationsA.length != visibleParameterAnnotationsB.length) {
                throw new IllegalStateException("invisibleParameterAnnotations length mismatch");
            }
            @SuppressWarnings("unchecked")
            List<AnnotationNode>[] copy = new List[visibleParameterAnnotationsA.length];
            for (int i = copy.length; i-- != 0;) {
                copy[i] = ASMUtil.mergeAnnotationLists(visibleParameterAnnotationsA[i], visibleParameterAnnotationsB[i]);
            }
            to.visibleParameterAnnotations = copy;
        }
    }

    @NotNull
    public static Collection<InjectionPointReference> enumerateTargets(@NotNull Collection<MixinTargetSelector> selectors, @NotNull Collection<SlicedInjectionPointSelector> ats, @NotNull ClassNode target, @NotNull MixinStub mixinSource, @NotNull MethodNode injectMethodSource, int require, int expect, int allow, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder, @NotNull MixinLoggingFacade logger) {
        Map<AbstractInsnNode, InjectionPointReference> matched = new HashMap<AbstractInsnNode, InjectionPointReference>();
        for (MixinTargetSelector selector : selectors) {
            for (SlicedInjectionPointSelector at : ats) {
                MethodNode targetMethod = selector.selectMethod(target, mixinSource);
                if (targetMethod != null) {
                    if (targetMethod.name.equals("<init>") && !(at.supportsInstanceCaptureInConstructors() || (injectMethodSource.access & Opcodes.ACC_STATIC) != 0)) {
                        throw new IllegalStateException("Illegal mixin: " + mixinSource.sourceNode.name + "." + injectMethodSource.name + injectMethodSource.desc + " targets " + target.name + ".<init>" + targetMethod.desc + ", which is a constructor. The handler method is non-static and the selector @At(\"" + at.getQualifiedSelectorName() + "\") does not support non-static handlers when targetting constructors.");
                    }

                    if ((targetMethod.access & Opcodes.ACC_STATIC) != 0) {
                        if (((injectMethodSource.access & Opcodes.ACC_STATIC) == 0)) {
                            throw new IllegalStateException("Illegal mixin: " + mixinSource.sourceNode.name + "." + injectMethodSource.name + injectMethodSource.desc + " targets " + target.name + "." + targetMethod.name + targetMethod.desc + " target is static, but the mixin is not.");
                        } else if (((injectMethodSource.access & Opcodes.ACC_PUBLIC) != 0)) {
                            throw new IllegalStateException("Illegal mixin: " + mixinSource.sourceNode.name + "." + injectMethodSource.name + injectMethodSource.desc + " targets " + target.name + "." + targetMethod.name + targetMethod.desc + " target is static, but the mixin is public. A mixin may not be static and public at the same time for whatever odd reasons.");
                        }
                    }

                    {
                        Deque<String> path = new ArrayDeque<String>();
                        path.add(mixinSource.sourceNode.name);
                        path.add(injectMethodSource.name + injectMethodSource.desc);
                        at.verifySlices(Collections.asLifoQueue(path), targetMethod, remapper, sharedBuilder);
                    }

                    for (AbstractInsnNode insn : at.getMatchedInstructions(targetMethod, remapper, sharedBuilder)) {
                        if (insn.getOpcode() == -1) {
                            throw new IllegalStateException("Selector " + at + " matched virtual instruction " + insn.getClass() + ". Declaring mixin " + mixinSource.sourceNode.name + "." + injectMethodSource.name + injectMethodSource.desc + " targets " + target.name + "." + targetMethod.name + targetMethod.desc);
                        }
                        matched.put(insn, new InjectionPointReference(insn, insn, targetMethod, at));
                    }
                }
            }
        }

        if (matched.size() < require) {
            throw new IllegalStateException("Illegal mixin: " + mixinSource.sourceNode.name + "." + injectMethodSource.name + injectMethodSource.desc + " requires at least" + require + " injection points but only found " + matched.size() + ".");
        }
        if (matched.size() < expect) {
            logger.warn(ASMUtil.class, "Potentially outdated mixin: {}.{} {} expects {} injection points but only found {}.", mixinSource.sourceNode.name, injectMethodSource.name, injectMethodSource.desc, expect, matched.size());
        }
        if (allow > 0 && matched.size() > allow) {
            throw new IllegalStateException("Illegal mixin: " + mixinSource.sourceNode.name + "." + injectMethodSource.name + injectMethodSource.desc + " allows up to " + allow + " injection points but " + matched.size() + " injection points were selected.");
        }

        Collection<InjectionPointReference> refs = matched.values();
        assert refs != null;
        return refs;
    }

    /**
     * Obtain the amount of arguments a method descriptor defines, irrespective of
     * their size on the stack (so both int and long are counted as 1).
     *
     * @param methodDesc The method descriptor to use
     * @return The amount of arguments
     */
    public static int getArgumentCount(@NotNull String methodDesc) {
        DescString dstring = new DescString(methodDesc);
        int count = 0;
        while (dstring.hasNext()) {
            dstring.nextType();
            count++;
        }
        return count;
    }

    @Nullable
    public static FieldNode getField(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        for (FieldNode field : node.fields) {
            if (field.name.equals(name) && field.desc.equals(desc)) {
                return field;
            }
        }
        return null;
    }

    public static int getInitialFrameSize(@NotNull MethodNode method) {
        int initialFrameSize = 0;
        if ((method.access & Opcodes.ACC_STATIC) == 0) {
            initialFrameSize++;
        }
        DescString descString = new DescString(method.desc);
        while (descString.hasNext()) {
            initialFrameSize++;
            if (ASMUtil.isCategory2(descString.nextReferenceType())) {
                initialFrameSize++;
            }
        }
        return initialFrameSize;
    }

    /**
     * Obtain the amount of input operands of a given instruction, irrespective
     * of how much space those input operands occupy on the operand stack.
     *
     * @param insn The instruction to analyze
     * @return The amount of input operands
     */
    public static int getInputOperandCount(@NotNull AbstractInsnNode insn) {
        if (insn instanceof MethodInsnNode) {
            return ASMUtil.getArgumentCount(((MethodInsnNode) insn).desc) + (insn.getOpcode() == Opcodes.INVOKESTATIC ? 0 : 1);
        } else if (insn instanceof FieldInsnNode) {
            if (insn.getOpcode() == Opcodes.GETSTATIC) {
                return 0;
            } else if (insn.getOpcode() == Opcodes.PUTSTATIC || insn.getOpcode() == Opcodes.GETFIELD) {
                return 1;
            } else {
                // PUTFIELD
                return 2;
            }
        } else {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
    }

    /**
     * Obtain the types of the input operands of a given instruction.
     * The returned list does not contain any null values, that is doubles
     * are a single value within the return list.
     *
     * <p>The values within the list are descriptor strings (e.g. Ljava/lang/Object;)
     *
     * @param insn The instruction
     * @return The input operand types
     */
    @NotNull
    public static List<String> getInputOperandTypes(AbstractInsnNode insn) {
        if (insn instanceof MethodInsnNode) {
            List<String> list = new ArrayList<String>();
            if (insn.getOpcode() != Opcodes.INVOKESTATIC) {
                list.add("L" + ((MethodInsnNode) insn).owner + ";");
            }
            DescString dString = new DescString(((MethodInsnNode) insn).desc);
            while (dString.hasNext()) {
                list.add(dString.nextType());
            }
            return list;
        } else if (insn instanceof FieldInsnNode) {
            if (insn.getOpcode() == Opcodes.GETSTATIC) {
                return Collections.emptyList();
            } else if (insn.getOpcode() == Opcodes.PUTSTATIC) {
                return Collections.singletonList(((FieldInsnNode) insn).desc);
            } else if (insn.getOpcode() == Opcodes.GETFIELD) {
                return Collections.singletonList("L" + ((FieldInsnNode) insn).owner + ";");
            } else {
                List<String> list = Arrays.asList("L" + ((FieldInsnNode) insn).owner + ";", ((FieldInsnNode) insn).desc);
                if (list == null) {
                    throw new AssertionError();
                }
                return list;
            }
        } else {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
    }

    @NotNull
    public static LabelNode getLabelNodeBefore(@NotNull AbstractInsnNode insn, @NotNull InsnList merge) {
        AbstractInsnNode lookbehind = insn.getPrevious();
        while (lookbehind.getOpcode() == -1 && !(lookbehind instanceof LabelNode)) {
            lookbehind = lookbehind.getPrevious();
            if (lookbehind == null) {
                // reached beginning of list (insn was first instruction - unlikely but possible with stuff like constants being used in an INVOKESTATIC context)
                LabelNode l = new LabelNode();
                merge.insert(l);
                return l;
            }
        }
        if (lookbehind instanceof LabelNode) {
            return (LabelNode) lookbehind;
        }
        LabelNode l = new LabelNode();
        merge.insertBefore(insn, l);
        return l;
    }

    @Nullable
    public static String getLastType(@NotNull String methodDesc) {
        DescString dstring = new DescString(methodDesc);
        String last = null;
        while (dstring.hasNext()) {
            last = dstring.nextType();
        }
        return last;
    }

    public static int getLoadOpcode(int descReturnType) {
        switch (descReturnType) {
        case '[': // array
        case 'L': // object
            return Opcodes.ALOAD;
        case 'I': // int
        case 'S': // short
        case 'C': // char
        case 'Z': // boolean
        case 'B': // byte
            return Opcodes.ILOAD;
        case 'J': // long
            return Opcodes.LLOAD;
        case 'F': // float
            return Opcodes.FLOAD;
        case 'D': // double
            return Opcodes.DLOAD;
        default:
            throw new IllegalStateException("Unknown return type: " + descReturnType + " (" + ((char) descReturnType) + ")");
        }
    }

    public static int getLoadOpcodeFromMethodDesc(@NotNull String methodDesc) {
        switch (methodDesc.codePointBefore(methodDesc.length())) {
        case ';':
            return Opcodes.ARETURN;
        case 'I': // int
        case 'S': // short
        case 'C': // char
        case 'Z': // boolean
        case 'B': // byte
            return Opcodes.IRETURN;
        case 'J': // long
            return Opcodes.LRETURN;
        case 'F': // float
            return Opcodes.FRETURN;
        case 'D': // double
            return Opcodes.DRETURN;
        case 'V':
            return Opcodes.RETURN;
        default:
            int codepoint = methodDesc.codePointBefore(methodDesc.length());
            throw new IllegalStateException("Unknown return type: " + codepoint + " ('" + new String(new int[] {codepoint}, 0, 1) + "') (0x" + Integer.toHexString(codepoint) + ") for descriptor '" + methodDesc + "'");
        }
    }

    @Nullable
    public static MethodNode getMethod(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        for (MethodNode method : node.methods) {
            if (method.name.equals(name) && method.desc.equals(desc)) {
                return method;
            }
        }
        return null;
    }

    @NotNull
    public static AbstractInsnNode getNext(AbstractInsnNode insn) {
        AbstractInsnNode next = insn.getNext();
        while (next.getOpcode() == -1) {
            next = next.getNext();
        }
        return next;
    }

    @NotNull
    public static String getRedirectHandlerSignature(@NotNull AbstractInsnNode insn) {
        switch (insn.getType()) {
            case AbstractInsnNode.METHOD_INSN: {
                MethodInsnNode minsn = (MethodInsnNode) insn;
                if (minsn.getOpcode() == Opcodes.INVOKESTATIC) {
                    return minsn.desc;
                } else {
                    return "(L" + minsn.owner + ";" + minsn.desc.substring(1);
                }
            }
            case AbstractInsnNode.FIELD_INSN: {
                FieldInsnNode fInsn = (FieldInsnNode) insn;
                if (fInsn.getOpcode() == Opcodes.GETSTATIC) {
                    return "()" + fInsn.desc;
                } else if (fInsn.getOpcode() == Opcodes.PUTSTATIC) {
                    return "(" + fInsn.desc + ")V";
                } else if (fInsn.getOpcode() == Opcodes.PUTFIELD) {
                    return "(L" + fInsn.owner + ";" + fInsn.desc + ")V";
                } else if (fInsn.getOpcode() == Opcodes.GETFIELD) {
                    return "(L" + fInsn.owner + ";)" + fInsn.desc;
                } else {
                    throw new AssertionError("Unknown opcode: " + fInsn.getOpcode());
                }
            }
            default:
                throw new UnsupportedOperationException("Instruction cannot be redirected (yet): " + insn + " of type " + insn.getType() + " (opcode " + insn.getOpcode() + ")");
        }
    }

    public static int getReturnComputationalType(@NotNull String methodDesc) {
        return methodDesc.codePointAt(methodDesc.lastIndexOf(')') + 1);
    }

    public static int getReturnOpcode(int descReturnType) {
        switch (descReturnType) {
        case 'V': // void
            return Opcodes.RETURN;
        case '[': // array
        case 'L': // object
            return Opcodes.ARETURN;
        case 'I': // int
        case 'S': // short
        case 'C': // char
        case 'Z': // boolean
        case 'B': // byte
            return Opcodes.IRETURN;
        case 'J': // long
            return Opcodes.LRETURN;
        case 'F': // float
            return Opcodes.FRETURN;
        case 'D': // double
            return Opcodes.DRETURN;
        default:
            throw new IllegalStateException("Unknown return type: " + descReturnType + " (" + ((char) descReturnType) + ")");
        }
    }

    public static int getReturnOpcode(@NotNull String methodDesc) {
        return ASMUtil.getReturnOpcode(ASMUtil.getReturnComputationalType(methodDesc));
    }

    /**
     * Obtains the descriptor type returned by a method descriptor.
     * Throws an arbitrary exception if the descriptor is not a method descriptor.
     *
     * @param methodDesc The method descriptor to get the return type of.
     * @return The returned type of the method
     */
    @NotNull
    public static String getReturnType(String methodDesc) {
        return methodDesc.substring(methodDesc.lastIndexOf(')') + 1);
    }

    public static int getShallowStashConfiguration(List<String> headTypes, int uniformDepth) {
        // 1 | 1, 1 // 1 | 2 // 2, 1 | 1, 1 // 2, 1 | 2
        // DUP2_X1, POP2 <-> DUP_X2, POP
        // 1, 1 | 1, 1 // 2 | 2 // 1, 1 | 2 // 2 | 1, 1
        // DUP2_X2, POP2 <-> DUP2_X2, POP2
        // 1, 1 | 1 // 2, | 1 // 1, 1 | 1, 2 // 2 | 1, 2
        // DUP_X2, POP <-> DUP2_X1, POP2
        int foregroundBeginIndex = headTypes.size() - uniformDepth;
        if (foregroundBeginIndex == 0) {
            if (uniformDepth == 0) {
                return 0;
            }
            // No background operands, only compute the foreground operand amount.
            if (ASMUtil.isCategory2(headTypes.get(0).codePointAt(0))
                    || (uniformDepth >= 2 && !ASMUtil.isCategory2(headTypes.get(1).codePointAt(0)))) {
                return 2;
            } else {
                return 1;
            }
        }

        int backgroundStack;
        int foregroundStack;

        if (ASMUtil.isCategory2(headTypes.get(foregroundBeginIndex - 1).codePointAt(0))
                || (foregroundBeginIndex != 1
                && !ASMUtil.isCategory2(headTypes.get(foregroundBeginIndex - 2).codePointAt(0)))) {
            backgroundStack = 2;
        } else {
            backgroundStack = 1;
        }

        if (ASMUtil.isCategory2(headTypes.get(foregroundBeginIndex).codePointAt(0))
                || (uniformDepth >= 2 && !ASMUtil.isCategory2(headTypes.get(foregroundBeginIndex + 1).codePointAt(0)))) {
            foregroundStack = 2;
        } else {
            foregroundStack = 1;
        }

        return (backgroundStack << 2) | foregroundStack;
    }

    public static int getStoreOpcode(int descReturnType) {
        switch (descReturnType) {
        case '[': // array
        case 'L': // object
            return Opcodes.ASTORE;
        case 'I': // int
        case 'S': // short
        case 'C': // char
        case 'Z': // boolean
        case 'B': // byte
            return Opcodes.ISTORE;
        case 'J': // long
            return Opcodes.LSTORE;
        case 'F': // float
            return Opcodes.FSTORE;
        case 'D': // double
            return Opcodes.DSTORE;
        default:
            throw new IllegalStateException("Unknown return type: " + descReturnType + " (" + ((char) descReturnType) + ")");
        }
    }

    public static int getStoreOpcodeFromMethodDesc(@NotNull String methodDesc) {
        return ASMUtil.getStoreOpcode(ASMUtil.getReturnComputationalType(methodDesc));
    }

    @NotNull
    public static String getTargetDesc(@NotNull MethodNode method, @NotNull StringBuilder sharedBuilder) {
        // To be fair, I am not really honest if argument capture has any effect on the selected method.
        sharedBuilder.setLength(0);
        sharedBuilder.append('(');
        DescString dstring = new DescString(method.desc);
        boolean ciPresent = false;
        while (dstring.hasNext()) {
            String selected = dstring.nextType();
            if (selected.equals(CALLBACK_INFO_RETURNABLE_DESC) || selected.equals(CALLBACK_INFO_DESC)) {
                ciPresent = true;
                break;
            }
        }
        sharedBuilder.append(")V"); // Void methods are targeted unless otherwise specified
        if (!ciPresent) {
            throw new MixinParseException("Method " + method.name + method.desc + " should have a CallbackInfo or a CallbackInfoReturnable as one of it's arguments. But it does not.");
        }
        return sharedBuilder.toString();
    }

    public static boolean hasField(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        // TODO Also check supers?
        return getField(node, name, desc) != null;
    }

    public static boolean hasMethod(@NotNull ClassNode node, @NotNull String name, @NotNull String desc) {
        // TODO Also check supers? (and interfaces too?)
        return getMethod(node, name, desc) != null;
    }

    public static boolean hasReducedAccess(int witnessAccess, int testAccess) {
        if ((witnessAccess & Opcodes.ACC_PUBLIC) != 0) {
            return (testAccess & Opcodes.ACC_PUBLIC) == 0;
        } else if ((witnessAccess & Opcodes.ACC_PROTECTED) != 0) {
            return (testAccess & (Opcodes.ACC_PROTECTED | Opcodes.ACC_PUBLIC)) == 0;
        } else if ((witnessAccess & Opcodes.ACC_PRIVATE) != 0) {
            return false; // You can't go below private
        } else {
            // Package-protected
            return (testAccess & Opcodes.ACC_PRIVATE) != 0;
        }
    }

    public static boolean isBetween(AbstractInsnNode start, AbstractInsnNode end, @NotNull AbstractInsnNode insn) {
        AbstractInsnNode walkerInsn = start;
        while ((walkerInsn = walkerInsn.getNext()) != null && walkerInsn != insn);
        if (walkerInsn == null) {
            return false;
        }
        while ((walkerInsn = walkerInsn.getNext()) != null && walkerInsn != end);
        return walkerInsn != null;
    }

    public static boolean isCategory2(int descType) {
        return descType == 'J' || descType == 'D';
    }

    public static boolean isCategory2VarInsn(int opcode) {
        return opcode == Opcodes.DSTORE
                || opcode == Opcodes.DLOAD
                || opcode == Opcodes.LLOAD
                || opcode == Opcodes.LSTORE;
    }

    public static boolean isLoad(int opcode) {
        return Opcodes.ILOAD <= opcode && opcode <= Opcodes.ALOAD;
    }

    public static boolean isReturn(int opcode) {
        return Opcodes.IRETURN <= opcode && opcode <= Opcodes.RETURN;
    }

    public static boolean isStore(int opcode) {
        return Opcodes.ISTORE <= opcode && opcode <= Opcodes.ASTORE;
    }

    /**
     * Lazily allocates a shared <code>CallbackInfo</code> or <code>CallbackInfoCallbackInfo</code>
     * instance for the given target method. This method will try to aggressively reuse indices
     * for the <code>CallbackInfo</code> or <code>CallbackInfoCallbackInfo</code>.
     *
     * <p>The instance will be loaded onto the top of the stack within the provided usageSiteOutput
     * {@link InsnList}.
     *
     * <p>This method mutates the injectTarget {@link MethodNode}.
     *
     * <p>The callback info will be cancellable.
     *
     * @param injectTarget The method which is being injected into.
     * @param usageSiteOutput An instruction list where the allocation of the instance occurs in,
     * if the allocation did not already occur.
     * @return The index from which the callback info instance can be loaded from afterwards.
     * @since 0.6.3
     */
    public static int loadCallbackInfoInstance(@NotNull MethodNode injectTarget, @NotNull InsnList usageSiteOutput) {
        if (injectTarget.invisibleAnnotations == null) {
            injectTarget.invisibleAnnotations = new ArrayList<AnnotationNode>();
        }

        int sharedIndex = -1;
        for (AnnotationNode annotation : injectTarget.invisibleAnnotations) {
            if (annotation.desc.equals(ASMUtil.COMMON_CI_INSTANCE_ANNOT_DESC)) {
                List<Object> annotationValues = annotation.values;
                if (annotationValues == null || annotationValues.size() != 2) {
                    throw new UnsupportedOperationException("Common CI Instance annotation value count > 2: " + annotationValues);
                }
                if (!"index".equals(annotationValues.get(0))) {
                    throw new IllegalStateException("Common CI Instance annotation has an unexpected key: '" + annotationValues.get(0) + "'; Expected 'index'.");
                }
                sharedIndex = ((Integer) annotationValues.get(1)).intValue();
            }
        }

        if (sharedIndex < 0) {
            int minimumLocalIndex = ASMUtil.getInitialFrameSize(injectTarget);
            for (AbstractInsnNode insn = injectTarget.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (insn instanceof VarInsnNode) {
                    int varIndex = ((VarInsnNode) insn).var + (ASMUtil.isCategory2VarInsn(insn.getOpcode()) ? 1 : 0);
                    minimumLocalIndex = Math.max(minimumLocalIndex, varIndex);
                }
            }

            sharedIndex = minimumLocalIndex + 1;
            AnnotationNode annotation = new AnnotationNode(ASMUtil.COMMON_CI_INSTANCE_ANNOT_DESC);
            annotation.values = new ArrayList<Object>(Arrays.<Object>asList("index", sharedIndex));
            injectTarget.invisibleAnnotations.add(annotation);
            injectTarget.instructions.insert(new VarInsnNode(Opcodes.ASTORE, sharedIndex));
            injectTarget.instructions.insert(new InsnNode(Opcodes.ACONST_NULL));
        }

        String callbackInfoType = injectTarget.desc.codePointBefore(injectTarget.desc.length()) != 'V' ? ASMUtil.CALLBACK_INFO_RETURNABLE_NAME : ASMUtil.CALLBACK_INFO_NAME;

        usageSiteOutput.add(new VarInsnNode(Opcodes.ALOAD, sharedIndex));
        LabelNode label = new LabelNode();
        usageSiteOutput.add(new JumpInsnNode(Opcodes.IFNONNULL, label));
        usageSiteOutput.add(new TypeInsnNode(Opcodes.NEW, callbackInfoType));
        usageSiteOutput.add(new InsnNode(Opcodes.DUP));
        usageSiteOutput.add(new LdcInsnNode(injectTarget.name));
        usageSiteOutput.add(new InsnNode(Opcodes.ICONST_1)); // cancellable = true
        usageSiteOutput.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, callbackInfoType, "<init>", "(Ljava/lang/String;Z)V"));
        usageSiteOutput.add(new VarInsnNode(Opcodes.ASTORE, sharedIndex));
        usageSiteOutput.add(label);
        usageSiteOutput.add(new VarInsnNode(Opcodes.ALOAD, sharedIndex));

        return sharedIndex;
    }

    private static <T extends AnnotationNode> List<T> mergeAnnotationLists(@Nullable List<T> annotationsA, @Nullable List<T> annotationsB) {
        List<T> annotationsACopy = ASMUtil.copyAnnotationList(annotationsA);
        List<T> annotationsBCopy = ASMUtil.copyAnnotationList(annotationsB);

        if (annotationsACopy == null) {
            return annotationsBCopy;
        } else if (annotationsBCopy != null) {
            annotationsACopy.addAll(annotationsBCopy);
        }

        return annotationsACopy;
    }

    /**
     * Temporarily pop (or hide them deep in the stack) a given amount of operands.
     * This method will attempt to use as few additional local variables as possible,
     * depending on the implementation this may be achieved via annotations or even
     * stack analysis. The method's signature is made in a way to easily change the implementation
     * as needed, so arguments that appear superfluous on the surface ought not be
     * discarded.
     *
     * @param method The method where the instructions belong to. For locals analysis purposes only.
     * @param beginMoveInsn The instruction at which the stack has it's original contents.
     * For locals analysis purposes only.
     * @param endRollbackInsn The instruction at which the stack should return to it's original
     * constellation. No jump instruction which moves outside beginMoveInsn and this instruction
     * may be between the two instructions. For locals analysis purposes only.
     * @param headTypes The type descriptors of the types on the top of the stack.
     * There might be more types a bit more deeply hidden but they are not of relevance.
     * @param uniformDepth The amount of operands to pop - category 2 operands count as
     * a single value. May not be larger than the size of headTypes .
     * @param moveOut Instructions which "store" the operands of the rollback operation are appended to
     * this insn list.
     * @param rollbackOut Instructions which "load" the operands of the rollback operation are <b>prepended</b>
     * (as per {@link InsnList#insert(AbstractInsnNode)}) to this insn list.
     */
    public static void moveStackHead(MethodNode method, @NotNull AbstractInsnNode beginMoveInsn,
            @NotNull AbstractInsnNode endRollbackInsn, @NotNull List<String> headTypes, int uniformDepth,
            @NotNull InsnList moveOut, @NotNull InsnList rollbackOut) {

        int shallowStashConfig = ASMUtil.getShallowStashConfiguration(headTypes, uniformDepth);

        if (shallowStashConfig == 0) {
            return; // There is nothing to move the stack with
        }

        int shallowStashedElements = shallowStashConfig & 0x03 /* = 0b11 */;
        boolean cat2StashedElem = shallowStashedElements == 2 && ASMUtil.isCategory2(headTypes.get(headTypes.size() - uniformDepth).codePointAt(0));
        int deepStashedElementIndex = headTypes.size() - uniformDepth + (cat2StashedElem ? 1 : shallowStashedElements);

        Set<Integer> reusableLocals = new LinkedHashSet<Integer>();

        int highestLVTIndex = 0;
        {
            Set<Integer> unusableLocals = new LinkedHashSet<Integer>();
            AbstractInsnNode insnNode = method.instructions.getFirst();
            while (insnNode != null) {
                if (insnNode.getType() == AbstractInsnNode.VAR_INSN) {
                    int local = ((VarInsnNode) insnNode).var;
                    boolean cat2 = ASMUtil.isCategory2VarInsn(insnNode.getOpcode());
                    highestLVTIndex = Math.max(highestLVTIndex, cat2 ? local + 1 : local);
                    List<TypeAnnotationNode> annots = insnNode.invisibleTypeAnnotations;
                    if (annots != null) {
                        boolean annotated = false;
                        for (TypeAnnotationNode annot : annots) {
                            if (annot.desc.equals(ASMUtil.ROLL_ANNOT_DESC)
                                    || annot.desc.equals(ASMUtil.UNROLL_ANNOT_DESC)) {
                                annotated = true;
                                if (cat2) {
                                    reusableLocals.add(local);
                                    reusableLocals.add(local + 1);
                                } else {
                                    reusableLocals.add(local);
                                }
                                break;
                            }
                        }
                        if (!annotated) {
                            if (cat2) {
                                unusableLocals.add(local);
                                unusableLocals.add(local + 1);
                            } else {
                                unusableLocals.add(local);
                            }
                        }
                    }
                }
                insnNode = insnNode.getNext();
            }

            // Other transformers could still mess up our locals reuse policies, so we better not risk anything and declare
            // them as void.
            reusableLocals.removeAll(unusableLocals);
        }

        // TODO Nested roll/unroll not yet supported. As of now this method assumes that this is never the case

        for (int i = headTypes.size() - 1; i >= deepStashedElementIndex; i--) {
            int type = headTypes.get(i).codePointAt(0);
            boolean cat2 = ASMUtil.isCategory2(type);
            int localIndex = -1;
            Iterator<Integer> it = reusableLocals.iterator();
            while (it.hasNext()) {
                int l0 = it.next();
                if (cat2 && !reusableLocals.contains(l0 + 1)) {
                    continue;
                }
                localIndex = l0;
                reusableLocals.remove(l0);
                if (cat2) {
                    reusableLocals.remove(l0 + 1);
                }
                break;
            }

            if (localIndex < 0) {
                localIndex = ++highestLVTIndex;
                if (cat2) {
                    highestLVTIndex++;
                }
            }

            VarInsnNode storeInsn = new VarInsnNode(ASMUtil.getStoreOpcode(type), localIndex);
            VarInsnNode loadInsn = new VarInsnNode(ASMUtil.getLoadOpcode(type), localIndex);
            loadInsn.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>();
            storeInsn.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>();
            // the type reference is not really right and rather made-up, but at least noone in the ecosystem should complain about it
            loadInsn.invisibleTypeAnnotations.add(new TypeAnnotationNode(TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT << 24, null, ROLL_ANNOT_DESC));
            storeInsn.invisibleTypeAnnotations.add(new TypeAnnotationNode(TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT << 24, null, UNROLL_ANNOT_DESC));
            rollbackOut.insert(loadInsn);
            moveOut.add(storeInsn);
        }

        switch (shallowStashConfig) {
        // Cases 1 & 2 happen if no background exists. Nothing needs to happen in these cases
        case 1 /* = 0b00_01 */:
        case 2 /* = 0b00_10 */:
            break;
        case 5 /* = 0b01_01 */:
            rollbackOut.insert(new InsnNode(Opcodes.POP));
            rollbackOut.insert(new InsnNode(Opcodes.DUP_X1));
            moveOut.add(new InsnNode(Opcodes.DUP_X1));
            moveOut.add(new InsnNode(Opcodes.POP));
            break;
        case 6 /* = 0b01_10 */:
            rollbackOut.insert(new InsnNode(Opcodes.POP));
            rollbackOut.insert(new InsnNode(Opcodes.DUP_X2));
            moveOut.add(new InsnNode(Opcodes.DUP2_X1));
            moveOut.add(new InsnNode(Opcodes.POP2));
            break;
        case 9 /* = 0b10_01 */:
            rollbackOut.insert(new InsnNode(Opcodes.POP2));
            rollbackOut.insert(new InsnNode(Opcodes.DUP2_X1));
            moveOut.add(new InsnNode(Opcodes.DUP_X2));
            moveOut.add(new InsnNode(Opcodes.POP));
            break;
        case 10 /* = 0b10_10 */:
            rollbackOut.insert(new InsnNode(Opcodes.POP2));
            rollbackOut.insert(new InsnNode(Opcodes.DUP2_X2));
            moveOut.add(new InsnNode(Opcodes.DUP2_X2));
            moveOut.add(new InsnNode(Opcodes.POP2));
            break;
        default:
            throw new IllegalStateException("Unknown shallow stash config value: " + shallowStashConfig);
        }
    }

    public static int popReturn(@NotNull String methodDesc) {
        switch (methodDesc.codePointBefore(methodDesc.length())) {
        case ';': // Object or array
        case 'I': // int
        case 'S': // short
        case 'C': // char
        case 'Z': // boolean
        case 'B': // byte
        case 'F': // float
            return Opcodes.POP; // Category 1 (1 slot in operand stack)
        case 'J': // long
        case 'D': // double
            return Opcodes.POP2; // Category 2 (2 slots in operand stack)
        case 'V': // void (nothing to pop)
            return Opcodes.NOP;
        default:
            throw new IllegalStateException("Unable to infer the required pop opcode corresponding to the return type of method descriptor: " + methodDesc);
        }
    }

    public static AbstractInsnNode shiftInsn(AbstractInsnNode insn, int offset) {
        if (offset == 0) {
            return insn;
        } else if (offset < 0) {
            while (offset++ != 0 && insn != null) {
                insn = insn.getPrevious();
                while (insn != null && insn.getOpcode() == -1) {
                    insn = insn.getPrevious();
                }
            }
        } else {
            while (offset-- != 0 && insn != null) {
                insn = ASMUtil.getNext(insn);
            }
        }

        if (insn == null) {
            throw new IllegalStateException("Instruction shifted out of bounds. Perhaps the offset is too large?");
        }
        return insn;
    }

    public static int toOperandDepth(List<String> headTypes, int uniformDepth) {
        int operandDepth = 0;
        for (int i = 0; i < uniformDepth; i++) {
            if (ASMUtil.isCategory2(headTypes.get(headTypes.size() - i).codePointAt(0))) {
                operandDepth += 2;
            } else {
                operandDepth++;
            }
        }
        return operandDepth;
    }

    public static int toUniformDepth(List<String> headTypes, int operandDepth) {
        int uniformDepth = 0;
        for (int popDepth = 0, i = headTypes.size() - 1; popDepth < operandDepth; i--, uniformDepth++) {
            if (ASMUtil.isCategory2(headTypes.get(i).codePointAt(0))) {
                popDepth += 2;
            } else {
                popDepth++;
            }
        }
        return uniformDepth;
    }
}
