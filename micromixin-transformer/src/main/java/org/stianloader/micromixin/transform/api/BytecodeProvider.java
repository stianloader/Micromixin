package org.stianloader.micromixin.transform.api;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

/**
 * A {@link BytecodeProvider} provides {@link ClassNode} instances for arbitrary classes.
 * It is up to the API consumer to implement this interface.
 *
 * <p>In order to easily support modularity and further segregation of code, the bytecode provider
 * is always given a Modularity attachment. The {@link MixinTransformer} will have the same type
 * of the modularity attachment as the {@link BytecodeProvider}.
 *
 * <p>Implementors are highly recommended to also implements the {@link CodeSourceURIProvider} interface.
 * Future versions of micromxin-transformer are likely to require all implementations of {@link BytecodeProvider}
 * to also implement {@link CodeSourceURIProvider} for maintenance, stability and uniformity reasons.
 *
 * @param <M> The type of the modularity attachment. A recommended type can be {@link ClassLoader},
 * but really anything goes, {@link Void} for example is perfectly fine as long as the {@link BytecodeProvider}
 * accounts for null attachments.
 * @see CodeSourceURIProvider
 */
public interface BytecodeProvider<M> {
    @NotNull
    ClassNode getClassNode(M modularityAttachment, @NotNull String internalName) throws ClassNotFoundException;
}
