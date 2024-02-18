package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.stianloader.micromixin.test.j8.targets.ModifyConstantAuxiliaryTest;

@Mixin(ModifyConstantAuxiliaryTest.class)
public class ModifyConstantAuxiliaryTestMixins {

    @ModifyConstant(constant = @Constant(classValue = void.class), method = "untargetableConstantVoidExplicit")
    private static Class<?> untargetableConstantVoidExplicit(Class<?> val) {
        return null;
    }

    @ModifyConstant(method = "untargetableConstantVoidImplicit")
    private static Class<?> untargetableConstantVoidImplicit(Class<?> val) {
        return null;
    }

    @ModifyConstant(constant = @Constant(classValue = int.class), method = "untargetableConstantIntExplicit")
    private static Class<?> untargetableConstantIntExplicit(Class<?> val) {
        return null;
    }

    @ModifyConstant(method = "untargetableConstantIntImplicit")
    private static Class<?> untargetableConstantIntImplicit(Class<?> val) {
        return null;
    }

    @ModifyConstant(constant = @Constant(classValue = ModifyConstantAuxiliaryTest.class), method = "targetableConstantSelfExplicit")
    private static Class<?> targetableConstantSelfExplicit(Class<?> val) {
        return null;
    }

    @ModifyConstant(method = "targetableConstantSelfImplicit")
    private static Class<?> targetableConstantSelfImplicit(Class<?> val) {
        return null;
    }

    //@ModifyConstant(method = "slicedConstant0", slice = @Slice(from = @At(value = "HEAD"), to = @At(value = "CONSTANT", args = "intValue=3")))
    @ModifyConstant(method = "slicedConstant0", slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=3"), to = @At(value = "CONSTANT", args = "intValue=3")))
    private static int slicedConstant0(int val) {
        return val * 9;
    }

    @ModifyConstant(method = "slicedConstant1", slice = @Slice(from = @At(value = "CONSTANT", args = "intValue=3"), to = @At(value = "CONSTANT", args = "intValue=3")), constant = @Constant(intValue = 3))
    private static int slicedConstant1(int val) {
        return val * 9;
    }

    @ModifyConstant(target = @Desc("captureMultipleImplicit0"))
    private static int captureMultipleImplicit0(int val) {
        return val * 3;
    }

    @ModifyConstant(target = @Desc("captureMultipleExplicit0"), constant = { @Constant(intValue = 3) })
    private static int captureMultipleExplicit0(int val) {
        return val * 3;
    }

    @ModifyConstant(target = @Desc("captureMultipleExplicit0B"), constant = { })
    private static int captureMultipleExplicit0B(int val) {
        return val * 3;
    }

    @ModifyConstant(target = @Desc("captureMultipleImplicit1"))
    private static int captureMultipleImplicit1(int val) {
        return val * 3;
    }

    @ModifyConstant(target = @Desc("captureMultipleExplicit1"), constant = { @Constant(intValue = 3) })
    private static int captureMultipleExplicit1(int val) {
        return val * 3;
    }

    // Note: Even though below constant modifiers do not match anything, they are still included in the unit
    // tests to ensure that this behaviour does not change or is otherwise altered.
    // Note2: They target "==" or "!=" instructions, but "<= 0", ">= 0", "< 0" and "> 0" would still apply (though micromixin does not implement these)

    @ModifyConstant(target = @Desc(value = "captureIfZ0", args = boolean.class), constant = { @Constant(intValue = 0) })
    private int captureIfZ0(int val) {
        return val * 3;
    }

    @ModifyConstant(target = @Desc(value = "captureIfZ1", args = boolean.class), constant = { @Constant(intValue = 1) })
    private int captureIfZ1(int val) {
        return val * 3;
    }

    @ModifyConstant(target = @Desc(value = "captureIfI0", args = boolean.class), constant = { @Constant(intValue = 0) })
    private int captureIfI0(int val) {
        return val * 3;
    }

    @ModifyConstant(target = @Desc(value = "captureIfI1", args = boolean.class), constant = { @Constant(intValue = 0) })
    private int captureIfI1(int val) {
        return val * 3;
    }

    @ModifyConstant(target = @Desc(value = "captureIfI2", args = boolean.class), constant = { @Constant(intValue = 8) })
    private int captureIfI2(int val) {
        return val * 3;
    }

    @ModifyConstant(target = @Desc(value = "captureIfI3", args = boolean.class), constant = { @Constant(intValue = 8) })
    private int captureIfI3(int val) {
        return val * 3;
    }
}
