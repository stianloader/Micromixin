package de.geolykt.micromixin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import de.geolykt.micromixin.internal.HandlerContextHelper;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.MixinStub;
import de.geolykt.micromixin.internal.util.Objects;

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

    private final Map<ModularityAttached<M, String>, MixinConfig> packageDeclarations = new HashMap<ModularityAttached<M, String>, MixinConfig>();
    private final Map<ModularityAttached<M, String>, MixinConfig> mixins = new HashMap<ModularityAttached<M, String>, MixinConfig>();
    private final Map<ModularityAttached<M, String>, ClassNode> mixinNodes = new HashMap<ModularityAttached<M, String>, ClassNode>();
    private final Map<ModularityAttached<M, String>, MixinStub> mixinStubs = new HashMap<ModularityAttached<M, String>, MixinStub>();
    private final Map<String, TreeSet<MixinStub>> mixinTargets = new HashMap<String, TreeSet<MixinStub>>();
    private final BytecodeProvider<M> bytecodeProvider;

    public MixinTransformer(BytecodeProvider<M> bytecodeProvider) {
        this.bytecodeProvider = bytecodeProvider;
    }

    public void addMixin(M attachment, @NotNull MixinConfig config) {
        Objects.requireNonNull(config, "config must not be null");
        if (isMixin(attachment, config.mixinPackage)) { // FIXME: also validate other configs inserted previously.
            throw new IllegalStateException("Two mixin configurations within the same modularity attachment (" + attachment + ") target the same package (" + config.mixinPackage + ").");
        }
        packageDeclarations.put(new ModularityAttached<M, String>(attachment, config.mixinPackage), config);
        // FIXME unregister registered stuff if it fails
        for (String mixin : config.mixins) {
            ModularityAttached<M, String> mixinRef = new ModularityAttached<M, String>(attachment, config.mixinPackage + "/" + mixin);
            if (mixins.containsKey(mixinRef)) {
                throw new IllegalStateException("Two mixin configurations within the same modularity attachment (" + attachment + ") use the same class (" + mixinRef.value + ").");
            }
            mixins.put(mixinRef, config);
            try {
                ClassNode node = bytecodeProvider.getClassNode(attachment, mixinRef.value);
                MixinStub stub = MixinStub.parse(config.priority, node);
                mixinNodes.put(mixinRef, node);
                mixinStubs.put(mixinRef, stub);
                Set<String> targets = new HashSet<String>();
                for (String desc : stub.header.targets) {
                    if (!targets.add(desc)) {
                        continue;
                    }
                    TreeSet<MixinStub> val = mixinTargets.get(desc);
                    if (val == null) {
                        val = new TreeSet<MixinStub>();
                        mixinTargets.put(desc, val);
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
        if (packageDeclarations.containsKey(attached)) {
            return true;
        }
        int slashIndex = internalName.lastIndexOf('/');
        if (slashIndex == -1) {
            return false;
        } else {
            return isMixin(attachment, internalName.substring(0, slashIndex));
        }
    }

    public boolean isMixinTarget(@NotNull String name) {
        return this.mixinTargets.containsKey(name);
    }

    public void transform(@NotNull ClassNode in) {
        Iterable<MixinStub> mixins = mixinTargets.get(in.name);
        if (mixins == null) {
            return;
        }
        HandlerContextHelper hctx = HandlerContextHelper.from(in);
        for (MixinStub stub : mixins) {
            stub.applyTo(in, hctx);
        }
    }
}
