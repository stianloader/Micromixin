package org.stianloader.micromixin.transform.internal.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.api.SlicedInjectionPointSelector;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;
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
import org.stianloader.micromixin.transform.internal.util.InjectionPointReference;
import org.stianloader.micromixin.transform.internal.util.Objects;
import org.stianloader.micromixin.transform.internal.util.commenttable.CommentTable;
import org.stianloader.micromixin.transform.internal.util.locals.ArgumentCaptureContext;
import org.stianloader.micromixin.transform.internal.util.locals.LocalCaptureResult;
import org.stianloader.micromixin.transform.internal.util.locals.LocalsCapture;

public class MixinModifyVariableAnnotation extends MixinAnnotation<MixinMethodStub> {
    @NotNull
    public static MixinModifyVariableAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot, @NotNull MixinTransformer<?> transformer, @NotNull StringBuilder sharedBuilder) throws MixinParseException {
        if ((method.access & Opcodes.ACC_STATIC) != 0 && (method.access & Opcodes.ACC_PRIVATE) == 0) {
            throw new MixinParseException("The variable modifier method " + node.name + "." + method.name + method.desc + " is static, but isn't private. Consider making the method private.");
        }

        ArgumentCaptureContext argCapture = ArgumentCaptureContext.parseModifyHandler(node, method, "ModifyVariable");
        List<MixinAtAnnotation> at = new ArrayList<MixinAtAnnotation>();
        List<MixinSliceAnnotation> slice = new ArrayList<MixinSliceAnnotation>();
        Collection<MixinDescAnnotation> target = null;
        Set<String> variableNames = null;
        String[] targetSelectors = null;
        int require = -1;
        int expect = -1;
        int allow = -1;

        for (int i = 0; i < annot.values.size(); i += 2) {
            String name = (String) annot.values.get(i);
            Object val = annot.values.get(i + 1);
            if (name.equals("at")) {
                // For some reason @ModifyVariable's at annotation is not in a list as is the case otherwise.
                // While the parser now only can find a singular annotation, the internals are still the same
                // as every other annotation in case this is changed. Just in case you wonder why everything
                // is how it is.
                AnnotationNode atValue = (AnnotationNode) val;
                if (atValue == null) {
                    throw new NullPointerException();
                }
                try {
                    at.add(MixinAtAnnotation.parse(node, atValue, transformer.getInjectionPointSelectors()));
                } catch (MixinParseException mpe) {
                    throw new MixinParseException("Unable to parse @At annotation defined by " + node.name + "." + method.name + method.desc, mpe);
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
                    throw new MixinParseException("Duplicate \"method\" field in @ModifyVariable.");
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
                AnnotationNode sliceValue = (AnnotationNode) val;
                if (sliceValue == null) {
                    throw new NullPointerException();
                }
                try {
                    slice.add(MixinSliceAnnotation.parse(node, sliceValue, transformer.getInjectionPointSelectors()));
                } catch (MixinParseException mpe) {
                    throw new MixinParseException("Unable to parse @Slice annotation defined by " + node.name + "." + method.name + method.desc, mpe);
                }
            } else if (name.equals("name")) {
                if (variableNames != null) {
                    throw new MixinParseException("Duplicate \"name\" field in @ModifyVariable.");
                }
                @SuppressWarnings("all")
                @NotNull String[] hack = (String[]) ((List) val).toArray(new String[0]);
                if (hack.length == 1) {
                    variableNames = Collections.singleton(hack[0]);
                } else {
                    Set<String> variableNamesSet = new HashSet<String>();
                    for (String lvtName : hack) {
                        variableNamesSet.add(lvtName);
                    }
                    variableNames = Collections.unmodifiableSet(variableNamesSet);
                }
            } else {
                throw new MixinParseException("Unimplemented key in @ModifyVariable: " + name);
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

        return new MixinModifyVariableAnnotation(slicedAts, Collections.unmodifiableCollection(selectors), method, require, expect, allow, transformer.getLogger(), argCapture, variableNames, transformer.getPool());
    }

    private final int allow;
    @NotNull
    private final ArgumentCaptureContext capturedArgs;
    private final int expect;
    @NotNull
    private final MethodNode injectSource;
    @NotNull
    private final MixinLoggingFacade logger;
    @Nullable
    private final Set<String> lvtVariableNames;
    private final int require;
    @NotNull
    public final Collection<MixinTargetSelector> selectors;
    @NotNull
    public final Collection<SlicedInjectionPointSelector> slicedAts;
    @NotNull
    private final ClassWrapperPool pool;

    private MixinModifyVariableAnnotation(@NotNull Collection<SlicedInjectionPointSelector> slicedAts,
            @NotNull Collection<MixinTargetSelector> selectors,
            @NotNull MethodNode injectSource, int require, int expect, int allow, @NotNull MixinLoggingFacade logger,
            @NotNull ArgumentCaptureContext capturedArgs, @Nullable Set<String> lvtVariableNames,
            @NotNull ClassWrapperPool pool) {
        this.slicedAts = slicedAts;
        this.selectors = selectors;
        this.injectSource = injectSource;
        this.require = require;
        this.expect = expect;
        this.allow = allow;
        this.logger = logger;
        this.capturedArgs = capturedArgs;
        this.lvtVariableNames = lvtVariableNames;
        this.pool = pool;
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub,
            @NotNull MixinMethodStub source, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        MethodNode handlerNode = CodeCopyUtil.copyHandler(this.injectSource, sourceStub, to, hctx.generateUniqueLocalPrefix() + this.injectSource.name, remapper, hctx.lineAllocator, this.logger);
        Collection<InjectionPointReference> matched = ASMUtil.enumerateTargets(this.selectors, this.slicedAts, to, sourceStub, this.injectSource, this.require, this.expect, this.allow, remapper, sharedBuilder, this.logger);
        String argumentType = ASMUtil.getReturnType(this.injectSource.desc);
        int computationalArgumentType = argumentType.codePointAt(0);

        Map<MethodNode, List<AbstractInsnNode>> selectedInstructions = new HashMap<MethodNode, List<AbstractInsnNode>>();

        for (InjectionPointReference entry : matched) {
            MethodNode method = entry.targetedMethod;

            List<AbstractInsnNode> insns = selectedInstructions.get(method);
            if (insns == null) {
                insns = new ArrayList<AbstractInsnNode>();
                selectedInstructions.put(method, insns);
            }

            insns.add(entry.shiftedInstruction);
        }

        for (Map.Entry<MethodNode, List<AbstractInsnNode>> entry : selectedInstructions.entrySet()) {
            MethodNode method = entry.getKey();
            List<AbstractInsnNode> injectLocations = entry.getValue();

            int selectedLocal = -1;
            selectLocal:
            {
                List<LocalVariableNode> lvt = method.localVariables;
                Set<String> lvtNames = this.lvtVariableNames;
                if (lvt != null && lvtNames != null) {
                    lvnIterator:
                    for (LocalVariableNode lvn : lvt) {
                        if (lvtNames.contains(lvn.name)
                                && argumentType.equals(lvn.desc)) {
                            for (AbstractInsnNode injectLoc : injectLocations) {
                                assert injectLoc != null;
                                if (!ASMUtil.isBetween(lvn.start, lvn.end, injectLoc)) {
                                    continue lvnIterator;
                                }
                            }
                            selectedLocal = lvn.index;
                            break selectLocal;
                        }
                    }
                }

                // <not an issue [yet, as the infringing feature hasn't yet been implemented - but might be implemented one day]>
                // this skews the semantics of `allow`, `expect` and `require` as they are already handled in ASMUtil#enumerateTargets.
                assert injectLocations != null;
                Map<AbstractInsnNode, LocalCaptureResult> locals = LocalsCapture.multiCaptureLocals(to, method, injectLocations, this.pool);
                int maxLocals = Integer.MAX_VALUE;
                for (LocalCaptureResult localCapture : locals.values()) {
                    Frame<BasicValue> frame = localCapture.frame;
                    if (frame == null) {
                        Throwable error = localCapture.error;
                        if (error != null) {
                            throw new IllegalStateException("Cannot capture locals within method " + method.name + method.desc, error);
                        } else {
                            throw new IllegalStateException("Cannot capture locals within method " + method.name + method.desc + ": No further information.");
                        }
                    }
                    maxLocals = Math.min(maxLocals, frame.getLocals());
                }

                Set<Integer> candidates = new HashSet<Integer>();
                if (maxLocals == Integer.MAX_VALUE) {
                    // Severe logic bug; shouldn't happen - hence the vague description of the issue
                    throw new IllegalStateException("Cannot correctly determine the size of available locals.");
                }

                for (int i = (method.access & Opcodes.ACC_STATIC) == 0 ? 1 : 0; i < maxLocals; i++) {
                    candidates.add(i);
                }

                for (LocalCaptureResult localCapture : locals.values()) {
                    Frame<BasicValue> frame = localCapture.frame;
                    if (frame == null) {
                        throw new AssertionError();
                    }

                    int i = (method.access & Opcodes.ACC_STATIC) == 0 ? 1 : 0;
                    DescString dString = new DescString(method.desc);

                    // Pull argument types from the method's descriptor and not from the computed local frames
                    // This is because for ASM, booleans, shorts and other int-like types become ints.
                    // Technically speaking that is too much of a hacky workaround and we'd need to find some
                    // other solution - perhaps using the local variable table within the target method?
                    while (dString.hasNext()) {
                        String type = dString.nextType();
                        if (!type.equals(argumentType)) {
                            candidates.remove(i);
                        }
                        if (ASMUtil.isCategory2(type.codePointAt(0))) {
                            candidates.remove(++i);
                        }
                        i++;
                    }

                    for (; i < maxLocals; i++) {
                        BasicValue local = frame.getLocal(i);
                        Type type = local.getType();
                        if (type == null || !type.getDescriptor().equals(argumentType)) {
                            candidates.remove(i);
                        }
                        if (local.getSize() == 2) {
                            candidates.remove(++i);
                        }
                    }
                }

                if (candidates.isEmpty()) {
                    CommentTable table = new CommentTable();
                    for (LocalCaptureResult localCapture : locals.values()) {
                        table.addSection(localCapture.asLocalPrintTable(sharedBuilder));
                    }
                    throw new IllegalStateException("Cannot capture locals within method " + method.name + method.desc + "; implicit local variable selection found no candidates. Consider using explicit local variable selection instead. Note: Local capture (including for @ModifyVariable) is inherently brittle; consider using other approaches if they are viable. Local capture information:\n" + table.toString());
                }

                if (candidates.size() != 1) {
                    CommentTable table = new CommentTable();
                    for (LocalCaptureResult localCapture : locals.values()) {
                        table.addSection(localCapture.asLocalPrintTable(sharedBuilder));
                    }
                    throw new IllegalStateException("Cannot capture locals within method " + method.name + method.desc + "; implicit local variable selection found too many candidates (" + candidates.size() + " candidates found, but implicit local variable selection requires only a single candidate). Consider using explicit local variable selection instead. Note: Local capture (including for @ModifyVariable) is inherently brittle; consider using other approaches if they are viable. Local capture information:\n" + table.toString());
                }

                /*
                CommentTable table = new CommentTable();
                for (LocalCaptureResult localCapture : locals.values()) {
                    table.addSection(localCapture.asLocalPrintTable(sharedBuilder));
                }
                table.addSection(new KeyValueTableSection()
                        .add("methodName", method.name)
                        .add("methodDesc", method.desc)
                        .add("candidates", Objects.toString(candidates)));
                this.logger.info(MixinModifyVariableAnnotation.class, "ModifyVariable meta:\n{}", table.toString());
                */

                selectedLocal = candidates.iterator().next().intValue();
            }

            for (AbstractInsnNode injectLoc : injectLocations) {
                assert injectLoc != null;

                int handlerInvokeOpcode;
                InsnList inject = new InsnList();

                if ((handlerNode.access & Opcodes.ACC_STATIC) == 0) {
                    handlerInvokeOpcode = Opcodes.INVOKEVIRTUAL;
                    inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                } else {
                    handlerInvokeOpcode = Opcodes.INVOKESTATIC;
                }

                inject.add(new VarInsnNode(ASMUtil.getLoadOpcode(computationalArgumentType), selectedLocal));
                this.capturedArgs.appendCaptures(to, Objects.requireNonNull(method, "method may not be null"), source, injectLoc, inject);
                inject.add(new MethodInsnNode(handlerInvokeOpcode, to.name, handlerNode.name, handlerNode.desc));
                inject.add(new VarInsnNode(ASMUtil.getStoreOpcode(computationalArgumentType), selectedLocal));
                method.instructions.insertBefore(injectLoc, inject);
            }
        }
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        // NOP
    }
}
