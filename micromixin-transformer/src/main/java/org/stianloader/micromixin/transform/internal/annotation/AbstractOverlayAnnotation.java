package org.stianloader.micromixin.transform.internal.annotation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.ClassMemberStub;
import org.stianloader.micromixin.transform.internal.HandlerContextHelper;
import org.stianloader.micromixin.transform.internal.MixinFieldStub;
import org.stianloader.micromixin.transform.internal.MixinMethodStub;
import org.stianloader.micromixin.transform.internal.MixinStub;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;
import org.stianloader.micromixin.transform.internal.util.CodeCopyUtil;
import org.stianloader.micromixin.transform.internal.util.Objects;
import org.stianloader.micromixin.transform.internal.util.smap.MultiplexLineNumberAllocator;
import org.stianloader.micromixin.transform.internal.util.smap.NOPMultiplexLineNumberAllocator;

public abstract class AbstractOverlayAnnotation<T extends ClassMemberStub> extends MixinAnnotation<T> {

    @NotNull
    protected final MixinLoggingFacade logger;

    public AbstractOverlayAnnotation(@NotNull MixinLoggingFacade logger) {
        this.logger = logger;
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull T source, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        if (source instanceof MixinMethodStub) {
            this.applyMethod(source, sourceStub, to, remapper, sharedBuilder, hctx.lineAllocator, false);
        } else if (source instanceof MixinFieldStub) {
            this.applyField(source, to, remapper, sharedBuilder, false);
        } else {
            throw new UnsupportedOperationException("Unknown/Unsupported implementation of ClassMemberStub: " + source.getClass().getName());
        }
    }

    private void applyField(@NotNull T source, @NotNull ClassNode target, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder, boolean pre) {
        MixinFieldStub stub = (MixinFieldStub) source;
        String desiredName = this.getDesiredName(source, target, remapper, sharedBuilder);
        String desiredDescMapped = remapper.getRemappedFieldDescriptor(source.getDesc(), sharedBuilder);

        boolean overwrite = false;
        FieldNode overwritten = ASMUtil.getField(target, desiredName, desiredDescMapped);
        if (overwritten != null && !this.handleCollision(source, target, overwritten.access)) {
            return;
        }

        if (pre) {
            // Make the changes "public" to the entire class
            remapper.remapField(source.getOwner().name, source.getDesc(), source.getName(), desiredName);
            return;
        }

        // Remove the old field
        if (overwrite) {
            // TODO How about copying access modifiers and annotations?
            target.fields.remove(overwritten);
        }

        // And create a new one
        FieldNode overlaid = new FieldNode(stub.field.access, desiredName, desiredDescMapped, null, stub.field.value);
        target.fields.add(overlaid);
    }

    @Contract(pure = false, mutates = "param3,param4,param6",
            value = "_, null, _, _, _, _, false -> fail; _, _, _, _, _, _, true -> ")
    private void applyMethod(@NotNull T source, MixinStub sourceStub, @NotNull ClassNode target, @NotNull SimpleRemapper remapper,
            @NotNull StringBuilder sharedBuilder, @NotNull MultiplexLineNumberAllocator lineAllocator, boolean pre) {
        MixinMethodStub stub = (MixinMethodStub) source;
        String desiredName = this.getDesiredName(source, target, remapper, sharedBuilder);
        String desiredDescMapped = remapper.getRemappedMethodDescriptor(source.getDesc(), sharedBuilder);
        MethodNode overwritten = ASMUtil.getMethod(target, desiredName, desiredDescMapped);
        if (overwritten != null && !this.handleCollision(source, target, overwritten.access)) {
            return;
        }

        if (pre) {
            // Make the changes "public" to the entire class
            remapper.remapMethod(source.getOwner().name, source.getDesc(), source.getName(), desiredName);
            return;
        }
        sourceStub = Objects.requireNonNull(sourceStub, "\"sourceStub\" may not be null if \"pre\" is false as per method contract.");

        // Remove the old method
        if (overwritten != null) {
            // TODO How about copying access modifiers (such as synchronised) and annotations?
            // How about exceptions?
            if (!target.methods.remove(overwritten)) {
                throw new IllegalStateException("Tried to remove a method that does not exist.");
            }
        }

        // And create a new one
        String[] exceptions = stub.method.exceptions.toArray(new String[0]);
        for (int i = 0; i < exceptions.length; i++) {
            exceptions[i] = remapper.remapInternalName(Objects.requireNonNull(exceptions[i]), sharedBuilder);
        }
        MethodNode overlaidMethod = new MethodNode(stub.method.access, desiredName, desiredDescMapped, null, exceptions);
        CodeCopyUtil.copyOverwrite(sourceStub, stub.method, target, overlaidMethod, remapper, lineAllocator, this.logger);
        target.methods.add(overlaidMethod);
    }

    @Override
    public void collectMappings(@NotNull T source, @NotNull HandlerContextHelper hctx, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        if (source instanceof MixinMethodStub) {
            this.applyMethod(source, null, target, remapper, sharedBuilder, NOPMultiplexLineNumberAllocator.INSTANCE, true);
        } else if (source instanceof MixinFieldStub) {
            this.applyField(source, target, remapper, sharedBuilder, true);
        } else {
            throw new UnsupportedOperationException("Unknown/Unsupported implementation of ClassMemberStub: " + source.getClass().getName());
        }
    }

    @NotNull
    public abstract String getDesiredName(@NotNull T source, @NotNull ClassNode target, @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder);

    /**
     * Method that is called if the source collides with an already existing member.
     *
     * @param source The source handler
     * @param target The target class
     * @param collidedAccess The access modifiers of the member that already exists.
     * @return True to overwrite the member, false to abort overlaying.
     */
    public abstract boolean handleCollision(@NotNull T source, @NotNull ClassNode target, int collidedAccess);
}
