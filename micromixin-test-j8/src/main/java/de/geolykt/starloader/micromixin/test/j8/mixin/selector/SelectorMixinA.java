package de.geolykt.starloader.micromixin.test.j8.mixin.selector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.geolykt.starloader.micromixin.test.j8.selector.SelectorTestA;

@Mixin(SelectorTestA.class)
public class SelectorMixinA {

    @Inject(at = @At(value = "CONSTANT", args = "stringValue=MyConstant"), method = {"constantProvider0([Ljava/lang/String;)V", "constantProvider1"}, require = 7)
    public void selectorTestA(CallbackInfo ci) {
        
    }
    @Inject(at = {@At(value = "RETURN"), @At("HEAD")}, method = "targetMultiA", require = 2)
    public void testInjectMultiAt(CallbackInfo ci) {
    }
    @Inject(at = {@At("RETURN"), @At("TAIL")}, method = "targetMultiDuplicated", require = 1)
    public void testInjectMultiAtDup(CallbackInfo ci) {
        System.out.println("This shouldn't appear more than once!");
    }
}
