package org.stianloader.micromixin.transform.internal.annotation.mixinsextras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.stianloader.micromixin.transform.MixinTransformer;
import org.stianloader.micromixin.transform.SimpleRemapper;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
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
import org.stianloader.micromixin.transform.internal.util.Objects;

public class MixinExtrasModifyReturnValueAnnotation extends MixinAnnotation<MixinMethodStub> {

    private final int allow;
    @NotNull
    public final Collection<SlicedInjectionPointSelector> at;
    @NotNull
    public final Collection<MixinTargetSelector> selectors;
    @NotNull
    private final MethodNode injectSource;
    private final int require;
    private final int expect;
    @NotNull
    private final MixinLoggingFacade logger;

    private MixinExtrasModifyReturnValueAnnotation(@NotNull Collection<SlicedInjectionPointSelector> at, @NotNull Collection<MixinTargetSelector> selectors,
            @NotNull MethodNode injectSource, int require, int expect, int allow, @NotNull MixinLoggingFacade logger) {
        this.at = at;
        this.selectors = selectors;
        this.injectSource = injectSource;
        this.require = require;
        this.expect = expect;
        this.allow = allow;
        this.logger = logger;
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub,
            @NotNull MixinMethodStub source, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        MethodNode handlerNode = CodeCopyUtil.copyHandler(this.injectSource, sourceStub, to, hctx.handlerPrefix + hctx.handlerCounter++ + "$" + this.injectSource.name, remapper, hctx.lineAllocator);
        Map<AbstractInsnNode, MethodNode> matched = ASMUtil.enumerateTargets(this.selectors, this.at, to, sourceStub, this.injectSource, this.require, this.expect, this.allow, remapper, sharedBuilder, this.logger);
        String returnType = ASMUtil.getReturnType(this.injectSource.desc);

        for (Map.Entry<AbstractInsnNode, MethodNode> entry : matched.entrySet()) {
            AbstractInsnNode insn = entry.getKey();
            MethodNode method = entry.getValue();
            if (!ASMUtil.getReturnType(method.desc).equals(returnType)) {
                throw new IllegalStateException("The return value modifier method " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " is annotated with @ModifyReturnValue but does not consume the return type of the method it targets (" + to.name + "." + method.name + method.desc + ")");
            }
            if (!ASMUtil.isReturn(insn.getOpcode())) {
                throw new IllegalStateException("The return value modifier method " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets an instruction that isn't in the xRETURN family of instructions. The targeted instruction is in " + to.name + "." + method.name + method.desc);
            }
            InsnList inject = new InsnList();
            int opcode;
            if ((handlerNode.access & Opcodes.ACC_STATIC) == 0) {
                opcode = Opcodes.INVOKEVIRTUAL;
                inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                // To be honest I wouldn't have caught this bug if it wasn't for the fact that I was a bit overkill with my tests
                if (ASMUtil.isCategory2(returnType.codePointAt(0))) {
                    inject.add(new InsnNode(Opcodes.DUP_X2));
                    inject.add(new InsnNode(Opcodes.POP));
                } else {
                    inject.add(new InsnNode(Opcodes.SWAP));
                }
            } else {
                opcode = Opcodes.INVOKESTATIC;
            }
            inject.add(new MethodInsnNode(opcode, to.name, handlerNode.name, handlerNode.desc));
            method.instructions.insertBefore(insn, inject);
        }
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        // NOP
    }

    @NotNull
    public static MixinExtrasModifyReturnValueAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) throws MixinParseException {
        if ((method.access & Opcodes.ACC_STATIC) != 0 && (method.access & Opcodes.ACC_PRIVATE) == 0) {
            throw new MixinParseException("The return value modifier method " + node.name + "." + method.name + method.desc + " is static, but isn't private. Consider making the method private.");
        }

        String returnType;

        {
            DescString descString = new DescString(method.desc);

            if (!descString.hasNext()) {
                throw new MixinParseException("The return value modifier method " + node.name + "." + method.name + method.desc + " is annotated with @ModifyReturnValue but it does not consume the original return value. Return value modifiers may not be no-args methods!");
            }

            returnType = descString.nextType();

            if (descString.hasNext()) {
                throw new MixinParseException("The return value modifier method " + node.name + "." + method.name + method.desc + " is annotated with @ModifyReturnValue but it has more than a single argument! Note that return value modifiers are ineligble for argument and local capture.");
            }

            if (!ASMUtil.getReturnType(method.desc).equals(returnType)) {
                throw new MixinParseException("The return value modifier method " + node.name + "." + method.name + method.desc + " is annotated with @ModifyReturnValue but has an invalid descriptor! Return value modifiers must return the same type as they consume - irrespective of class hierarchy.");
            }
        }

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
                transformer.getLogger().warn(MixinExtrasModifyReturnValueAnnotation.class, "Potentially outdated mixin: {}.{} {} has an @ModifyReturnValue annotation with the 'target = ...' attribute. However, this attribute is not yet officially implemented in either MixinExtras or micromxin-annotations. You are likely running an outdated version of micromixin-transformer!", node.name, method.name, method.desc);
                if (target != null) {
                    throw new MixinParseException("Duplicate \"target\" field in @ModifyReturnValue.");
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
                    throw new MixinParseException("Duplicate \"method\" field in @ModifyReturnValue.");
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
                throw new MixinParseException("Unimplemented key in @ModifyReturnValue: " + name);
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
            throw new MixinParseException("No available selectors: Mixin " + node.name + "." + method.name + method.desc + " does not match anything and is not a valid mixin.");
        }

        if (allow < require) {
            allow = -1;
        }

        Collection<SlicedInjectionPointSelector> slicedAts = Collections.unmodifiableCollection(MixinAtAnnotation.bake(at, slice));

        return new MixinExtrasModifyReturnValueAnnotation(slicedAts, Collections.unmodifiableCollection(selectors), method, require, expect, allow, transformer.getLogger());
    }
}
