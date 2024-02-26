package org.stianloader.micromixin.transform.internal.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
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
import org.stianloader.micromixin.transform.internal.selectors.DescSelector;
import org.stianloader.micromixin.transform.internal.selectors.MixinTargetSelector;
import org.stianloader.micromixin.transform.internal.selectors.StringSelector;
import org.stianloader.micromixin.transform.internal.selectors.constant.WildcardConstantSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.ConstantInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;
import org.stianloader.micromixin.transform.internal.util.CodeCopyUtil;
import org.stianloader.micromixin.transform.internal.util.DescString;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class MixinModifyConstantAnnotation extends MixinAnnotation<MixinMethodStub> {
    @NotNull
    public final Collection<SlicedInjectionPointSelector> slicedAts;
    @NotNull
    public final Collection<MixinTargetSelector> selectors;
    @NotNull
    private final MethodNode injectSource;
    private final int require;
    private final int expect;
    @NotNull
    private final MixinLoggingFacade logger;

    private MixinModifyConstantAnnotation(@NotNull Collection<SlicedInjectionPointSelector> slicedAts, @NotNull Collection<MixinTargetSelector> selectors,
            @NotNull MethodNode injectSource, int require, int expect, @NotNull MixinLoggingFacade logger) {
        this.slicedAts = slicedAts;
        this.selectors = selectors;
        this.injectSource = injectSource;
        this.require = require;
        this.expect = expect;
        this.logger = logger;
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub,
            @NotNull MixinMethodStub source, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        MethodNode handlerNode = CodeCopyUtil.copyHandler(this.injectSource, sourceStub, to, hctx.handlerPrefix + hctx.handlerCounter++ + "$" + this.injectSource.name, remapper, hctx.lineAllocator);
        Map<LabelNode, MethodNode> labels = new HashMap<LabelNode, MethodNode>();
        for (MixinTargetSelector selector : this.selectors) {
            for (SlicedInjectionPointSelector at : this.slicedAts) {
                MethodNode targetMethod = selector.selectMethod(to, sourceStub);
                if (targetMethod != null) {
                    if ((targetMethod.access & Opcodes.ACC_STATIC) != 0) {
                        if (((this.injectSource.access & Opcodes.ACC_STATIC) == 0)) {
                            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + targetMethod.desc + " target is static, but the mixin is not.");
                        } else if (((this.injectSource.access & Opcodes.ACC_PUBLIC) != 0)) {
                            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + targetMethod.desc + " target is static, but the mixin is public. A mixin may not be static and public at the same time for whatever odd reasons.");
                        }
                    } else if ((this.injectSource.access & Opcodes.ACC_STATIC) != 0) {
                        // Technically that one could be doable, but it'd be nasty.
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + targetMethod.desc + " target is not static, but the callback handler is.");
                    }
                    for (LabelNode label : at.getLabels(targetMethod, remapper, sharedBuilder)) {
                        labels.put(label, targetMethod);
                    }
                }
            }
        }

        if (labels.size() < this.require) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " requires " + this.require + " injection points but only found " + labels.size() + ".");
        } else if (labels.size() < this.expect) {
            this.logger.warn(MixinModifyConstantAnnotation.class, "Potentially outdated mixin: {}.{} {} expects {} injection points but only found {}.", sourceStub.sourceNode.name, this.injectSource.name, this.injectSource.desc, this.expect, labels.size());
        }

        String argumentType = ASMUtil.getReturnType(this.injectSource.desc);

        for (Map.Entry<LabelNode, MethodNode> entry : labels.entrySet()) {
            LabelNode label = entry.getKey();
            MethodNode method = entry.getValue();
            AbstractInsnNode insn = ASMUtil.getNext(label);
            // TODO Perform constant type sanity handling (for example when capturing a BIPUSH, one may not have a descriptor of (J)J)
            InsnList inject = new InsnList();
            int handlerInvokeOpcode;

            if ((handlerNode.access & Opcodes.ACC_STATIC) == 0) {
                handlerInvokeOpcode = Opcodes.INVOKEVIRTUAL;
                inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                if (ASMUtil.isCategory2(argumentType.codePointAt(0))) {
                    inject.add(new InsnNode(Opcodes.DUP_X2));
                    inject.add(new InsnNode(Opcodes.POP));
                } else {
                    inject.add(new InsnNode(Opcodes.SWAP));
                }
            } else {
                handlerInvokeOpcode = Opcodes.INVOKESTATIC;
            }

            inject.add(new MethodInsnNode(handlerInvokeOpcode, to.name, handlerNode.name, handlerNode.desc));
            method.instructions.insert(insn, inject);
        }
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        // NOP
    }

    @NotNull
    public static MixinModifyConstantAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) throws MixinParseException {
        if ((method.access & Opcodes.ACC_STATIC) != 0 && (method.access & Opcodes.ACC_PRIVATE) == 0) {
            throw new MixinParseException("The constant value modifier method " + node.name + "." + method.name + method.desc + " is static, but isn't private. Consider making the method private.");
        }

        String argType;

        {
            DescString descString = new DescString(method.desc);

            if (!descString.hasNext()) {
                throw new MixinParseException("The constant value modifier method " + node.name + "." + method.name + method.desc + " is annotated with @ModifyConstant but it does not consume the original constant value. Constant modifiers may not be no-args methods!");
            }

            argType = descString.nextType();

            if (descString.hasNext()) {
                throw new MixinParseException("The constant value modifier method " + node.name + "." + method.name + method.desc + " is annotated with @ModifyConstant but it has more than a single argument! Note that constant modifiers are ineligble for argument and local capture.");
            }

            if (!ASMUtil.getReturnType(method.desc).equals(argType)) {
                throw new MixinParseException("The constant value modifier method " + node.name + "." + method.name + method.desc + " is annotated with @ModifyConstant but has an invalid descriptor! Constant modifiers must return the same type as they consume - irrespective of class hierarchy.");
            }
        }

        List<MixinAtAnnotation> constantAts = new ArrayList<MixinAtAnnotation>();
        List<MixinSliceAnnotation> slice = new ArrayList<MixinSliceAnnotation>();
        Collection<MixinDescAnnotation> target = null;
        String[] targetSelectors = null;
        int require = -1;
        int expect = -1;

        for (int i = 0; i < annot.values.size(); i += 2) {
            String name = (String) annot.values.get(i);
            Object val = annot.values.get(i + 1);
            if (name.equals("constant")) {
                @SuppressWarnings("unchecked")
                List<AnnotationNode> constantValues = ((List<AnnotationNode>) val);
                for (AnnotationNode constantValue : constantValues) {
                    if (constantValue == null) {
                        throw new NullPointerException();
                    }
                    try {
                        constantAts.add(MixinAtAnnotation.parseConstantAnnotation(node, constantValue));
                    } catch (MixinParseException mpe) {
                        throw new MixinParseException("Unable to parse @Constant annotation defined by " + node.name + "." + method.name + method.desc, mpe);
                    }
                }
            } else if (name.equals("target")) {
                if (target != null) {
                    throw new MixinParseException("Duplicate \"target\" field in @ModifyConstant.");
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
                    throw new MixinParseException("Duplicate \"method\" field in @ModifyConstant.");
                }
                @SuppressWarnings("all")
                @NotNull String[] hack = (String[]) ((List) val).toArray(new String[0]);
                targetSelectors = hack;
            } else if (name.equals("require")) {
                require = ((Integer) val).intValue();
            } else if (name.equals("expect")) {
                expect = ((Integer) val).intValue();
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
                throw new MixinParseException("Unimplemented key in @ModifyConstant: " + name);
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

        if (constantAts.isEmpty()) {
            constantAts.add(new MixinAtAnnotation("CONSTANT", new ConstantInjectionPointSelector(new WildcardConstantSelector(argType)), ""));
        }

        Collection<SlicedInjectionPointSelector> slicedAts = Collections.unmodifiableCollection(MixinAtAnnotation.bake(constantAts, slice));

        return new MixinModifyConstantAnnotation(slicedAts, Collections.unmodifiableCollection(selectors), method, require, expect, transformer.getLogger());
    }
}
