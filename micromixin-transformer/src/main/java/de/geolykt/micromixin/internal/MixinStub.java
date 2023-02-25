package de.geolykt.micromixin.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import de.geolykt.micromixin.internal.util.Remapper;
import de.geolykt.micromixin.supertypes.ClassWrapperPool;

public class MixinStub implements Comparable<MixinStub> {

    @NotNull
    public final MixinHeader header;
    @NotNull
    public final ClassNode sourceNode;
    @NotNull
    public final Collection<MixinMethodStub> methods;
    @NotNull
    public final Collection<MixinFieldStub> fields;

    public MixinStub(@NotNull ClassNode sourceNode, @NotNull MixinHeader header, @NotNull Collection<MixinMethodStub> methods, @NotNull Collection<MixinFieldStub> fields) {
        this.sourceNode = sourceNode;
        this.header = header;
        this.methods = methods;
        this.fields = fields;
    }

    @NotNull
    public static MixinStub parse(int defaultPriority, @NotNull ClassNode node, @NotNull ClassWrapperPool pool) {
        List<MixinMethodStub> methods = new ArrayList<MixinMethodStub>();
        List<MixinFieldStub> fields = new ArrayList<MixinFieldStub>();
        for (MethodNode method : node.methods) {
            if (method == null) {
                throw new NullPointerException();
            }
            MixinMethodStub methodStub = MixinMethodStub.parse(node, method, pool);
            methods.add(methodStub);
        }
        for (FieldNode field : node.fields) {
            if (field == null) {
                throw new NullPointerException();
            }
            fields.add(MixinFieldStub.parse(node, field));
        }
        return new MixinStub(node, MixinHeader.parse(node, defaultPriority), Collections.unmodifiableCollection(methods), Collections.unmodifiableCollection(fields));
    }

    @Override
    public int compareTo(MixinStub o) {
        return this.header.priority - o.header.priority; // TODO is this correct?
    }

    public void applyTo(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx) {
        // Perform basic validation
        for (MixinMethodStub methodStub : this.methods) {
            if (methodStub.method.name.startsWith(hctx.handlerPrefix)) {
                throw new IllegalStateException("Names of Mixin methods may not start with the handler prefix (which is " + hctx.handlerPrefix + ")");
            }
        }
        StringBuilder sharedBuilder = new StringBuilder();
        Remapper remapper = getRemapper(target, hctx, sharedBuilder);
        for (MixinFieldStub stub : this.fields) {
            stub.applyTo(target, hctx, this, remapper, sharedBuilder);
        }
        for (MixinMethodStub stub : this.methods) {
            stub.applyTo(target, hctx, this, remapper, sharedBuilder);
        }
        // Merge interfaces
        for (String itf : this.sourceNode.interfaces) {
            if (!target.interfaces.contains(itf)) {
                target.interfaces.add(itf);
            }
        }
    }

    @NotNull
    public Remapper getRemapper(@NotNull ClassNode targetClass, @NotNull HandlerContextHelper hctx, @NotNull StringBuilder sharedBuilder) {
        Remapper r = new Remapper();
        // TODO That probably doesn't work when the source class is an interface.
        // We may need to analyse the proper behaviour there
        r.remapClassName(this.sourceNode.name, targetClass.name);
        // Note: Before calling the #collectMappings methods, the class mappings should be known (as it greatly simplifies the method/field lookup process)
        for (MixinFieldStub field : this.fields) {
            field.collectMappings(targetClass, hctx, this, r, sharedBuilder);
        }
        for (MixinMethodStub method : this.methods) {
            method.collectMappings(targetClass, hctx, this, r, sharedBuilder);
        }
        return r;
    }
}
