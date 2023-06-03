package de.geolykt.starloader.micromixin.test.j8.mixin.redirect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;

import de.geolykt.starloader.micromixin.test.j8.targets.redirect.GenericInvokeTarget;
import de.geolykt.starloader.micromixin.test.j8.targets.redirect.GenericInvoker;

@Mixin(GenericInvoker.class)
public class GenericInvokerMixins {

    // TODO all static handlers must be private

    @Redirect(target = @Desc(value = "callForeignInstanced"), at = @At(value = "INVOKE", target = "assertCalledIV"))
    public void redirect$assertCalledIV(GenericInvokeTarget target, int value) {
        // NOP
    }

    @Redirect(target = @Desc(value = "callForeignInstanced"), at = @At(value = "INVOKE", desc = @Desc(value = "assertCalledVV", owner = GenericInvokeTarget.class, args = {})), require = 1)
    public void redirect$assertCalledVV(GenericInvokeTarget target) {
        // NOP
    }

    @Redirect(target = @Desc(value = "callForeignInstanced"), at = @At(value = "INVOKE", target = "assertCalledStaticIV"))
    public void redirect$assertCalledStaticIV(int value) {
        // NOP
    }

    @Redirect(target = @Desc(value = "callForeignInstanced"), at = @At(value = "INVOKE", target = "assertCalledStaticVV"))
    public void redirect$assertCalledStaticVV() {
        // NOP
    }


    // ---


    @Redirect(target = @Desc(value = "callForeignStatically"), at = @At(value = "INVOKE", target = "assertCalledIV"))
    private static void sredirect$assertCalledIV(GenericInvokeTarget target, int value) {
        // NOP
    }

    @Redirect(target = @Desc(value = "callForeignStatically"), at = @At(value = "INVOKE", desc = @Desc(value = "assertCalledVV", owner = GenericInvokeTarget.class)))
    private static void sredirect$assertCalledVV(GenericInvokeTarget targe) {
        // NOP
    }

    @Redirect(target = @Desc(value = "callForeignStatically"), at = @At(value = "INVOKE", target = "assertCalledStaticIV"))
    private static void sredirect$assertCalledStaticIV(int value) {
        // NOP
    }

    @Redirect(target = @Desc(value = "callForeignStatically"), at = @At(value = "INVOKE", target = "assertCalledStaticVV"))
    private static void sredirect$assertCalledStaticVV() {
        // NOP
    }


    // ---


    @Redirect(target = @Desc(value = "getObjectStatic0", ret = Object.class), at = @At(value = "INVOKE", desc = @Desc(value = "return0Static", owner = GenericInvokeTarget.class, ret = int.class)), require = 1)
    private int redirect$getObjectStatic0() {
        return 1;
    }

    @Redirect(target = @Desc(value = "getObjectStatic1", ret = Object.class), at = @At(value = "INVOKE", desc = @Desc(value = "returnNullStatic", owner = GenericInvokeTarget.class, ret = Object.class)), require = 1)
    private Object redirect$getObjectStatic1() {
        return new Object();
    }

    @Redirect(target = @Desc(value = "getObjectInstanced0", ret = Object.class), at = @At(value = "INVOKE", desc = @Desc(value = "return0Instanced", owner = GenericInvokeTarget.class, ret = int.class)), require = 1)
    private int redirect$getObjectInstanced0(GenericInvokeTarget target) {
        return 1;
    }

    @Redirect(target = @Desc(value = "getObjectInstanced1", ret = Object.class), at = @At(value = "INVOKE", desc = @Desc(value = "returnNullInstanced", owner = GenericInvokeTarget.class, ret = Object.class)), require = 1)
    private Object redirect$getObjectInstanced1(GenericInvokeTarget target) {
        return new Object();
    }

    @Redirect(target = @Desc(value = "getIntStatic", ret = int.class), at = @At(value = "INVOKE", desc = @Desc(value = "return0Static", owner = GenericInvokeTarget.class, ret = int.class)), require = 1)
    private int redirect$getIntStatic() {
        return 1;
    }

    @Redirect(target = @Desc(value = "getIntInstanced", ret = int.class), at = @At(value = "INVOKE", desc = @Desc(value = "return0Instanced", owner = GenericInvokeTarget.class, ret = int.class)), require = 1)
    private int redirect$getIntInstanced(GenericInvokeTarget target) {
        return 1;
    }
}
