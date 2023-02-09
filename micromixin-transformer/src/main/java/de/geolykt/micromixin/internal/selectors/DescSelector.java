package de.geolykt.micromixin.internal.selectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.MixinStub;
import de.geolykt.micromixin.internal.annotation.MixinDescAnnotation;

public class DescSelector implements MixinTargetSelector {

    @NotNull
    private final MixinDescAnnotation desc;

    public DescSelector(@NotNull MixinDescAnnotation desc) {
        this.desc = desc;
    }

    @Override
    @Nullable
    public MethodNode selectMethod(@NotNull ClassNode within, @NotNull MixinStub source) {
        if (this.desc.target.desc.codePointAt(0) != '(' || !(this.desc.target.owner.equals(within.name) || this.desc.target.owner.equals(source.sourceNode.name))) {
            return null;
        }
        for (MethodNode method : within.methods) {
            if (this.desc.target.desc.equals(method.desc)
                    && this.desc.target.name.equals(method.name)) {
                return method;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DescSelector[desc = " + this.desc + "]";
    }
}
