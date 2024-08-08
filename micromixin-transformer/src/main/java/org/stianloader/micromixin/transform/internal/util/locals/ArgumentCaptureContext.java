package org.stianloader.micromixin.transform.internal.util.locals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.annotation.AbstractOverlayAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinInjectAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinModifyConstantAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.mixinsextras.MixinExtrasModifyReturnValueAnnotation;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;
import org.stianloader.micromixin.transform.internal.util.DescString;

public class ArgumentCaptureContext {

    public static enum ArgumentType {
        CANCELLABLE,
        NORMAL_ARGUMENT;
    }

    private static final class CapturedArgument {
        @NotNull
        private final String capturedType;

        /**
         * The index of the argument within the local variable table, excluding
         * the implicit `this` argument (so for static and non-static methods,
         * the first argument is at index 0).
         *
         * <p>Be aware that some computational types take two entries in the LVT,
         * that is doubles and longs have a different size than ints or objects.
         */
        private final int captureOffset;

        public CapturedArgument(int offset, @NotNull String capturedType) {
            this.captureOffset = offset;
            this.capturedType = capturedType;
        }
    }

    @NotNull
    private static final ArgumentCaptureContext NO_CAPTURES = new ArgumentCaptureContext(Collections.<CapturedArgument>emptyList());

    private static boolean captureLocals(@Nullable List<AnnotationNode> annotations) {
        if (annotations == null) {
            return false;
        }

        for (AnnotationNode annotationNode : annotations) {
            if (annotationNode.desc.startsWith("Lcom/llamalad7/mixinextras/")) {
                if (annotationNode.desc.equals("Lcom/llamalad7/mixinextras/sugar/Local;")) {
                    return true;
                } else {
                    // TODO support @Share
                    throw new MixinParseException("Unknown/Unimplemented annotation: " + annotationNode.desc);
                }
            }
        }
        return false;
    }

    @NotNull
    public static ArgumentType getType(List<AnnotationNode>[] invisibileParameterAnnotations, int argumentIndex) {
        if (invisibileParameterAnnotations == null) {
            return ArgumentType.NORMAL_ARGUMENT;
        }

        List<AnnotationNode> annotations = invisibileParameterAnnotations[argumentIndex];

        if (annotations == null) {
            return ArgumentType.NORMAL_ARGUMENT;
        }

        for (AnnotationNode annotationNode : annotations) {
            if (annotationNode.desc.startsWith("Lcom/llamalad7/mixinextras/")) {
                if (annotationNode.desc.equals("Lcom/llamalad7/mixinextras/sugar/Cancellable;")) {
                    return ArgumentType.CANCELLABLE;
                } else {
                    // TODO support @Local, support @Share
                    throw new MixinParseException("Unknown/Unimplemented annotation: " + annotationNode.desc);
                }
            }
        }

        return ArgumentType.NORMAL_ARGUMENT;
    }

    /**
     * Parses the captured arguments for annotation handlers such as {@link MixinModifyConstantAnnotation ModifyConstant}
     * or {@link MixinExtrasModifyReturnValueAnnotation ModifyReturnValue}.
     *
     * <p>This method has the added benefit of also checking whether the non-captured argument
     * matches the return type, as would be expected from these modify handlers. Due to this restriction
     * this method is not meant to be used for redirect handlers or other annotations such as &#64;WrapOperation.
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
     * @return The {@link ArgumentCaptureContext} instance that corresponds to the {@link MethodNode MethodNode's} signature
     * with whom the insertion of argument and local capture can be more easily pulled off without having dedicated
     * implementations for every annotation.
     */
    @NotNull
    public static ArgumentCaptureContext parseModifyHandler(@NotNull ClassNode owner, @NotNull MethodNode mixinSource, @NotNull String annotationName) {
        DescString dString = new DescString(mixinSource.desc);
        List<AnnotationNode>[] parameterAnnotations = mixinSource.invisibleParameterAnnotations;

        if (parameterAnnotations != null && ArgumentCaptureContext.captureLocals(parameterAnnotations[0])) {
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

        int offset = 0;
        List<CapturedArgument> arguments = new ArrayList<ArgumentCaptureContext.CapturedArgument>();
        while (dString.hasNext()) {
            if (parameterAnnotations != null && ArgumentCaptureContext.captureLocals(parameterAnnotations[offset + 1])) {
                // TODO implement local capture via @Local
                throw new MixinParseException("The @" + annotationName + "-annotated modifier method " + owner.name + "." + mixinSource.name + mixinSource.desc + " uses @Local to capture local variables, but this feature is not yet supported.");
            } else {
                String type = dString.nextType();
                arguments.add(new CapturedArgument(offset++, type));
                if (ASMUtil.isCategory2(type.codePointAt(0))) {
                    offset++;
                }
            }
        }

        return new ArgumentCaptureContext(Collections.unmodifiableList(arguments));
    }

    @NotNull
    private final List<CapturedArgument> capturedArguments;

    private ArgumentCaptureContext(@NotNull List<CapturedArgument> arguments) {
        this.capturedArguments = arguments;
    }

    public void appendCaptures(@NotNull ClassNode targetNode, @NotNull MethodNode targetMethod, @NotNull MixinMethodStub sourceStub, @NotNull AbstractInsnNode selectedInjectionPointInsn, @NotNull InsnList output) {
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

        int localOffset = (targetMethod.access & Opcodes.ACC_STATIC) == 0 ? 1 : 0;

        for (CapturedArgument captureArg : this.capturedArguments) {
            if (!captureArg.capturedType.equals(availableLocals.get(captureArg.captureOffset))) {
                throw new IllegalStateException("Unable to capture argument: Descriptor mismatch: Captured argument tries to capture an " + captureArg.capturedType + " at offset " + captureArg.captureOffset + ", but instead there is a " + availableLocals.get(captureArg.captureOffset) + " at this place. Failed mixin stub: " + sourceStub.getOwner().name + "." + sourceStub.getName() + sourceStub.getDesc() + " targets " + targetNode.name + "." + targetMethod.name + targetMethod.desc);
            }
            output.add(new VarInsnNode(ASMUtil.getLoadOpcode(captureArg.capturedType.codePointAt(0)), localOffset + captureArg.captureOffset));
        }
    }
}
