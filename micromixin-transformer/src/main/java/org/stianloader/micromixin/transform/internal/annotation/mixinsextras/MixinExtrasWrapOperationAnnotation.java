package org.stianloader.micromixin.transform.internal.annotation.mixinsextras;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.annotation.MixinAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinAtAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinDescAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinSliceAnnotation;
import org.stianloader.micromixin.transform.internal.selectors.DescSelector;
import org.stianloader.micromixin.transform.internal.selectors.MixinTargetSelector;
import org.stianloader.micromixin.transform.internal.selectors.StringSelector;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;
import org.stianloader.micromixin.transform.internal.util.CodeCopyUtil;
import org.stianloader.micromixin.transform.internal.util.DescString;
import org.stianloader.micromixin.transform.internal.util.InjectionPointReference;
import org.stianloader.micromixin.transform.internal.util.Objects;
import org.stianloader.micromixin.transform.internal.util.PrintUtils;
import org.stianloader.micromixin.transform.internal.util.locals.ArgumentCaptureContext;

public class MixinExtrasWrapOperationAnnotation extends MixinAnnotation<MixinMethodStub> {

    @NotNull
    private static final String METAFACTORY_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;";

    @NotNull
    public static final String OPERATION_TYPE = "com/llamalad7/mixinextras/injector/wrapoperation/Operation";

    @NotNull
    private static final String RUNTIME_COMPANION_TYPE = "com/llamalad7/mixinextras/injector/wrapoperation/WrapOperationRuntime";

    @NotNull
    public static MixinExtrasWrapOperationAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) throws MixinParseException {
        if ((method.access & Opcodes.ACC_STATIC) != 0 && (method.access & Opcodes.ACC_PRIVATE) == 0) {
            throw new MixinParseException("The return value modifier method " + node.name + "." + method.name + method.desc + " is static, but isn't private. Consider making the method private.");
        }

        ArgumentCaptureContext argCapture = ArgumentCaptureContext.parseWrapHandler(node, method, "WrapOperation", sharedBuilder);
        List<MixinAtAnnotation> at = new ArrayList<MixinAtAnnotation>();
        List<MixinSliceAnnotation> slice = new ArrayList<MixinSliceAnnotation>();
        Collection<MixinDescAnnotation> target = null;
        String[] targetSelectors = null;
        int require = -1;
        int expect = -1;
        int allow = -1;

        for (int i = 0; i < annot.values.size(); i += 2) {
            String name = (String) annot.values.get(i);
            Object val = annot.values.get(i + 1);
            if (name.equals("at")) {
                @SuppressWarnings("unchecked")
                List<AnnotationNode> atValues = ((List<AnnotationNode>) val);
                for (AnnotationNode atValue : atValues) {
                    if (atValue == null) {
                        throw new NullPointerException();
                    }

                    try {
                        at.add(MixinAtAnnotation.parse(node, atValue, transformer.getInjectionPointSelectors()));
                    } catch (MixinParseException mpe) {
                        throw new MixinParseException("Unable to parse @At annotation defined by " + node.name + "." + method.name + method.desc, mpe);
                    }
                }
            } else if (name.equals("target")) {
                transformer.getLogger().warn(MixinExtrasWrapOperationAnnotation.class, "Potentially outdated mixin: {}.{} {} has an @WrapOperation annotation with the 'target = ...' attribute. However, this attribute is not yet officially implemented in either MixinExtras or micromxin-annotations. You are likely running an outdated version of micromixin-transformer!", node.name, method.name, method.desc);

                if (target != null) {
                    throw new MixinParseException("Duplicate \"target\" field in @WrapOperation.");
                }

                target = new ArrayList<MixinDescAnnotation>();
                @SuppressWarnings("unchecked")
                List<AnnotationNode> atValues = ((List<AnnotationNode>) val);

                for (AnnotationNode atValue : atValues) {
                    if (atValue == null) {
                        throw new NullPointerException();
                    }

                    MixinDescAnnotation parsed = MixinDescAnnotation.parse(node, atValue);
                    target.add(parsed);
                }

                target = Collections.unmodifiableCollection(target);
            } else if (name.equals("method")) {
                if (targetSelectors != null) {
                    throw new MixinParseException("Duplicate \"method\" field in @WrapOperation.");
                }

                @SuppressWarnings("all")
                @NotNull String[] hack = (String[]) ((List) val).toArray(new String[0]);
                targetSelectors = hack;
            } else if (name.equals("require")) {
                require = ((Integer) val).intValue();
            } else if (name.equals("expect")) {
                expect = ((Integer) val).intValue();
            } else if (name.equals("allow")) {
                allow = ((Integer) val).intValue();
            } else if (name.equals("slice")) {
                @SuppressWarnings("unchecked")
                List<AnnotationNode> sliceValues = ((List<AnnotationNode>) val);
                for (AnnotationNode sliceValue : sliceValues) {
                    if (sliceValue == null) {
                        throw new NullPointerException();
                    }

                    try {
                        slice.add(MixinSliceAnnotation.parse(node, sliceValue, transformer.getInjectionPointSelectors()));
                    } catch (MixinParseException mpe) {
                        throw new MixinParseException("Unable to parse @Slice annotation defined by " + node.name + "." + method.name + method.desc, mpe);
                    }
                }
            } else {
                throw new MixinParseException("Unimplemented key in @WrapOperation: " + name);
            }
        }

        List<MixinTargetSelector> selectors = new ArrayList<MixinTargetSelector>();

        if (target != null) {
            for (MixinDescAnnotation desc : target) {
                selectors.add(new DescSelector(Objects.requireNonNull(desc)));
            }
        }

        if (targetSelectors != null) {
            for (String s : targetSelectors) {
                selectors.add(new StringSelector(Objects.requireNonNull(s)));
            }
        }

        if (selectors.isEmpty()) {
            throw new MixinParseException("No available selectors: Mixin " + node.name + "." + method.name + method.desc + " does not match anything and is not a valid mixin. Did you forget to specify 'method' or 'target'?");
        }

        if (allow < require) {
            allow = -1;
        }

        Collection<SlicedInjectionPointSelector> slicedAts = Collections.unmodifiableCollection(MixinAtAnnotation.bake(at, slice));

        return new MixinExtrasWrapOperationAnnotation(slicedAts, Collections.unmodifiableCollection(selectors), method, require, expect, allow, transformer.getLogger(), argCapture);
    }

    private final int allow;
    @NotNull
    public final Collection<SlicedInjectionPointSelector> at;
    @NotNull
    private final ArgumentCaptureContext capturedArguments;
    private final int expect;
    @NotNull
    private final MethodNode injectSource;
    @NotNull
    private final MixinLoggingFacade logger;
    private final int require;
    @NotNull
    public final Collection<MixinTargetSelector> selectors;

    private MixinExtrasWrapOperationAnnotation(@NotNull Collection<SlicedInjectionPointSelector> at, @NotNull Collection<MixinTargetSelector> selectors,
            @NotNull MethodNode injectSource, int require, int expect, int allow, @NotNull MixinLoggingFacade logger,
            @NotNull ArgumentCaptureContext capturedArguments) {
        this.at = at;
        this.selectors = selectors;
        this.injectSource = injectSource;
        this.require = require;
        this.expect = expect;
        this.allow = allow;
        this.logger = logger;
        this.capturedArguments = capturedArguments;
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub,
            @NotNull MixinMethodStub source, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        MethodNode handlerNode = CodeCopyUtil.copyHandler(this.injectSource, sourceStub, to, remapper, hctx.lineAllocator, this.logger);
        Collection<InjectionPointReference> matched = ASMUtil.enumerateTargets(this.selectors, this.at, to, sourceStub, this.injectSource, this.require, this.expect, this.allow, remapper, sharedBuilder, this.logger);

        String expectedResultType = ASMUtil.getReturnType(this.injectSource.desc);
        List<String> expectedOperandTypes = new ArrayList<String>();

        // Note: Parameters have already been validated in ArgumentCaptureContext#parseWrapHandler which is called in the #parse method.
        // As such, validation does not need to occur again.
        // That being said, the parameters of the target instructions matched by the @At-selector need to be validated anyways,
        // so we need to re-extract them here.
        for (DescString dString = new DescString(this.injectSource.desc); dString.hasNext();) {
            String operandType = dString.nextType();

            if (operandType.equals("L" + MixinExtrasWrapOperationAnnotation.OPERATION_TYPE + ";")) {
                break;
            }

            expectedOperandTypes.add(operandType);
        }

        for (InjectionPointReference entry : matched) {
            MethodNode method = entry.targetedMethod;

            this.validateTargetInsn(sourceStub, to, entry, expectedResultType, expectedOperandTypes);

            InsnList injectBefore = new InsnList();
            InsnList injectAfter = new InsnList();
            int opcode;

            if ((handlerNode.access & Opcodes.ACC_STATIC) == 0) {
                opcode = Opcodes.INVOKEVIRTUAL;
                ASMUtil.moveStackHead(entry.targetedMethod, entry.shiftedInstruction, entry.shiftedInstruction, expectedOperandTypes, expectedOperandTypes.size(), injectBefore, injectAfter);
                injectBefore.add(new VarInsnNode(Opcodes.ALOAD, 0));

                if (!expectedOperandTypes.isEmpty()) {
                    if (ASMUtil.isCategory2(expectedOperandTypes.get(0).codePointAt(0))
                            || (expectedOperandTypes.size() > 1 && !ASMUtil.isCategory2(expectedOperandTypes.get(1).codePointAt(0)))) {
                        injectBefore.add(new InsnNode(Opcodes.DUP_X2));
                        injectBefore.add(new InsnNode(Opcodes.POP));
                    } else {
                        injectBefore.add(new InsnNode(Opcodes.SWAP));
                    }
                }

            } else {
                opcode = Opcodes.INVOKESTATIC;
            }

            injectBefore.add(this.wrapInstruction(to, hctx, entry.shiftedInstruction));
            this.capturedArguments.appendCaptures(to, method, source, entry.shiftedInstruction, injectBefore);

            MethodInsnNode replacementInsn = new MethodInsnNode(opcode, to.name, handlerNode.name, handlerNode.desc);
            method.instructions.insertBefore(entry.shiftedInstruction, injectBefore);
            method.instructions.insertBefore(entry.shiftedInstruction, replacementInsn);
            method.instructions.remove(entry.shiftedInstruction);
            method.instructions.insert(replacementInsn, injectAfter);
        }
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull HandlerContextHelper hctx,
            @NotNull ClassNode target, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        remapper.remapMethod(Objects.requireNonNull(source.getOwner().name, "name"), source.getDesc(), source.getName(), hctx.generateUniqueLocalPrefix() + source.getName());
    }

    @NotNull
    private MethodNode generateSpreader(@NotNull ClassNode targetClass, @NotNull HandlerContextHelper hctx, @NotNull MethodInsnNode insn) {
        String spreaderName = hctx.getBridgePrefix() + insn.name + hctx.getBridgeSuffix();

        String boxedReturnDesc;
        AbstractInsnNode boxInsn;
        int returnDescStart = insn.desc.lastIndexOf(')') + 1;

        // TODO validate return type

        switch (insn.desc.charAt(returnDescStart)) {
        case 'B':
            boxedReturnDesc = "Ljava/lang/Byte;";
            boxInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
            break;
        case 'C':
            boxedReturnDesc = "Ljava/lang/Character;";
            boxInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
            break;
        case 'D':
            boxedReturnDesc = "Ljava/lang/Double;";
            boxInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
            break;
        case 'I':
            boxedReturnDesc = "Ljava/lang/Integer;";
            boxInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            break;
        case 'F':
            boxedReturnDesc = "Ljava/lang/Float;";
            boxInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
            break;
        case 'S':
            boxedReturnDesc = "Ljava/lang/Short;";
            boxInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
            break;
        case 'J':
            boxedReturnDesc = "Ljava/lang/Long;";
            boxInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
            break;
        case 'V':
            boxedReturnDesc = "Ljava/lang/Void;";
            boxInsn = new InsnNode(Opcodes.ACONST_NULL);
            break;
        case 'Z':
            boxedReturnDesc = "Ljava/lang/Boolean;";
            boxInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
            break;
        case 'L':
        case '[':
            boxedReturnDesc = insn.desc.substring(returnDescStart);
            boxInsn = null;
            break;
        default:
            throw new UnsupportedOperationException("Cannot box type: " + insn.desc.substring(returnDescStart));
        }

        MethodNode spreader = new MethodNode(Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PRIVATE, spreaderName, "([Ljava/lang/Object;)" + boxedReturnDesc, null, null);

        String[] argTypes = new String[256];
        int argCount = 0;

        if (insn.getOpcode() != Opcodes.INVOKESTATIC) {
            argTypes[argCount++] = "L" + insn.owner + ";";
        }

        for (DescString dString = new DescString(insn.desc); dString.hasNext();) {
            if (argTypes.length == argCount) {
                String[] temp = argTypes;
                argTypes = new String[argCount * 2];
                System.arraycopy(temp, 0, argTypes, 0, argCount);
            }

            argTypes[argCount++] = dString.nextType();
        }

        {
            String[] shrunk = new String[argCount];
            System.arraycopy(argTypes, 0, shrunk, 0, argCount);
            argTypes = shrunk;
        }

        InsnList insnOut = spreader.instructions;

        insnOut.add(new VarInsnNode(Opcodes.ALOAD, 0));

//        if (argCount != 0) {
//            insnOut.add(new InsnNode(Opcodes.DUP));
//        }

        insnOut.add(ASMUtil.pushInt(argCount));
        insnOut.add(new LdcInsnNode(Arrays.toString(argTypes)));
        insnOut.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MixinExtrasWrapOperationAnnotation.RUNTIME_COMPANION_TYPE, "checkArgumentCount", "([Ljava/lang/Object;ILjava/lang/String;)V"));

        if (argCount != 0) {
            int i = 0;

            do {
                // ME is showing off with a lot of SWAPs and DUPs, well - we'll use simpler bytecode here for now
                insnOut.add(new VarInsnNode(Opcodes.ALOAD, 0));
//                insnOut.add(new InsnNode(Opcodes.SWAP));
//                insnOut.add(new InsnNode(Opcodes.DUP));

                insnOut.add(ASMUtil.pushInt(i));
                insnOut.add(new InsnNode(Opcodes.AALOAD));

                String argType = argTypes[i++];

                assert argType != null;

                ASMUtil.unboxType(argType, insnOut);
            } while (i < argCount);
        }

        insnOut.add(new MethodInsnNode(insn.getOpcode(), insn.owner, insn.name, insn.desc));

        if (boxInsn != null) {
            insnOut.add(boxInsn);
        }

        insnOut.add(new InsnNode(Opcodes.ARETURN));

        return spreader;
    }

    private void validateTargetInsn(@NotNull MixinStub sourceStub, @NotNull ClassNode targetClass, @NotNull InjectionPointReference target, @NotNull String expectedResultType, @NotNull List<String> expectedOperands) {
        AbstractInsnNode shiftedInsn = target.shiftedInstruction;

        if (shiftedInsn instanceof MethodInsnNode) {
            int i = 0;
            MethodInsnNode mInsn = (MethodInsnNode) shiftedInsn;

            // Validate input operands
            if (shiftedInsn.getOpcode() != Opcodes.INVOKESTATIC) {
                if (expectedOperands.size() <= i) {
                    throw new IllegalStateException("Invalid mixin: The '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' instruction cannot be wrapped by the @WrapOperation-handler. " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets INVOKE instruction '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' within method " + targetClass.name + "." + target.targetedMethod.name + target.targetedMethod.desc + " due to @At-selector " + target.atSelector.toString() + ". Expected operands of target instruction: " + expectedOperands + ", but the " + (i + 1) + PrintUtils.cardinalSuffix(i + 1) + " operand (\"owner\"/\"reciever\") doesn't match (it should be 'L" + mInsn.owner + ";', but does not exist in the @WrapOperation-handler). Captured operands should come before the parameter of type " + MixinExtrasWrapOperationAnnotation.OPERATION_TYPE + "!");
                }

                String operand0 = expectedOperands.get(i++);

                if (!operand0.regionMatches(1, mInsn.owner, 0, mInsn.owner.length())) {
                    throw new IllegalStateException("Invalid mixin: The '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' instruction cannot be wrapped by the @WrapOperation-handler. " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets INVOKE instruction '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' within method " + targetClass.name + "." + target.targetedMethod.name + target.targetedMethod.desc + " due to @At-selector " + target.atSelector.toString() + ". Expected operands of target instruction: " + expectedOperands + ", but the " + i + PrintUtils.cardinalSuffix(i) + " operand (\"owner\"/\"reciever\") doesn't match (it should be 'L" + mInsn.owner + ";'). Captured operands should come before the parameter of type " + MixinExtrasWrapOperationAnnotation.OPERATION_TYPE + "!");
                }
            }

            for (DescString dString = new DescString(mInsn.desc); dString.hasNext();) {
                if (expectedOperands.size() <= i) {
                    throw new IllegalStateException("Invalid mixin: The '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' instruction cannot be wrapped by the @WrapOperation-handler. " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets INVOKE instruction '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' within method " + targetClass.name + "." + target.targetedMethod.name + target.targetedMethod.desc + " due to @At-selector " + target.atSelector.toString() + ". Expected operands of target instruction: " + expectedOperands + ", but the " + (i + 1) + PrintUtils.cardinalSuffix(i + 1) + " operand doesn't match (it should be 'L" + mInsn.owner + ";', but does not exist in the @WrapOperation-handler). Captured operands should come before the parameter of type " + MixinExtrasWrapOperationAnnotation.OPERATION_TYPE + "!");
                }

                String operand = expectedOperands.get(i++);
                String actualOperand = dString.nextType();

                if (!operand.equals(actualOperand)) {
                    throw new IllegalStateException("Invalid mixin: The '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' instruction cannot be wrapped by the @WrapOperation-handler. " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets INVOKE instruction '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' within method " + targetClass.name + "." + target.targetedMethod.name + target.targetedMethod.desc + " due to @At-selector " + target.atSelector.toString() + ". Expected operands of target instruction: " + expectedOperands + ", but the " + (i) + PrintUtils.cardinalSuffix(i) + " operand doesn't match (it should be 'L" + mInsn.owner + ";'). Captured operands should come before the parameter of type " + MixinExtrasWrapOperationAnnotation.OPERATION_TYPE + "!");
                }
            }

            if (expectedOperands.size() > i) {
                throw new IllegalStateException("Invalid mixin: The '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' instruction cannot be wrapped by the @WrapOperation-handler. " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets INVOKE instruction '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' within method " + targetClass.name + "." + target.targetedMethod.name + target.targetedMethod.desc + " due to @At-selector " + target.atSelector.toString() + ". Expected operands of target instruction: " + expectedOperands + ", but the target instruction has fewer operands compared to those that get captured.");
            }

            // Validate return
            String returnType = ASMUtil.getReturnType(mInsn.desc);

            if (!returnType.equals(expectedResultType)) {
                throw new IllegalStateException("Invalid mixin: The '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' instruction cannot be wrapped by the @WrapOperation-handler. " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets INVOKE instruction '" + ASMUtil.getOpcodeName(mInsn.getOpcode()) + " " + mInsn.owner + "." + mInsn.name + mInsn.desc + "' within method " + targetClass.name + "." + target.targetedMethod.name + target.targetedMethod.desc + " due to @At-selector " + target.atSelector.toString() + ". Expected return type of target instruction: " + expectedResultType + ", but the target instruction returns a value of type " + returnType + ". Consider changing the return type of the @WrapOperation-handler method.");
            }
        } else {
            throw new UnsupportedOperationException("Instruction of type " + shiftedInsn.getClass().getSimpleName() + " not supported by micromixin-transformer's implementation of @WrapOperation at this point. Only the INVOKEx (except INVOKEDYNAMIC) family of instructions is supported at this point. The method " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets method " + targetClass.name + "." + target.targetedMethod.name + target.targetedMethod.desc + " using @At-selector " + target.atSelector.toString() + ", causing this exception.");
        }
    }

    @NotNull
    private InvokeDynamicInsnNode wrapInstruction(@NotNull ClassNode targetClass, @NotNull HandlerContextHelper hctx, @NotNull AbstractInsnNode insn) {
        if (insn instanceof MethodInsnNode) {
            MethodInsnNode mInsn = (MethodInsnNode) insn;
            MethodNode spreader = this.generateSpreader(targetClass, hctx, mInsn);
            targetClass.methods.add(spreader);
            Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", MixinExtrasWrapOperationAnnotation.METAFACTORY_DESC, false);
            Type arg0 = Type.getMethodType("([Ljava/lang/Object;)Ljava/lang/Object;");
            Handle arg1 = new Handle(Opcodes.H_INVOKESTATIC, targetClass.name, spreader.name, spreader.desc, false);
            Type arg2 = Type.getMethodType(spreader.desc);

            return new InvokeDynamicInsnNode("call", "()L" + MixinExtrasWrapOperationAnnotation.OPERATION_TYPE + ";", bsm, arg0, arg1, arg2);
        } else {
            throw new UnsupportedOperationException("Instruction of type " + insn.getClass().getSimpleName() + " cannot be wrapped (by this method).");
        }
    }
}
