package org.stianloader.micromixin.transform.internal;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.internal.annotation.mixinsextras.MixinExtrasWrapOperationAnnotation;
import org.stianloader.micromixin.transform.internal.util.smap.MultiplexLineNumberAllocator;

public class HandlerContextHelper {

    @SuppressWarnings("null")
    @NotNull
    public static HandlerContextHelper from(@NotNull ClassNode node) {
        String[] methodNames = new String[node.methods.size()];
        int n = 0;

        for (MethodNode method : node.methods) {
            methodNames[n++] = method.name;
        }

        String handlerPrefix = HandlerContextHelper.uniquePrefix("$handler$", methodNames);
        String bridgePrefix = HandlerContextHelper.uniquePrefix("mixinextras$bridge$", methodNames);

        return new HandlerContextHelper(handlerPrefix, bridgePrefix, new MultiplexLineNumberAllocator(node));
    }

    private static String uniquePrefix(@NotNull String preferredPrefix, @NotNull String[] from) {
        for (String s : from) {
            if (s.startsWith(preferredPrefix)) {
                String prefix;
                int i = 0;
                outer:
                while (true) {
                    prefix = preferredPrefix + i++ + "$";
                    for (String mname : from) {
                        if (mname.startsWith(prefix)) {
                            continue outer;
                        }
                    }
                    break;
                }
                return prefix;
            }
        }

        return preferredPrefix;
    }

    /**
     * The bridge counter is an incrementing integer tied to the {@link HandlerContextHelper};
     * it is applied alongside the {@link #bridgePrefix} to form a unique prefix/suffix combination
     * to ensure that bridge methods all have a unique name within a single class.
     *
     * <p>This counter will be unique for each invocation of a {@link #getBridgeSuffix()}
     * within the same {@link HandlerContextHelper} instance.
     *
     * <p>The intention of this counter is to prevent multiple mixins defining bridges with
     * the same name causing classes to be transformed invalidly.
     *
     * @see #getBridgeSuffix()
     */
    private int bridgeCounter = 0;

    /**
     * The bridge prefix is a prefix that is applied on all mixin-introduced
     * bridge methods for a given target class. Bridge methods are generated
     * when using {@link MixinExtrasWrapOperationAnnotation WrapOperation} because
     * the {@code Operation} class accepts a {@code Object[]} but the arguments
     * need to be spread/unboxed from the array for the underlying operation to succeed.
     *
     * <p>The prefix will be the same for all mixins applied with this {@link HandlerContextHelper},
     * which is usually generated for each call for {@link MixinTransformer#transform(ClassNode)},
     * meaning that different mixin classes will share the same bridge prefix for as long as
     * they target the same class within the same session (unless multiple calls to
     * {@link MixinTransformer#transform(ClassNode)}) are dispatched.
     *
     * <p>The main intention of this field is to prevent collisions between mixins when
     * transforming a class with mixin transformers multiple times.
     *
     * <p>Whilst the bridge prefix ensures uniqueness across multiple transformations,
     * the bridge suffix is required for uniqueness within a single transformation.
     */
    @NotNull
    private final String bridgePrefix;

    /**
     * The global handler prefix is a prefix that is applied on all mixin-introduced
     * handler methods for a given target class.
     *
     * <p>The prefix will be the same for all mixins applied with this {@link HandlerContextHelper},
     * which is usually generated for each call for {@link MixinTransformer#transform(ClassNode)},
     * meaning that different mixin classes will share the same global prefix for as long as
     * they target the same class within the same session (unless multiple calls to
     * {@link MixinTransformer#transform(ClassNode)}) are dispatched.
     *
     * <p>The main intention of this field is to prevent collisions between mixins when
     * transforming a class with mixin transformers multiple times.
     */
    @NotNull
    private final String globalHandlerPrefix;

    /**
     * The handler counter is an incrementing integer tied to the {@link HandlerContextHelper};
     * it is applied alongside the {@link #globalHandlerPrefix} to form the
     * {@link #generateUniqueLocalPrefix() unique local prefix}.
     *
     * <p>This counter will be unique for each invocation of a {@link #generateUniqueLocalPrefix()}
     * within the same {@link HandlerContextHelper} instance.
     *
     * <p>The intention of this counter is to prevent multiple mixins defining mixin handlers with
     * the same name causing classes to be transformed invalidly.
     *
     * @see #generateUniqueLocalPrefix()
     */
    private int handlerCounter = 0;

    @NotNull
    public final MultiplexLineNumberAllocator lineAllocator;

    private HandlerContextHelper(@NotNull String handlerPrefix, @NotNull String bridgePrefix, @NotNull MultiplexLineNumberAllocator lineAllocator) {
        this.globalHandlerPrefix = handlerPrefix;
        this.bridgePrefix = bridgePrefix;
        this.lineAllocator = lineAllocator;
    }

    /**
     * Generate a unique prefix for a mixin handler method. This prefix contains a trailing "$"
     *
     * <p>The intention of this prefix is to prevent multiple mixins defining mixin handlers with
     * the same name causing classes to be transformed invalidly.
     *
     * @return A unique prefix applied on the mixin handler method that is copied to the target (transformed) class.
     */
    @NotNull
    public String generateUniqueLocalPrefix() {
        return this.globalHandlerPrefix + this.handlerCounter++ + "$";
    }

    /**
     * The bridge prefix is a prefix that is applied on all mixin-introduced
     * bridge methods for a given target class. Bridge methods are generated
     * when using {@link MixinExtrasWrapOperationAnnotation WrapOperation} because
     * the {@code Operation} class accepts a {@code Object[]} but the arguments
     * need to be spread/unboxed from the array for the underlying operation to succeed.
     *
     * <p>The prefix will be the same for all mixins applied with this {@link HandlerContextHelper},
     * which is usually generated for each call for {@link MixinTransformer#transform(ClassNode)},
     * meaning that different mixin classes will share the same bridge prefix for as long as
     * they target the same class within the same session (unless multiple calls to
     * {@link MixinTransformer#transform(ClassNode)}) are dispatched.
     *
     * <p>The main intention of this field is to prevent collisions between mixins when
     * transforming a class with mixin transformers multiple times.
     *
     * <p>Whilst the bridge prefix ensures uniqueness across multiple transformations,
     * the bridge suffix is required for uniqueness within a single transformation.
     *
     * @return The bridge prefix of this {@link HandlerContextHelper} instance. It is constant
     * for every invocation of this method on a given {@link HandlerContextHelper} instance.
     * @see #getBridgeSuffix()
     */
    @NotNull
    @Contract(pure = true)
    public String getBridgePrefix() {
        return this.bridgePrefix;
    }

    /**
     * The bridge suffix is a string that is attached to the end of a bridge method's
     * name to ensure that the bridge method is named uniquely among any other method
     * with the same {@link #getBridgePrefix() bridge prefix}.
     *
     * @return A generated {@link String} that is unique for every invocation of this method
     * for this {@link HandlerContextHelper} instance.
     * @see #getBridgePrefix()
     */
    @NotNull
    @Contract(pure = false, mutates = "this", value = "-> new")
    public String getBridgeSuffix() {
        return "$" + this.bridgeCounter++;
    }
}
