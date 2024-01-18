/* following code is generated - do not touch directly. */
package org.stianloader.micromixin.test.j8.targets;

public class ModifyArgTest {
    private static String concatI(int var0) {
        return java.util.Objects.toString(var0);
    }

    public static String testConcatDirectI() {
        return concatI(0);
    }

    public static String testConcatLVTI() {
        int var0 = -957256832;
        return concatI(var0);
    }

    private static String concatD(double var0) {
        return java.util.Objects.toString(var0);
    }

    public static String testConcatDirectD() {
        return concatD(0D);
    }

    public static String testConcatLVTD() {
        double var0 = 2.5D;
        return concatD(var0);
    }

    private static String concatL(Object var0) {
        return java.util.Objects.toString(var0);
    }

    public static String testConcatDirectL() {
        return concatL(null);
    }

    public static String testConcatLVTL() {
        Object var0 = Boolean.TRUE;
        return concatL(var0);
    }

    private static String concatII(int var0, int var1) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1);
    }

    public static String testConcatDirectII() {
        return concatII(0, 0);
    }

    public static String testConcatLVTII() {
        int var0 = -957256832;
        int var1 = -957256832;
        return concatII(var0, var1);
    }

    private static String concatID(int var0, double var1) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1);
    }

    public static String testConcatDirectID() {
        return concatID(0, 0D);
    }

    public static String testConcatLVTID() {
        int var0 = -957256832;
        double var1 = 2.5D;
        return concatID(var0, var1);
    }

    private static String concatIL(int var0, Object var1) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1);
    }

    public static String testConcatDirectIL() {
        return concatIL(0, null);
    }

    public static String testConcatLVTIL() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        return concatIL(var0, var1);
    }

    private static String concatDI(double var0, int var1) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1);
    }

    public static String testConcatDirectDI() {
        return concatDI(0D, 0);
    }

    public static String testConcatLVTDI() {
        double var0 = 2.5D;
        int var1 = -957256832;
        return concatDI(var0, var1);
    }

    private static String concatDD(double var0, double var1) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1);
    }

    public static String testConcatDirectDD() {
        return concatDD(0D, 0D);
    }

    public static String testConcatLVTDD() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        return concatDD(var0, var1);
    }

    private static String concatDL(double var0, Object var1) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1);
    }

    public static String testConcatDirectDL() {
        return concatDL(0D, null);
    }

    public static String testConcatLVTDL() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        return concatDL(var0, var1);
    }

    private static String concatLI(Object var0, int var1) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1);
    }

    public static String testConcatDirectLI() {
        return concatLI(null, 0);
    }

    public static String testConcatLVTLI() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        return concatLI(var0, var1);
    }

    private static String concatLD(Object var0, double var1) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1);
    }

    public static String testConcatDirectLD() {
        return concatLD(null, 0D);
    }

    public static String testConcatLVTLD() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        return concatLD(var0, var1);
    }

    private static String concatLL(Object var0, Object var1) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1);
    }

    public static String testConcatDirectLL() {
        return concatLL(null, null);
    }

    public static String testConcatLVTLL() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        return concatLL(var0, var1);
    }

    private static String concatIII(int var0, int var1, int var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectIII() {
        return concatIII(0, 0, 0);
    }

    public static String testConcatLVTIII() {
        int var0 = -957256832;
        int var1 = -957256832;
        int var2 = -957256832;
        return concatIII(var0, var1, var2);
    }

    private static String concatIID(int var0, int var1, double var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectIID() {
        return concatIID(0, 0, 0D);
    }

    public static String testConcatLVTIID() {
        int var0 = -957256832;
        int var1 = -957256832;
        double var2 = 2.5D;
        return concatIID(var0, var1, var2);
    }

    private static String concatIIL(int var0, int var1, Object var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectIIL() {
        return concatIIL(0, 0, null);
    }

    public static String testConcatLVTIIL() {
        int var0 = -957256832;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        return concatIIL(var0, var1, var2);
    }

    private static String concatIDI(int var0, double var1, int var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectIDI() {
        return concatIDI(0, 0D, 0);
    }

    public static String testConcatLVTIDI() {
        int var0 = -957256832;
        double var1 = 2.5D;
        int var2 = -957256832;
        return concatIDI(var0, var1, var2);
    }

    private static String concatIDD(int var0, double var1, double var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectIDD() {
        return concatIDD(0, 0D, 0D);
    }

    public static String testConcatLVTIDD() {
        int var0 = -957256832;
        double var1 = 2.5D;
        double var2 = 2.5D;
        return concatIDD(var0, var1, var2);
    }

    private static String concatIDL(int var0, double var1, Object var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectIDL() {
        return concatIDL(0, 0D, null);
    }

    public static String testConcatLVTIDL() {
        int var0 = -957256832;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        return concatIDL(var0, var1, var2);
    }

    private static String concatILI(int var0, Object var1, int var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectILI() {
        return concatILI(0, null, 0);
    }

    public static String testConcatLVTILI() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        return concatILI(var0, var1, var2);
    }

    private static String concatILD(int var0, Object var1, double var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectILD() {
        return concatILD(0, null, 0D);
    }

    public static String testConcatLVTILD() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        return concatILD(var0, var1, var2);
    }

    private static String concatILL(int var0, Object var1, Object var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectILL() {
        return concatILL(0, null, null);
    }

    public static String testConcatLVTILL() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        return concatILL(var0, var1, var2);
    }

    private static String concatDII(double var0, int var1, int var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectDII() {
        return concatDII(0D, 0, 0);
    }

    public static String testConcatLVTDII() {
        double var0 = 2.5D;
        int var1 = -957256832;
        int var2 = -957256832;
        return concatDII(var0, var1, var2);
    }

    private static String concatDID(double var0, int var1, double var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectDID() {
        return concatDID(0D, 0, 0D);
    }

    public static String testConcatLVTDID() {
        double var0 = 2.5D;
        int var1 = -957256832;
        double var2 = 2.5D;
        return concatDID(var0, var1, var2);
    }

    private static String concatDIL(double var0, int var1, Object var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectDIL() {
        return concatDIL(0D, 0, null);
    }

    public static String testConcatLVTDIL() {
        double var0 = 2.5D;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        return concatDIL(var0, var1, var2);
    }

    private static String concatDDI(double var0, double var1, int var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectDDI() {
        return concatDDI(0D, 0D, 0);
    }

    public static String testConcatLVTDDI() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        int var2 = -957256832;
        return concatDDI(var0, var1, var2);
    }

    private static String concatDDD(double var0, double var1, double var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectDDD() {
        return concatDDD(0D, 0D, 0D);
    }

    public static String testConcatLVTDDD() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        double var2 = 2.5D;
        return concatDDD(var0, var1, var2);
    }

    private static String concatDDL(double var0, double var1, Object var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectDDL() {
        return concatDDL(0D, 0D, null);
    }

    public static String testConcatLVTDDL() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        return concatDDL(var0, var1, var2);
    }

    private static String concatDLI(double var0, Object var1, int var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectDLI() {
        return concatDLI(0D, null, 0);
    }

    public static String testConcatLVTDLI() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        return concatDLI(var0, var1, var2);
    }

    private static String concatDLD(double var0, Object var1, double var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectDLD() {
        return concatDLD(0D, null, 0D);
    }

    public static String testConcatLVTDLD() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        return concatDLD(var0, var1, var2);
    }

    private static String concatDLL(double var0, Object var1, Object var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectDLL() {
        return concatDLL(0D, null, null);
    }

    public static String testConcatLVTDLL() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        return concatDLL(var0, var1, var2);
    }

    private static String concatLII(Object var0, int var1, int var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectLII() {
        return concatLII(null, 0, 0);
    }

    public static String testConcatLVTLII() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        int var2 = -957256832;
        return concatLII(var0, var1, var2);
    }

    private static String concatLID(Object var0, int var1, double var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectLID() {
        return concatLID(null, 0, 0D);
    }

    public static String testConcatLVTLID() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        double var2 = 2.5D;
        return concatLID(var0, var1, var2);
    }

    private static String concatLIL(Object var0, int var1, Object var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectLIL() {
        return concatLIL(null, 0, null);
    }

    public static String testConcatLVTLIL() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        return concatLIL(var0, var1, var2);
    }

    private static String concatLDI(Object var0, double var1, int var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectLDI() {
        return concatLDI(null, 0D, 0);
    }

    public static String testConcatLVTLDI() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        int var2 = -957256832;
        return concatLDI(var0, var1, var2);
    }

    private static String concatLDD(Object var0, double var1, double var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectLDD() {
        return concatLDD(null, 0D, 0D);
    }

    public static String testConcatLVTLDD() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        double var2 = 2.5D;
        return concatLDD(var0, var1, var2);
    }

    private static String concatLDL(Object var0, double var1, Object var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectLDL() {
        return concatLDL(null, 0D, null);
    }

    public static String testConcatLVTLDL() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        return concatLDL(var0, var1, var2);
    }

    private static String concatLLI(Object var0, Object var1, int var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectLLI() {
        return concatLLI(null, null, 0);
    }

    public static String testConcatLVTLLI() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        return concatLLI(var0, var1, var2);
    }

    private static String concatLLD(Object var0, Object var1, double var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectLLD() {
        return concatLLD(null, null, 0D);
    }

    public static String testConcatLVTLLD() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        return concatLLD(var0, var1, var2);
    }

    private static String concatLLL(Object var0, Object var1, Object var2) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2);
    }

    public static String testConcatDirectLLL() {
        return concatLLL(null, null, null);
    }

    public static String testConcatLVTLLL() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        return concatLLL(var0, var1, var2);
    }

    private static String concatIIII(int var0, int var1, int var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIIII() {
        return concatIIII(0, 0, 0, 0);
    }

    public static String testConcatLVTIIII() {
        int var0 = -957256832;
        int var1 = -957256832;
        int var2 = -957256832;
        int var3 = -957256832;
        return concatIIII(var0, var1, var2, var3);
    }

    private static String concatIIID(int var0, int var1, int var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIIID() {
        return concatIIID(0, 0, 0, 0D);
    }

    public static String testConcatLVTIIID() {
        int var0 = -957256832;
        int var1 = -957256832;
        int var2 = -957256832;
        double var3 = 2.5D;
        return concatIIID(var0, var1, var2, var3);
    }

    private static String concatIIIL(int var0, int var1, int var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIIIL() {
        return concatIIIL(0, 0, 0, null);
    }

    public static String testConcatLVTIIIL() {
        int var0 = -957256832;
        int var1 = -957256832;
        int var2 = -957256832;
        Object var3 = Boolean.TRUE;
        return concatIIIL(var0, var1, var2, var3);
    }

    private static String concatIIDI(int var0, int var1, double var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIIDI() {
        return concatIIDI(0, 0, 0D, 0);
    }

    public static String testConcatLVTIIDI() {
        int var0 = -957256832;
        int var1 = -957256832;
        double var2 = 2.5D;
        int var3 = -957256832;
        return concatIIDI(var0, var1, var2, var3);
    }

    private static String concatIIDD(int var0, int var1, double var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIIDD() {
        return concatIIDD(0, 0, 0D, 0D);
    }

    public static String testConcatLVTIIDD() {
        int var0 = -957256832;
        int var1 = -957256832;
        double var2 = 2.5D;
        double var3 = 2.5D;
        return concatIIDD(var0, var1, var2, var3);
    }

    private static String concatIIDL(int var0, int var1, double var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIIDL() {
        return concatIIDL(0, 0, 0D, null);
    }

    public static String testConcatLVTIIDL() {
        int var0 = -957256832;
        int var1 = -957256832;
        double var2 = 2.5D;
        Object var3 = Boolean.TRUE;
        return concatIIDL(var0, var1, var2, var3);
    }

    private static String concatIILI(int var0, int var1, Object var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIILI() {
        return concatIILI(0, 0, null, 0);
    }

    public static String testConcatLVTIILI() {
        int var0 = -957256832;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        int var3 = -957256832;
        return concatIILI(var0, var1, var2, var3);
    }

    private static String concatIILD(int var0, int var1, Object var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIILD() {
        return concatIILD(0, 0, null, 0D);
    }

    public static String testConcatLVTIILD() {
        int var0 = -957256832;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        double var3 = 2.5D;
        return concatIILD(var0, var1, var2, var3);
    }

    private static String concatIILL(int var0, int var1, Object var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIILL() {
        return concatIILL(0, 0, null, null);
    }

    public static String testConcatLVTIILL() {
        int var0 = -957256832;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        Object var3 = Boolean.TRUE;
        return concatIILL(var0, var1, var2, var3);
    }

    private static String concatIDII(int var0, double var1, int var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIDII() {
        return concatIDII(0, 0D, 0, 0);
    }

    public static String testConcatLVTIDII() {
        int var0 = -957256832;
        double var1 = 2.5D;
        int var2 = -957256832;
        int var3 = -957256832;
        return concatIDII(var0, var1, var2, var3);
    }

    private static String concatIDID(int var0, double var1, int var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIDID() {
        return concatIDID(0, 0D, 0, 0D);
    }

    public static String testConcatLVTIDID() {
        int var0 = -957256832;
        double var1 = 2.5D;
        int var2 = -957256832;
        double var3 = 2.5D;
        return concatIDID(var0, var1, var2, var3);
    }

    private static String concatIDIL(int var0, double var1, int var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIDIL() {
        return concatIDIL(0, 0D, 0, null);
    }

    public static String testConcatLVTIDIL() {
        int var0 = -957256832;
        double var1 = 2.5D;
        int var2 = -957256832;
        Object var3 = Boolean.TRUE;
        return concatIDIL(var0, var1, var2, var3);
    }

    private static String concatIDDI(int var0, double var1, double var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIDDI() {
        return concatIDDI(0, 0D, 0D, 0);
    }

    public static String testConcatLVTIDDI() {
        int var0 = -957256832;
        double var1 = 2.5D;
        double var2 = 2.5D;
        int var3 = -957256832;
        return concatIDDI(var0, var1, var2, var3);
    }

    private static String concatIDDD(int var0, double var1, double var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIDDD() {
        return concatIDDD(0, 0D, 0D, 0D);
    }

    public static String testConcatLVTIDDD() {
        int var0 = -957256832;
        double var1 = 2.5D;
        double var2 = 2.5D;
        double var3 = 2.5D;
        return concatIDDD(var0, var1, var2, var3);
    }

    private static String concatIDDL(int var0, double var1, double var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIDDL() {
        return concatIDDL(0, 0D, 0D, null);
    }

    public static String testConcatLVTIDDL() {
        int var0 = -957256832;
        double var1 = 2.5D;
        double var2 = 2.5D;
        Object var3 = Boolean.TRUE;
        return concatIDDL(var0, var1, var2, var3);
    }

    private static String concatIDLI(int var0, double var1, Object var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIDLI() {
        return concatIDLI(0, 0D, null, 0);
    }

    public static String testConcatLVTIDLI() {
        int var0 = -957256832;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        int var3 = -957256832;
        return concatIDLI(var0, var1, var2, var3);
    }

    private static String concatIDLD(int var0, double var1, Object var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIDLD() {
        return concatIDLD(0, 0D, null, 0D);
    }

    public static String testConcatLVTIDLD() {
        int var0 = -957256832;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        double var3 = 2.5D;
        return concatIDLD(var0, var1, var2, var3);
    }

    private static String concatIDLL(int var0, double var1, Object var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectIDLL() {
        return concatIDLL(0, 0D, null, null);
    }

    public static String testConcatLVTIDLL() {
        int var0 = -957256832;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        Object var3 = Boolean.TRUE;
        return concatIDLL(var0, var1, var2, var3);
    }

    private static String concatILII(int var0, Object var1, int var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectILII() {
        return concatILII(0, null, 0, 0);
    }

    public static String testConcatLVTILII() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        int var3 = -957256832;
        return concatILII(var0, var1, var2, var3);
    }

    private static String concatILID(int var0, Object var1, int var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectILID() {
        return concatILID(0, null, 0, 0D);
    }

    public static String testConcatLVTILID() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        double var3 = 2.5D;
        return concatILID(var0, var1, var2, var3);
    }

    private static String concatILIL(int var0, Object var1, int var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectILIL() {
        return concatILIL(0, null, 0, null);
    }

    public static String testConcatLVTILIL() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        Object var3 = Boolean.TRUE;
        return concatILIL(var0, var1, var2, var3);
    }

    private static String concatILDI(int var0, Object var1, double var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectILDI() {
        return concatILDI(0, null, 0D, 0);
    }

    public static String testConcatLVTILDI() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        int var3 = -957256832;
        return concatILDI(var0, var1, var2, var3);
    }

    private static String concatILDD(int var0, Object var1, double var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectILDD() {
        return concatILDD(0, null, 0D, 0D);
    }

    public static String testConcatLVTILDD() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        double var3 = 2.5D;
        return concatILDD(var0, var1, var2, var3);
    }

    private static String concatILDL(int var0, Object var1, double var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectILDL() {
        return concatILDL(0, null, 0D, null);
    }

    public static String testConcatLVTILDL() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        Object var3 = Boolean.TRUE;
        return concatILDL(var0, var1, var2, var3);
    }

    private static String concatILLI(int var0, Object var1, Object var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectILLI() {
        return concatILLI(0, null, null, 0);
    }

    public static String testConcatLVTILLI() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        int var3 = -957256832;
        return concatILLI(var0, var1, var2, var3);
    }

    private static String concatILLD(int var0, Object var1, Object var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectILLD() {
        return concatILLD(0, null, null, 0D);
    }

    public static String testConcatLVTILLD() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        double var3 = 2.5D;
        return concatILLD(var0, var1, var2, var3);
    }

    private static String concatILLL(int var0, Object var1, Object var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectILLL() {
        return concatILLL(0, null, null, null);
    }

    public static String testConcatLVTILLL() {
        int var0 = -957256832;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        Object var3 = Boolean.TRUE;
        return concatILLL(var0, var1, var2, var3);
    }

    private static String concatDIII(double var0, int var1, int var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDIII() {
        return concatDIII(0D, 0, 0, 0);
    }

    public static String testConcatLVTDIII() {
        double var0 = 2.5D;
        int var1 = -957256832;
        int var2 = -957256832;
        int var3 = -957256832;
        return concatDIII(var0, var1, var2, var3);
    }

    private static String concatDIID(double var0, int var1, int var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDIID() {
        return concatDIID(0D, 0, 0, 0D);
    }

    public static String testConcatLVTDIID() {
        double var0 = 2.5D;
        int var1 = -957256832;
        int var2 = -957256832;
        double var3 = 2.5D;
        return concatDIID(var0, var1, var2, var3);
    }

    private static String concatDIIL(double var0, int var1, int var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDIIL() {
        return concatDIIL(0D, 0, 0, null);
    }

    public static String testConcatLVTDIIL() {
        double var0 = 2.5D;
        int var1 = -957256832;
        int var2 = -957256832;
        Object var3 = Boolean.TRUE;
        return concatDIIL(var0, var1, var2, var3);
    }

    private static String concatDIDI(double var0, int var1, double var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDIDI() {
        return concatDIDI(0D, 0, 0D, 0);
    }

    public static String testConcatLVTDIDI() {
        double var0 = 2.5D;
        int var1 = -957256832;
        double var2 = 2.5D;
        int var3 = -957256832;
        return concatDIDI(var0, var1, var2, var3);
    }

    private static String concatDIDD(double var0, int var1, double var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDIDD() {
        return concatDIDD(0D, 0, 0D, 0D);
    }

    public static String testConcatLVTDIDD() {
        double var0 = 2.5D;
        int var1 = -957256832;
        double var2 = 2.5D;
        double var3 = 2.5D;
        return concatDIDD(var0, var1, var2, var3);
    }

    private static String concatDIDL(double var0, int var1, double var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDIDL() {
        return concatDIDL(0D, 0, 0D, null);
    }

    public static String testConcatLVTDIDL() {
        double var0 = 2.5D;
        int var1 = -957256832;
        double var2 = 2.5D;
        Object var3 = Boolean.TRUE;
        return concatDIDL(var0, var1, var2, var3);
    }

    private static String concatDILI(double var0, int var1, Object var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDILI() {
        return concatDILI(0D, 0, null, 0);
    }

    public static String testConcatLVTDILI() {
        double var0 = 2.5D;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        int var3 = -957256832;
        return concatDILI(var0, var1, var2, var3);
    }

    private static String concatDILD(double var0, int var1, Object var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDILD() {
        return concatDILD(0D, 0, null, 0D);
    }

    public static String testConcatLVTDILD() {
        double var0 = 2.5D;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        double var3 = 2.5D;
        return concatDILD(var0, var1, var2, var3);
    }

    private static String concatDILL(double var0, int var1, Object var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDILL() {
        return concatDILL(0D, 0, null, null);
    }

    public static String testConcatLVTDILL() {
        double var0 = 2.5D;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        Object var3 = Boolean.TRUE;
        return concatDILL(var0, var1, var2, var3);
    }

    private static String concatDDII(double var0, double var1, int var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDDII() {
        return concatDDII(0D, 0D, 0, 0);
    }

    public static String testConcatLVTDDII() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        int var2 = -957256832;
        int var3 = -957256832;
        return concatDDII(var0, var1, var2, var3);
    }

    private static String concatDDID(double var0, double var1, int var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDDID() {
        return concatDDID(0D, 0D, 0, 0D);
    }

    public static String testConcatLVTDDID() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        int var2 = -957256832;
        double var3 = 2.5D;
        return concatDDID(var0, var1, var2, var3);
    }

    private static String concatDDIL(double var0, double var1, int var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDDIL() {
        return concatDDIL(0D, 0D, 0, null);
    }

    public static String testConcatLVTDDIL() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        int var2 = -957256832;
        Object var3 = Boolean.TRUE;
        return concatDDIL(var0, var1, var2, var3);
    }

    private static String concatDDDI(double var0, double var1, double var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDDDI() {
        return concatDDDI(0D, 0D, 0D, 0);
    }

    public static String testConcatLVTDDDI() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        double var2 = 2.5D;
        int var3 = -957256832;
        return concatDDDI(var0, var1, var2, var3);
    }

    private static String concatDDDD(double var0, double var1, double var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDDDD() {
        return concatDDDD(0D, 0D, 0D, 0D);
    }

    public static String testConcatLVTDDDD() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        double var2 = 2.5D;
        double var3 = 2.5D;
        return concatDDDD(var0, var1, var2, var3);
    }

    private static String concatDDDL(double var0, double var1, double var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDDDL() {
        return concatDDDL(0D, 0D, 0D, null);
    }

    public static String testConcatLVTDDDL() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        double var2 = 2.5D;
        Object var3 = Boolean.TRUE;
        return concatDDDL(var0, var1, var2, var3);
    }

    private static String concatDDLI(double var0, double var1, Object var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDDLI() {
        return concatDDLI(0D, 0D, null, 0);
    }

    public static String testConcatLVTDDLI() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        int var3 = -957256832;
        return concatDDLI(var0, var1, var2, var3);
    }

    private static String concatDDLD(double var0, double var1, Object var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDDLD() {
        return concatDDLD(0D, 0D, null, 0D);
    }

    public static String testConcatLVTDDLD() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        double var3 = 2.5D;
        return concatDDLD(var0, var1, var2, var3);
    }

    private static String concatDDLL(double var0, double var1, Object var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDDLL() {
        return concatDDLL(0D, 0D, null, null);
    }

    public static String testConcatLVTDDLL() {
        double var0 = 2.5D;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        Object var3 = Boolean.TRUE;
        return concatDDLL(var0, var1, var2, var3);
    }

    private static String concatDLII(double var0, Object var1, int var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDLII() {
        return concatDLII(0D, null, 0, 0);
    }

    public static String testConcatLVTDLII() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        int var3 = -957256832;
        return concatDLII(var0, var1, var2, var3);
    }

    private static String concatDLID(double var0, Object var1, int var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDLID() {
        return concatDLID(0D, null, 0, 0D);
    }

    public static String testConcatLVTDLID() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        double var3 = 2.5D;
        return concatDLID(var0, var1, var2, var3);
    }

    private static String concatDLIL(double var0, Object var1, int var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDLIL() {
        return concatDLIL(0D, null, 0, null);
    }

    public static String testConcatLVTDLIL() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        Object var3 = Boolean.TRUE;
        return concatDLIL(var0, var1, var2, var3);
    }

    private static String concatDLDI(double var0, Object var1, double var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDLDI() {
        return concatDLDI(0D, null, 0D, 0);
    }

    public static String testConcatLVTDLDI() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        int var3 = -957256832;
        return concatDLDI(var0, var1, var2, var3);
    }

    private static String concatDLDD(double var0, Object var1, double var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDLDD() {
        return concatDLDD(0D, null, 0D, 0D);
    }

    public static String testConcatLVTDLDD() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        double var3 = 2.5D;
        return concatDLDD(var0, var1, var2, var3);
    }

    private static String concatDLDL(double var0, Object var1, double var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDLDL() {
        return concatDLDL(0D, null, 0D, null);
    }

    public static String testConcatLVTDLDL() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        Object var3 = Boolean.TRUE;
        return concatDLDL(var0, var1, var2, var3);
    }

    private static String concatDLLI(double var0, Object var1, Object var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDLLI() {
        return concatDLLI(0D, null, null, 0);
    }

    public static String testConcatLVTDLLI() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        int var3 = -957256832;
        return concatDLLI(var0, var1, var2, var3);
    }

    private static String concatDLLD(double var0, Object var1, Object var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDLLD() {
        return concatDLLD(0D, null, null, 0D);
    }

    public static String testConcatLVTDLLD() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        double var3 = 2.5D;
        return concatDLLD(var0, var1, var2, var3);
    }

    private static String concatDLLL(double var0, Object var1, Object var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectDLLL() {
        return concatDLLL(0D, null, null, null);
    }

    public static String testConcatLVTDLLL() {
        double var0 = 2.5D;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        Object var3 = Boolean.TRUE;
        return concatDLLL(var0, var1, var2, var3);
    }

    private static String concatLIII(Object var0, int var1, int var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLIII() {
        return concatLIII(null, 0, 0, 0);
    }

    public static String testConcatLVTLIII() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        int var2 = -957256832;
        int var3 = -957256832;
        return concatLIII(var0, var1, var2, var3);
    }

    private static String concatLIID(Object var0, int var1, int var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLIID() {
        return concatLIID(null, 0, 0, 0D);
    }

    public static String testConcatLVTLIID() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        int var2 = -957256832;
        double var3 = 2.5D;
        return concatLIID(var0, var1, var2, var3);
    }

    private static String concatLIIL(Object var0, int var1, int var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLIIL() {
        return concatLIIL(null, 0, 0, null);
    }

    public static String testConcatLVTLIIL() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        int var2 = -957256832;
        Object var3 = Boolean.TRUE;
        return concatLIIL(var0, var1, var2, var3);
    }

    private static String concatLIDI(Object var0, int var1, double var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLIDI() {
        return concatLIDI(null, 0, 0D, 0);
    }

    public static String testConcatLVTLIDI() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        double var2 = 2.5D;
        int var3 = -957256832;
        return concatLIDI(var0, var1, var2, var3);
    }

    private static String concatLIDD(Object var0, int var1, double var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLIDD() {
        return concatLIDD(null, 0, 0D, 0D);
    }

    public static String testConcatLVTLIDD() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        double var2 = 2.5D;
        double var3 = 2.5D;
        return concatLIDD(var0, var1, var2, var3);
    }

    private static String concatLIDL(Object var0, int var1, double var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLIDL() {
        return concatLIDL(null, 0, 0D, null);
    }

    public static String testConcatLVTLIDL() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        double var2 = 2.5D;
        Object var3 = Boolean.TRUE;
        return concatLIDL(var0, var1, var2, var3);
    }

    private static String concatLILI(Object var0, int var1, Object var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLILI() {
        return concatLILI(null, 0, null, 0);
    }

    public static String testConcatLVTLILI() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        int var3 = -957256832;
        return concatLILI(var0, var1, var2, var3);
    }

    private static String concatLILD(Object var0, int var1, Object var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLILD() {
        return concatLILD(null, 0, null, 0D);
    }

    public static String testConcatLVTLILD() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        double var3 = 2.5D;
        return concatLILD(var0, var1, var2, var3);
    }

    private static String concatLILL(Object var0, int var1, Object var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLILL() {
        return concatLILL(null, 0, null, null);
    }

    public static String testConcatLVTLILL() {
        Object var0 = Boolean.TRUE;
        int var1 = -957256832;
        Object var2 = Boolean.TRUE;
        Object var3 = Boolean.TRUE;
        return concatLILL(var0, var1, var2, var3);
    }

    private static String concatLDII(Object var0, double var1, int var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLDII() {
        return concatLDII(null, 0D, 0, 0);
    }

    public static String testConcatLVTLDII() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        int var2 = -957256832;
        int var3 = -957256832;
        return concatLDII(var0, var1, var2, var3);
    }

    private static String concatLDID(Object var0, double var1, int var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLDID() {
        return concatLDID(null, 0D, 0, 0D);
    }

    public static String testConcatLVTLDID() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        int var2 = -957256832;
        double var3 = 2.5D;
        return concatLDID(var0, var1, var2, var3);
    }

    private static String concatLDIL(Object var0, double var1, int var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLDIL() {
        return concatLDIL(null, 0D, 0, null);
    }

    public static String testConcatLVTLDIL() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        int var2 = -957256832;
        Object var3 = Boolean.TRUE;
        return concatLDIL(var0, var1, var2, var3);
    }

    private static String concatLDDI(Object var0, double var1, double var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLDDI() {
        return concatLDDI(null, 0D, 0D, 0);
    }

    public static String testConcatLVTLDDI() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        double var2 = 2.5D;
        int var3 = -957256832;
        return concatLDDI(var0, var1, var2, var3);
    }

    private static String concatLDDD(Object var0, double var1, double var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLDDD() {
        return concatLDDD(null, 0D, 0D, 0D);
    }

    public static String testConcatLVTLDDD() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        double var2 = 2.5D;
        double var3 = 2.5D;
        return concatLDDD(var0, var1, var2, var3);
    }

    private static String concatLDDL(Object var0, double var1, double var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLDDL() {
        return concatLDDL(null, 0D, 0D, null);
    }

    public static String testConcatLVTLDDL() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        double var2 = 2.5D;
        Object var3 = Boolean.TRUE;
        return concatLDDL(var0, var1, var2, var3);
    }

    private static String concatLDLI(Object var0, double var1, Object var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLDLI() {
        return concatLDLI(null, 0D, null, 0);
    }

    public static String testConcatLVTLDLI() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        int var3 = -957256832;
        return concatLDLI(var0, var1, var2, var3);
    }

    private static String concatLDLD(Object var0, double var1, Object var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLDLD() {
        return concatLDLD(null, 0D, null, 0D);
    }

    public static String testConcatLVTLDLD() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        double var3 = 2.5D;
        return concatLDLD(var0, var1, var2, var3);
    }

    private static String concatLDLL(Object var0, double var1, Object var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLDLL() {
        return concatLDLL(null, 0D, null, null);
    }

    public static String testConcatLVTLDLL() {
        Object var0 = Boolean.TRUE;
        double var1 = 2.5D;
        Object var2 = Boolean.TRUE;
        Object var3 = Boolean.TRUE;
        return concatLDLL(var0, var1, var2, var3);
    }

    private static String concatLLII(Object var0, Object var1, int var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLLII() {
        return concatLLII(null, null, 0, 0);
    }

    public static String testConcatLVTLLII() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        int var3 = -957256832;
        return concatLLII(var0, var1, var2, var3);
    }

    private static String concatLLID(Object var0, Object var1, int var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLLID() {
        return concatLLID(null, null, 0, 0D);
    }

    public static String testConcatLVTLLID() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        double var3 = 2.5D;
        return concatLLID(var0, var1, var2, var3);
    }

    private static String concatLLIL(Object var0, Object var1, int var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLLIL() {
        return concatLLIL(null, null, 0, null);
    }

    public static String testConcatLVTLLIL() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        int var2 = -957256832;
        Object var3 = Boolean.TRUE;
        return concatLLIL(var0, var1, var2, var3);
    }

    private static String concatLLDI(Object var0, Object var1, double var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLLDI() {
        return concatLLDI(null, null, 0D, 0);
    }

    public static String testConcatLVTLLDI() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        int var3 = -957256832;
        return concatLLDI(var0, var1, var2, var3);
    }

    private static String concatLLDD(Object var0, Object var1, double var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLLDD() {
        return concatLLDD(null, null, 0D, 0D);
    }

    public static String testConcatLVTLLDD() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        double var3 = 2.5D;
        return concatLLDD(var0, var1, var2, var3);
    }

    private static String concatLLDL(Object var0, Object var1, double var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLLDL() {
        return concatLLDL(null, null, 0D, null);
    }

    public static String testConcatLVTLLDL() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        double var2 = 2.5D;
        Object var3 = Boolean.TRUE;
        return concatLLDL(var0, var1, var2, var3);
    }

    private static String concatLLLI(Object var0, Object var1, Object var2, int var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLLLI() {
        return concatLLLI(null, null, null, 0);
    }

    public static String testConcatLVTLLLI() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        int var3 = -957256832;
        return concatLLLI(var0, var1, var2, var3);
    }

    private static String concatLLLD(Object var0, Object var1, Object var2, double var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLLLD() {
        return concatLLLD(null, null, null, 0D);
    }

    public static String testConcatLVTLLLD() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        double var3 = 2.5D;
        return concatLLLD(var0, var1, var2, var3);
    }

    private static String concatLLLL(Object var0, Object var1, Object var2, Object var3) {
        return java.util.Objects.toString(var0) + java.util.Objects.toString(var1) + java.util.Objects.toString(var2) + java.util.Objects.toString(var3);
    }

    public static String testConcatDirectLLLL() {
        return concatLLLL(null, null, null, null);
    }

    public static String testConcatLVTLLLL() {
        Object var0 = Boolean.TRUE;
        Object var1 = Boolean.TRUE;
        Object var2 = Boolean.TRUE;
        Object var3 = Boolean.TRUE;
        return concatLLLL(var0, var1, var2, var3);
    }
}
