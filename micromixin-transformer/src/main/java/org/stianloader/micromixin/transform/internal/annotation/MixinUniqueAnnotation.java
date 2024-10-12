package org.stianloader.micromixin.transform.internal.annotation;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.transform.api.MixinLoggingFacade;
import org.stianloader.micromixin.transform.api.SimpleRemapper;
import org.stianloader.micromixin.transform.internal.ClassMemberStub;
import org.stianloader.micromixin.transform.internal.MixinFieldStub;
import org.stianloader.micromixin.transform.internal.MixinParseException;
import org.stianloader.micromixin.transform.internal.util.ASMUtil;

public class MixinUniqueAnnotation<T extends ClassMemberStub> extends AbstractOverlayAnnotation<T> {

    // TODO Now I see why Mixin uses the blackboard and why it is mandatory. As of now it isn't something that is necessarily
    // required, but it might improve stability in rare edge cases (mostly caused by layered transformation) and bad luck
    // We likely want to store the UID counter globally in order to nullify all chances of UID collisions.
    private static final AtomicLong UID_COUNTER = new AtomicLong(new Random().nextInt() + (1L << 32));

    private final boolean silent;

    @NotNull
    private final String uniquePrefix = "$unique$" + Long.toHexString(MixinUniqueAnnotation.UID_COUNTER.getAndIncrement()) + "$";

    private MixinUniqueAnnotation(boolean silent, @NotNull MixinLoggingFacade logger) {
        super(logger);
        this.silent = silent;
    }

    @NotNull
    public static <T0 extends ClassMemberStub> MixinUniqueAnnotation<T0> parse(@NotNull AnnotationNode annotation, @NotNull MixinLoggingFacade logger) {
        boolean silent = false;
        if (annotation.values != null) {
            for (int i = 0; i < annotation.values.size(); i += 2) {
                String name = (String) annotation.values.get(i);
                if (name.equals("silent")) {
                    silent = (Boolean) annotation.values.get(i + 1);
                } else {
                    throw new MixinParseException("Unimplemented option for @Unique: " + name);
                }
            }
        }
        return new MixinUniqueAnnotation<T0>(silent, logger);
    }

    @Override
    public void collectMappings(@NotNull T source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        if (source instanceof MixinFieldStub && (((MixinFieldStub) source).field.access & Opcodes.ACC_PUBLIC) != 0) {
            return; // @Unique does nothing on public fields
        }
        super.collectMappings(source, target, remapper, sharedBuilder);
    }

    @Override
    public boolean handleCollision(@NotNull T source, @NotNull ClassNode target, int access) {
        if ((source.getAccess() & Opcodes.ACC_PUBLIC) != 0) {
            if (!this.silent) {
                this.logger.warn(MixinUniqueAnnotation.class, "{}.{} {} is public and annotated through @Unique. Furthermore, it collides with something already present in the class and as such the original mapping was discarded. Use silent=true in the @Unique annotation to supress this warning", source.getOwner().name, source.getName(), source.getDesc());
            }
            return false;
        }
        // IMPLEMENT UID Regeneration.
        this.logger.error(MixinUniqueAnnotation.class, "Unable to transform  {}.{} {} due to @Unique collision. Ensure that you do not have multiple Micromixin instances running.", source.getOwner().name, source.getName(), source.getDesc());
        throw new UnsupportedOperationException("Unique prefix (\"" + this.uniquePrefix + "\") does not suffice. Note: restarting the application usually suffices as that regenerates the unique prefix for the member.");
    }

    @Override
    @NotNull
    public String getDesiredName(@NotNull T source, @NotNull ClassNode target,
            @NotNull SimpleRemapper remapper, @NotNull StringBuilder sharedBuilder) {
        String name = source.getName();
        String desc = source.getDesc();
        if (desc.charAt(0) == '(') {
            // Method
            if ((source.getAccess() & Opcodes.ACC_PUBLIC) != 0) {
                // Collision handling is done in #handleCollision
                return name;
            }
            desc = remapper.getRemappedMethodDescriptor(desc, sharedBuilder);
            if (!ASMUtil.hasMethod(target, name, desc)) {
                return name;
            }
        } else {
            // Field
            if ((source.getAccess() & Opcodes.ACC_PUBLIC) != 0) {
                return name;
            }
            desc = remapper.getRemappedFieldDescriptor(desc, sharedBuilder);
            if (!ASMUtil.hasField(target, name, desc)) {
                return name;
            }
        }
        return uniquePrefix + name;
    }
}
