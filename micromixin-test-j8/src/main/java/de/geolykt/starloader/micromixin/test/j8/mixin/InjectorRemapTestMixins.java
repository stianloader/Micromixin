package de.geolykt.starloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.geolykt.starloader.micromixin.test.j8.targets.InjectorRemapTest;

@Mixin(InjectorRemapTest.class)
public class InjectorRemapTestMixins {

    @Overwrite(aliases = "aliasedReturnStatic")
    private static int returnStatic() {
        return 1;
    }

    @Overwrite(aliases = "aliasedReturnInstance")
    private int returnInstance() {
        return 1;
    }

    @Shadow(aliases = "aliasedReturnStatic")
    private static int aliasingShadowMethodStatic() {
        return -2;
    }

    @Shadow(aliases = "aliasedReturnInstance")
    private int aliasingShadowMethodInstance() {
        return -2;
    }

    @Shadow(aliases = "aliasedVoidStatic")
    private static void aliasingVoidShadowStatic() {
    }

    @Shadow(aliases = "aliasedVoidInstance")
    private void aliasingVoidShadowInstance() {
    }

    @Shadow
    private static void shadow$aliasedVoidStatic() {
    }

    @Shadow(prefix = "explicitShadow$")
    private static void explicitShadow$aliasedVoidStatic() {
    }

    @Shadow
    private void shadow$aliasedVoidInstance() {
    }

    @Shadow(prefix = "explicitShadow$")
    private void explicitShadow$aliasedVoidInstance() {
    }

    @Overwrite(aliases = "aliasedVoidStatic")
    private static void voidStatic() {
    }

    @Overwrite(aliases = "aliasedVoidInstance")
    private void voidInstance() {
    }

    // --- Instance tests ---

    @Inject(at = @At("HEAD"), cancellable = true, method = "runInjectorInstance0")
    private void injectInstance0(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "runInjectorInstance1()I")
    private void injectInstance1(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc("runInjectorInstance2"))
    private void injectInstance2(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(value = "runInjectorInstance3", ret = int.class))
    private void injectInstance3(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorInstance4()I")
    private void injectInstance4(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorInstance5", ret = int.class))
    private void injectInstance5(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorInstance6()I")
    private void injectInstance6(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorInstance7", ret = int.class))
    private void injectInstance7(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorInstance8()I")
    private void injectInstance8(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(aliasingShadowMethodInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorInstance9", ret = int.class))
    private void injectInstance9(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(aliasingShadowMethodInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorInstanceV0()V")
    private void injectInstanceV0(CallbackInfo ci) {
        aliasingShadowMethodInstance();
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorInstanceV1"))
    private void injectInstanceV1(CallbackInfo ci) {
        voidInstance();
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorInstanceV2()V")
    private void injectInstanceV2(CallbackInfo ci) {
        shadow$aliasedVoidInstance();
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorInstanceV3", ret = void.class))
    private void injectInstanceV3(CallbackInfo ci) {
        explicitShadow$aliasedVoidInstance();
        ci.cancel();
    }

    // --- Static tests ---

    @Inject(at = @At("HEAD"), cancellable = true, method = "runInjectorStatic0")
    private static void injectStatic0(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "runInjectorStatic1()I")
    private static void injectStatic1(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(value = "runInjectorStatic2"))
    private static void injectStatic2(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(value = "runInjectorStatic3", ret = int.class))
    private static void injectStatic3(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorStatic4()I")
    private static void injectStatic4(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorStatic5", ret = int.class))
    private static void injectStatic5(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(returnStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorStatic6()I")
    private static void injectStatic6(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(new InjectorRemapTestMixins().returnInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorStatic7", ret = int.class))
    private static void injectStatic7(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(new InjectorRemapTestMixins().returnInstance());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorStatic8()I")
    private static void injectStatic8(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(aliasingShadowMethodStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorStatic9", ret = int.class))
    private static void injectStatic9(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(aliasingShadowMethodStatic());
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorStaticV0()V")
    private static void injectStaticV0(CallbackInfo ci) {
        aliasingVoidShadowStatic();
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorStaticV1"))
    private static void injectStaticV1(CallbackInfo ci) {
        voidStatic();
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "Lde/geolykt/starloader/micromixin/test/j8/targets/InjectorRemapTest;runInjectorStaticV2()V")
    private static void injectStaticV2(CallbackInfo ci) {
        shadow$aliasedVoidStatic();
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), cancellable = true, target = @Desc(owner = InjectorRemapTest.class, value = "runInjectorStaticV3", ret = void.class))
    private static void injectStaticV3(CallbackInfo ci) {
        explicitShadow$aliasedVoidStatic();
        ci.cancel();
    }
}
