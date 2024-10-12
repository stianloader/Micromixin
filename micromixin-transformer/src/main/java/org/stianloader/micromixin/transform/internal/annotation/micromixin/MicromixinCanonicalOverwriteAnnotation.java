package org.stianloader.micromixin.transform.internal.annotation.micromixin;

import java.util.List;

import org.jetbrains.annotations.ApiStatus.AvailableSince;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.annotation.MixinAnnotation;
import org.stianloader.micromixin.transform.internal.annotation.MixinDescAnnotation;
import org.stianloader.micromixin.transform.internal.selectors.DescSelector;
import org.stianloader.micromixin.transform.internal.selectors.MixinTargetSelector;
import org.stianloader.micromixin.transform.internal.selectors.StringSelector;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;
import org.stianloader.micromixin.transform.internal.util.CodeCopyUtil;
import org.stianloader.micromixin.transform.internal.util.DescString;

@AvailableSince("0.6.4-a20240825")
public class MicromixinCanonicalOverwriteAnnotation extends MixinAnnotation<MixinMethodStub> {

    @NotNull
    public static MicromixinCanonicalOverwriteAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method,
            @NotNull AnnotationNode annot, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) throws MixinParseException{

        if ((method.access & Opcodes.ACC_STATIC) != 0) {
            throw new MixinParseException("The @CanonicalOverwrite handler " + node.name + "." + method.name + method.desc + " is static. This is not allowed as per the documentation of @CanonicalOverwrite.");
        }

        MixinTargetSelector selector = null;

        if (annot.values != null) {
            int i = annot.values.size();
            while (i-- != 0) {
                Object value = annot.values.get(i--);
                String key = (String) annot.values.get(i);

                if (key.equals("method")) {
                    if (selector != null) {
                        throw new MixinParseException("Duplicate/Superfluous element '" + key + "' in @CanonicalOverwrite handler " + node.name + "." + method.name + method.desc + ". You may only specify 'target' or 'method', but not both (and only once).");
                    }
                    if (!(value instanceof String)) {
                        throw new MixinParseException("Invalid element '" + key + "' in @CanonicalOverwrite handler " + node.name + "." + method.name + method.desc + ". Expected a String, instead got '" + value + "'.");
                    }
                    selector = new StringSelector((String) value);
                } else if (key.equals("target")) {
                    if (selector != null) {
                        throw new MixinParseException("Duplicate/Superfluous element '" + key + "' in @CanonicalOverwrite handler " + node.name + "." + method.name + method.desc + ". You may only specify 'target' or 'method', but not both (and only once).");
                    }
                    if (!(value instanceof AnnotationNode)) {
                        throw new MixinParseException("Invalid element '" + key + "' in @CanonicalOverwrite handler " + node.name + "." + method.name + method.desc + ". Expected an AnnotationNode, instead got '" + value + "'.");
                    }
                    selector = new DescSelector(MixinDescAnnotation.parse(node, (AnnotationNode) value));
                } else {
                    throw new MixinParseException("Unknown element value key in @CanonicalOverwrite handler " + node.name + "." + method.name + method.desc + ": " + key + "; It is likely that the used version of micromixin-transformer is out-of-date. Consider updating or not using the infringing feature..");
                }
            }
        }

        if (selector == null) {
            // Create implicit selector
            selector = new StringSelector(null, method.name, method.desc);
        }

        return new MicromixinCanonicalOverwriteAnnotation(method, selector, transformer);
    }

    @NotNull
    private final MethodNode injectSource;
    @NotNull
    private final MixinLoggingFacade logger;
    @NotNull
    private final MixinTargetSelector selector;

    private MicromixinCanonicalOverwriteAnnotation(@NotNull MethodNode injectSource,
            @NotNull MixinTargetSelector targetSelector, @NotNull MixinTransformer<?> transformer) {
        this.injectSource = injectSource;
        this.selector = targetSelector;
        this.logger = transformer.getLogger();
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub,
            @NotNull MixinMethodStub source, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        MethodNode targetMethod = this.selector.selectMethod(to, sourceStub);
        if (targetMethod == null) {
            throw new IllegalStateException("The @CanonicalOverwrite handler " + sourceStub.sourceNode.name + "." + source.getName() + source.getDesc() + " does not match any target method that it could overwrite.");
        } else if ((targetMethod.access & Opcodes.ACC_STATIC) != 0) {
            throw new IllegalStateException("The @CanonicalOverwrite handler " + sourceStub.sourceNode.name + "." + source.getName() + source.getDesc() + " targets method " + to.name + "." + targetMethod.name + targetMethod.desc + ", a method which is static. This is not supported as per the documentation of @CanonicalOverwrite.");
        } else if (!targetMethod.desc.equals(this.injectSource.desc)) {
            throw new IllegalStateException("The @CanonicalOverwrite handler " + sourceStub.sourceNode.name + "." + source.getName() + source.getDesc() + " targets method " + to.name + "." + targetMethod.name + targetMethod.desc + ". However, the two methods have different descriptors, resulting in the overwrite to fail to apply. Please validate the target and adjust the descriptor of the mentioned handler method accordingly.");
        }

        targetMethod.instructions.clear();

        {
            // We're overwriting the contents of the method - so we also need to remove stale references to
            // labels that no longer exist.
            List<LocalVariableNode> locals = targetMethod.localVariables;
            if (locals != null) {
                locals.clear();
            }
            List<TryCatchBlockNode> exceptionHandlers = targetMethod.tryCatchBlocks;
            if (exceptionHandlers != null) {
                exceptionHandlers.clear();
            }
        }

        MethodNode handlerMethod;
        if (targetMethod.name.equals(this.injectSource.name) && targetMethod.desc.equals(this.injectSource.desc)) {
            handlerMethod = targetMethod;
        } else {
            handlerMethod = new MethodNode(this.injectSource.access, this.injectSource.name, this.injectSource.desc, this.injectSource.signature, this.injectSource.exceptions.toArray(new String[0]));
            to.methods.add(handlerMethod);
            targetMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            int localIdx = 1;
            DescString dString = new DescString(targetMethod.desc);
            while (dString.hasNext()) {
                int type = dString.nextReferenceType();
                targetMethod.instructions.add(new VarInsnNode(ASMUtil.getLoadOpcode(type), localIdx));
                if (ASMUtil.isCategory2(type)) {
                    localIdx += 2;
                } else {
                    localIdx++;
                }
            }
            targetMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, to.name, this.injectSource.name, this.injectSource.desc));
            targetMethod.instructions.add(new InsnNode(ASMUtil.getReturnOpcode(source.getDesc())));
        }

        CodeCopyUtil.copyOverwrite(sourceStub, this.injectSource, to, handlerMethod, remapper, hctx.lineAllocator, this.logger, false);
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        // NOP
    }
}
