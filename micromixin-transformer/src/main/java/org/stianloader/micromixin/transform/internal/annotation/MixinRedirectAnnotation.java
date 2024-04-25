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
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
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
import org.stianloader.micromixin.transform.internal.util.Objects;

public final class MixinRedirectAnnotation extends MixinAnnotation<MixinMethodStub> {

    private final int allow;
    @NotNull
    public final SlicedInjectionPointSelector at;
    @NotNull
    public final Collection<MixinTargetSelector> selectors;
    @NotNull
    private final MethodNode injectSource;
    private final int require;
    private final int expect;
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
            throw new MixinParseException("No available selectors: Mixin " + node.name + "." + method.name + method.desc + " does not match anything and is not a valid mixin.");
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

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx, @NotNull MixinStub sourceStub,
            @NotNull MixinMethodStub source, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        if ((this.injectSource.access & Opcodes.ACC_STATIC) != 0 && (this.injectSource.access & Opcodes.ACC_PRIVATE) == 0) {
            throw new MixinParseException("The redirect handler method " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " is static, but isn't private. Consider making the method private, as both access modifiers cannot be present at the same time.");
        }
        if (!this.at.supportsRedirect()) {
            throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " uses selector @At(\"" + at.getSelector().fullyQualifiedName + "\") which does not support usage within a @Redirect context.");
        }
        MethodNode handlerNode = CodeCopyUtil.copyHandler(this.injectSource, sourceStub, to, hctx.handlerPrefix + hctx.handlerCounter++ + "$redirect$" + this.injectSource.name, remapper, hctx.lineAllocator);
        Map<MethodInsnNode, MethodNode> matched = new HashMap<MethodInsnNode, MethodNode>();
        for (MixinTargetSelector selector : selectors) {
            MethodNode targetMethod = selector.selectMethod(to, sourceStub);
            if (targetMethod != null) {
                // TODO ACC_STATIC is mandated in the constructor before the super() call even though the constructor itself is not ACC_STATIC.
                if ((targetMethod.access & Opcodes.ACC_STATIC) != 0) {
                    if ((this.injectSource.access & Opcodes.ACC_STATIC) == 0) {
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + targetMethod.desc + ". Target is static, but the mixin is not.");
                    }
                } else if ((this.injectSource.access & Opcodes.ACC_STATIC) != 0) {
                    // Technically that one could be doable, but it'd be nasty.
                    throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " targets " + to.name + "." + targetMethod.name + targetMethod.desc + " target is not static, but the callback handler is.");
                }
                for (AbstractInsnNode insn : this.at.getMatchedInstructions(targetMethod, remapper, sharedBuilder)) {
                    if (!(insn instanceof MethodInsnNode)) {
                        throw new IllegalStateException("Illegal mixin: " + sourceStub.sourceNode.name + "." + this.injectSource.name + this.injectSource.desc + " selects an instruction in target method " + to.name + "." + targetMethod.name + targetMethod.desc + " that isn't a MethodInsnNode (should be any of [INVOKESTATIC, INVOKEVIRTUAL, INVOKESPECIAL]) but rather is a " + insn.getClass().getName() + ". This issue is most likely caused by an erroneous @At-value (or an invalid shift). Using @At(" + this.at.getSelector().fullyQualifiedName + ")");
                    }
                    matched.put((MethodInsnNode) insn, targetMethod);
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

        // IMPLEMENT @Redirect-chaining. The main part could be done through annotations.
        // Note: The Spongeian mixin impl probably does not support that anyways - so is there a point implementing such
        // behaviour anyways? Besides - what is the point of chaining redirects? Wouldn't that cause unending hazard?
        // I think yes. Instead one ought to use @ModifyX anyways
        for (Map.Entry<MethodInsnNode, MethodNode> entry : matched.entrySet()) {
            MethodInsnNode insn = entry.getKey();
            // IMPLEMENT verify arguments.
            // TODO test whether argument capture is a thing with @Redirect
            MethodNode targetMethod = entry.getValue();
            InsnList instructions =  targetMethod.instructions;
            int insertedOpcode;
            if ((this.injectSource.access & Opcodes.ACC_STATIC) == 0) {
                VarInsnNode preInsert = new VarInsnNode(Opcodes.ALOAD, 0);
                instructions.insertBefore(insn, preInsert);
                ASMUtil.shiftDownByDesc(handlerNode.desc, false, to, targetMethod, preInsert, this.transformer.getPool());
                insertedOpcode = Opcodes.INVOKEVIRTUAL;
            } else {
                insertedOpcode = Opcodes.INVOKESTATIC;
            }
            MethodInsnNode inserted = new MethodInsnNode(insertedOpcode, to.name, handlerNode.name, handlerNode.desc);
            instructions.insertBefore(insn, inserted);
            instructions.remove(insn);
        }
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        // NOP
    }
}
