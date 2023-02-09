package de.geolykt.micromixin.internal.annotation;

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

import de.geolykt.micromixin.internal.HandlerContextHelper;
import de.geolykt.micromixin.internal.MixinMethodStub;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.MixinStub;
import de.geolykt.micromixin.internal.selectors.DescSelector;
import de.geolykt.micromixin.internal.selectors.MixinTargetSelector;
import de.geolykt.micromixin.internal.selectors.StringSelector;
import de.geolykt.micromixin.internal.util.CodeCopyUtil;
import de.geolykt.micromixin.internal.util.DescString;
import de.geolykt.micromixin.internal.util.Remapper;

public class MixinInjectAnnotation implements MixinAnnotation<MixinMethodStub> {

    @NotNull
    public final Collection<MixinAtAnnotation> at;
    @NotNull
    public final Collection<MixinTargetSelector> selectors;
    @NotNull
    private final MethodNode injectSource;
    private final int require;
    private final int expect;
    private final boolean cancellable;

    public MixinInjectAnnotation(@NotNull Collection<MixinAtAnnotation> at, @NotNull Collection<MixinTargetSelector> selectors,
            @NotNull MethodNode injectSource, int require, int expect) {
        this.at = at;
        this.selectors = selectors;
        this.injectSource = injectSource;
        this.require = require;
        this.expect = expect;
        this.cancellable = false; // IMPLEMENT support
    }

    @NotNull
    public static MixinInjectAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot) {
        List<MixinAtAnnotation> at = new ArrayList<>();
        Collection<@NotNull MixinDescAnnotation> target = null;
        @NotNull String[] targetSelectors = null;
        String fallbackMethodDesc = AnnotationUtil.getTargetDesc(method, true);
        int require = -1;
        int expect = -1;
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
                target = new ArrayList<>();
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
                @NotNull String[] hack = (@NotNull String[]) ((List) val).toArray(new @NotNull String[0]);
                targetSelectors = hack;
            } else if (name.equals("require")) {
                require = ((Integer) val).intValue();
            } else if (name.equals("expect")) {
                expect = ((Integer) val).intValue();
            } else {
                throw new MixinParseException("Unimplemented key in @Inject: " + name);
            }
        }
        List<MixinTargetSelector> selectors = new ArrayList<>();
        if (target != null) {
            for (@NotNull MixinDescAnnotation desc : target) {
                selectors.add(new DescSelector(desc));
            }
        }
        if (targetSelectors != null) {
            for (@NotNull String s : targetSelectors) {
                selectors.add(new StringSelector(s));
            }
        }
        if (selectors.isEmpty()) {
            // IMPLEMENT what about injector groups?
            throw new MixinParseException("No available selectors: Mixin " + node.name + "." + method.name + method.desc + " does not match anything and is not a valid mixin.");
        }
        return new MixinInjectAnnotation(Collections.unmodifiableCollection(at), Collections.unmodifiableCollection(selectors), method, require, expect);
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        MethodNode handlerNode = CodeCopyUtil.copyHandler(this.injectSource, sourceStub, to, hctx.handlerPrefix + hctx.handlerCounter++ + "$" + this.injectSource.name);
        Map<LabelNode, MethodNode> labels = new HashMap<>();
        for (MixinTargetSelector selector : selectors) {
            for (MixinAtAnnotation at : this.at) {
                MethodNode targetMethod = selector.selectMethod(to, sourceStub);
                if (targetMethod != null) {
                    if (targetMethod.name.equals("<init>") && !at.supportsConstructors()) {
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + ".<init>" + targetMethod.desc + ", which is a constructor. However the selector @At(\"" + at.value + "\") does not support usage within a constructor.");
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
            System.err.println("Potentially outdated mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " expects " + this.expect + " injection points but only found " + labels.size() + ".");
        }
        // IMPLEMENT the hell that is known as local capture
        for (Map.Entry<LabelNode, MethodNode> entry : labels.entrySet()) {
            LabelNode label = entry.getKey();
            MethodNode method = entry.getValue();
            if ((method.access & Opcodes.ACC_STATIC) != 0) {
                InsnList injectedInsns = new InsnList();
                injectedInsns.add(new TypeInsnNode(Opcodes.NEW, "org/spongepowered/asm/mixin/injection/callback/CallbackInfo"));
                injectedInsns.add(new InsnNode(this.cancellable ? Opcodes.DUP2 : Opcodes.DUP));
                injectedInsns.add(new LdcInsnNode(method.name));
                injectedInsns.add(new InsnNode(this.cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
                injectedInsns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "org/spongepowered/asm/mixin/injection/callback/CallbackInfo", "<init>", "(Ljava/lang/String;Z)V"));
                injectedInsns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, to.name, handlerNode.name, handlerNode.desc));
                if (this.cancellable) {
                    // TODO What happens if two injectors have the same entrypoint? Is mixin smart or not so smart here?
                    injectedInsns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/spongepowered/asm/mixin/injection/callback/CallbackInfo", "isCancelled", "()Z"));
                    LabelNode skipReturn = new LabelNode();
                    injectedInsns.add(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
                    injectedInsns.add(new InsnNode(Opcodes.RETURN));
                    injectedInsns.add(skipReturn);
                }
                method.instructions.insert(label, injectedInsns); // Not insertBefore due to jump instructions and stuff
            } else {
                int idx = getCallbackInfoIndex(method, cancellable);
                InsnList injected = new InsnList();
                injected.add(new VarInsnNode(Opcodes.ALOAD, idx));
                injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, to.name, handlerNode.name, handlerNode.desc));
                if (cancellable) {
                    injected.add(new VarInsnNode(Opcodes.ALOAD, idx));
                    injected.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/spongepowered/asm/mixin/injection/callback/CallbackInfo", "isCancelled", "()Z"));
                    LabelNode skipReturn = new LabelNode();
                    injected.add(new JumpInsnNode(Opcodes.IFEQ, skipReturn));
                    injected.add(new InsnNode(Opcodes.RETURN));
                    injected.add(skipReturn);
                }
                injected.add(label);
            }
        }
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
            if (!typeInsn.desc.equals("org/spongepowered/asm/mixin/injection/callback/CallbackInfo")
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
            String type = dstring.nextType();
            if (type.codePointAt(0) == 'J' || type.codePointAt(0) == 'D') {
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
        injected.add(new TypeInsnNode(Opcodes.NEW, "org/spongepowered/asm/mixin/injection/callback/CallbackInfo"));
        injected.add(new InsnNode(Opcodes.DUP));
        injected.add(new LdcInsnNode(method.name));
        injected.add(new InsnNode(cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
        injected.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "org/spongepowered/asm/mixin/injection/callback/CallbackInfo", "<init>", "(Ljava/lang/String;Z)V"));
        injected.add(new VarInsnNode(Opcodes.ASTORE, index));
        method.instructions.insert(injected);
        return index;
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            de.geolykt.micromixin.internal.util.@NotNull Remapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        // NOP
    }
}
