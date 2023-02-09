package de.geolykt.starloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "de/geolykt/starloader/micromixin/test/j8/OverwriteTarget")
public class OverwriteTargetMixin {

    @Overwrite
    public void callOverwrittenMethod() {
        System.out.println("I have overwritten that method!");
    }

    @Overwrite(aliases = {"multiOverwrite0", "multiOverwrite1", "bogusOverwrite"})
    public void multiOverwriter() {
        System.out.println("multiOverwriter()V has overwritten that method!");
    }
}
