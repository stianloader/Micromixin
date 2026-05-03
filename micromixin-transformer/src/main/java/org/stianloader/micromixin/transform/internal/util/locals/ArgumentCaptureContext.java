package org.stianloader.micromixin.transform.internal.util.locals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.MixinVendor;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.annotation.AbstractOverlayAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinInjectAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinModifyConstantAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.mixinsextras.MixinExtrasModifyReturnValueAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.mixinsextras.MixinExtrasWrapOperationAnnotation;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;
import org.stianloader.micromixin.transform.internal.util.DescString;
import org.stianloader.micromixin.transform.internal.util.Objects;
import org.stianloader.micromixin.transform.internal.util.PrintUtils;

public class ArgumentCaptureContext {

    private static final class CapturedArgument implements Capturing {
        @NotNull
        private final String capturedType;

        /**
         * The index of the argument within the local variable table, excluding
         * the implicit `this` argument (so for static and non-static methods,
         * the first argument is at index 0).
         *
         * <p>Be aware that some computational types take two entries in the LVT,
         * that is doubles and longs have a different size than ints or objects.
         *
         * <p>In other words, this is the LVT index of the
         * captured argument to load via {@link VarInsnNode} (except
         * for non-static methods, where it is incremented by 1 beforehand).
         */
        private final int captureOffset;

        public CapturedArgument(int offset, @NotNull String capturedType) {
            this.captureOffset = offset;
            this.capturedType = Objects.requireNonNull(capturedType, "'capturedType' may not be null!");
        }

        @Override
        public void capture(@NotNull InsnList outputInstructionListBeforeInject, @NotNull InsnList outputInstructionListAfterInject, @NotNull MixinMethodStub sourceStub, @NotNull ClassNode targetNode, @NotNull MethodNode targetMethod, @NotNull List<String> availableArgs) {
            int localOffset = (targetMethod.access & Opcodes.ACC_STATIC) == 0 ? 1 : 0;

            if (this.captureOffset >= availableArgs.size()) {
                throw new IndexOutOfBoundsException("Cannot capture arguments of target method: The injector defined by " + sourceStub.getOwner().name + "." + sourceStub.getName() + sourceStub.getDesc() + " is probably attempting to capture more arguments than are available in the target method. Capture offset outside applicable bounds: " + this.captureOffset + ". Available capturable arguments: " + availableArgs + ". As such the maximum value is " + availableArgs.size() + " (exclusive).");
            }

            if (!this.capturedType.equals(availableArgs.get(this.captureOffset))) {
                throw new IllegalStateException("Unable to capture argument: Descriptor mismatch: Captured argument tries to capture an " + this.capturedType + " at offset " + this.captureOffset + ", but instead there is a " + availableArgs.get(this.captureOffset) + " at this place. Failed mixin stub: " + sourceStub.getOwner().name + "." + sourceStub.getName() + sourceStub.getDesc() + " targets " + targetNode.name + "." + targetMethod.name + targetMethod.desc);
            }

            outputInstructionListBeforeInject.add(new VarInsnNode(ASMUtil.getLoadOpcode(this.capturedType.codePointAt(0)), localOffset + this.captureOffset));
        }
    }

    private static final class CapturedCallbackInfo implements Capturing {
        @NotNull
        private final String capturedType;

        private final int paramIndex;

        public CapturedCallbackInfo(int paramIndex, @NotNull String capturedType) {
            this.paramIndex = paramIndex;
            this.capturedType = capturedType;
        }

        @Override
        public void capture(@NotNull InsnList outputInstructionListBeforeInject, @NotNull InsnList outputInstructionListAfterInject, @NotNull MixinMethodStub sourceStub, @NotNull ClassNode targetNode, @NotNull MethodNode targetMethod, @NotNull List<String> targetMethodArgTypes) {
            int returnType = targetMethod.desc.codePointBefore(targetMethod.desc.length());

            if (returnType != 'V') {
                if (!ASMUtil.CALLBACK_INFO_RETURNABLE_DESC.equals(this.capturedType)) {
                    throw new IllegalStateException("The target method " + targetMethod.name + targetMethod.desc + " returns void, but the parameter at index " + this.paramIndex + " of type '" + this.capturedType + "' is annotated with @Cancellable. The type should be '" + ASMUtil.CALLBACK_INFO_RETURNABLE_DESC + "' in this case. Invalid mixin: " + sourceStub.getOwner().name + "." + sourceStub.getName() + sourceStub.getDesc());
                }
            } else {
                if (!ASMUtil.CALLBACK_INFO_DESC.equals(this.capturedType)) {
                    throw new IllegalStateException("The target method " + targetMethod.name + targetMethod.desc + " returns void, but the parameter at index " + this.paramIndex + " of type '" + this.capturedType + "' is annotated with @Cancellable. The type should be '" + ASMUtil.CALLBACK_INFO_DESC + "' in this case. Invalid mixin: " + sourceStub.getOwner().name + "." + sourceStub.getName() + sourceStub.getDesc());
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
        }
    }

    public static enum CaptureType {
        CANCELLABLE("com/llamalad7/mixinextras/sugar/Cancellable"),
        NORMAL_ARGUMENT(null);

        @Nullable
        private final String annotationName;

        private CaptureType(@Nullable String annotationName) {
            this.annotationName = annotationName;
        }

        @Nullable
        public String getAnnotationName() {
            return this.annotationName;
        }
    }

    private static interface Capturing {
        void capture(@NotNull InsnList outputInstructionListBeforeInject, @NotNull InsnList outputInstructionListAfterInject, @NotNull MixinMethodStub sourceStub, @NotNull ClassNode targetNode, @NotNull MethodNode targetMethod, @NotNull List<String> targetMethodArgTypes);
    }

    @NotNull
    private static final ArgumentCaptureContext NO_CAPTURES = new ArgumentCaptureContext(Collections.<Capturing>emptyList());

    @NotNull
    @Contract(pure = false, mutates = "arg1")
    private static ArgumentCaptureContext baseParse(@NotNull ClassNode mixinSourceClass, @NotNull MethodNode mixinSource, @NotNull DescString dString, int paramOffset, int lvtOffset, @NotNull MixinLoggingFacade logger, @Nullable MixinVendor vendorCompatibility) {
        List<Capturing> arguments = new ArrayList<Capturing>();
        int argCaptureLVTIndex = 0; // Used for argument capture (to offset everything loaded by operand capture and other miscellaneous things)

        CaptureType previousType = null;

        while (dString.hasNext()) {
            CaptureType captureType = ArgumentCaptureContext.getType(mixinSource.invisibleParameterAnnotations, paramOffset++);
            String type = dString.nextType();

            validateCaptureType:
            if (previousType != null && previousType != CaptureType.NORMAL_ARGUMENT && captureType == CaptureType.NORMAL_ARGUMENT) {

                if (vendorCompatibility == MixinVendor.MICROMIXIN) {
                    break validateCaptureType; // "Standard" and special (e.g. Cancellable) arguments can be interspersed in micromixin
                } else if (vendorCompatibility == null) {
                    logger.warn(ArgumentCaptureContext.class, "The mixin handler {}.{}{} has a parameter annotated for argument capture (i.e. it is not annotated with @Cancellable, @Share, @Local, or similar) at parameter index {}, however the parameter before that was annotated with @{}. Under sponge-like mixin implementations, this behaviour is not supported. As such, this mixin will not run in an environment using a spongeian mixin transformer. To suppress this warning message, explicitly enable the MICROMIXIN vendor compatibility mode using the '{}' system property.", mixinSourceClass.name, mixinSource.name, mixinSource.desc, paramOffset - 1, previousType.getAnnotationName(), MixinTransformer.VENDOR_COMPAT_SYSTEM_PROPERTY);
                    break validateCaptureType;
                } else if (vendorCompatibility.isSpongeLike()) {
                    throw new MixinParseException("Invalid mixin handler: " + mixinSourceClass.name + "." + mixinSource.name + mixinSource.desc + " captures a @" + captureType.getAnnotationName() + "-annotated parameter before capturing a parameter without special annotations at index " + (paramOffset - 1) + ". Special parameters (such as those annotated with @Cancellable, @Share, or @Local) must come after parameters used for argument capture without additional meaning when using the " + vendorCompatibility + " vendor compatibility mode. Consider using the MICROMIXIN vendor compatibility mode, which supports this behaviour.");
                } else {
                    throw new UnsupportedOperationException("Unknown/Unsupported mixin vendor: " + vendorCompatibility);
                }
            }

            previousType = captureType;

            if (captureType == CaptureType.NORMAL_ARGUMENT) {
                arguments.add(new CapturedArgument(argCaptureLVTIndex, type));
                int size = ASMUtil.getLVTSize(type);
                lvtOffset += size;
                argCaptureLVTIndex += size;
            } else if (captureType == CaptureType.CANCELLABLE) {
                if (!type.equals(ASMUtil.CALLBACK_INFO_RETURNABLE_DESC) && !type.equals(ASMUtil.CALLBACK_INFO_DESC)) {
                    throw new MixinParseException("Illegal mixin detected: A parameter of type '" + type + "' was annotated as @Cancellable. However, @Cancellable can only be applied on parameters of type CallbackInfo or CallbackInfoReturnable. Triggered by: " + mixinSourceClass.name + "." + mixinSource.name + mixinSource.desc + ", parameter " + (paramOffset - 1));
                }

                arguments.add(new CapturedCallbackInfo(paramOffset - 1, type));
                lvtOffset++;
            } else {
                throw new UnsupportedOperationException("Unknown/Unsupported capture type " + captureType + ". Triggered by " + mixinSourceClass.name + "." + mixinSource.name + mixinSource.desc);
            }
        }

        return new ArgumentCaptureContext(Collections.unmodifiableList(arguments));
    }

    @NotNull
    public static CaptureType getType(@Nullable List<AnnotationNode>[] invisibileParameterAnnotations, int argumentIndex) {
        if (invisibileParameterAnnotations == null) {
            return CaptureType.NORMAL_ARGUMENT;
        }

        List<AnnotationNode> annotations = invisibileParameterAnnotations[argumentIndex];

        if (annotations == null) {
            return CaptureType.NORMAL_ARGUMENT;
        }

        for (AnnotationNode annotationNode : annotations) {
            if (annotationNode.desc.startsWith("Lcom/llamalad7/mixinextras/")) {
                if (annotationNode.desc.equals("Lcom/llamalad7/mixinextras/sugar/Cancellable;")) {
                    return CaptureType.CANCELLABLE;
                } else {
                    // TODO support @Local, support @Share
                    throw new MixinParseException("Unknown/Unimplemented annotation: " + annotationNode.desc);
                }
            }
        }

        return CaptureType.NORMAL_ARGUMENT;
    }

    /**
     * Parses the captured arguments for annotation handlers such as {@link MixinModifyConstantAnnotation ModifyConstant}
     * or {@link MixinExtrasModifyReturnValueAnnotation ModifyReturnValue}.
     *
     * <p>This method has the added benefit of also checking whether the non-captured argument
     * matches the return type, as would be expected from these modify handlers. Due to this restriction
     * this method is not meant to be used for redirect handlers or other annotations such as {@link MixinExtrasWrapOperationAnnotation &#64;WrapOperation}.
     * Meanwhile other annotations (mainly {@link MixinInjectAnnotation &#64;Inject}) do not support processing
     * argument and local capture using {@link ArgumentCaptureContext} at all. They most likely have their
     * own dedicated infrastructure as such or have clear technical reasons to not do so (as is the case for
     * {@link AbstractOverlayAnnotation overlaying annotations}).
     *
     * <p>Although this method does not account for MixinExtras' &#64;Local annotation at this point in time,
     * this class - and as such this method - is engineered to easily account for it in due time.
     *
     * @param owner The {@link ClassNode} in which the modify handler is located in.
     * @param mixinSource The {@link MethodNode} which is the modify handler's source (as opposed to the target method, which is irrelevant)
     * @param annotationName The name of the annotation that invoked this method. For debugging reasons only (this string is added to a thrown {@link MixinParseException}).
     * @param logger The logger to use for warning messages (mostly related to vendor compatibility warnings).
     * @param vendorCompat The {@link MixinVendor} compatibility mode to use whilst parsing argument captures.
     * @return The {@link ArgumentCaptureContext} instance that corresponds to the {@link MethodNode MethodNode's} signature
     * with whom the insertion of argument and local capture can be more easily pulled off without having dedicated
     * implementations for every annotation.
     */
    @NotNull
    public static ArgumentCaptureContext parseModifyHandler(@NotNull ClassNode owner, @NotNull MethodNode mixinSource, @NotNull String annotationName, @NotNull MixinLoggingFacade logger, @Nullable MixinVendor vendorCompat) {
        DescString dString = new DescString(mixinSource.desc);
        List<AnnotationNode>[] parameterAnnotations = mixinSource.invisibleParameterAnnotations;

        if (ArgumentCaptureContext.getType(parameterAnnotations, 0) != CaptureType.NORMAL_ARGUMENT) {
            throw new MixinParseException("The provided modify handler " + owner.name + "." + mixinSource.name + mixinSource.desc + " has an incompatible annotation on the original (or captured) argument that needs to be modified. Note that the first parameter of a modifier handler is ineligible for argument capture when using @" + annotationName);
        }

        String returnType;
        if (!dString.hasNext()) {
            throw new MixinParseException("The modifier method " + owner.name + "." + mixinSource.name + mixinSource.desc + " is annotated with @" + annotationName + " but it does not consume the original return value. Modify handlers may not be no-args methods!");
        }

        returnType = dString.nextType();

        if (!ASMUtil.getReturnType(mixinSource.desc).equals(returnType)) {
            throw new MixinParseException("The modifier method " + owner.name + "." + mixinSource.name + mixinSource.desc + " is annotated with @" + annotationName + " but has an invalid descriptor! Modify handlers must return the same type as they consume - irrespective of class hierarchy.");
        }

        if (!dString.hasNext()) {
            return ArgumentCaptureContext.NO_CAPTURES;
        }

        return ArgumentCaptureContext.baseParse(owner, mixinSource, dString, 1, ASMUtil.getLVTSize(returnType), logger, vendorCompat);
    }

    /**
     * Parses the captured arguments for annotation handlers such as {@link MixinExtrasWrapOperationAnnotation WrapOperation}.
     *
     * @param owner The {@link ClassNode} in which the wrap handler is located in.
     * @param mixinSource The {@link MethodNode} which is the wrap handler's source (as opposed to the target method, which is irrelevant)
     * @param annotationName The name of the annotation that invoked this method. For debugging reasons only (this string is added to a thrown {@link MixinParseException}).
     * @param sharedBuilder A shared {@link StringBuilder} instance. The content of this builder might get overwritten.
     * @param logger The logger to use for warning messages (mostly related to vendor compatibility warnings).
     * @param vendorCompat The {@link MixinVendor} compatibility mode to use whilst parsing argument captures.
     * @return The {@link ArgumentCaptureContext} instance that corresponds to the {@link MethodNode MethodNode's} signature
     * with whom the insertion of argument and local capture can be more easily pulled off without having dedicated
     * implementations for every annotation.
     */
    @NotNull
    public static ArgumentCaptureContext parseWrapHandler(@NotNull ClassNode owner, @NotNull MethodNode mixinSource, @NotNull String annotationName, @NotNull StringBuilder sharedBuilder, @NotNull MixinLoggingFacade logger, @Nullable MixinVendor vendorCompat) {
        DescString dString = new DescString(mixinSource.desc);
        List<AnnotationNode>[] parameterAnnotations = mixinSource.invisibleParameterAnnotations;
        boolean hasOperation = false;
        int lvtIndex = 0;
        int paramIndex;

        for (paramIndex = 0; dString.hasNext(); paramIndex++) {
            String type = dString.nextType();

            if (ArgumentCaptureContext.getType(parameterAnnotations, paramIndex) != CaptureType.NORMAL_ARGUMENT) {
                sharedBuilder.setLength(0);
                sharedBuilder.append("The provided wrap handler ")
                    .append(owner.name)
                    .append(".")
                    .append(mixinSource.name)
                    .append(mixinSource.desc)
                    .append(" has an incompatible annotation on parameter ")
                    .append(paramIndex)
                    .append(" (a ");
                PrintUtils.fastPrettySingleDesc(type, 0, sharedBuilder);
                sharedBuilder.append("). Note that all arguments before (and including) the Operation<T> parameter of a @")
                    .append(annotationName)
                    .append(" handler is inelibible of argument capture (not to be confused with operand capture). Captured arguments come after the argument of type 'Operation<T>'.");

                throw new MixinParseException(sharedBuilder.toString());
            }

            lvtIndex += ASMUtil.getLVTSize(type);

            if (type.equals("L" + MixinExtrasWrapOperationAnnotation.OPERATION_TYPE + ";")) {
                hasOperation = true;
                paramIndex++;
                break;
            }
        }

        if (!hasOperation) {
            throw new MixinParseException("Invalid wrap handler " + owner.name + "." + mixinSource.name + mixinSource.desc + ": A method annotated with @" + annotationName + " must have an argument of type " + MixinExtrasWrapOperationAnnotation.OPERATION_TYPE);
        } else if (!dString.hasNext()) {
            return ArgumentCaptureContext.NO_CAPTURES;
        }

        return ArgumentCaptureContext.baseParse(owner, mixinSource, dString, paramIndex, lvtIndex, logger, vendorCompat);
    }

    @NotNull
    private final List<Capturing> capturedArguments;

    private ArgumentCaptureContext(@NotNull List<Capturing> arguments) {
        this.capturedArguments = arguments;
    }

    public void appendCaptures(@NotNull ClassNode targetNode, @NotNull MethodNode targetMethod, @NotNull MixinMethodStub sourceStub, @NotNull AbstractInsnNode selectedInjectionPointInsn, @NotNull InsnList outputInstructionListBeforeInject, @NotNull InsnList outputInstructionListAfterInject) {
        if (this.capturedArguments.isEmpty()) {
            return;
        }

        List<String> availableLocals = new ArrayList<String>();

        for (DescString dString = new DescString(targetMethod.desc); dString.hasNext();) {
            String arg = dString.nextType();
            availableLocals.add(arg);
            if (ASMUtil.isCategory2(arg.codePointAt(0))) {
                availableLocals.add(null);
            }
        }

        for (Capturing captureArg : this.capturedArguments) {
            captureArg.capture(outputInstructionListBeforeInject, outputInstructionListAfterInject, sourceStub, targetNode, targetMethod, availableLocals);
        }
    }
}
