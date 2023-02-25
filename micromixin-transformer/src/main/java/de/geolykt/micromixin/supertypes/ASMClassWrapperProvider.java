package de.geolykt.micromixin.supertypes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public abstract class ASMClassWrapperProvider implements ClassWrapperProvider {

    @Nullable
    public abstract ClassNode getNode(@NotNull String name);

    @Override
    @Nullable
    public ClassWrapper provide(@NotNull String name, @NotNull ClassWrapperPool pool) {
        ClassNode node = getNode(name);
        if (node == null) {
            return null;
        }
        String[] interfaces = new String[node.interfaces.size()];
        node.interfaces.toArray(interfaces);
        return new ClassWrapper(node.name, node.superName, interfaces,
                (node.access & Opcodes.ACC_INTERFACE) != 0, pool);
    }
}
