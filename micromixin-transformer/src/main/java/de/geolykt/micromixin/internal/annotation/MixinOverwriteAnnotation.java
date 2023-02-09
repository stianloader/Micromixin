package de.geolykt.micromixin.internal.annotation;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.HandlerContextHelper;
import de.geolykt.micromixin.internal.MixinMethodStub;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.MixinStub;

public class MixinOverwriteAnnotation implements MixinAnnotation<MixinMethodStub> {

    @NotNull
    private final String sourceClass;
    @NotNull
    private final String descriptor;
    @NotNull
    private final String name;
    @NotNull
    private final MethodNode method;
    @Nullable
    private final List<String> aliases;

    public MixinOverwriteAnnotation(@NotNull String sourceClass, @NotNull String descriptor, @NotNull String name, @NotNull MethodNode method, @Nullable List<String> aliases) {
        this.sourceClass = sourceClass;
        this.descriptor = descriptor;
        this.name = name;
        this.method = method;
        this.aliases = aliases;
    }

    @NotNull
    public static MixinOverwriteAnnotation generateImplicit(@NotNull ClassNode node, @NotNull MethodNode method) {
        return new MixinOverwriteAnnotation(node.name, method.desc, method.name, method, null);
    }

    @NotNull
    public static MixinOverwriteAnnotation parse(@NotNull ClassNode node, @NotNull MethodNode method, @NotNull AnnotationNode annot) {
        List<String> aliases = null;
        if (annot.values != null) {
            for (int i = 0; i < annot.values.size(); i += 2) {
                String name = (String) annot.values.get(i);
                Object val = annot.values.get(i + 1);
                if (name.equals("aliases")) {
                    @SuppressWarnings("unchecked")
                    List<String> aval = (List<String>) val;
                    aliases = aval;
                } else {
                    throw new MixinParseException("Unimplemented key in @Overwrite: " + name);
                }
            }
        }
        return new MixinOverwriteAnnotation(node.name, method.desc, method.name, method, aliases);
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull MixinMethodStub source,
            de.geolykt.micromixin.internal.util.@NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        List<String> aliases = this.aliases;
        if (aliases == null) {
            MethodNode injected = null;
            for (MethodNode method : to.methods) {
                if (method.name.equals(this.name) && method.desc.equals(this.descriptor)) {
                    injected = method;
                    break;
                }
            }
            if (injected == null) {
                injected = new MethodNode();
                injected.name = this.name;
                injected.desc = this.descriptor; // TODO does this need to be remapped?
                injected.access = this.method.access;
            }
            injected.instructions.clear();
            MethodRemapper methodRemapper = new MethodRemapper(injected, new Remapper() {
                @Override
                public String map(String internalName) {
                    if (internalName.equals(MixinOverwriteAnnotation.this.sourceClass)) {
                        return to.name;
                    }
                    return internalName;
                }
            });
            this.method.accept(methodRemapper);
            if (injected.invisibleAnnotations != null) {
                injected.invisibleAnnotations.removeIf((annot) -> {
                    return annot.desc.equals("Lorg/spongepowered/asm/mixin/Overwrite;");
                });
            }
            to.methods.add(injected);
        } else {
            List<@NotNull String> targetted = new ArrayList<>();
            to.methods.removeIf((method) -> {
                if (method.desc.equals(this.descriptor) // TODO does this need to be remapped?
                        && (method.name.equals(this.name) || aliases.contains(method.name))) {
                    targetted.add(method.name);
                    return true;
                }
                return false;
            });
            if (targetted.isEmpty()) {
                throw new IllegalStateException("Transformed class " + to.name + " cannot be transformed as " + name + descriptor + " is not present under any of it's aliases.");
            }
            for (String name : targetted) {
                MethodNode injected = new MethodNode();
                // FIXME use CCU instead
                MethodRemapper methodRemapper = new MethodRemapper(injected, new Remapper() {
                    @Override
                    public String map(String internalName) {
                        if (internalName.equals(MixinOverwriteAnnotation.this.sourceClass)) {
                            return to.name;
                        }
                        return internalName;
                    }
                });
                this.method.accept(methodRemapper);
                if (injected.invisibleAnnotations != null) {
                    injected.invisibleAnnotations.removeIf((annot) -> {
                        return annot.desc.equals("Lorg/spongepowered/asm/mixin/Overwrite;");
                    });
                }
                injected.name = name;
                injected.desc = this.descriptor; // TODO does this need to be remapped?
                injected.access = this.method.access;
                to.methods.add(injected);
            }
        }
    }

    @Override
    public void collectMappings(@NotNull MixinMethodStub source, @NotNull ClassNode target,
            de.geolykt.micromixin.internal.util.@NotNull Remapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        // NOP
    }
}
