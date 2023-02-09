package de.geolykt.micromixin.internal.selectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.MixinStub;

public class StringSelector implements MixinTargetSelector {

    @Nullable
    private final String owner;
    @Nullable
    private final String name;
    @Nullable
    private final String desc;

    public StringSelector(@NotNull String text) {
        // TODO parse that stuff with equals. And apparently regex is also supported?
        int semicolonIndex = text.indexOf(';');
        int descStartIndex = text.indexOf('(');
        int endName;
        int startName;
        if (semicolonIndex != -1 && (descStartIndex == -1 || semicolonIndex < descStartIndex)) {
            this.owner = text.substring(1, semicolonIndex);
            startName = semicolonIndex + 1;
        } else {
            this.owner = null;
            startName = 0;
        }
        if (descStartIndex == -1) {
            this.desc = null;
            endName = text.length();
        } else {
            this.desc = text.substring(descStartIndex);
            endName = descStartIndex;
        }
        if (endName > startName) {
            this.name = text.substring(startName, endName);
        } else {
            this.name = null;
        }
    }

    @Override
    @Nullable
    public MethodNode selectMethod(@NotNull ClassNode within, @NotNull MixinStub source) {
        for (MethodNode method : within.methods) {
            if (this.name != null && !method.name.equals(this.name)) {
                continue;
            }
            if (this.desc != null && !method.desc.equals(this.desc)) {
                continue;
            }
            if (this.owner != null && !within.name.equals(this.owner)) {
                continue;
            }
            return method;
        }
        return null;
    }

    @Override
    public String toString() {
        return "StringSelector[owner = " + this.owner + ", name = " + name + ", desc = " + desc + "]";
    }
}
