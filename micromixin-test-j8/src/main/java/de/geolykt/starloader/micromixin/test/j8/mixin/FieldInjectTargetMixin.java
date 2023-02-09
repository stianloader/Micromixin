package de.geolykt.starloader.micromixin.test.j8.mixin;

import java.util.Random;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import de.geolykt.starloader.micromixin.test.j8.FieldInjectTarget;

@SuppressWarnings("unused")
@Mixin(FieldInjectTarget.class)
public class FieldInjectTargetMixin {

    @Unique
    public String uniqueField = "Hello world!";

    public int answerToEverything = 42;

    private int randomInt = ThreadLocalRandom.current().nextInt();

    private static int sint = 785253;
    private static int srint;
    @Unique
    private static int usrint;

    private final UUID finalUUID = new UUID(ThreadLocalRandom.current().nextLong(), new Random().nextLong());
    private static UUID staticUUID = new UUID(ThreadLocalRandom.current().nextLong(), new Random().nextLong());
    private static final UUID STATIC_FINAL_UUID = new UUID(ThreadLocalRandom.current().nextLong(), new Random().nextLong());
    @Unique
    private static final UUID UNIQUE_STATIC_FINAL_UUID = new UUID(ThreadLocalRandom.current().nextLong(), new Random().nextLong());

    static {
        srint = new SplittableRandom().nextInt();
        usrint = new Random(48).nextInt();
    }
}
