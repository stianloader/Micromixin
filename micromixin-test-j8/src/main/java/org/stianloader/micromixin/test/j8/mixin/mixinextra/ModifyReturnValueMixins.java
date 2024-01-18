/* following code is generated - do not touch directly. */
package org.stianloader.micromixin.test.j8.mixin.mixinextra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTest;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(value = ModifyReturnValueTest.class, priority = 100)
public class ModifyReturnValueMixins {
    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteI" })
    private static int identityOverwriteI (int val) {
        return -957256832;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteJ" })
    private static long identityOverwriteJ (long val) {
        return Long.MAX_VALUE;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteS" })
    private static short identityOverwriteS (short val) {
        return ((short) -58);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteC" })
    private static char identityOverwriteC (char val) {
        return 'c';
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteB" })
    private static byte identityOverwriteB (byte val) {
        return ((byte) 126);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteZ" })
    private static boolean identityOverwriteZ (boolean val) {
        return true;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteF" })
    private static float identityOverwriteF (float val) {
        return 1.2F;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteD" })
    private static double identityOverwriteD (double val) {
        return 2.5D;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteNul" })
    private static Object identityOverwriteNul (Object val) {
        return Boolean.TRUE;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteNul2" })
    private static Object identityOverwriteNul2 (Object val) {
        return null;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteStr" })
    private static String identityOverwriteStr (String val) {
        return "Test2";
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteNulStr" })
    private static String identityOverwriteNulStr (String val) {
        return "non-null";
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteNulStr2" })
    private static String identityOverwriteNulStr2 (String val) {
        return null;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteEmptyStr" })
    private static String identityOverwriteEmptyStr (String val) {
        return "";
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteClass" })
    private static Class<?> identityOverwriteClass (Class<?> val) {
        return int.class;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteNulClass" })
    private static Class<?> identityOverwriteNulClass (Class<?> val) {
        return Object.class;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityOverwriteNulClass2" })
    private static Class<?> identityOverwriteNulClass2 (Class<?> val) {
        return null;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughI" })
    private static int identityPassthroughI (int val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughJ" })
    private static long identityPassthroughJ (long val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughS" })
    private static short identityPassthroughS (short val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughC" })
    private static char identityPassthroughC (char val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughB" })
    private static byte identityPassthroughB (byte val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughZ" })
    private static boolean identityPassthroughZ (boolean val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughF" })
    private static float identityPassthroughF (float val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughD" })
    private static double identityPassthroughD (double val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughNul" })
    private static Object identityPassthroughNul (Object val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughNul2" })
    private static Object identityPassthroughNul2 (Object val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughStr" })
    private static String identityPassthroughStr (String val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughNulStr" })
    private static String identityPassthroughNulStr (String val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughNulStr2" })
    private static String identityPassthroughNulStr2 (String val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughEmptyStr" })
    private static String identityPassthroughEmptyStr (String val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughClass" })
    private static Class<?> identityPassthroughClass (Class<?> val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughNulClass" })
    private static Class<?> identityPassthroughNulClass (Class<?> val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "identityPassthroughNulClass2" })
    private static Class<?> identityPassthroughNulClass2 (Class<?> val) {
        return val;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteI" })
    public int virtualIdentityOverwriteI (int val) {
        return -957256832;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteJ" })
    public long virtualIdentityOverwriteJ (long val) {
        return Long.MAX_VALUE;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteS" })
    public short virtualIdentityOverwriteS (short val) {
        return ((short) -58);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteC" })
    public char virtualIdentityOverwriteC (char val) {
        return 'c';
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteB" })
    public byte virtualIdentityOverwriteB (byte val) {
        return ((byte) 126);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteZ" })
    public boolean virtualIdentityOverwriteZ (boolean val) {
        return true;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteF" })
    public float virtualIdentityOverwriteF (float val) {
        return 1.2F;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteD" })
    public double virtualIdentityOverwriteD (double val) {
        return 2.5D;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteNul" })
    public Object virtualIdentityOverwriteNul (Object val) {
        return Boolean.TRUE;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteNul2" })
    public Object virtualIdentityOverwriteNul2 (Object val) {
        return null;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteStr" })
    public String virtualIdentityOverwriteStr (String val) {
        return "Test2";
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteNulStr" })
    public String virtualIdentityOverwriteNulStr (String val) {
        return "non-null";
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteNulStr2" })
    public String virtualIdentityOverwriteNulStr2 (String val) {
        return null;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteEmptyStr" })
    public String virtualIdentityOverwriteEmptyStr (String val) {
        return "";
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteClass" })
    public Class<?> virtualIdentityOverwriteClass (Class<?> val) {
        return int.class;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteNulClass" })
    public Class<?> virtualIdentityOverwriteNulClass (Class<?> val) {
        return Object.class;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "virtualIdentityOverwriteNulClass2" })
    public Class<?> virtualIdentityOverwriteNulClass2 (Class<?> val) {
        return null;
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2I" })
    private static int mul3add2I (int val) {
        return (int) (val * 3);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2J" })
    private static long mul3add2J (long val) {
        return (long) (val * 3);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2S" })
    private static short mul3add2S (short val) {
        return (short) (val * 3);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2C" })
    private static char mul3add2C (char val) {
        return (char) (val * 3);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2B" })
    private static byte mul3add2B (byte val) {
        return (byte) (val * 3);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2F" })
    private static float mul3add2F (float val) {
        return (float) (val * 3);
    }

    @ModifyReturnValue(at = @At("TAIL"), method = { "mul3add2D" })
    private static double mul3add2D (double val) {
        return (double) (val * 3);
    }

}