/* following code is generated - do not touch directly. */
package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.stianloader.micromixin.test.j8.targets.ModifyArgTest;

@Mixin(value = ModifyArgTest.class, priority = 100)
public class ModifyArgMixins {
    @ModifyArg(method = "testConcatDirectI", at = @At(value = "INVOKE", target = "concatI"), index = 0)
    private static int onConcatDirectI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTI", at = @At(value = "INVOKE", target = "concatI"), index = 0)
    private static int onConcatLVTI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectD", at = @At(value = "INVOKE", target = "concatD"))
    private static double onConcatDirectD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTD", at = @At(value = "INVOKE", target = "concatD"))
    private static double onConcatLVTD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectL", at = @At(value = "INVOKE", target = "concatL"), index = 0)
    private static Object onConcatDirectL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTL", at = @At(value = "INVOKE", target = "concatL"), index = 0)
    private static Object onConcatLVTL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectII", at = @At(value = "INVOKE", target = "concatII"), index = 0)
    private static int onConcatDirectII(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTII", at = @At(value = "INVOKE", target = "concatII"), index = 0)
    private static int onConcatLVTII(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectID", at = @At(value = "INVOKE", target = "concatID"), index = 1)
    private static double onConcatDirectID(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTID", at = @At(value = "INVOKE", target = "concatID"), index = 1)
    private static double onConcatLVTID(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectIL", at = @At(value = "INVOKE", target = "concatIL"))
    private static Object onConcatDirectIL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTIL", at = @At(value = "INVOKE", target = "concatIL"))
    private static Object onConcatLVTIL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectDI", at = @At(value = "INVOKE", target = "concatDI"), index = 0)
    private static double onConcatDirectDI(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDI", at = @At(value = "INVOKE", target = "concatDI"), index = 0)
    private static double onConcatLVTDI(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDD", at = @At(value = "INVOKE", target = "concatDD"), index = 0)
    private static double onConcatDirectDD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDD", at = @At(value = "INVOKE", target = "concatDD"), index = 0)
    private static double onConcatLVTDD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDL", at = @At(value = "INVOKE", target = "concatDL"))
    private static Object onConcatDirectDL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDL", at = @At(value = "INVOKE", target = "concatDL"))
    private static Object onConcatLVTDL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLI", at = @At(value = "INVOKE", target = "concatLI"), index = 0)
    private static Object onConcatDirectLI(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLI", at = @At(value = "INVOKE", target = "concatLI"), index = 0)
    private static Object onConcatLVTLI(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLD", at = @At(value = "INVOKE", target = "concatLD"), index = 1)
    private static double onConcatDirectLD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLD", at = @At(value = "INVOKE", target = "concatLD"), index = 1)
    private static double onConcatLVTLD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLL", at = @At(value = "INVOKE", target = "concatLL"), index = 0)
    private static Object onConcatDirectLL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLL", at = @At(value = "INVOKE", target = "concatLL"), index = 0)
    private static Object onConcatLVTLL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectIII", at = @At(value = "INVOKE", target = "concatIII"), index = 0)
    private static int onConcatDirectIII(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIII", at = @At(value = "INVOKE", target = "concatIII"), index = 0)
    private static int onConcatLVTIII(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIID", at = @At(value = "INVOKE", target = "concatIID"), index = 1)
    private static int onConcatDirectIID(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIID", at = @At(value = "INVOKE", target = "concatIID"), index = 1)
    private static int onConcatLVTIID(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIIL", at = @At(value = "INVOKE", target = "concatIIL"), index = 2)
    private static Object onConcatDirectIIL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTIIL", at = @At(value = "INVOKE", target = "concatIIL"), index = 2)
    private static Object onConcatLVTIIL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectIDI", at = @At(value = "INVOKE", target = "concatIDI"), index = 0)
    private static int onConcatDirectIDI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIDI", at = @At(value = "INVOKE", target = "concatIDI"), index = 0)
    private static int onConcatLVTIDI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIDD", at = @At(value = "INVOKE", target = "concatIDD"), index = 0)
    private static int onConcatDirectIDD(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIDD", at = @At(value = "INVOKE", target = "concatIDD"), index = 0)
    private static int onConcatLVTIDD(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIDL", at = @At(value = "INVOKE", target = "concatIDL"), index = 1)
    private static double onConcatDirectIDL(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTIDL", at = @At(value = "INVOKE", target = "concatIDL"), index = 1)
    private static double onConcatLVTIDL(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectILI", at = @At(value = "INVOKE", target = "concatILI"), index = 0)
    private static int onConcatDirectILI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTILI", at = @At(value = "INVOKE", target = "concatILI"), index = 0)
    private static int onConcatLVTILI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectILD", at = @At(value = "INVOKE", target = "concatILD"))
    private static double onConcatDirectILD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTILD", at = @At(value = "INVOKE", target = "concatILD"))
    private static double onConcatLVTILD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectILL", at = @At(value = "INVOKE", target = "concatILL"), index = 0)
    private static int onConcatDirectILL(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTILL", at = @At(value = "INVOKE", target = "concatILL"), index = 0)
    private static int onConcatLVTILL(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDII", at = @At(value = "INVOKE", target = "concatDII"), index = 1)
    private static int onConcatDirectDII(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTDII", at = @At(value = "INVOKE", target = "concatDII"), index = 1)
    private static int onConcatLVTDII(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDID", at = @At(value = "INVOKE", target = "concatDID"), index = 1)
    private static int onConcatDirectDID(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTDID", at = @At(value = "INVOKE", target = "concatDID"), index = 1)
    private static int onConcatLVTDID(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDIL", at = @At(value = "INVOKE", target = "concatDIL"))
    private static Object onConcatDirectDIL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDIL", at = @At(value = "INVOKE", target = "concatDIL"))
    private static Object onConcatLVTDIL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectDDI", at = @At(value = "INVOKE", target = "concatDDI"), index = 0)
    private static double onConcatDirectDDI(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDDI", at = @At(value = "INVOKE", target = "concatDDI"), index = 0)
    private static double onConcatLVTDDI(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDDD", at = @At(value = "INVOKE", target = "concatDDD"), index = 1)
    private static double onConcatDirectDDD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDDD", at = @At(value = "INVOKE", target = "concatDDD"), index = 1)
    private static double onConcatLVTDDD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDDL", at = @At(value = "INVOKE", target = "concatDDL"), index = 2)
    private static Object onConcatDirectDDL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDDL", at = @At(value = "INVOKE", target = "concatDDL"), index = 2)
    private static Object onConcatLVTDDL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectDLI", at = @At(value = "INVOKE", target = "concatDLI"))
    private static int onConcatDirectDLI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTDLI", at = @At(value = "INVOKE", target = "concatDLI"))
    private static int onConcatLVTDLI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDLD", at = @At(value = "INVOKE", target = "concatDLD"), index = 1)
    private static Object onConcatDirectDLD(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDLD", at = @At(value = "INVOKE", target = "concatDLD"), index = 1)
    private static Object onConcatLVTDLD(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectDLL", at = @At(value = "INVOKE", target = "concatDLL"), index = 1)
    private static Object onConcatDirectDLL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDLL", at = @At(value = "INVOKE", target = "concatDLL"), index = 1)
    private static Object onConcatLVTDLL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLII", at = @At(value = "INVOKE", target = "concatLII"), index = 2)
    private static int onConcatDirectLII(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTLII", at = @At(value = "INVOKE", target = "concatLII"), index = 2)
    private static int onConcatLVTLII(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectLID", at = @At(value = "INVOKE", target = "concatLID"))
    private static double onConcatDirectLID(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLID", at = @At(value = "INVOKE", target = "concatLID"))
    private static double onConcatLVTLID(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLIL", at = @At(value = "INVOKE", target = "concatLIL"), index = 2)
    private static Object onConcatDirectLIL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLIL", at = @At(value = "INVOKE", target = "concatLIL"), index = 2)
    private static Object onConcatLVTLIL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLDI", at = @At(value = "INVOKE", target = "concatLDI"), index = 1)
    private static double onConcatDirectLDI(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLDI", at = @At(value = "INVOKE", target = "concatLDI"), index = 1)
    private static double onConcatLVTLDI(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLDD", at = @At(value = "INVOKE", target = "concatLDD"), index = 2)
    private static double onConcatDirectLDD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLDD", at = @At(value = "INVOKE", target = "concatLDD"), index = 2)
    private static double onConcatLVTLDD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLDL", at = @At(value = "INVOKE", target = "concatLDL"), index = 2)
    private static Object onConcatDirectLDL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLDL", at = @At(value = "INVOKE", target = "concatLDL"), index = 2)
    private static Object onConcatLVTLDL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLLI", at = @At(value = "INVOKE", target = "concatLLI"), index = 0)
    private static Object onConcatDirectLLI(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLLI", at = @At(value = "INVOKE", target = "concatLLI"), index = 0)
    private static Object onConcatLVTLLI(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLLD", at = @At(value = "INVOKE", target = "concatLLD"), index = 1)
    private static Object onConcatDirectLLD(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLLD", at = @At(value = "INVOKE", target = "concatLLD"), index = 1)
    private static Object onConcatLVTLLD(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLLL", at = @At(value = "INVOKE", target = "concatLLL"), index = 2)
    private static Object onConcatDirectLLL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLLL", at = @At(value = "INVOKE", target = "concatLLL"), index = 2)
    private static Object onConcatLVTLLL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectIIII", at = @At(value = "INVOKE", target = "concatIIII"), index = 0)
    private static int onConcatDirectIIII(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIIII", at = @At(value = "INVOKE", target = "concatIIII"), index = 0)
    private static int onConcatLVTIIII(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIIID", at = @At(value = "INVOKE", target = "concatIIID"), index = 1)
    private static int onConcatDirectIIID(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIIID", at = @At(value = "INVOKE", target = "concatIIID"), index = 1)
    private static int onConcatLVTIIID(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIIIL", at = @At(value = "INVOKE", target = "concatIIIL"), index = 2)
    private static int onConcatDirectIIIL(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIIIL", at = @At(value = "INVOKE", target = "concatIIIL"), index = 2)
    private static int onConcatLVTIIIL(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIIDI", at = @At(value = "INVOKE", target = "concatIIDI"), index = 3)
    private static int onConcatDirectIIDI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIIDI", at = @At(value = "INVOKE", target = "concatIIDI"), index = 3)
    private static int onConcatLVTIIDI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIIDD", at = @At(value = "INVOKE", target = "concatIIDD"), index = 0)
    private static int onConcatDirectIIDD(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIIDD", at = @At(value = "INVOKE", target = "concatIIDD"), index = 0)
    private static int onConcatLVTIIDD(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIIDL", at = @At(value = "INVOKE", target = "concatIIDL"), index = 1)
    private static int onConcatDirectIIDL(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIIDL", at = @At(value = "INVOKE", target = "concatIIDL"), index = 1)
    private static int onConcatLVTIIDL(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIILI", at = @At(value = "INVOKE", target = "concatIILI"), index = 2)
    private static Object onConcatDirectIILI(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTIILI", at = @At(value = "INVOKE", target = "concatIILI"), index = 2)
    private static Object onConcatLVTIILI(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectIILD", at = @At(value = "INVOKE", target = "concatIILD"), index = 3)
    private static double onConcatDirectIILD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTIILD", at = @At(value = "INVOKE", target = "concatIILD"), index = 3)
    private static double onConcatLVTIILD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectIILL", at = @At(value = "INVOKE", target = "concatIILL"), index = 0)
    private static int onConcatDirectIILL(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIILL", at = @At(value = "INVOKE", target = "concatIILL"), index = 0)
    private static int onConcatLVTIILL(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIDII", at = @At(value = "INVOKE", target = "concatIDII"), index = 1)
    private static double onConcatDirectIDII(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTIDII", at = @At(value = "INVOKE", target = "concatIDII"), index = 1)
    private static double onConcatLVTIDII(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectIDID", at = @At(value = "INVOKE", target = "concatIDID"), index = 2)
    private static int onConcatDirectIDID(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIDID", at = @At(value = "INVOKE", target = "concatIDID"), index = 2)
    private static int onConcatLVTIDID(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIDIL", at = @At(value = "INVOKE", target = "concatIDIL"), index = 3)
    private static Object onConcatDirectIDIL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTIDIL", at = @At(value = "INVOKE", target = "concatIDIL"), index = 3)
    private static Object onConcatLVTIDIL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectIDDI", at = @At(value = "INVOKE", target = "concatIDDI"), index = 0)
    private static int onConcatDirectIDDI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIDDI", at = @At(value = "INVOKE", target = "concatIDDI"), index = 0)
    private static int onConcatLVTIDDI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIDDD", at = @At(value = "INVOKE", target = "concatIDDD"), index = 3)
    private static double onConcatDirectIDDD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTIDDD", at = @At(value = "INVOKE", target = "concatIDDD"), index = 3)
    private static double onConcatLVTIDDD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectIDDL", at = @At(value = "INVOKE", target = "concatIDDL"))
    private static int onConcatDirectIDDL(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIDDL", at = @At(value = "INVOKE", target = "concatIDDL"))
    private static int onConcatLVTIDDL(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIDLI", at = @At(value = "INVOKE", target = "concatIDLI"), index = 3)
    private static int onConcatDirectIDLI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTIDLI", at = @At(value = "INVOKE", target = "concatIDLI"), index = 3)
    private static int onConcatLVTIDLI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectIDLD", at = @At(value = "INVOKE", target = "concatIDLD"), index = 1)
    private static double onConcatDirectIDLD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTIDLD", at = @At(value = "INVOKE", target = "concatIDLD"), index = 1)
    private static double onConcatLVTIDLD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectIDLL", at = @At(value = "INVOKE", target = "concatIDLL"), index = 2)
    private static Object onConcatDirectIDLL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTIDLL", at = @At(value = "INVOKE", target = "concatIDLL"), index = 2)
    private static Object onConcatLVTIDLL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectILII", at = @At(value = "INVOKE", target = "concatILII"), index = 2)
    private static int onConcatDirectILII(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTILII", at = @At(value = "INVOKE", target = "concatILII"), index = 2)
    private static int onConcatLVTILII(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectILID", at = @At(value = "INVOKE", target = "concatILID"), index = 3)
    private static double onConcatDirectILID(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTILID", at = @At(value = "INVOKE", target = "concatILID"), index = 3)
    private static double onConcatLVTILID(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectILIL", at = @At(value = "INVOKE", target = "concatILIL"), index = 0)
    private static int onConcatDirectILIL(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTILIL", at = @At(value = "INVOKE", target = "concatILIL"), index = 0)
    private static int onConcatLVTILIL(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectILDI", at = @At(value = "INVOKE", target = "concatILDI"), index = 1)
    private static Object onConcatDirectILDI(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTILDI", at = @At(value = "INVOKE", target = "concatILDI"), index = 1)
    private static Object onConcatLVTILDI(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectILDD", at = @At(value = "INVOKE", target = "concatILDD"), index = 2)
    private static double onConcatDirectILDD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTILDD", at = @At(value = "INVOKE", target = "concatILDD"), index = 2)
    private static double onConcatLVTILDD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectILDL", at = @At(value = "INVOKE", target = "concatILDL"), index = 3)
    private static Object onConcatDirectILDL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTILDL", at = @At(value = "INVOKE", target = "concatILDL"), index = 3)
    private static Object onConcatLVTILDL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectILLI", at = @At(value = "INVOKE", target = "concatILLI"), index = 0)
    private static int onConcatDirectILLI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTILLI", at = @At(value = "INVOKE", target = "concatILLI"), index = 0)
    private static int onConcatLVTILLI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectILLD", at = @At(value = "INVOKE", target = "concatILLD"), index = 0)
    private static int onConcatDirectILLD(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTILLD", at = @At(value = "INVOKE", target = "concatILLD"), index = 0)
    private static int onConcatLVTILLD(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectILLL", at = @At(value = "INVOKE", target = "concatILLL"), index = 1)
    private static Object onConcatDirectILLL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTILLL", at = @At(value = "INVOKE", target = "concatILLL"), index = 1)
    private static Object onConcatLVTILLL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectDIII", at = @At(value = "INVOKE", target = "concatDIII"), index = 2)
    private static int onConcatDirectDIII(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTDIII", at = @At(value = "INVOKE", target = "concatDIII"), index = 2)
    private static int onConcatLVTDIII(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDIID", at = @At(value = "INVOKE", target = "concatDIID"), index = 0)
    private static double onConcatDirectDIID(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDIID", at = @At(value = "INVOKE", target = "concatDIID"), index = 0)
    private static double onConcatLVTDIID(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDIIL", at = @At(value = "INVOKE", target = "concatDIIL"))
    private static double onConcatDirectDIIL(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDIIL", at = @At(value = "INVOKE", target = "concatDIIL"))
    private static double onConcatLVTDIIL(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDIDI", at = @At(value = "INVOKE", target = "concatDIDI"), index = 2)
    private static double onConcatDirectDIDI(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDIDI", at = @At(value = "INVOKE", target = "concatDIDI"), index = 2)
    private static double onConcatLVTDIDI(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDIDD", at = @At(value = "INVOKE", target = "concatDIDD"), index = 3)
    private static double onConcatDirectDIDD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDIDD", at = @At(value = "INVOKE", target = "concatDIDD"), index = 3)
    private static double onConcatLVTDIDD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDIDL", at = @At(value = "INVOKE", target = "concatDIDL"), index = 0)
    private static double onConcatDirectDIDL(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDIDL", at = @At(value = "INVOKE", target = "concatDIDL"), index = 0)
    private static double onConcatLVTDIDL(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDILI", at = @At(value = "INVOKE", target = "concatDILI"), index = 3)
    private static int onConcatDirectDILI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTDILI", at = @At(value = "INVOKE", target = "concatDILI"), index = 3)
    private static int onConcatLVTDILI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDILD", at = @At(value = "INVOKE", target = "concatDILD"), index = 2)
    private static Object onConcatDirectDILD(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDILD", at = @At(value = "INVOKE", target = "concatDILD"), index = 2)
    private static Object onConcatLVTDILD(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectDILL", at = @At(value = "INVOKE", target = "concatDILL"), index = 0)
    private static double onConcatDirectDILL(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDILL", at = @At(value = "INVOKE", target = "concatDILL"), index = 0)
    private static double onConcatLVTDILL(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDDII", at = @At(value = "INVOKE", target = "concatDDII"), index = 0)
    private static double onConcatDirectDDII(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDDII", at = @At(value = "INVOKE", target = "concatDDII"), index = 0)
    private static double onConcatLVTDDII(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDDID", at = @At(value = "INVOKE", target = "concatDDID"), index = 1)
    private static double onConcatDirectDDID(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDDID", at = @At(value = "INVOKE", target = "concatDDID"), index = 1)
    private static double onConcatLVTDDID(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDDIL", at = @At(value = "INVOKE", target = "concatDDIL"), index = 2)
    private static int onConcatDirectDDIL(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTDDIL", at = @At(value = "INVOKE", target = "concatDDIL"), index = 2)
    private static int onConcatLVTDDIL(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDDDI", at = @At(value = "INVOKE", target = "concatDDDI"), index = 3)
    private static int onConcatDirectDDDI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTDDDI", at = @At(value = "INVOKE", target = "concatDDDI"), index = 3)
    private static int onConcatLVTDDDI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDDDD", at = @At(value = "INVOKE", target = "concatDDDD"), index = 0)
    private static double onConcatDirectDDDD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDDDD", at = @At(value = "INVOKE", target = "concatDDDD"), index = 0)
    private static double onConcatLVTDDDD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDDDL", at = @At(value = "INVOKE", target = "concatDDDL"), index = 1)
    private static double onConcatDirectDDDL(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDDDL", at = @At(value = "INVOKE", target = "concatDDDL"), index = 1)
    private static double onConcatLVTDDDL(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDDLI", at = @At(value = "INVOKE", target = "concatDDLI"), index = 2)
    private static Object onConcatDirectDDLI(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDDLI", at = @At(value = "INVOKE", target = "concatDDLI"), index = 2)
    private static Object onConcatLVTDDLI(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectDDLD", at = @At(value = "INVOKE", target = "concatDDLD"), index = 3)
    private static double onConcatDirectDDLD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDDLD", at = @At(value = "INVOKE", target = "concatDDLD"), index = 3)
    private static double onConcatLVTDDLD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDDLL", at = @At(value = "INVOKE", target = "concatDDLL"), index = 0)
    private static double onConcatDirectDDLL(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDDLL", at = @At(value = "INVOKE", target = "concatDDLL"), index = 0)
    private static double onConcatLVTDDLL(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDLII", at = @At(value = "INVOKE", target = "concatDLII"), index = 0)
    private static double onConcatDirectDLII(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDLII", at = @At(value = "INVOKE", target = "concatDLII"), index = 0)
    private static double onConcatLVTDLII(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDLID", at = @At(value = "INVOKE", target = "concatDLID"), index = 2)
    private static int onConcatDirectDLID(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTDLID", at = @At(value = "INVOKE", target = "concatDLID"), index = 2)
    private static int onConcatLVTDLID(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDLIL", at = @At(value = "INVOKE", target = "concatDLIL"), index = 2)
    private static int onConcatDirectDLIL(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTDLIL", at = @At(value = "INVOKE", target = "concatDLIL"), index = 2)
    private static int onConcatLVTDLIL(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectDLDI", at = @At(value = "INVOKE", target = "concatDLDI"), index = 0)
    private static double onConcatDirectDLDI(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDLDI", at = @At(value = "INVOKE", target = "concatDLDI"), index = 0)
    private static double onConcatLVTDLDI(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDLDD", at = @At(value = "INVOKE", target = "concatDLDD"), index = 1)
    private static Object onConcatDirectDLDD(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDLDD", at = @At(value = "INVOKE", target = "concatDLDD"), index = 1)
    private static Object onConcatLVTDLDD(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectDLDL", at = @At(value = "INVOKE", target = "concatDLDL"), index = 2)
    private static double onConcatDirectDLDL(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDLDL", at = @At(value = "INVOKE", target = "concatDLDL"), index = 2)
    private static double onConcatLVTDLDL(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDLLI", at = @At(value = "INVOKE", target = "concatDLLI"), index = 1)
    private static Object onConcatDirectDLLI(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDLLI", at = @At(value = "INVOKE", target = "concatDLLI"), index = 1)
    private static Object onConcatLVTDLLI(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectDLLD", at = @At(value = "INVOKE", target = "concatDLLD"), index = 0)
    private static double onConcatDirectDLLD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTDLLD", at = @At(value = "INVOKE", target = "concatDLLD"), index = 0)
    private static double onConcatLVTDLLD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectDLLL", at = @At(value = "INVOKE", target = "concatDLLL"), index = 3)
    private static Object onConcatDirectDLLL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTDLLL", at = @At(value = "INVOKE", target = "concatDLLL"), index = 3)
    private static Object onConcatLVTDLLL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLIII", at = @At(value = "INVOKE", target = "concatLIII"))
    private static Object onConcatDirectLIII(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLIII", at = @At(value = "INVOKE", target = "concatLIII"))
    private static Object onConcatLVTLIII(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLIID", at = @At(value = "INVOKE", target = "concatLIID"), index = 0)
    private static Object onConcatDirectLIID(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLIID", at = @At(value = "INVOKE", target = "concatLIID"), index = 0)
    private static Object onConcatLVTLIID(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLIIL", at = @At(value = "INVOKE", target = "concatLIIL"), index = 0)
    private static Object onConcatDirectLIIL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLIIL", at = @At(value = "INVOKE", target = "concatLIIL"), index = 0)
    private static Object onConcatLVTLIIL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLIDI", at = @At(value = "INVOKE", target = "concatLIDI"), index = 2)
    private static double onConcatDirectLIDI(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLIDI", at = @At(value = "INVOKE", target = "concatLIDI"), index = 2)
    private static double onConcatLVTLIDI(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLIDD", at = @At(value = "INVOKE", target = "concatLIDD"), index = 3)
    private static double onConcatDirectLIDD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLIDD", at = @At(value = "INVOKE", target = "concatLIDD"), index = 3)
    private static double onConcatLVTLIDD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLIDL", at = @At(value = "INVOKE", target = "concatLIDL"), index = 3)
    private static Object onConcatDirectLIDL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLIDL", at = @At(value = "INVOKE", target = "concatLIDL"), index = 3)
    private static Object onConcatLVTLIDL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLILI", at = @At(value = "INVOKE", target = "concatLILI"), index = 0)
    private static Object onConcatDirectLILI(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLILI", at = @At(value = "INVOKE", target = "concatLILI"), index = 0)
    private static Object onConcatLVTLILI(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLILD", at = @At(value = "INVOKE", target = "concatLILD"), index = 1)
    private static int onConcatDirectLILD(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTLILD", at = @At(value = "INVOKE", target = "concatLILD"), index = 1)
    private static int onConcatLVTLILD(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectLILL", at = @At(value = "INVOKE", target = "concatLILL"), index = 2)
    private static Object onConcatDirectLILL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLILL", at = @At(value = "INVOKE", target = "concatLILL"), index = 2)
    private static Object onConcatLVTLILL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLDII", at = @At(value = "INVOKE", target = "concatLDII"), index = 3)
    private static int onConcatDirectLDII(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTLDII", at = @At(value = "INVOKE", target = "concatLDII"), index = 3)
    private static int onConcatLVTLDII(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectLDID", at = @At(value = "INVOKE", target = "concatLDID"))
    private static Object onConcatDirectLDID(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLDID", at = @At(value = "INVOKE", target = "concatLDID"))
    private static Object onConcatLVTLDID(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLDIL", at = @At(value = "INVOKE", target = "concatLDIL"), index = 1)
    private static double onConcatDirectLDIL(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLDIL", at = @At(value = "INVOKE", target = "concatLDIL"), index = 1)
    private static double onConcatLVTLDIL(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLDDI", at = @At(value = "INVOKE", target = "concatLDDI"), index = 1)
    private static double onConcatDirectLDDI(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLDDI", at = @At(value = "INVOKE", target = "concatLDDI"), index = 1)
    private static double onConcatLVTLDDI(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLDDD", at = @At(value = "INVOKE", target = "concatLDDD"), index = 2)
    private static double onConcatDirectLDDD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLDDD", at = @At(value = "INVOKE", target = "concatLDDD"), index = 2)
    private static double onConcatLVTLDDD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLDDL", at = @At(value = "INVOKE", target = "concatLDDL"), index = 0)
    private static Object onConcatDirectLDDL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLDDL", at = @At(value = "INVOKE", target = "concatLDDL"), index = 0)
    private static Object onConcatLVTLDDL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLDLI", at = @At(value = "INVOKE", target = "concatLDLI"), index = 1)
    private static double onConcatDirectLDLI(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLDLI", at = @At(value = "INVOKE", target = "concatLDLI"), index = 1)
    private static double onConcatLVTLDLI(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLDLD", at = @At(value = "INVOKE", target = "concatLDLD"), index = 2)
    private static Object onConcatDirectLDLD(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLDLD", at = @At(value = "INVOKE", target = "concatLDLD"), index = 2)
    private static Object onConcatLVTLDLD(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLDLL", at = @At(value = "INVOKE", target = "concatLDLL"), index = 3)
    private static Object onConcatDirectLDLL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLDLL", at = @At(value = "INVOKE", target = "concatLDLL"), index = 3)
    private static Object onConcatLVTLDLL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLLII", at = @At(value = "INVOKE", target = "concatLLII"), index = 0)
    private static Object onConcatDirectLLII(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLLII", at = @At(value = "INVOKE", target = "concatLLII"), index = 0)
    private static Object onConcatLVTLLII(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLLID", at = @At(value = "INVOKE", target = "concatLLID"), index = 1)
    private static Object onConcatDirectLLID(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLLID", at = @At(value = "INVOKE", target = "concatLLID"), index = 1)
    private static Object onConcatLVTLLID(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLLIL", at = @At(value = "INVOKE", target = "concatLLIL"), index = 2)
    private static int onConcatDirectLLIL(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTLLIL", at = @At(value = "INVOKE", target = "concatLLIL"), index = 2)
    private static int onConcatLVTLLIL(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectLLDI", at = @At(value = "INVOKE", target = "concatLLDI"), index = 3)
    private static int onConcatDirectLLDI(int var) {
        return -957256832;
    }

    @ModifyArg(method = "testConcatLVTLLDI", at = @At(value = "INVOKE", target = "concatLLDI"), index = 3)
    private static int onConcatLVTLLDI(int var) {
        return 0;
    }

    @ModifyArg(method = "testConcatDirectLLDD", at = @At(value = "INVOKE", target = "concatLLDD"), index = 0)
    private static Object onConcatDirectLLDD(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLLDD", at = @At(value = "INVOKE", target = "concatLLDD"), index = 0)
    private static Object onConcatLVTLLDD(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLLDL", at = @At(value = "INVOKE", target = "concatLLDL"), index = 1)
    private static Object onConcatDirectLLDL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLLDL", at = @At(value = "INVOKE", target = "concatLLDL"), index = 1)
    private static Object onConcatLVTLLDL(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLLLI", at = @At(value = "INVOKE", target = "concatLLLI"), index = 2)
    private static Object onConcatDirectLLLI(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLLLI", at = @At(value = "INVOKE", target = "concatLLLI"), index = 2)
    private static Object onConcatLVTLLLI(Object var) {
        return null;
    }

    @ModifyArg(method = "testConcatDirectLLLD", at = @At(value = "INVOKE", target = "concatLLLD"), index = 3)
    private static double onConcatDirectLLLD(double var) {
        return 2.5D;
    }

    @ModifyArg(method = "testConcatLVTLLLD", at = @At(value = "INVOKE", target = "concatLLLD"), index = 3)
    private static double onConcatLVTLLLD(double var) {
        return 0D;
    }

    @ModifyArg(method = "testConcatDirectLLLL", at = @At(value = "INVOKE", target = "concatLLLL"), index = 0)
    private static Object onConcatDirectLLLL(Object var) {
        return Boolean.TRUE;
    }

    @ModifyArg(method = "testConcatLVTLLLL", at = @At(value = "INVOKE", target = "concatLLLL"), index = 0)
    private static Object onConcatLVTLLLL(Object var) {
        return null;
    }

}
