package org.stianloader.micromixin.transform.internal;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.MixinTransformer;
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
        for (String s : methodNames) {
            if (s.startsWith("$handler$")) {
                String prefix;
                int i = 0;
                outer:
                while (true) {
                    prefix = "$handler$" + i++ + "$";
                    for (String mname : methodNames) {
                        if (mname.startsWith(prefix)) {
                            continue outer;
                        }
                    }
                    break;
                }
                return new HandlerContextHelper(prefix, new MultiplexLineNumberAllocator(node));
            }
        }
        return new HandlerContextHelper("$handler$", new MultiplexLineNumberAllocator(node));
    }

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

    private HandlerContextHelper(@NotNull String handlerPrefix, @NotNull MultiplexLineNumberAllocator lineAllocator) {
        this.globalHandlerPrefix = handlerPrefix;
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
}
