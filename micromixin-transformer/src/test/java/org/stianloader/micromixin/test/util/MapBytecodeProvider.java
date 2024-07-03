package org.stianloader.micromixin.test.util;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.BytecodeProvider;
import org.stianloader.micromixin.transform.api.supertypes.ASMClassWrapperProvider;

public class MapBytecodeProvider<M> extends ASMClassWrapperProvider implements BytecodeProvider<M> {

    @NotNull
    private final Map<String, ClassNode> nodes;

    public MapBytecodeProvider(@NotNull Map<String, ClassNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    @NotNull
    public ClassNode getClassNode(M modularityAttachment, @NotNull String internalName) throws ClassNotFoundException {
        ClassNode node = this.nodes.get(internalName);
        if (node == null) {
            throw new ClassNotFoundException("Class '" + internalName + "' could not be located for attachment '" + modularityAttachment + "'.");
        }
        return node;
    }

    @Override
    @Nullable
    public ClassNode getNode(@NotNull String name) {
        return this.nodes.get(name);
    }
}
