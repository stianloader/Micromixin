package org.stianloader.micromixin.transform.api;

import java.net.URI;
import java.security.CodeSource;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;

/**
 * An extension to the {@link BytecodeProvider} interface which creates support for
 * obtaining the {@link URI} where a resource (usually a mixin class) is located
 * on the filesystem. The returned URI is akin to {@link CodeSource#getLocation()},
 * that is it only returns the jar or directory in which the resource is located in.
 * This functionality is at this point in time only intended for use in JSR-45 SMAPs
 * (see {@link ClassNode#sourceDebug}) in the &quot;jdt&quot; stratum and is intended
 * to improve debugging support under eclipse when the mixin classes are not present
 * within the source files but rather already compiled on the runtime classpath.
 *
 * @param <M> The modularity attachment to use, see {@link BytecodeProvider}.
 * In general, the modularity attachment maps 1:1 to a given URI, and vice-versa.
 * However that is not guaranteed, as a modularity attachment can at times map
 * several directories (e.g. J8 compiled source, J9 compiled sources and resource
 * directories) to a single module. In order to still be able to differentiate
 * between the individual sources, the name of the class is passed in too.
 *
 * @since 0.7.0-a20241008
 */
@ApiStatus.AvailableSince(value = "0.7.0-a20241008")
public interface CodeSourceURIProvider<M> {
    @Nullable
    URI findURI(M modularityAttachment, @NotNull String internalClassName);
}
