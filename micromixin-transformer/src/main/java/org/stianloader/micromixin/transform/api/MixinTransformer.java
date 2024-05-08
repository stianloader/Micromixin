package org.stianloader.micromixin.transform.api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.stianloader.micromixin.transform.api.supertypes.ClassWrapperPool;
import org.stianloader.micromixin.transform.internal.DefaultMixinLogger;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.selectors.inject.ConstantInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.FieldInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.HeadInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.InvokeInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.ReturnInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.selectors.inject.TailInjectionPointSelector;
import org.stianloader.micromixin.transform.internal.util.Objects;

/**
 * The central brain of Micromixin.
 * An instance of this class stores all mixin-related matters.
 * Multiple instances can be used at the same time, as long as their inputs
 * are separated.
 *
 * @param <M> The modularity attachment type. Modularity attachments are needed to pin down
 * the source of a mixin. This reduces the chance of incompatibilities between mods loaded
 * by two different classloaders. The modularity attachment should ideally implement {@link Object#equals(Object)}
 * and {@link Object#hashCode()} if the identity comparison semantics of the default implementations
 * are a potential issue. A recommended type can be {@link ClassLoader},
 * but really anything goes, {@link Void} for example is perfectly fine as long as the {@link BytecodeProvider}
 * accounts for null attachments. All methods in {@link MixinTransformer} support null attachments internally,
 * albeit it is not recommended to use null for any method.
 */
public class MixinTransformer<M> {

    private static final boolean DEBUG = Boolean.getBoolean("org.stianloader.micromixin.debug");

    @NotNull
    private final BytecodeProvider<M> bytecodeProvider;
    private boolean delayParseExceptions = Boolean.getBoolean("org.stianloader.micromixin.delayedParseException");
    @NotNull
    private final InjectionPointSelectorFactory injectionPointSelectors = new InjectionPointSelectorFactory();
    @NotNull
    private MixinLoggingFacade logger = new DefaultMixinLogger(MixinTransformer.DEBUG);
    private boolean mergeClassFileVersions = true;
    @NotNull
    private final Map<ModularityAttached<M, String>, ClassNode> mixinNodes = new HashMap<ModularityAttached<M, String>, ClassNode>();
    @NotNull
    private final Map<ModularityAttached<M, String>, MixinConfig> mixins = new HashMap<ModularityAttached<M, String>, MixinConfig>();
    @NotNull
    private final Map<ModularityAttached<M, String>, MixinStub> mixinStubs = new HashMap<ModularityAttached<M, String>, MixinStub>();
    @NotNull
    private final Map<String, TreeSet<MixinStub>> mixinTargets = new HashMap<String, TreeSet<MixinStub>>();
    @NotNull
    private final Map<ModularityAttached<M, String>, MixinConfig> packageDeclarations = new HashMap<ModularityAttached<M, String>, MixinConfig>();
    @NotNull
    private final ClassWrapperPool pool;

    public MixinTransformer(@NotNull BytecodeProvider<M> bytecodeProvider, @NotNull ClassWrapperPool pool) {
        this.bytecodeProvider = bytecodeProvider;
        this.pool = pool;
        this.injectionPointSelectors.register(ConstantInjectionPointSelector.PROVIDER);
        this.injectionPointSelectors.register(HeadInjectionPointSelector.INSTANCE);
        this.injectionPointSelectors.register(InvokeInjectionPointSelector.PROVIDER);
        this.injectionPointSelectors.register(ReturnInjectionPointSelector.INSTANCE);
        this.injectionPointSelectors.register(TailInjectionPointSelector.INSTANCE);
        this.injectionPointSelectors.register(FieldInjectionPointSelector.PROVIDER);
    }

    public void addMixin(M attachment, @NotNull MixinConfig config) {
        Objects.requireNonNull(config, "config must not be null");
        if (this.isMixin(attachment, config.mixinPackage)) { // FIXME: also validate other configs inserted previously.
            throw new IllegalStateException("Two mixin configurations within the same modularity attachment (" + attachment + ") target the same package (" + config.mixinPackage + ").");
        }
        this.packageDeclarations.put(new ModularityAttached<M, String>(attachment, config.mixinPackage), config);
        StringBuilder sharedBuilder = new StringBuilder();
        // FIXME unregister registered stuff if it fails
        for (String mixin : config.mixins) {
            ModularityAttached<M, String> mixinRef = new ModularityAttached<M, String>(attachment, config.mixinPackage + "/" + mixin);
            if (this.mixins.containsKey(mixinRef)) {
                throw new IllegalStateException("Two mixin configurations within the same modularity attachment (" + attachment + ") use the same class (" + mixinRef.value + ").");
            }
            this.mixins.put(mixinRef, config);
            try {
                ClassNode node = bytecodeProvider.getClassNode(attachment, mixinRef.value);
                MixinStub stub = MixinStub.parse(config.priority, node, this, sharedBuilder);
                this.mixinNodes.put(mixinRef, node);
                this.mixinStubs.put(mixinRef, stub);
                Set<String> targets = new HashSet<String>();
                for (String desc : stub.header.targets) {
                    if (!targets.add(desc)) {
                        continue;
                    }
                    TreeSet<MixinStub> val = this.mixinTargets.get(desc);
                    if (val == null) {
                        val = new TreeSet<MixinStub>();
                        this.mixinTargets.put(desc, val);
                    }
                    val.add(stub);
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Broken mixin: " + mixinRef.value + " (attached via " + attachment + ")", e);
            } catch (MixinParseException e) {
                throw new IllegalStateException("Broken mixin: " + mixinRef.value + " (attached via " + attachment + ")", e);
            }
        }
    }

    /**
     * Obtains the {@link InjectionPointSelectorFactory} that can be used to register custom injection point selectors.
     *
     * <p>The instance is specific to this specific {@link MixinTransformer}. Registering an injection point selector to
     * the factory of this instance will not have an effect of any mixins registered via any other transformers.
     *
     * @return The active {@link InjectionPointSelectorFactory} instance.
     */
    @NotNull
    @Contract(pure = true)
    public InjectionPointSelectorFactory getInjectionPointSelectors() {
        return this.injectionPointSelectors;
    }

    @NotNull
    public MixinLoggingFacade getLogger() {
        return this.logger;
    }

    @NotNull
    public ClassWrapperPool getPool() {
        return this.pool;
    }

    public boolean isDelayingParseExceptions() {
        return this.delayParseExceptions;
    }

    public boolean isMergingClassFileVersions() {
        return this.mergeClassFileVersions;
    }

    @Deprecated
    public boolean isMergeingClassFileVersions() {
        return this.mergeClassFileVersions;
    }

    /**
     * Returns whether a package or a class is within a mixin.
     * Such classes or packages should not be loaded by the classloader.
     *
     * <p>The internal name must be represented as a fully qualified name
     * using slashes ('/') as package separators.
     *
     * @param attachment The modularity attachment to use
     * @param internalName The internal name of the package or class
     * @return True if it is within the package, false otherwise.
     */
    public boolean isMixin(M attachment, @NotNull String internalName) {
        ModularityAttached<M, String> attached = new ModularityAttached<M, String>(attachment, internalName);
        if (this.packageDeclarations.containsKey(attached)) {
            return true;
        }
        int slashIndex = internalName.lastIndexOf('/');
        if (slashIndex == -1) {
            return false;
        } else {
            return this.isMixin(attachment, internalName.substring(0, slashIndex));
        }
    }

    public boolean isMixinTarget(@NotNull String name) {
        return this.mixinTargets.containsKey(name);
    }

    public void setDelayParseExceptions(boolean delayParseExceptions) {
        this.delayParseExceptions = delayParseExceptions;
    }

    public void setLogger(@NotNull MixinLoggingFacade logger) {
        this.logger = logger;
    }

    public void setMergeClassFileVersions(boolean mergeClassFileVersions) {
        this.mergeClassFileVersions = mergeClassFileVersions;
    }

    public void transform(@NotNull ClassNode in) {
        Iterable<MixinStub> mixins = mixinTargets.get(in.name);
        if (mixins == null) {
            return;
        }
        HandlerContextHelper hctx = HandlerContextHelper.from(in);
        StringBuilder sharedBuilder = new StringBuilder();
        for (MixinStub stub : mixins) {
            try {
                stub.applyTo(in, hctx, sharedBuilder);
            } catch (Throwable t) {
                if (t instanceof Error && !(t instanceof AssertionError)) {
                    throw (Error) t;
                }
                if (DEBUG) {
                    StringWriter sw = new StringWriter();
                    TraceClassVisitor tcv = new TraceClassVisitor(null, new Textifier(), new PrintWriter(sw));
                    in.accept(tcv);
                    this.getLogger().info(MixinTransformer.class, "Disassembled class file:\n{}", sw);
                }
                throw new RuntimeException("Failed to apply mixin stub originating from " + stub.sourceNode.name + " to node " + in.name, t);
            }

            if (this.isMergeingClassFileVersions()) {
                int adjustOrigin = in.version & ~Opcodes.V_PREVIEW;
                int adjustSource = stub.sourceNode.version & ~Opcodes.V_PREVIEW;
                if (adjustOrigin < adjustSource) {
                    in.version = stub.sourceNode.version;
                } else if (adjustOrigin == adjustSource) {
                    boolean previewOrigin = (in.version & Opcodes.V_PREVIEW) == Opcodes.V_PREVIEW;
                    boolean previewSource = (stub.sourceNode.version & Opcodes.V_PREVIEW) == Opcodes.V_PREVIEW;
                    if (previewOrigin | previewSource) {
                        in.version |= Opcodes.V_PREVIEW;
                    }
                }
            }
        }
        hctx.lineAllocator.exportToSMAP("Mixin").applyTo(in, sharedBuilder);
        if (DEBUG) {
            try {
                CheckClassAdapter cca = new CheckClassAdapter(null);
                in.accept(cca);
            } catch (Throwable t) {
                this.getLogger().error(MixinTransformer.class, "Invalidly transformed class: {}", in.name, t);
                StringWriter sw = new StringWriter();
                TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(sw));
                in.accept(tcv);
                this.getLogger().info(MixinTransformer.class, "Disassembled class file:\n", sw);
            }
        }
    }
}
