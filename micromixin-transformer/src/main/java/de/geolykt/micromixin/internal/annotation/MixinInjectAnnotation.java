package de.geolykt.micromixin.internal.annotation;

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
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import de.geolykt.micromixin.internal.HandlerContextHelper;
import de.geolykt.micromixin.internal.MixinMethodStub;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.MixinStub;
import de.geolykt.micromixin.internal.selectors.DescSelector;
import de.geolykt.micromixin.internal.selectors.MixinTargetSelector;
import de.geolykt.micromixin.internal.selectors.StringSelector;
import de.geolykt.micromixin.internal.util.ASMUtil;
import de.geolykt.micromixin.internal.util.CodeCopyUtil;
import de.geolykt.micromixin.internal.util.DescString;
import de.geolykt.micromixin.internal.util.Objects;
import de.geolykt.micromixin.internal.util.PrintUtils;
import de.geolykt.micromixin.internal.util.Remapper;
import de.geolykt.micromixin.internal.util.commenttable.CommentTable;
import de.geolykt.micromixin.internal.util.commenttable.KeyValueTableSection;
import de.geolykt.micromixin.internal.util.commenttable.StringTableSection;
import de.geolykt.micromixin.internal.util.locals.LocalCaptureResult;
import de.geolykt.micromixin.internal.util.locals.LocalsCapture;
import de.geolykt.micromixin.supertypes.ClassWrapperPool;

public final class MixinInjectAnnotation extends MixinAnnotation<MixinMethodStub> {

    @NotNull
    private static final String CALLBACK_INFO_TYPE = "org/spongepowered/asm/mixin/injection/callback/CallbackInfo";
    @NotNull
    private static final String CALLBACK_INFO_DESC = "L" + CALLBACK_INFO_TYPE + ";";
    @NotNull
    private static final String CALLBACK_INFO_RETURNABLE_TYPE = "org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable";
    @NotNull
    private static final String CALLBACK_INFO_RETURNABLE_DESC = "L" + CALLBACK_INFO_RETURNABLE_TYPE + ";";

    @NotNull
    public final Collection<MixinAtAnnotation> at;
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
    private final ClassWrapperPool pool;

    private MixinInjectAnnotation(@NotNull Collection<MixinAtAnnotation> at, @NotNull Collection<MixinTargetSelector> selectors,
            @NotNull MethodNode injectSource, int require, int expect, boolean cancellable, boolean denyVoids, @NotNull String locals,
            @NotNull ClassWrapperPool pool) {
        this.at = at;
        this.selectors = selectors;
        this.injectSource = injectSource;
        this.require = require;
        this.expect = expect;
        this.cancellable = cancellable;
        this.denyVoids = denyVoids;
        this.locals = locals;
        this.pool = pool;
    }

    @NotNull
    public static MixinInjectAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot, @NotNull ClassWrapperPool pool) throws MixinParseException {
        if ((method.access & Opcodes.ACC_STATIC) != 0 && (method.access & Opcodes.ACC_PRIVATE) == 0) {
            throw new MixinParseException("The injector handler method " + node.name + "." + method.name + method.desc + " is static, but isn't private. Consider making the method private.");
        }
        List<MixinAtAnnotation> at = new ArrayList<MixinAtAnnotation>();
        Collection<MixinDescAnnotation> target = null;
        String[] targetSelectors = null;
        String fallbackMethodDesc = ASMUtil.getTargetDesc(method);
        int require = -1;
        int expect = -1;
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
                        at.add(MixinAtAnnotation.parse(atValue));
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
                    MixinDescAnnotation parsed = MixinDescAnnotation.parse(node, fallbackMethodDesc, atValue);
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
            } else if (name.equals("cancellable")) {
                cancellable = (Boolean) val;
            } else if (name.equals("cancellable")) {
                cancellable = (Boolean) val;
            } else if (name.equals("locals")) {
                locals = ((String[]) val)[1];
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
        return new MixinInjectAnnotation(Collections.unmodifiableCollection(at), Collections.unmodifiableCollection(selectors), method, require, expect, cancellable, denyVoids, locals, pool);
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        MethodNode handlerNode = CodeCopyUtil.copyHandler(this.injectSource, sourceStub, to, hctx.handlerPrefix + hctx.handlerCounter++ + "$" + this.injectSource.name, remapper);
        Map<LabelNode, MethodNode> labels = new HashMap<LabelNode, MethodNode>();
        for (MixinTargetSelector selector : selectors) {
            for (MixinAtAnnotation at : this.at) {
                MethodNode targetMethod = selector.selectMethod(to, sourceStub);
                if (targetMethod != null) {
                    if (targetMethod.name.equals("<init>") && !at.supportsConstructors()) {
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + ".<init>" + targetMethod.desc + ", which is a constructor. However the selector @At(\"" + at.value + "\") does not support usage within a constructor.");
                    }
                    if (this.denyVoids && targetMethod.desc.codePointBefore(targetMethod.desc.length()) == 'V') {
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + "V, which is a void method. The injector however has a CallbackInfoReturnable, which suggests a non-void type as the target's return type. This issue is caused due to the following selector (Make sure to set the return type accordingly!): " + selector);
                    }
                    // Verify access modifiers (TODO How about static -> non-static?)
                    if ((targetMethod.access & Opcodes.ACC_STATIC) != 0) {
                        if (((this.injectSource.access & Opcodes.ACC_STATIC) == 0)) {
                            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + targetMethod.desc + " target is static, but the mixin is not.");
                        } else if (((this.injectSource.access & Opcodes.ACC_PUBLIC) != 0)) {
                            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + targetMethod.desc + " target is static, but the mixin is public. A mixin may not be static and public at the same time for whatever odd reasons.");
                        }
                    }
                    for (LabelNode label : at.getLabels(targetMethod)) {
                        labels.put(label, targetMethod);
                    }
                }
            }
        }
        if (labels.size() < this.require) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " requires " + this.require + " injection points but only found " + labels.size() + ".");
        }
        if (labels.size() < this.expect) {
            // IMPLEMENT proper logging mechanism
            System.err.println("[WARNING:MM/MIA] Potentially outdated mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " expects " + this.expect + " injection points but only found " + labels.size() + ".");
        }
        // IMPLEMENT CallbackInfo-chaining. The main part could be done through annotations.
        for (Map.Entry<LabelNode, MethodNode> entry : labels.entrySet()) {
            LabelNode label = entry.getKey();
            MethodNode method = entry.getValue();
            int returnType = method.desc.codePointAt(method.desc.lastIndexOf(')') + 1);
            boolean category2 = ASMUtil.isCategory2(returnType);
            InsnList injected = new InsnList();
            if (handleCapture(sourceStub.sourceNode, to, method, injected, label, sharedBuilder)) {
                continue;
            }
            if (returnType != 'V' && category2) {
                // This method could theoretically work with both cat 1 and cat 2 return types,
                // but uses the local variable table for temporary storage
                int returnOpcode = ASMUtil.getReturnOpcode(returnType);
                int lvt0 = scanNextFreeLVTIndex(method);
                AbstractInsnNode nextInsn = label.getNext();
                while (nextInsn.getOpcode() == -1) { // If this line NPEs, the label is misplaced. This may be caused by invalid shifts. (Are shifts that go past the last RETURN valid? - Can you even shift to after a RETURN at all?)
                    nextInsn = nextInsn.getNext();
                }
                int storedType;
                if (nextInsn.getOpcode() != returnOpcode) {
                    injected.add(new InsnNode(Opcodes.ACONST_NULL));
                    storedType = 'L';
                } else {
                    injected.add(new InsnNode(Opcodes.DUP2));
                    storedType = returnType;
                }
                int storeOpcode = ASMUtil.getStoreOpcode(storedType);
                int loadOpcode = ASMUtil.getLoadOpcode(storedType);

                injected.add(new VarInsnNode(storeOpcode, lvt0));
                injected.add(new TypeInsnNode(Opcodes.NEW, CALLBACK_INFO_RETURNABLE_TYPE));
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
                injected.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, CALLBACK_INFO_RETURNABLE_TYPE, "<init>", ctorDesc));
                // Operand stack: CIR
                injected.add(new InsnNode(Opcodes.DUP));
                if ((method.access & Opcodes.ACC_STATIC) != 0) {
                    this.captureArguments(sourceStub, injected, to, method);
                    injected.add(new MethodInsnNode(Opcodes.INVOKESTATIC, to.name, handlerNode.name, handlerNode.desc));
                } else {
                    injected.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    injected.add(new InsnNode(Opcodes.SWAP));
                    // Now RET, CIR, THIS, CIR
                    this.captureArguments(sourceStub, injected, to, method);
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, to.name, handlerNode.name, handlerNode.desc));
                }
                injected.add(new InsnNode(ASMUtil.popReturn(handlerNode.desc))); // The official mixin implementation doesn't seem to pop here, but we'll do it anyways as that is more likely to be more stable
                // Operand stack: CIR
                if (cancellable) {
                    injected.add(new InsnNode(Opcodes.DUP));
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CALLBACK_INFO_RETURNABLE_TYPE, "isCancelled", "()Z"));
                    // Now CIR, BOOL
                    LabelNode skipReturn = new LabelNode();
                    injected.add(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
                    // Now CIR
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CALLBACK_INFO_RETURNABLE_TYPE, "getReturnValue" + ((char) returnType), "()" + ((char) returnType)));
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
                AbstractInsnNode nextInsn = label.getNext();
                while (nextInsn.getOpcode() == -1) { // If this line NPEs, the label is misplaced. This may be caused by invalid shifts. (Are shifts that go past the last RETURN valid? - Can you even shift to after a RETURN at all?)
                    nextInsn = nextInsn.getNext();
                }
                int storedType;
                if (nextInsn.getOpcode() != returnOpcode) {
                    injected.add(new InsnNode(Opcodes.ACONST_NULL));
                    storedType = 'L';
                } else {
                    injected.add(new InsnNode(Opcodes.DUP));
                    storedType = returnType;
                }
                // Now RET (or RET, RET - but the first RET is used later and thus discarded for our purposes)
                injected.add(new TypeInsnNode(Opcodes.NEW, CALLBACK_INFO_RETURNABLE_TYPE));
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
                injected.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, CALLBACK_INFO_RETURNABLE_TYPE, "<init>", ctorDesc));
                // Now RET, CIR
                injected.add(new InsnNode(Opcodes.DUP));
                // Now RET, CIR, CIR
                if ((method.access & Opcodes.ACC_STATIC) != 0) {
                    this.captureArguments(sourceStub, injected, to, method);
                    injected.add(new MethodInsnNode(Opcodes.INVOKESTATIC, to.name, handlerNode.name, handlerNode.desc));
                } else {
                    injected.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    injected.add(new InsnNode(Opcodes.SWAP));
                    // Now RET, CIR, THIS, CIR
                    this.captureArguments(sourceStub, injected, to, method);
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, to.name, handlerNode.name, handlerNode.desc));
                }
                injected.add(new InsnNode(ASMUtil.popReturn(handlerNode.desc))); // The official mixin implementation doesn't seem to pop here, but we'll do it anyways as that is more likely to be more stable
                // Now RET, CIR
                if (cancellable) {
                    injected.add(new InsnNode(Opcodes.DUP));
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CALLBACK_INFO_RETURNABLE_TYPE, "isCancelled", "()Z"));
                    // Now RET, CIR, BOOL
                    LabelNode skipReturn = new LabelNode();
                    injected.add(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
                    // Now RET, CIR
                    if (returnOpcode == Opcodes.ARETURN) {
                        injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CALLBACK_INFO_RETURNABLE_TYPE, "getReturnValue", "()Ljava/lang/Object;"));
                        injected.add(new TypeInsnNode(Opcodes.CHECKCAST, method.desc.substring(method.desc.lastIndexOf(')') + 2, method.desc.length() - 1)));
                    } else {
                        injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CALLBACK_INFO_RETURNABLE_TYPE, "getReturnValue" + ((char) returnType), "()" + ((char) returnType)));
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
                injected.add(new TypeInsnNode(Opcodes.NEW, CALLBACK_INFO_TYPE));
                injected.add(new InsnNode(Opcodes.DUP));
                if (cancellable) {
                    injected.add(new InsnNode(Opcodes.DUP));
                }
                injected.add(new LdcInsnNode(method.name));
                injected.add(new InsnNode(this.cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
                injected.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, CALLBACK_INFO_TYPE, "<init>", "(Ljava/lang/String;Z)V"));
                this.captureArguments(sourceStub, injected, to, method);
                injected.add(new MethodInsnNode(Opcodes.INVOKESTATIC, to.name, handlerNode.name, handlerNode.desc));
                injected.add(new InsnNode(ASMUtil.popReturn(handlerNode.desc))); // The official mixin implementation doesn't seem to pop here, but we'll do it anyways as that is more likely to be more stable
                if (this.cancellable) {
                    // TODO What happens if two injectors have the same entrypoint? Is mixin smart or not so smart here?
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CALLBACK_INFO_TYPE, "isCancelled", "()Z"));
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
                injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, to.name, handlerNode.name, handlerNode.desc));
                injected.add(new InsnNode(ASMUtil.popReturn(handlerNode.desc))); // The official mixin implementation doesn't seem to pop here, but we'll do it anyways as that is more likely to be more stable
                if (cancellable) {
                    injected.add(new VarInsnNode(Opcodes.ALOAD, idx));
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CALLBACK_INFO_TYPE, "isCancelled", "()Z"));
                    LabelNode skipReturn = new LabelNode();
                    injected.add(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
                    injected.add(new InsnNode(Opcodes.RETURN));
                    injected.add(skipReturn);
                }
            }
            method.instructions.insert(label, injected); // Not insertBefore due to jump instructions and stuff
        }
    }

    /**
     * Handles local capture.
     *
     * @param handlerOwner The owner class of the injector source.
     * @param targetClass The ASM {@link ClassNode} representation of the class that is targeted by the mixin
     * @param target The ASM {@link MethodNode} representation of the method that should be transformed by the inject.
     * @param out Instructions generated through the local capture that should be prefixed before the actual injection handling. Intended to load the local variables.
     * @param label The label which is targeted by the injection.
     * @param sharedBuilder A shared {@link StringBuilder} instance used to reduce duplicate allocations
     * @return True to abort injection (for example with PRINT), false otherwise.
     */
    private boolean handleCapture(@NotNull ClassNode handlerOwner, @NotNull ClassNode targetClass, @NotNull MethodNode target,
            @NotNull InsnList out, LabelNode label, @NotNull StringBuilder sharedBuilder) {
        if (this.locals.equals("NO_CAPTURE")) {
            return false;
        }
        LocalCaptureResult result = LocalsCapture.captureLocals(targetClass, target, Objects.requireNonNull(label), this.pool);
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
            if (!typeInsn.desc.equals(CALLBACK_INFO_TYPE)
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
        injected.add(new TypeInsnNode(Opcodes.NEW, CALLBACK_INFO_TYPE));
        injected.add(new InsnNode(Opcodes.DUP));
        injected.add(new LdcInsnNode(method.name));
        injected.add(new InsnNode(cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
        injected.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, CALLBACK_INFO_TYPE, "<init>", "(Ljava/lang/String;Z)V"));
        injected.add(new VarInsnNode(Opcodes.ASTORE, index));
        method.instructions.insert(injected);
        return index;
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull Remapper remapper,
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
            if (handlerType.equals(CALLBACK_INFO_DESC) || handlerType.equals(CALLBACK_INFO_RETURNABLE_DESC)) {
                if (this.injectSource.desc.startsWith("(" + CALLBACK_INFO_DESC + ")")
                        || this.injectSource.desc.startsWith("(" + CALLBACK_INFO_RETURNABLE_DESC + ")")) {
                    // Not capturing any argument is supported even in the case of an argument underflow.
                    return;
                }
                // Argument underflow (not supported by the spongeian implementation - even though underflows are supported for local capture):
                // the break will cause an exception to fire - that is intended.
                break;
            }
            String targetType = targetDesc.nextType();
            if (!handlerType.equals(targetType)) {
                // TODO Can it also be subtypes?
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
            if (handlerType.equals(CALLBACK_INFO_DESC) || handlerType.equals(CALLBACK_INFO_RETURNABLE_DESC)) {
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
