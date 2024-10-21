package org.stianloader.micromixin.transform.internal.annotation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.selectors.DescSelector;
import org.stianloader.micromixin.transform.internal.selectors.MixinTargetSelector;
import org.stianloader.micromixin.transform.internal.selectors.StringSelector;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;
import org.stianloader.micromixin.transform.internal.util.CodeCopyUtil;
import org.stianloader.micromixin.transform.internal.util.DescString;
import org.stianloader.micromixin.transform.internal.util.Objects;
import org.stianloader.micromixin.transform.internal.util.locals.ArgumentCaptureContext;
import org.stianloader.micromixin.transform.internal.util.locals.ArgumentCaptureContext.ArgumentType;

public final class MixinRedirectAnnotation extends MixinAnnotation<MixinMethodStub> {

    @NotNull
    public static MixinRedirectAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) throws MixinParseException {
        MixinAtAnnotation at = null;
        Collection<MixinDescAnnotation> target = null;
        String[] targetSelectors = null;
        int require = -1;
        int expect = -1;
        int allow = -1;
        MixinSliceAnnotation slice = null;

        for (int i = 0; i < annot.values.size(); i += 2) {
            String name = (String) annot.values.get(i);
            Object val = annot.values.get(i + 1);
            if (name.equals("at")) {
                try {
                    if (val == null) {
                        throw new MixinParseException("Null annotation node");
                    }
                    at = MixinAtAnnotation.parse(node, (AnnotationNode) val, transformer.getInjectionPointSelectors());
                } catch (MixinParseException mpe) {
                    throw new MixinParseException("Unable to parse @At annotation defined by " + node.name + "." + method.name + method.desc, mpe);
                }
            } else if (name.equals("target")) {
                if (target != null) {
                    throw new MixinParseException("Duplicate \"target\" field in @Redirect " + node.name + "." + method.name + method.desc);
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
                    throw new MixinParseException("Duplicate \"method\" field in @Redirect " + node.name + "." + method.name + method.desc);
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
                assert val != null;
                slice = MixinSliceAnnotation.parse(node, (AnnotationNode) val, transformer.getInjectionPointSelectors());
            } else {
                throw new MixinParseException("Unimplemented key in @Redirect " + node.name + "." + method.name + method.desc + ": " + name);
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
            // IMPLEMENT what about injector groups?
            throw new MixinParseException("No available selectors: Mixin " + node.name + "." + method.name + method.desc + " does not match anything and is not a valid mixin. Did you forget to specify 'method' or 'target'?");
        }

        if (at == null) {
            throw new MixinParseException("Redirector Mixin " + node.name + "." + method.name + method.desc + " should define the at-value but does not. The mixin may be compiled for a future version of mixin.");
        }

        if (allow < require) {
            allow = -1;
        }

        SlicedInjectionPointSelector slicedAt = MixinAtAnnotation.bake(at, slice);
        return new MixinRedirectAnnotation(slicedAt, Collections.unmodifiableCollection(selectors), method, require, expect, allow, transformer);
    }

    private final int allow;
    @NotNull
    public final SlicedInjectionPointSelector at;
    private final int expect;
    @NotNull
    private final MethodNode injectSource;
    private final int require;
    @NotNull
    public final Collection<MixinTargetSelector> selectors;
    @NotNull
    private final MixinTransformer<?> transformer;

    private MixinRedirectAnnotation(@NotNull SlicedInjectionPointSelector at, @NotNull Collection<MixinTargetSelector> selectors,
            @NotNull MethodNode injectSource, int require, int expect, int allow, @NotNull MixinTransformer<?> transformer) {
        this.at = at;
        this.selectors = selectors;
        this.injectSource = injectSource;
        this.require = require;
        this.expect = expect;
        this.allow = allow;
        this.transformer = transformer;
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub,
            @NotNull MixinMethodStub source, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        if ((this.injectSource.access & Opcodes.ACC_STATIC) != 0 && (this.injectSource.access & Opcodes.ACC_PRIVATE) == 0) {
            throw new MixinParseException("The redirect handler method " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " is static, but isn't private. Consider making the method private, as both access modifiers cannot be present at the same time.");
        }
        if (!this.at.supportsRedirect()) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " uses selector @At(\"" + this.at.getQualifiedSelectorName() + "\") which does not support usage within a @Redirect context.");
        }
        MethodNode handlerNode = CodeCopyUtil.copyHandler(this.injectSource, sourceStub, to, remapper, hctx.lineAllocator, this.transformer.getLogger());
        Map<AbstractInsnNode, MethodNode> matched = new HashMap<AbstractInsnNode, MethodNode>();
        for (MixinTargetSelector selector : selectors) {
            MethodNode targetMethod = selector.selectMethod(to, sourceStub);
            if (targetMethod != null) {
                // TODO ACC_STATIC is mandated in the constructor before the super() call even though the constructor itself is not ACC_STATIC.
                if ((targetMethod.access & Opcodes.ACC_STATIC) != 0) {
                    if ((this.injectSource.access & Opcodes.ACC_STATIC) == 0) {
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + targetMethod.desc + ". Target is static, but the mixin is not.");
                    }
                }

                {
                    Deque<String> path = new ArrayDeque<String>();
                    path.add(sourceStub.sourceNode.name);
                    path.add(source.getName() + source.getDesc());
                    this.at.verifySlices(Collections.asLifoQueue(path), targetMethod, remapper, sharedBuilder);
                }

                for (AbstractInsnNode insn : this.at.getMatchedInstructions(targetMethod, remapper, sharedBuilder)) {
                    if (!(insn instanceof MethodInsnNode || insn instanceof FieldInsnNode)) {
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " selects an instruction in target method " + to.name + "." + targetMethod.name + targetMethod.desc + " that isn't a MethodInsnNode or FieldInsnNode (should be any of [INVOKESTATIC, INVOKEVIRTUAL, INVOKESPECIAL, GETSTATIC, GETFIELD, PUTSTATIC, PUTFIELD]) but rather is a " + insn.getClass().getName() + ". This issue is most likely caused by an erroneous @At-value (or an invalid shift). Using @At(" + this.at.getQualifiedSelectorName() + ")");
                    }
                    matched.put(insn, targetMethod);
                }
            }
        }
        if (matched.size() < this.require) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " requires " + this.require + " injection points but only found " + matched.size() + ".");
        }
        if (matched.size() < this.expect) {
            this.transformer.getLogger().warn(MixinRedirectAnnotation.class, "Potentially outdated mixin: {}.{} {} expects {} injection points but only found {}.", sourceStub.sourceNode.name, this.injectSource.name, this.injectSource.desc, this.expect, matched.size());
        }
        if (this.allow > 0 && matched.size() > this.allow) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " allows up to " + this.allow + " injection points but " + matched.size() + " injection points were selected.");
        }

        for (Map.Entry<AbstractInsnNode, MethodNode> entry : matched.entrySet()) {
            AbstractInsnNode insn = entry.getKey();

            // TODO test whether different arguments are captured if two instructions with different signatures
            // are redirected.

            assert insn != null;

            // Validate the handler method's signature
            String expectedDesc = ASMUtil.getRedirectHandlerSignature(insn);
            // Validate return type
            int handlerArgumentEnd = handlerNode.desc.lastIndexOf(')');
            if (!expectedDesc.regionMatches(expectedDesc.lastIndexOf(')'), handlerNode.desc, handlerArgumentEnd, handlerNode.desc.length() - handlerArgumentEnd)) {
                throw new IllegalStateException("The descriptor of the handler method '" + this.injectSource.name + handlerNode.desc + "' does not matched the expected signature required to redirect the selected instruction (return type mismatch). The expected descriptor would be follows: " + expectedDesc);
            }

            DescString expectedSignature = new DescString(expectedDesc);
            DescString handlerSignature = new DescString(handlerNode.desc);
            int handlerSignatureIndex = 0;

            while (expectedSignature.hasNext()) {
                if (!handlerSignature.hasNext()) {
                    throw new IllegalStateException("The descriptor of the handler method '" + this.injectSource.name + handlerNode.desc + "' does not matched the expected signature required to redirect the selected instruction (argument count mismatch). The expected descriptor would be follows: " + expectedDesc);
                }

                String handlerArgumentType = handlerSignature.nextType();
                if (ArgumentCaptureContext.getType(handlerNode.invisibleParameterAnnotations, handlerSignatureIndex++) != ArgumentType.NORMAL_ARGUMENT) {
                    throw new IllegalStateException("The descriptor of the handler method '" + this.injectSource.name + handlerNode.desc + "' includes the improperly annotated argument " + handlerArgumentType + " at index " + (handlerSignatureIndex - 1) + "; Only arguments eligble for argument capture may be annotated with @Local, @Share, @Cancellable, or similar. However, this argument is not eligble for argument capture as it is part of the expected descriptor for the targetted instruction: " + expectedDesc);
                }

                if (!expectedSignature.nextType().equals(handlerArgumentType)) {
                    throw new IllegalStateException("The descriptor of the handler method '" + this.injectSource.name + handlerNode.desc + "' does not matched the expected signature required to redirect the selected instruction (argument type mismatch). The expected descriptor would be follows: " + expectedDesc);
                }
            }

            MethodNode targetMethod = entry.getValue();
            InsnList instructions = targetMethod.instructions;
            InsnList postInject = new InsnList();
            int insertedOpcode;

            if ((this.injectSource.access & Opcodes.ACC_STATIC) == 0) {
                List<String> argTypes = ASMUtil.getInputOperandTypes(insn);
                InsnList preRollback = new InsnList();
                InsnList rollback = new InsnList();

                this.handleArgumentCapture(handlerSignature, handlerSignatureIndex, targetMethod, insn, preRollback, postInject, expectedDesc, argTypes);
                ASMUtil.moveStackHead(targetMethod, insn, insn, argTypes, argTypes.size(), preRollback, rollback);
                instructions.insertBefore(insn, preRollback);
                instructions.insertBefore(insn, new VarInsnNode(Opcodes.ALOAD, 0));

                if (!argTypes.isEmpty()) {
                    if (ASMUtil.isCategory2(argTypes.get(0).codePointAt(0))
                            || (argTypes.size() > 1 && !ASMUtil.isCategory2(argTypes.get(1).codePointAt(0)))) {
                        instructions.insertBefore(insn, new InsnNode(Opcodes.DUP_X2));
                        instructions.insertBefore(insn, new InsnNode(Opcodes.POP));
                    } else {
                        instructions.insertBefore(insn, new InsnNode(Opcodes.SWAP));
                    }
                }

                instructions.insertBefore(insn, rollback);
                insertedOpcode = Opcodes.INVOKEVIRTUAL;
            } else {
                insertedOpcode = Opcodes.INVOKESTATIC;
                if (handlerSignature.hasNext()) {
                    InsnList injectInsns = new InsnList();
                    this.handleArgumentCapture(handlerSignature, handlerSignatureIndex, targetMethod, insn, injectInsns, postInject, expectedDesc, null);
                    instructions.insertBefore(insn, injectInsns);
                }
            }

            MethodInsnNode replacementInsn = new MethodInsnNode(insertedOpcode, to.name, handlerNode.name, handlerNode.desc);
            instructions.insertBefore(insn, replacementInsn);
            instructions.insert(replacementInsn, postInject);
            instructions.remove(insn);
        }
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull HandlerContextHelper hctx,
            @NotNull ClassNode target, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        remapper.remapMethod(source.getOwner().name, source.getDesc(), source.getName(), hctx.generateUniqueLocalPrefix() + "redirect$" + source.getName());
    }

    /**
     * {@link MixinRedirectAnnotation Redirect}-specific implementation for argument capture
     * or other special captures, such as the capture of &#64;Local, &#64;Share, or &#64;Cancellable.
     *
     * <p>This injector-specific implementation is used as redirect is not conforming to the standard
     * ModifyX pattern and thus the capture and non-capture arguments cannot be known ahead of time.
     *
     * @param remainingHandlerSignature The {@link DescString} storing the remaining capturing arguments.
     * @param handlerSignatureIndex The index of the next type stored in the given {@link DescString} which
     * marks the index of the type within the handler method's descriptor string. Note: Both Category 1 and
     * Category 2 types count as having a length of 1 as concerns the semantics of the parameter.
     * @param targetMethod The method in which the handler method is injecting into.
     * @param targetInsn The instruction before which the injection should occur (used for stack analysis
     * required for the &#64;Local sugar annotation).
     * @param outputInstructionListBeforeInject The {@link InsnList} where all instructions concerning
     * the loading of captured arguments should be appended to. These instructions should be inserted
     * immediately before the call to the handler method.
     * @param outputInstructionListAfterInject An {@link InsnList} where instructions are added concerning
     * method cancellation. These instructions should be inserted immediately after the call to the handler
     * method.
     * @param expectedDesc The minimum descriptor for the redirect handler to be able to inject
     * into the given target at the given instruction. See {@link ASMUtil#getRedirectHandlerSignature(AbstractInsnNode)}.
     * @param loadedTypesOut Collection in which the types loaded into the stack are recorded to.
     * Or <code>null</code>, in order to not perform this recording. Used for methods such as
     * {@link ASMUtil#moveStackHead(MethodNode, AbstractInsnNode, AbstractInsnNode, List, int, InsnList, InsnList)}.
     * @since 0.6.3
     */
    private void handleArgumentCapture(@NotNull DescString remainingHandlerSignature, int handlerSignatureIndex,
            @NotNull MethodNode targetMethod, @NotNull AbstractInsnNode targetInsn,
            @NotNull InsnList outputInstructionListBeforeInject, @NotNull InsnList outputInstructionListAfterInject,
            @NotNull String expectedDesc, @Nullable Collection<String> loadedTypesOut) {
        if (!remainingHandlerSignature.hasNext()) {
            return;
        }

        DescString targetMethodDesc = new DescString(targetMethod.desc);
        int capturedArgIndex = (targetMethod.access & Opcodes.ACC_STATIC) == 0 ? 1 : 0;
        do {
            String capturedType = remainingHandlerSignature.nextType();
            ArgumentType action = ArgumentCaptureContext.getType(this.injectSource.invisibleParameterAnnotations, handlerSignatureIndex++);

            if (action == ArgumentType.NORMAL_ARGUMENT) {
                if (!targetMethodDesc.hasNext()) {
                    throw new IllegalStateException("The descriptor of the handler method '" + this.injectSource.name + this.injectSource.desc + "' does not matched the expected signature required to redirect the selected instruction (argument capture overrun). The expected descriptor would be follows: " + expectedDesc + ". Descriptor of target method for reference: " + targetMethod.desc);
                }

                if (!capturedType.equals(targetMethodDesc.nextType())) {
                    throw new IllegalStateException("The descriptor of the handler method '" + this.injectSource.name + this.injectSource.desc + "' does not matched the expected signature required to redirect the selected instruction (argument capture type mismatch). The expected descriptor would be follows: " + expectedDesc + ". Descriptor of target method for reference: " + targetMethod.desc);
                }
                int type = capturedType.codePointAt(0);
                outputInstructionListBeforeInject.add(new VarInsnNode(ASMUtil.getLoadOpcode(type), capturedArgIndex++));
                if (ASMUtil.isCategory2(type)) {
                    capturedArgIndex++;
                }
            } else if (action == ArgumentType.CANCELLABLE) {
                int returnType = targetMethod.desc.codePointBefore(targetMethod.desc.length());
                if (returnType != 'V') {
                    if (!ASMUtil.CALLBACK_INFO_RETURNABLE_DESC.equals(capturedType)) {
                        throw new IllegalStateException("The target method " + targetMethod.name + targetMethod.desc + " returns void, but the parameter at index " + (handlerSignatureIndex - 1) + " of type '" + capturedType + "' is annotated with @Cancellable. The type should be '" + ASMUtil.CALLBACK_INFO_RETURNABLE_DESC + "' in this case.");
                    }
                } else {
                    if (!ASMUtil.CALLBACK_INFO_DESC.equals(capturedType)) {
                        throw new IllegalStateException("The target method " + targetMethod.name + targetMethod.desc + " returns void, but the parameter at index " + (handlerSignatureIndex - 1) + " of type '" + capturedType + "' is annotated with @Cancellable. The type should be '" + ASMUtil.CALLBACK_INFO_DESC + "' in this case.");
                    }
                }

                int callbackSlot = ASMUtil.loadCallbackInfoInstance(targetMethod, outputInstructionListBeforeInject);
                outputInstructionListAfterInject.add(new VarInsnNode(Opcodes.ALOAD, callbackSlot));
                outputInstructionListAfterInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ASMUtil.CALLBACK_INFO_NAME, "isCancelled", "()Z"));
                LabelNode label = new LabelNode();
                outputInstructionListAfterInject.add(new JumpInsnNode(Opcodes.IFEQ, label));
                if (returnType == 'V') {
                    outputInstructionListAfterInject.add(new InsnNode(Opcodes.RETURN));
                } else {
                    String methodName;
                    String returnDesc;
                    int returnOpcode;
                    switch (returnType) {
                    case ';': // objects &  arrays
                        methodName = "getReturnValue";
                        returnDesc = "()Ljava/lang/Object;";
                        returnOpcode = Opcodes.ARETURN;
                        break;
                    case 'I': // int
                        methodName = "getReturnValueI";
                        returnDesc = "()I";
                        returnOpcode = Opcodes.IRETURN;
                        break;
                    case 'S': // short
                        methodName = "getReturnValueS";
                        returnDesc = "()S";
                        returnOpcode = Opcodes.IRETURN;
                        break;
                    case 'C': // char
                        methodName = "getReturnValueC";
                        returnDesc = "()C";
                        returnOpcode = Opcodes.IRETURN;
                        break;
                    case 'Z': // boolean
                        methodName = "getReturnValueZ";
                        returnDesc = "()Z";
                        returnOpcode = Opcodes.IRETURN;
                        break;
                    case 'B': // byte
                        methodName = "getReturnValueB";
                        returnDesc = "()B";
                        returnOpcode = Opcodes.IRETURN;
                        break;
                    case 'F': // float
                        methodName = "getReturnValueF";
                        returnDesc = "()F";
                        returnOpcode = Opcodes.FRETURN;
                        break;
                    case 'J': // long
                        methodName = "getReturnValueJ";
                        returnDesc = "()J";
                        returnOpcode = Opcodes.LRETURN;
                        break;
                    case 'D': // double
                        methodName = "getReturnValueD";
                        returnDesc = "()D";
                        returnOpcode = Opcodes.DRETURN;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown reference type: " + returnType + " for return desc of method " + targetMethod.name + targetMethod.desc);
                    }
                    outputInstructionListAfterInject.add(new VarInsnNode(Opcodes.ALOAD, callbackSlot));
                    outputInstructionListAfterInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME, methodName, returnDesc));
                    if (returnType == ';') {
                        int beginReturnIndex = targetMethod.desc.lastIndexOf(')') + 1;
                        if (targetMethod.desc.codePointAt(beginReturnIndex) == 'L') {
                            outputInstructionListAfterInject.add(new TypeInsnNode(Opcodes.CHECKCAST, targetMethod.desc.substring(beginReturnIndex + 1, targetMethod.desc.length() - 1)));
                        } else {
                            outputInstructionListAfterInject.add(new TypeInsnNode(Opcodes.CHECKCAST, targetMethod.desc.substring(beginReturnIndex)));
                        }
                    }
                    outputInstructionListAfterInject.add(new InsnNode(returnOpcode));
                }
                outputInstructionListAfterInject.add(label);
            } else {
                throw new UnsupportedOperationException("The handler method defines the capture argument of type '" + capturedType + "' at index " + (handlerSignatureIndex - 1) + " bound to action " + action + "; however this class does not know how to handle this case. The @Redirect-handler method " + this.injectSource.name + this.injectSource.desc + " can thus not be properly called.");
            }

            if (loadedTypesOut != null) {
                loadedTypesOut.add(capturedType);
            }
        } while (remainingHandlerSignature.hasNext());
    }
}
