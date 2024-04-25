package org.stianloader.micromixin.transform.internal.annotation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import org.stianloader.micromixin.transform.MixinTransformer;
import org.stianloader.micromixin.transform.SimpleRemapper;
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
import org.stianloader.micromixin.transform.internal.util.PrintUtils;
import org.stianloader.micromixin.transform.internal.util.commenttable.CommentTable;
import org.stianloader.micromixin.transform.internal.util.commenttable.KeyValueTableSection;
import org.stianloader.micromixin.transform.internal.util.commenttable.StringTableSection;
import org.stianloader.micromixin.transform.internal.util.locals.LocalCaptureResult;
import org.stianloader.micromixin.transform.internal.util.locals.LocalsCapture;

public final class MixinInjectAnnotation extends MixinAnnotation<MixinMethodStub> {

    @NotNull
    private static final String CALLBACK_INFO_DESC = "L" + ASMUtil.CALLBACK_INFO_NAME + ";";

    private final int allow;
    @NotNull
    public final Collection<SlicedInjectionPointSelector> at;
    @NotNull
    public final Collection<MixinTargetSelector> selectors;
    @NotNull
    private final MethodNode injectSource;
    private final int require;
    private final int expect;
    private final boolean cancellable;
    private final boolean denyVoids;
    @NotNull
    private final String locals;
    @NotNull
    private final MixinTransformer<?> transformer;

    private MixinInjectAnnotation(@NotNull Collection<SlicedInjectionPointSelector> at, @NotNull Collection<MixinTargetSelector> selectors,
            @NotNull MethodNode injectSource, int require, int expect, int allow, boolean cancellable, boolean denyVoids, @NotNull String locals,
            @NotNull MixinTransformer<?> transformer) {
        this.at = at;
        this.selectors = selectors;
        this.injectSource = injectSource;
        this.require = require;
        this.expect = expect;
        this.allow = allow;
        this.cancellable = cancellable;
        this.denyVoids = denyVoids;
        this.locals = locals;
        this.transformer = transformer;
    }

    @NotNull
    public static MixinInjectAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) throws MixinParseException {
        if ((method.access & Opcodes.ACC_STATIC) != 0 && (method.access & Opcodes.ACC_PRIVATE) == 0) {
            throw new MixinParseException("The injector handler method " + node.name + "." + method.name + method.desc + " is static, but isn't private. Consider making the method private.");
        }
        List<MixinAtAnnotation> at = new ArrayList<MixinAtAnnotation>();
        List<MixinSliceAnnotation> slice = new ArrayList<MixinSliceAnnotation>();
        Collection<MixinDescAnnotation> target = null;
        String[] targetSelectors = null;
        int require = -1;
        int expect = -1;
        int allow = -1;
        boolean cancellable = false;
        boolean denyVoids = ASMUtil.CALLBACK_INFO_RETURNABLE_DESC.equals(ASMUtil.getLastType(method.desc));
        String locals = null;
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
                if (target != null) {
                    throw new MixinParseException("Duplicate \"target\" field in @Inject.");
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
                    throw new MixinParseException("Duplicate \"method\" field in @Inject.");
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
            } else if (name.equals("cancellable")) {
                cancellable = (Boolean) val;
            } else if (name.equals("locals")) {
                locals = ((String[]) val)[1];
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
                throw new MixinParseException("Unimplemented key in @Inject: " + name);
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
            throw new MixinParseException("No available selectors: Mixin " + node.name + "." + method.name + method.desc + " does not match anything and is not a valid mixin.");
        }

        if (locals == null) {
            locals = "NO_CAPTURE";
        }

        if (allow < require) {
            allow = -1;
        }

        Collection<SlicedInjectionPointSelector> slicedAts = Collections.unmodifiableCollection(MixinAtAnnotation.bake(at, slice));

        return new MixinInjectAnnotation(slicedAts, Collections.unmodifiableCollection(selectors), method, require, expect, allow, cancellable, denyVoids, locals, transformer);
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        MethodNode handlerNode = CodeCopyUtil.copyHandler(this.injectSource, sourceStub, to, hctx.handlerPrefix + hctx.handlerCounter++ + "$" + this.injectSource.name, remapper, hctx.lineAllocator);
        Map<AbstractInsnNode, MethodNode> matched = new HashMap<AbstractInsnNode, MethodNode>();
        for (MixinTargetSelector selector : this.selectors) {
            for (SlicedInjectionPointSelector at : this.at) {
                MethodNode targetMethod = selector.selectMethod(to, sourceStub);
                if (targetMethod != null) {
                    if (targetMethod.name.equals("<init>") && !at.supportsConstructors()) {
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + ".<init>" + targetMethod.desc + ", which is a constructor. However the selector @At(\"" + at.getSelector().fullyQualifiedName + "\") does not support usage within a constructor.");
                    }

                    if (this.denyVoids && targetMethod.desc.codePointBefore(targetMethod.desc.length()) == 'V') {
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + "V, which is a void method. The injector however has a CallbackInfoReturnable, which suggests a non-void type as the target's return type. This issue is caused due to the following selector (Make sure to set the return type accordingly!): " + selector);
                    }

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
                    for (AbstractInsnNode insn : at.getMatchedInstructions(targetMethod, remapper, sharedBuilder)) {
                        if (insn.getOpcode() == -1) {
                            throw new IllegalStateException("Selector " + at + " matched virtual instruction " + insn.getClass() + ". Declaring mixin " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + targetMethod.desc);
                        }
                        matched.put(insn, targetMethod);
                    }
                }
            }
        }

        if (matched.size() < this.require) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " requires " + this.require + " injection points but only found " + matched.size() + ".");
        }
        if (matched.size() < this.expect) {
            this.transformer.getLogger().warn(MixinInjectAnnotation.class, "Potentially outdated mixin: {}.{} {} expects {} injection points but only found {}.", sourceStub.sourceNode.name, this.injectSource.name, this.injectSource.desc, this.expect, matched.size());
        }
        if (this.allow > 0 && matched.size() > this.allow) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " allows up to " + this.allow + " injection points but " + matched.size() + " injection points were selected.");
        }

        // IMPLEMENT CallbackInfo-chaining. The main part could be done through annotations.
        for (Map.Entry<AbstractInsnNode, MethodNode> entry : matched.entrySet()) {
            AbstractInsnNode insn = entry.getKey();
            MethodNode method = entry.getValue();

            int returnType = method.desc.codePointAt(method.desc.lastIndexOf(')') + 1);
            boolean category2 = ASMUtil.isCategory2(returnType);
            InsnList injected = new InsnList();
            if (this.captureLocalsEarly(sourceStub.sourceNode, to, method, insn, sharedBuilder)) {
                continue;
            }
            if (returnType != 'V' && category2) {
                // This method could theoretically work with both cat 1 and cat 2 return types,
                // but uses the local variable table for temporary storage
                int returnOpcode = ASMUtil.getReturnOpcode(returnType);
                int lvt0 = scanNextFreeLVTIndex(method);
                int storedType;
                if (insn.getOpcode() != returnOpcode) {
                    injected.add(new InsnNode(Opcodes.ACONST_NULL));
                    storedType = 'L';
                } else {
                    injected.add(new InsnNode(Opcodes.DUP2));
                    storedType = returnType;
                }
                int storeOpcode = ASMUtil.getStoreOpcode(storedType);
                int loadOpcode = ASMUtil.getLoadOpcode(storedType);

                injected.add(new VarInsnNode(storeOpcode, lvt0));
                injected.add(new TypeInsnNode(Opcodes.NEW, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME));
                injected.add(new InsnNode(Opcodes.DUP));
                injected.add(new LdcInsnNode(method.name));
                injected.add(new InsnNode(this.cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
                injected.add(new VarInsnNode(loadOpcode, lvt0));
                String ctorDesc;
                if (storedType == 'L') {
                    ctorDesc = "(Ljava/lang/String;ZLjava/lang/Object;)V";
                } else {
                    ctorDesc = "(Ljava/lang/String;Z" + ((char) storedType) + ")V";
                }
                injected.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME, "<init>", ctorDesc));
                // Operand stack: CIR
                injected.add(new InsnNode(Opcodes.DUP));
                if ((method.access & Opcodes.ACC_STATIC) != 0) {
                    this.captureArguments(sourceStub, injected, to, method);
                    this.captureLocals(sourceStub.sourceNode, to, method, injected, insn, sharedBuilder);
                    injected.add(new MethodInsnNode(Opcodes.INVOKESTATIC, to.name, handlerNode.name, handlerNode.desc));
                } else {
                    injected.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    injected.add(new InsnNode(Opcodes.SWAP));
                    // Now RET, CIR, THIS, CIR
                    this.captureArguments(sourceStub, injected, to, method);
                    this.captureLocals(sourceStub.sourceNode, to, method, injected, insn, sharedBuilder);
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, to.name, handlerNode.name, handlerNode.desc));
                }
                injected.add(new InsnNode(ASMUtil.popReturn(handlerNode.desc))); // The official mixin implementation doesn't seem to pop here, but we'll do it anyways as that is more likely to be more stable
                // Operand stack: CIR
                if (cancellable) {
                    injected.add(new InsnNode(Opcodes.DUP));
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME, "isCancelled", "()Z"));
                    // Now CIR, BOOL
                    LabelNode skipReturn = new LabelNode();
                    injected.add(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
                    // Now CIR
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME, "getReturnValue" + ((char) returnType), "()" + ((char) returnType)));
                    // Now VAL
                    injected.add(new InsnNode(returnOpcode));
                    injected.add(skipReturn);
                }
                // Operand stack: CIR (Both paths)
                injected.add(new InsnNode(Opcodes.POP));
            } else if (returnType != 'V') {
                // Note: only applies for category 1 return types.
                // In turn, it wholly operates on the stack.
                int returnOpcode = ASMUtil.getReturnOpcode(returnType);
                int storedType;
                if (insn.getOpcode() != returnOpcode) {
                    injected.add(new InsnNode(Opcodes.ACONST_NULL));
                    storedType = 'L';
                } else {
                    injected.add(new InsnNode(Opcodes.DUP));
                    storedType = returnType;
                }
                // Now RET (or RET, RET - but the first RET is used later and thus discarded for our purposes)
                injected.add(new TypeInsnNode(Opcodes.NEW, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME));
                // Now RET, CIR
                injected.add(new InsnNode(Opcodes.DUP2));
                // Now RET, CIR, RET, CIR
                injected.add(new InsnNode(Opcodes.SWAP));
                // Now RET, CIR, CIR, RET
                injected.add(new LdcInsnNode(method.name));
                injected.add(new InsnNode(Opcodes.SWAP));
                // Now RET, CIR, CIR, NAME, RET
                injected.add(new InsnNode(this.cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
                injected.add(new InsnNode(Opcodes.SWAP));
                // Now RET, CIR, CIR, NAME, CANCELLABLE, RET
                String ctorDesc;
                if (storedType == 'L') {
                    ctorDesc = "(Ljava/lang/String;ZLjava/lang/Object;)V";
                } else {
                    ctorDesc = "(Ljava/lang/String;Z" + ((char) storedType) + ")V";
                }
                injected.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME, "<init>", ctorDesc));
                // Now RET, CIR
                injected.add(new InsnNode(Opcodes.DUP));
                // Now RET, CIR, CIR
                if ((method.access & Opcodes.ACC_STATIC) != 0) {
                    this.captureArguments(sourceStub, injected, to, method);
                    this.captureLocals(sourceStub.sourceNode, to, method, injected, insn, sharedBuilder);
                    injected.add(new MethodInsnNode(Opcodes.INVOKESTATIC, to.name, handlerNode.name, handlerNode.desc));
                } else {
                    injected.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    injected.add(new InsnNode(Opcodes.SWAP));
                    // Now RET, CIR, THIS, CIR
                    this.captureArguments(sourceStub, injected, to, method);
                    this.captureLocals(sourceStub.sourceNode, to, method, injected, insn, sharedBuilder);
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, to.name, handlerNode.name, handlerNode.desc));
                }
                injected.add(new InsnNode(ASMUtil.popReturn(handlerNode.desc))); // The official mixin implementation doesn't seem to pop here, but we'll do it anyways as that is more likely to be more stable
                // Now RET, CIR
                if (cancellable) {
                    injected.add(new InsnNode(Opcodes.DUP));
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME, "isCancelled", "()Z"));
                    // Now RET, CIR, BOOL
                    LabelNode skipReturn = new LabelNode();
                    injected.add(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
                    // Now RET, CIR
                    if (returnOpcode == Opcodes.ARETURN) {
                        injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME, "getReturnValue", "()Ljava/lang/Object;"));
                        injected.add(new TypeInsnNode(Opcodes.CHECKCAST, method.desc.substring(method.desc.lastIndexOf(')') + 2, method.desc.length() - 1)));
                    } else {
                        injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ASMUtil.CALLBACK_INFO_RETURNABLE_NAME, "getReturnValue" + ((char) returnType), "()" + ((char) returnType)));
                    }
                    // Now RET, VAL
                    injected.add(new InsnNode(returnOpcode));
                    injected.add(skipReturn);
                    // If it did jump (and thus didn't return) it is RET, CIR
                }
                // (Both paths have RET, CIR on the stack)
                injected.add(new InsnNode(Opcodes.POP2)); // Perhaps with less lazy engineering one could avoid having this pop, but at the moment it does just as well
                // Now nothing (or RET, but that RET is used later)
            } else if ((method.access & Opcodes.ACC_STATIC) != 0) {
                injected.add(new TypeInsnNode(Opcodes.NEW, ASMUtil.CALLBACK_INFO_NAME));
                injected.add(new InsnNode(Opcodes.DUP));
                if (cancellable) {
                    injected.add(new InsnNode(Opcodes.DUP));
                }
                injected.add(new LdcInsnNode(method.name));
                injected.add(new InsnNode(this.cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
                injected.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ASMUtil.CALLBACK_INFO_NAME, "<init>", "(Ljava/lang/String;Z)V"));
                this.captureArguments(sourceStub, injected, to, method);
                this.captureLocals(sourceStub.sourceNode, to, method, injected, insn, sharedBuilder);
                injected.add(new MethodInsnNode(Opcodes.INVOKESTATIC, to.name, handlerNode.name, handlerNode.desc));
                injected.add(new InsnNode(ASMUtil.popReturn(handlerNode.desc))); // The official mixin implementation doesn't seem to pop here, but we'll do it anyways as that is more likely to be more stable
                if (this.cancellable) {
                    // TODO What happens if two injectors have the same entrypoint? Is mixin smart or not so smart here?
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ASMUtil.CALLBACK_INFO_NAME, "isCancelled", "()Z"));
                    LabelNode skipReturn = new LabelNode();
                    injected.add(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
                    injected.add(new InsnNode(Opcodes.RETURN));
                    injected.add(skipReturn);
                }
            } else {
                int idx = getCallbackInfoIndex(method, cancellable);
                injected.add(new VarInsnNode(Opcodes.ALOAD, 0));
                injected.add(new VarInsnNode(Opcodes.ALOAD, idx));
                this.captureArguments(sourceStub, injected, to, method);
                this.captureLocals(sourceStub.sourceNode, to, method, injected, insn, sharedBuilder);
                injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, to.name, handlerNode.name, handlerNode.desc));
                injected.add(new InsnNode(ASMUtil.popReturn(handlerNode.desc))); // The official mixin implementation doesn't seem to pop here, but we'll do it anyways as that is more likely to be more stable
                if (this.cancellable) {
                    injected.add(new VarInsnNode(Opcodes.ALOAD, idx));
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ASMUtil.CALLBACK_INFO_NAME, "isCancelled", "()Z"));
                    LabelNode skipReturn = new LabelNode();
                    injected.add(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
                    injected.add(new InsnNode(Opcodes.RETURN));
                    injected.add(skipReturn);
                }
            }
            method.instructions.insertBefore(insn, injected);
        }
    }

    /**
     * Handles local capture.
     *
     * @param handlerOwner The owner class of the injector source.
     * @param targetClass The ASM {@link ClassNode} representation of the class that is targeted by the mixin
     * @param target The ASM {@link MethodNode} representation of the method that should be transformed by the inject.
     * @param out Instructions generated through the local capture that should be prefixed before the actual injection handling. Intended to load the local variables.
     * @param inspectionTarget The instruction which is targeted by the injection.
     * @param sharedBuilder A shared {@link StringBuilder} instance used to reduce duplicate allocations
     */
    private void captureLocals(@NotNull ClassNode handlerOwner, @NotNull ClassNode targetClass, @NotNull MethodNode target,
            @NotNull InsnList out, AbstractInsnNode inspectionTarget, @NotNull StringBuilder sharedBuilder) {
        if (this.locals.equals("NO_CAPTURE")) {
            // Nothing to do
            return;
        }
        LocalCaptureResult result = LocalsCapture.captureLocals(targetClass, target, Objects.requireNonNull(inspectionTarget), this.transformer.getPool());

        int initialFrameSize = ASMUtil.getInitialFrameSize(target);
        Frame<BasicValue> frame = result.frame;
        List<String> requestedLocals = new ArrayList<String>();

        String errorMessage = null;

        errorFailfast:
        if (frame == null) {
            errorMessage = "Handler " + handlerOwner.name + "." + this.injectSource.name + this.injectSource.desc + " targets an unreachable instruction in " + targetClass.name + "." + target.name + target.desc;
        } else {
            int maxLocals = frame.getLocals();
            DescString requesterDesc = new DescString(this.injectSource.desc);
            while (requesterDesc.hasNext()) {
                String desc = requesterDesc.nextType();
                if (ASMUtil.CALLBACK_INFO_DESC.equals(desc) || ASMUtil.CALLBACK_INFO_RETURNABLE_DESC.equals(desc)) {
                    break;
                }
            }
            while (requesterDesc.hasNext()) {
                requestedLocals.add(requesterDesc.nextType());
            }

            List<String> providedLocals = new ArrayList<String>();

            for (int i = initialFrameSize; i < maxLocals; i++) {
                BasicValue local = frame.getLocal(i);
                Type localType = local.getType();
                if (localType == null) {
                    StringWriter sw = new StringWriter();
                    AbstractInsnNode hackInstruction = new InsnNode(Opcodes.NOP);
                    hackInstruction.invisibleTypeAnnotations = new ArrayList<TypeAnnotationNode>();
                    hackInstruction.invisibleTypeAnnotations.add(new TypeAnnotationNode(TypeReference.RESOURCE_VARIABLE, null, "Lorg/stianloader/micromixin/internal/MixinInjectAnnotationTraceLabel;"));
                    try {
                        target.instructions.insertBefore(inspectionTarget, hackInstruction);
                        TraceMethodVisitor tmv = new TraceMethodVisitor(new Textifier());
                        target.accept(tmv);
                        tmv.p.print(new PrintWriter(sw));
                    } finally {
                        target.instructions.remove(hackInstruction);
                    }
                    throw new AssertionError("localType == null, for i = " + i + "; initialFrameSize = " + initialFrameSize + "; maxLocals = " + maxLocals + "; frame[i] = " + local + "; frame = " + frame + "; frame[i].class= " + local.getClass() + "; frame[i].size = " + local.getSize() + "; target = " + targetClass.name + "." + target.name + target.desc+ "; source = " + handlerOwner.name + "." + this.injectSource.name + this.injectSource.desc + "; ats = {" + this.at + "}; targetCode = \n" + sw.toString());
                }
                String desc = localType.getDescriptor();
                providedLocals.add(desc);
                if (ASMUtil.isCategory2(desc.codePointAt(0))) {
                    i++;
                }
            }

            int requestedCount = requestedLocals.size();
            if (requestedCount < providedLocals.size()) {
                errorMessage = "Handler " + handlerOwner.name + "." + this.injectSource.name + this.injectSource.desc + " whishes to capture more locals than " + targetClass.name + "." + target.name + target.desc + " can provide. Provided: " + providedLocals + ", Requested: " + requestedLocals + ".";
                break errorFailfast;
            }

            int localIndex = initialFrameSize;
            for (int i = 0; i < requestedCount; i++) {
                String desc = requestedLocals.get(i);
                if (!desc.equals(providedLocals.get(i))) {
                    // TODO Inheritance? No idea if the spongeian implementation is smart enough there (or whether it makes any sense to do so)
                    errorMessage = "Handler " + handlerOwner.name + "." + this.injectSource.name + this.injectSource.desc + " attempts to capture different locals than those supplied by " + targetClass.name + "." + target.name + target.desc + ". Provided: " + providedLocals + ", Requested: " + requestedLocals + ".";
                    break errorFailfast;
                }
                int type = desc.codePointAt(0);
                out.add(new VarInsnNode(ASMUtil.getLoadOpcode(type), localIndex));
                if (ASMUtil.isCategory2(type)) {
                    localIndex += 2;
                } else {
                    localIndex++;
                }
            }
        }

        if (this.locals.equals("CAPTURE_FAILHARD")) {
            if (errorMessage != null) {
                throw new Error(errorMessage);
            }
        } else {
            throw new IllegalStateException("Unsupported local capture flag: \"" + this.locals + "\"");
        }
    }

    /**
     * Handles local capture.
     * This method primarily deals with injectors that ordinarily skip the method to inject very early (such as it is the case
     * with LocalCapture#PRINT)
     *
     * @param handlerOwner The owner class of the injector source.
     * @param targetClass The ASM {@link ClassNode} representation of the class that is targeted by the mixin
     * @param target The ASM {@link MethodNode} representation of the method that should be transformed by the inject.
     * @param inspectionTarget The instruction which is targeted by the injection.
     * @param sharedBuilder A shared {@link StringBuilder} instance used to reduce duplicate allocations
     * @return True to abort injection (for example with PRINT), false otherwise.
     */
    private boolean captureLocalsEarly(@NotNull ClassNode handlerOwner, @NotNull ClassNode targetClass, @NotNull MethodNode target, AbstractInsnNode inspectionTarget, @NotNull StringBuilder sharedBuilder) {
        if (this.locals.equals("NO_CAPTURE") || this.locals.equals("CAPTURE_FAILHARD")) {
            // Nothing to do, for now
            return false;
        }
        LocalCaptureResult result = LocalsCapture.captureLocals(targetClass, target, Objects.requireNonNull(inspectionTarget), this.transformer.getPool());
        if (this.locals.equals("PRINT")) {
            KeyValueTableSection injectionPointInfo = new KeyValueTableSection();
            CommentTable printTable = new CommentTable().addSection(injectionPointInfo);

            injectionPointInfo.add("Target Class", targetClass.name.replace('/', '.'));
            sharedBuilder.setLength(0);
            PrintUtils.fastPrettyMethodName(target.name, target.desc, target.access, sharedBuilder);
            injectionPointInfo.add("Target Method", sharedBuilder.toString());

            Throwable error = result.error;
            Frame<BasicValue> frame = result.frame;
            String maxLocals;
            int initialFrameSize = ASMUtil.getInitialFrameSize(target);

            if (error != null) {
                List<String> lines = new ArrayList<String>();
                lines.add("A fatal error has occured while analyzing the target method: ");
                lines.add("");
                lines.add("");
                StringWriter writer = new StringWriter();
                error.printStackTrace(new PrintWriter(writer));
                for (String line : writer.getBuffer().toString().split("[\\n\\r]")) {
                    lines.add(line);
                }
                printTable.addSection(new StringTableSection(lines));
                maxLocals = "???";
            } else if (frame == null) {
                printTable.addSection(new StringTableSection(Collections.singletonList("Error: The desired injection point is not reachable. Local capture is not possible")));
                maxLocals = "???";
            } else {
                maxLocals = Integer.toString(frame.getLocals());
                printTable.addSection(result.asLocalPrintTable(sharedBuilder));
                printTable.addSection(new StringTableSection(PrintUtils.getExpectedCallbackSignature(this.injectSource, target, frame, sharedBuilder)));
            }

            injectionPointInfo.add("Target Max LOCALS", maxLocals);
            injectionPointInfo.add("Initial Frame Size", Integer.toString(initialFrameSize));
            injectionPointInfo.add("Callback Name", this.injectSource.name);
            injectionPointInfo.add("Instruction", "<not implemented>"); // IMPLEMENT Show instruction in Local capture
            System.err.println(printTable);
            return true;
        }
        throw new IllegalStateException("Unsupported local capture flag: \"" + this.locals + "\"");
    }

    private static int searchCallbackInfoIndex(@NotNull MethodNode method, boolean cancellable) {
        if (method.desc.codePointBefore(method.desc.length()) != 'V') {
            throw new AssertionError("Method called for a non-void method descriptor: " + method.desc + " (" + method.name + method.desc + "). Non-void methods should use CIR instead of CI, which shouldn't be cached.");
        }
        for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn.getOpcode() == -1) {
                continue;
            }
            if (insn.getOpcode() != Opcodes.NEW) {
                return -1;
            }
            TypeInsnNode typeInsn = (TypeInsnNode) insn;
            if (!typeInsn.desc.equals(ASMUtil.CALLBACK_INFO_NAME)
                    || (insn = insn.getNext()).getOpcode() != Opcodes.DUP
                    || (insn = insn.getNext()).getOpcode() != Opcodes.LDC
                    || ((LdcInsnNode) insn).cst.equals(method.name)) {
                return -1;
            }
            AbstractInsnNode cancellableFlag = insn.getNext();
            if (cancellableFlag.getOpcode() != Opcodes.ICONST_0 && cancellableFlag.getOpcode() != Opcodes.ICONST_1) {
                return -1;
            }
            if (cancellable) {
                if (cancellableFlag.getOpcode() == Opcodes.ICONST_0) {
                    // Skip the current CallbackInfo allocation as it is not cancellable
                    insn = cancellableFlag.getNext();
                    if (insn.getOpcode() != Opcodes.INVOKESPECIAL
                            || (insn = insn.getNext()).getOpcode() != Opcodes.ASTORE) {
                        return -1;
                    }
                    continue;
                } else if (cancellableFlag.getOpcode() != Opcodes.ICONST_1) {
                    return -1;
                }
            } else {
                if (cancellableFlag.getOpcode() == Opcodes.ICONST_1) {
                    // Skip the current CallbackInfo allocation as it is cancellable (but we'd not want it to be cancellable)
                    insn = cancellableFlag.getNext();
                    if (insn.getOpcode() != Opcodes.INVOKESPECIAL
                            || (insn = insn.getNext()).getOpcode() != Opcodes.ASTORE) {
                        return -1;
                    }
                    continue;
                } else if (cancellableFlag.getOpcode() != Opcodes.ICONST_0) {
                    return -1;
                }
            }
            insn = cancellableFlag.getNext();
            if (insn.getOpcode() != Opcodes.INVOKESPECIAL
                    || (insn = insn.getNext()).getOpcode() != Opcodes.ASTORE) {
                return -1;
            }
            return ((VarInsnNode) insn).var;
        }
        return -1;
    }

    private static int scanNextFreeLVTIndex(@NotNull MethodNode method) {
        int currentUsed = 0;
        if ((method.access & Opcodes.ACC_STATIC) == 0) {
            currentUsed++;
        }
        DescString dstring = new DescString(method.desc);
        while (dstring.hasNext()) {
            if (ASMUtil.isCategory2(dstring.nextReferenceType())) {
                currentUsed += 2;
            } else {
                currentUsed++;
            }
        }
        for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn.getType() == AbstractInsnNode.VAR_INSN) {
                int idx = ((VarInsnNode) insn).var;
                if (insn.getOpcode() == Opcodes.DSTORE || insn.getOpcode() == Opcodes.LSTORE) {
                    idx++; // Computation type 2 requires 2 slots in the local variable table
                }
                currentUsed = Math.max(currentUsed, idx);
            }
        }
        return ++currentUsed;
    }

    private static int getCallbackInfoIndex(@NotNull MethodNode method, boolean cancellable) {
        int preallocated = searchCallbackInfoIndex(method, cancellable);
        if (preallocated != -1) {
            return preallocated;
        }
        int index = scanNextFreeLVTIndex(method);
        InsnList injected = new InsnList();
        injected.add(new TypeInsnNode(Opcodes.NEW, ASMUtil.CALLBACK_INFO_NAME));
        injected.add(new InsnNode(Opcodes.DUP));
        injected.add(new LdcInsnNode(method.name));
        injected.add(new InsnNode(cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
        injected.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ASMUtil.CALLBACK_INFO_NAME, "<init>", "(Ljava/lang/String;Z)V"));
        injected.add(new VarInsnNode(Opcodes.ASTORE, index));
        method.instructions.insert(injected);
        return index;
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        // NOP
    }

    private void captureArguments(@NotNull MixinStub sourceStub, @NotNull InsnList output, @NotNull ClassNode targetClass, @NotNull MethodNode targetMethod) {
        DescString handlerDesc = new DescString(this.injectSource.desc);
        DescString targetDesc = new DescString(targetMethod.desc);

        int lvtIndex = 0;
        if ((this.injectSource.access & Opcodes.ACC_STATIC) == 0) {
            lvtIndex++;
        }

        while (handlerDesc.hasNext() && targetDesc.hasNext()) {
            String handlerType = handlerDesc.nextType();
            if (handlerType.equals(CALLBACK_INFO_DESC) || handlerType.equals(ASMUtil.CALLBACK_INFO_RETURNABLE_DESC)) {
                if (this.injectSource.desc.startsWith("(" + CALLBACK_INFO_DESC + ")")
                        || this.injectSource.desc.startsWith("(" + ASMUtil.CALLBACK_INFO_RETURNABLE_DESC + ")")) {
                    // Not capturing any argument is supported even in the case of an argument underflow.
                    return;
                }
                // Argument underflow (not supported by the spongeian implementation - even though underflows are supported for local capture):
                // the break will cause an exception to fire - that is intended.
                break;
            }
            String targetType = targetDesc.nextType();
            if (!handlerType.equals(targetType)) {
                // TODO Can it also be subtypes? Answer: @Coerce would do that
                throw new IllegalStateException("Handler method " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " injects into " + targetClass.name + "." + targetMethod.name + targetMethod.desc + ", however "
                        + "the target method defines different arguments than the source method is capturing. The first mismatched argument: \"" + handlerType + "\" defined by the handler where as \"" + targetType + "\" is defined by the target.");
            }

            int refType = targetType.codePointAt(0);
            output.add(new VarInsnNode(ASMUtil.getLoadOpcode(refType), lvtIndex++));
            if (ASMUtil.isCategory2(refType)) {
                output.add(new InsnNode(Opcodes.DUP2_X1));
                output.add(new InsnNode(Opcodes.POP2));
            } else {
                output.add(new InsnNode(Opcodes.SWAP));
            }
        }

        if (targetDesc.hasNext()) {
            // Argument underflows are not supported by the spongeian implementation
            throw new IllegalStateException("Handler method " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " injects into " + targetClass.name + "." + targetMethod.name + targetMethod.desc + ", however "
                    + "the target method has more arguments than the source method is capturing.");
        } else if (handlerDesc.hasNext()) {
            String handlerType = handlerDesc.nextType();
            if (handlerType.equals(CALLBACK_INFO_DESC) || handlerType.equals(ASMUtil.CALLBACK_INFO_RETURNABLE_DESC)) {
                return;
            }
            // handler wishes to capture more arguments than it should.
            // It is rather self-explanatory that argument overflows are not supported.
            throw new IllegalStateException("Handler method " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " injects into " + targetClass.name + "." + targetMethod.name + targetMethod.desc + ", however "
                    + "the target method has less arguments than the source method is wishing to capture. The first extraneous argument is of type: " + handlerType);
        } else {
            // Both lists exhausted without reaching a CI or a CIR parameter.
            throw new IllegalStateException("Handler method " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " must have a CallbackInfo or a CallbackInfoReturnable in it's arguments. But it does not.");
        }
    }
}
