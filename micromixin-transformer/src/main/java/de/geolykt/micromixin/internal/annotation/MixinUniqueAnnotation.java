package de.geolykt.micromixin.internal.annotation;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import de.geolykt.micromixin.internal.ClassMemberStub;
import de.geolykt.micromixin.internal.HandlerContextHelper;
import de.geolykt.micromixin.internal.MixinFieldStub;
import de.geolykt.micromixin.internal.MixinMethodStub;
import de.geolykt.micromixin.internal.MixinParseException;
import de.geolykt.micromixin.internal.MixinStub;
import de.geolykt.micromixin.internal.util.Remapper;

public class MixinUniqueAnnotation<T extends ClassMemberStub> extends AbstractOverlayAnnotation<T> {

    private final boolean silent;

    // TODO Now I see why Mixin uses the blackboard and why it is mandatory. As of now it isn't something that is necessarily
    // required, but it might improve stability in rare edge cases (mostly caused by layered transformation) and bad luck
    // We likely want to store the UID counter globally in order to nullify all chances of UID collisions.
    private static final AtomicLong UID_COUNTER = new AtomicLong(ThreadLocalRandom.current().nextLong(Integer.MAX_VALUE));
    private final String uniquePrefix = "$unique$" + Long.toHexString(UID_COUNTER.getAndIncrement());

    private MixinUniqueAnnotation(boolean silent) {
        this.silent = silent;
    }

    @NotNull
    public static <T0 extends ClassMemberStub> MixinUniqueAnnotation<T0> parse(@NotNull AnnotationNode annotation) {
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
        return new MixinUniqueAnnotation<>(silent);
    }

    @Override
    public void collectMappings(@NotNull T source, @NotNull ClassNode target,
            @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        if (source instanceof MixinFieldStub && (((MixinFieldStub) source).field.access & Opcodes.ACC_PUBLIC) != 0) {
            return; // @Unique does nothing on public fields
        }
        super.collectMappings(source, target, remapper, sharedBuilder);
    }

    @Override
    public void apply(@NotNull ClassNode to, @NotNull HandlerContextHelper hctx,
            @NotNull MixinStub sourceStub, @NotNull T source, @NotNull Remapper remapper,
            @NotNull StringBuilder sharedBuilder) {
        if (source instanceof MixinMethodStub) {
            if ((((MixinMethodStub) source).method.access & Opcodes.ACC_PUBLIC) != 0 && !silent) {
                // FIXME Use proper logging mechanisms instead
                System.err.println("[WARNING:MM/MUA]: @Unique-annotated method is public. @Unique will have no effect. Affected method: " + source.getOwner().name + "." + source.getName() + source.getDesc());
            }
        } else if (source instanceof MixinFieldStub && (((MixinFieldStub) source).field.access & Opcodes.ACC_PUBLIC) != 0) {
            return; // @Unique does nothing on public fields
        }
        super.apply(to, hctx, sourceStub, source, remapper, sharedBuilder);
    }

    @Override
    public boolean allowOverwrite(@NotNull T source, @NotNull ClassNode target) {
        return false;
    }

    @Override
    @NotNull
    public String getDesiredName(@NotNull T source, @NotNull ClassNode target,
            @NotNull Remapper remapper, @NotNull StringBuilder sharedBuilder) {
        String name = source.getName();
        String desc = source.getDesc();
        if (desc.charAt(0) == '(') {
            // Method
            desc = remapper.getRemappedMethodDescriptor(desc, sharedBuilder);
            // TODO Is the prefix really optional?
            if (!AnnotationUtil.hasMethod(target, name, desc)) {
                return name;
            }
        } else {
            // Field
            desc = remapper.getRemappedFieldDescriptor(desc, sharedBuilder);
            if (!AnnotationUtil.hasField(target, name, desc)) {
                return name;
            }
        }
        return uniquePrefix + name;
    }

    @Override
    protected void handleAlreadyExisting(@NotNull T source, @NotNull ClassNode target) {
        // IMPLEMENT UID Regeneration.
        // TODO Proper logging mechanisms
        System.err.println("[ERROR:MM/MUA]: Unable to transform class " + target.name + " due to @Unique collision. Ensure that you do not have multiple Micromixin instances running.");
        throw new UnsupportedOperationException("Unique prefix (\"" + uniquePrefix + "\") does not suffice. Note: restarting the application usually suffices as that regenerates the unique prefix for the member.");
    }
}
