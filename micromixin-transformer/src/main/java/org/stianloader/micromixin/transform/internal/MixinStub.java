package org.stianloader.micromixin.transform.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.MixinTransformer;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.util.Objects;

public class MixinStub implements Comparable<MixinStub> {

    @NotNull
    public static MixinStub parse(int defaultPriority, @NotNull ClassNode node, @NotNull MixinTransformer<?> transformer, @Nullable URI codeSourceURI, @NotNull StringBuilder sharedBuilder) {
        List<MixinMethodStub> methods = new ArrayList<MixinMethodStub>();
        List<MixinFieldStub> fields = new ArrayList<MixinFieldStub>();
        List<MixinParseException> delayedExceptions = null;
        for (MethodNode method : node.methods) {
            if (method == null) {
                throw new NullPointerException();
            }
            try {
                MixinMethodStub methodStub = MixinMethodStub.parse(node, method, transformer, sharedBuilder);
                methods.add(methodStub);
            } catch (MixinParseException e) {
                if (transformer.isDelayingParseExceptions()) {
                    if (delayedExceptions == null) {
                        delayedExceptions = new ArrayList<MixinParseException>();
                    }
                    delayedExceptions.add(e);
                } else {
                    throw e;
                }
            }
        }
        for (FieldNode field : node.fields) {
            if (field == null) {
                throw new NullPointerException();
            }
            try {
                fields.add(MixinFieldStub.parse(node, field, transformer, sharedBuilder));
            } catch (MixinParseException e) {
                if (transformer.isDelayingParseExceptions()) {
                    if (delayedExceptions == null) {
                        delayedExceptions = new ArrayList<MixinParseException>();
                    }
                    delayedExceptions.add(e);
                } else {
                    throw e;
                }
            }
        }
        return new MixinStub(node, MixinHeader.parse(node, defaultPriority), Collections.unmodifiableCollection(methods), Collections.unmodifiableCollection(fields), delayedExceptions, codeSourceURI);
    }

    @Nullable
    @ApiStatus.AvailableSince("0.7.0-a20241008")
    public final URI codeSourceURI;
    @Nullable
    private final List<MixinParseException> delayedExceptions;
    @NotNull
    public final Collection<MixinFieldStub> fields;
    @NotNull
    public final MixinHeader header;
    @NotNull
    public final Collection<MixinMethodStub> methods;
    @NotNull
    public final ClassNode sourceNode;

    public MixinStub(@NotNull ClassNode sourceNode, @NotNull MixinHeader header, @NotNull Collection<MixinMethodStub> methods, @NotNull Collection<MixinFieldStub> fields, @Nullable List<MixinParseException> delayedExceptions, @Nullable URI codeSourceURI) {
        this.sourceNode = sourceNode;
        this.header = header;
        this.methods = methods;
        this.fields = fields;
        this.delayedExceptions = delayedExceptions;
        this.codeSourceURI = codeSourceURI;
    }

    public void applyTo(@NotNull ClassNode target, @NotNull HandlerContextHelper hctx, @NotNull StringBuilder sharedBuilder) {
        List<MixinParseException> delayedExceptions = this.delayedExceptions;
        if (delayedExceptions != null && !delayedExceptions.isEmpty()) {
            Throwable t1 = new IllegalStateException("Some exceptions occured during the parsing process, which is why a mixin cannot be applied");
            Throwable rethrown =  Objects.addSuppressed(t1, (List<? extends Throwable>) delayedExceptions);
            throw (RuntimeException) rethrown;
        }

        SimpleRemapper remapper = getRemapper(target, hctx, sharedBuilder);
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

    @Override
    public int compareTo(MixinStub o) {
        int priority = this.header.priority - o.header.priority;
        if (priority != 0) {
            return priority;
        }
        return this.sourceNode.name.compareTo(o.sourceNode.name); // Under rather rare circumstances this check doesn't suffice, so we will need to improve that check eventually
    }

    @NotNull
    public SimpleRemapper getRemapper(@NotNull ClassNode targetClass, @NotNull HandlerContextHelper hctx, @NotNull StringBuilder sharedBuilder) {
        SimpleRemapper r = new SimpleRemapper();
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

    @Override
    public String toString() {
        return "MixinStub '" + this.sourceNode.name + "'";
    }
}
