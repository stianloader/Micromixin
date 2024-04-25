package org.stianloader.micromixin.test.j8;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;
import org.stianloader.micromixin.test.j8.localsprinting.LocalPrintingWitnesses;
import org.stianloader.micromixin.test.j8.targets.AllowTest;
import org.stianloader.micromixin.test.j8.targets.ArgumentCaptureTest;
import org.stianloader.micromixin.test.j8.targets.InjectionHeadTest;
import org.stianloader.micromixin.test.j8.targets.InjectorRemapTest;
import org.stianloader.micromixin.test.j8.targets.LocalCaptureTest;
import org.stianloader.micromixin.test.j8.targets.MixinOverwriteTest;
import org.stianloader.micromixin.test.j8.targets.ModifyArgTest;
import org.stianloader.micromixin.test.j8.targets.ModifyConstantAuxiliaryTest;
import org.stianloader.micromixin.test.j8.targets.ModifyConstantTest;
import org.stianloader.micromixin.test.j8.targets.MultiInjectTest;
import org.stianloader.micromixin.test.j8.targets.SliceTest;
import org.stianloader.micromixin.test.j8.targets.SliceTest.AmbigiousSliceTest;
import org.stianloader.micromixin.test.j8.targets.StringTargetTest;
import org.stianloader.micromixin.test.j8.targets.invalid.InjectorStackPosioningTest;
import org.stianloader.micromixin.test.j8.targets.invalid.InjectorStackPosioningTest.IllegalPoison;
import org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueInvalidTargetInsnTest;
import org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTest;
import org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueVisibilityTest;
import org.stianloader.micromixin.test.j8.targets.redirect.GenericInvoker;

public class TestHarness {

    public static TestReport runAllTests() {
        TestReport report = new TestReport();
        runClassloadingFailures(report);
        runInjectionHeadTests(report);
        runMultiInjectionTests(report);
        runOverwriteTests(report);
        runInjectorRemapTests(report);
        runLocalCaputureTest(report);
        runArgumentCaptureTest(report);
        runLocalPrintingTest(report);
        runRedirectTest(report);
        runModifyReturnValuesTest2(report);
        runModifyReturnValuesTest(report);
        runModifyArgTest(report);
        runSliceTest(report);
        runModifyConstantTest(report);
        runModifyConstantAuxTest(report);
        runStringTargetTest(report);
        runAllowTest(report);
        return report;
    }

    public static void runAllowTest(@NotNull TestReport report) {
        TestSet set = new TestSet();

        set.addUnitAssertEquals("AllowTest.testNegativeAllowInject", AllowTest::testNegativeAllowInject, true);
        set.addUnitAssertEquals("AllowTest.testNegativeAllowModifyArg", AllowTest::testNegativeAllowModifyArg, true);
        set.addUnitAssertEquals("AllowTest.testNegativeAllowModifyConstant", AllowTest::testNegativeAllowModifyConstant, true);
        set.addUnitAssertEquals("AllowTest.testNegativeAllowModifyReturnValue", AllowTest::testNegativeAllowModifyReturnValue, true);
        set.addUnitAssertEquals("AllowTest.testNegativeAllowRedirect", AllowTest::testNegativeAllowRedirect, true);

        set.addUnitAssertEquals("AllowTest.testZeroAllowInject", AllowTest::testZeroAllowInject, true);
        set.addUnitAssertEquals("AllowTest.testZeroAllowModifyArg", AllowTest::testZeroAllowModifyArg, true);
        set.addUnitAssertEquals("AllowTest.testZeroAllowModifyConstant", AllowTest::testZeroAllowModifyConstant, true);
        set.addUnitAssertEquals("AllowTest.testZeroAllowModifyReturnValue", AllowTest::testZeroAllowModifyReturnValue, true);
        set.addUnitAssertEquals("AllowTest.testZeroAllowRedirect", AllowTest::testZeroAllowRedirect, true);

        set.addUnitAssertEquals("AllowTest.testOneAllowInject", AllowTest::testOneAllowInject, true);
        set.addUnitAssertEquals("AllowTest.testOneAllowModifyArg", AllowTest::testOneAllowModifyArg, true);
        set.addUnitAssertEquals("AllowTest.testOneAllowModifyConstant", AllowTest::testOneAllowModifyConstant, true);
        set.addUnitAssertEquals("AllowTest.testOneAllowModifyReturnValue", AllowTest::testOneAllowModifyReturnValue, true);
        set.addUnitAssertEquals("AllowTest.testOneAllowRedirect", AllowTest::testOneAllowRedirect, true);

        set.addUnit("AllowTest.testHigherExpectInject", () -> AllowTest.testHigherExpectInject(new MutableInt()));
        set.addUnit("AllowTest.testHigherExpectModifyArg", () -> AllowTest.testHigherExpectModifyArg(new MutableInt()));
        set.addUnit("AllowTest.testHigherExpectModifyConstant", () -> AllowTest.testHigherExpectModifyConstant(new MutableInt()));
        set.addUnit("AllowTest.testHigherExpectRedirect", () -> AllowTest.testHigherExpectRedirect(new MutableInt()));

        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.AllowTest$TooFewInject");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.AllowTest$TooFewModifyArg");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.AllowTest$TooFewModifyConstant");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.AllowTest$TooFewRedirect");

        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.AllowTest$TooManyInject");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.AllowTest$TooManyModifyArg");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.AllowTest$TooManyModifyConstant");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.AllowTest$TooManyRedirect");

        LoggerFactory.getLogger(TestHarness.class).info("AllowTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runStringTargetTest(@NotNull TestReport report) {
        TestSet set = new TestSet();
        set.addUnit("StringTargetTest.loadDotMixinTarget", StringTargetTest::loadDotMixinTarget);
        set.addUnit("StringTargetTest.loadDetchedReturnDescriptorTarget", StringTargetTest::loadDetchedReturnDescriptorTarget);
        set.addUnit("StringTargetTest.loadSlashMixinTarget", StringTargetTest::loadSlashMixinTarget);
        set.addUnit("StringTargetTest.loadSlashDotMixinTarget", StringTargetTest::loadSlashDotMixinTarget);
        set.addUnitExpectThrow("StringTargetTest.loadFullSlashMixinTarget", StringTargetTest::loadFullSlashMixinTarget, AssertionError.class);
        set.addUnitExpectThrow("StringTargetTest.loadFullDotMixinTarget", StringTargetTest::loadFullDotMixinTarget, AssertionError.class);
        set.addUnit("StringTargetTest.loadWhitespaceDescriptorTarget0", StringTargetTest::loadWhitespaceDescriptorTarget0);
        set.addUnit("StringTargetTest.loadWhitespaceDescriptorTarget1", StringTargetTest::loadWhitespaceDescriptorTarget1);
        set.addUnit("StringTargetTest.loadWhitespaceDescriptorTarget2", StringTargetTest::loadWhitespaceDescriptorTarget2);
        set.addUnit("StringTargetTest.loadWhitespaceDescriptorTarget3", StringTargetTest::loadWhitespaceDescriptorTarget3);
        set.addUnit("StringTargetTest.loadWhitespaceDescriptorTarget4", StringTargetTest::loadWhitespaceDescriptorTarget4);
        set.addUnitExpectThrow("StringTargetTest.loadWhitespaceMixinTarget", StringTargetTest::loadWhitespaceMixinTarget, AssertionError.class);
        set.addUnit("StringTargetTest.loadWhitespaceNameTarget", StringTargetTest::loadWhitespaceNameTarget);

        LoggerFactory.getLogger(TestHarness.class).info("StringTargetTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runModifyConstantAuxTest(@NotNull TestReport report) {
        TestSet set = new TestSet();

        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.untargetableConstantVoidImplicit", ModifyConstantAuxiliaryTest::untargetableConstantVoidImplicit, void.class);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.untargetableConstantVoidExplicit", ModifyConstantAuxiliaryTest::untargetableConstantVoidExplicit, void.class);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.untargetableConstantIntImplicit", ModifyConstantAuxiliaryTest::untargetableConstantIntImplicit, int.class);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.untargetableConstantIntExplicit", ModifyConstantAuxiliaryTest::untargetableConstantIntExplicit, int.class);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.targetableConstantSelfImplicit", ModifyConstantAuxiliaryTest::targetableConstantSelfImplicit, null);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.targetableConstantSelfExplicit", ModifyConstantAuxiliaryTest::targetableConstantSelfExplicit, null);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.slicedConstant0 (false)", () -> ModifyConstantAuxiliaryTest.slicedConstant0(false), 60);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.slicedConstant0 (true)", () -> ModifyConstantAuxiliaryTest.slicedConstant0(true), 135);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.slicedConstant1 (false)", () -> ModifyConstantAuxiliaryTest.slicedConstant1(false), 60);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.slicedConstant1 (true)", () -> ModifyConstantAuxiliaryTest.slicedConstant1(true), 135);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureMultipleImplicit0", ModifyConstantAuxiliaryTest::captureMultipleImplicit0, 15);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureMultipleExplicit0", ModifyConstantAuxiliaryTest::captureMultipleExplicit0, 15);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureMultipleExplicit0B", ModifyConstantAuxiliaryTest::captureMultipleExplicit0B, 15);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureMultipleImplicit1", ModifyConstantAuxiliaryTest::captureMultipleImplicit1, 45);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureMultipleExplicit1", ModifyConstantAuxiliaryTest::captureMultipleExplicit1, 45);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureIfZ0", ModifyConstantAuxiliaryTest::captureIfZ0, 16);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureIfZ1", ModifyConstantAuxiliaryTest::captureIfZ1, 16);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureIfI0", ModifyConstantAuxiliaryTest::captureIfI0, 16);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureIfI1", ModifyConstantAuxiliaryTest::captureIfI1, 16);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureIfI2", ModifyConstantAuxiliaryTest::captureIfI2, 16);
        set.addUnitAssertEquals("ModifyConstantAuxiliaryTest.captureIfI3", ModifyConstantAuxiliaryTest::captureIfI3, 16);

        LoggerFactory.getLogger(TestHarness.class).info("ModifyConstantAuxiliaryTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runModifyConstantTest(@NotNull TestReport report) {
        /* following code is generated - do not touch directly. */
        TestSet set = new TestSet();

        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitI", ModifyConstantTest::modifyRetExplicitI, -957256832);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitI", ModifyConstantTest::modifyRetImplicitI, -957256832);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitJ", ModifyConstantTest::modifyRetExplicitJ, Long.MAX_VALUE);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitJ", ModifyConstantTest::modifyRetImplicitJ, Long.MAX_VALUE);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitS", ModifyConstantTest::modifyRetExplicitS, ((short) -58));
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitS", ModifyConstantTest::modifyRetImplicitS, ((short) -58));
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitC", ModifyConstantTest::modifyRetExplicitC, 'c');
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitC", ModifyConstantTest::modifyRetImplicitC, 'c');
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitB", ModifyConstantTest::modifyRetExplicitB, ((byte) 126));
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitB", ModifyConstantTest::modifyRetImplicitB, ((byte) 126));
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitZ", ModifyConstantTest::modifyRetExplicitZ, true);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitZ", ModifyConstantTest::modifyRetImplicitZ, true);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitF", ModifyConstantTest::modifyRetExplicitF, 1.2F);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitF", ModifyConstantTest::modifyRetImplicitF, 1.2F);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitD", ModifyConstantTest::modifyRetExplicitD, 2.5D);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitD", ModifyConstantTest::modifyRetImplicitD, 2.5D);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitStr", ModifyConstantTest::modifyRetExplicitStr, "Test2");
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitStr", ModifyConstantTest::modifyRetImplicitStr, "Test2");
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitNulStr2", ModifyConstantTest::modifyRetExplicitNulStr2, null);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitNulStr2", ModifyConstantTest::modifyRetImplicitNulStr2, null);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitEmptyStr", ModifyConstantTest::modifyRetExplicitEmptyStr, "");
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitEmptyStr", ModifyConstantTest::modifyRetImplicitEmptyStr, "");
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetExplicitNulClass2", ModifyConstantTest::modifyRetExplicitNulClass2, null);
        set.addUnitAssertEquals("ModifyConstantTest.modifyRetImplicitNulClass2", ModifyConstantTest::modifyRetImplicitNulClass2, null);

        LoggerFactory.getLogger(TestHarness.class).info("ModifyConstantTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runSliceTest(@NotNull TestReport report) {
        TestSet set = new TestSet();

        set.addUnit("SliceTest.sliceTest0Inject", SliceTest::sliceTest0Inject);
        set.addUnit("SliceTest.sliceTest0ModifyArg", SliceTest::sliceTest0ModifyArg);
        set.addUnit("SliceTest.sliceTest0Redirect", SliceTest::sliceTest0Redirect);
        set.addUnit("SliceTest.sliceTest1ModifyArg", SliceTest::sliceTest1ModifyArg);
        set.addUnit("SliceTest.sliceTest1Redirect", SliceTest::sliceTest1Redirect);
        set.addUnitAssertEquals("SliceTest.sliceTest1Redirect{1}", () -> SliceTest.sliceTest2Inject(-1), -1);
        set.addUnitAssertEquals("SliceTest.sliceTest1Redirect{2}", () -> SliceTest.sliceTest2Inject(6), 30);
        set.addUnitAssertEquals("SliceTest.sliceTest1Redirect{3}", () -> SliceTest.sliceTest2Inject(4), 3);
        set.addUnitAssertEquals("SliceTest.sliceTest2ModifyReturnValue{1}", () -> SliceTest.sliceTest2ModifyReturnValue(-1), -1);
        set.addUnitAssertEquals("SliceTest.sliceTest2ModifyReturnValue{2}", () -> SliceTest.sliceTest2ModifyReturnValue(6), 30);
        set.addUnitAssertEquals("SliceTest.sliceTest2ModifyReturnValue{3}", () -> SliceTest.sliceTest2ModifyReturnValue(4), 3);
        set.addUnitAssertEquals("SliceTest.sliceTest3ModifyReturnValue{1}", () -> SliceTest.sliceTest3ModifyReturnValue(-1), -1);
        set.addUnitAssertEquals("SliceTest.sliceTest3ModifyReturnValue{2}", () -> SliceTest.sliceTest3ModifyReturnValue(6), 30);
        set.addUnitAssertEquals("SliceTest.sliceTest3ModifyReturnValue{3}", () -> SliceTest.sliceTest3ModifyReturnValue(4), 6);
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.invalid.InvalidDuplicateSliceTestMixins");
        set.addUnit("SliceTest.sliceTest4InjectA", () -> SliceTest.sliceTest4InjectA(new MutableInt(2)));
        set.addUnitAssertEquals("SliceTest.sliceTest4InjectB", () -> SliceTest.sliceTest4InjectB().intValue(), 22);
        set.addUnit("SliceTest.sliceTest4InjectC", SliceTest::sliceTest4InjectC);
        set.addUnitAssertEquals("SliceTest.sliceTest4InjectD{1}", () -> SliceTest.sliceTest4InjectD(new MutableInt(0)).intValue(), 9);
        set.addUnitAssertEquals("SliceTest.sliceTest4InjectD{2}", () -> SliceTest.sliceTest4InjectD(new MutableInt(9)).intValue(), 22);
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.SliceTest$InvalidlyExcludedTailTest");
        AmbigiousSliceTest.class.toString();

        LoggerFactory.getLogger(TestHarness.class).info("SliceTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runModifyArgTest(@NotNull TestReport report) {
        /* following code is generated - do not touch directly. */
        TestSet set = new TestSet();

        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectI", ModifyArgTest::testConcatDirectI, "-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTI", ModifyArgTest::testConcatLVTI, "0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectD", ModifyArgTest::testConcatDirectD, "2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTD", ModifyArgTest::testConcatLVTD, "0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectL", ModifyArgTest::testConcatDirectL, "true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTL", ModifyArgTest::testConcatLVTL, "null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectII", ModifyArgTest::testConcatDirectII, "-9572568320");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTII", ModifyArgTest::testConcatLVTII, "0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectID", ModifyArgTest::testConcatDirectID, "02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTID", ModifyArgTest::testConcatLVTID, "-9572568320.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIL", ModifyArgTest::testConcatDirectIL, "0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIL", ModifyArgTest::testConcatLVTIL, "-957256832null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDI", ModifyArgTest::testConcatDirectDI, "2.50");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDI", ModifyArgTest::testConcatLVTDI, "0.0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDD", ModifyArgTest::testConcatDirectDD, "2.50.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDD", ModifyArgTest::testConcatLVTDD, "0.02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDL", ModifyArgTest::testConcatDirectDL, "0.0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDL", ModifyArgTest::testConcatLVTDL, "2.5null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLI", ModifyArgTest::testConcatDirectLI, "true0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLI", ModifyArgTest::testConcatLVTLI, "null-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLD", ModifyArgTest::testConcatDirectLD, "null2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLD", ModifyArgTest::testConcatLVTLD, "true0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLL", ModifyArgTest::testConcatDirectLL, "truenull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLL", ModifyArgTest::testConcatLVTLL, "nulltrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIII", ModifyArgTest::testConcatDirectIII, "-95725683200");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIII", ModifyArgTest::testConcatLVTIII, "0-957256832-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIID", ModifyArgTest::testConcatDirectIID, "0-9572568320.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIID", ModifyArgTest::testConcatLVTIID, "-95725683202.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIIL", ModifyArgTest::testConcatDirectIIL, "00true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIIL", ModifyArgTest::testConcatLVTIIL, "-957256832-957256832null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDI", ModifyArgTest::testConcatDirectIDI, "-9572568320.00");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDI", ModifyArgTest::testConcatLVTIDI, "02.5-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDD", ModifyArgTest::testConcatDirectIDD, "-9572568320.00.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDD", ModifyArgTest::testConcatLVTIDD, "02.52.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDL", ModifyArgTest::testConcatDirectIDL, "02.5null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDL", ModifyArgTest::testConcatLVTIDL, "-9572568320.0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILI", ModifyArgTest::testConcatDirectILI, "-957256832null0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILI", ModifyArgTest::testConcatLVTILI, "0true-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILD", ModifyArgTest::testConcatDirectILD, "0null2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILD", ModifyArgTest::testConcatLVTILD, "-957256832true0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILL", ModifyArgTest::testConcatDirectILL, "-957256832nullnull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILL", ModifyArgTest::testConcatLVTILL, "0truetrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDII", ModifyArgTest::testConcatDirectDII, "0.0-9572568320");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDII", ModifyArgTest::testConcatLVTDII, "2.50-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDID", ModifyArgTest::testConcatDirectDID, "0.0-9572568320.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDID", ModifyArgTest::testConcatLVTDID, "2.502.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDIL", ModifyArgTest::testConcatDirectDIL, "0.00true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDIL", ModifyArgTest::testConcatLVTDIL, "2.5-957256832null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDI", ModifyArgTest::testConcatDirectDDI, "2.50.00");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDI", ModifyArgTest::testConcatLVTDDI, "0.02.5-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDD", ModifyArgTest::testConcatDirectDDD, "0.02.50.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDD", ModifyArgTest::testConcatLVTDDD, "2.50.02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDL", ModifyArgTest::testConcatDirectDDL, "0.00.0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDL", ModifyArgTest::testConcatLVTDDL, "2.52.5null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLI", ModifyArgTest::testConcatDirectDLI, "0.0null-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLI", ModifyArgTest::testConcatLVTDLI, "2.5true0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLD", ModifyArgTest::testConcatDirectDLD, "0.0true0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLD", ModifyArgTest::testConcatLVTDLD, "2.5null2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLL", ModifyArgTest::testConcatDirectDLL, "0.0truenull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLL", ModifyArgTest::testConcatLVTDLL, "2.5nulltrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLII", ModifyArgTest::testConcatDirectLII, "null0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLII", ModifyArgTest::testConcatLVTLII, "true-9572568320");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLID", ModifyArgTest::testConcatDirectLID, "null02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLID", ModifyArgTest::testConcatLVTLID, "true-9572568320.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLIL", ModifyArgTest::testConcatDirectLIL, "null0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLIL", ModifyArgTest::testConcatLVTLIL, "true-957256832null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDI", ModifyArgTest::testConcatDirectLDI, "null2.50");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDI", ModifyArgTest::testConcatLVTLDI, "true0.0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDD", ModifyArgTest::testConcatDirectLDD, "null0.02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDD", ModifyArgTest::testConcatLVTLDD, "true2.50.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDL", ModifyArgTest::testConcatDirectLDL, "null0.0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDL", ModifyArgTest::testConcatLVTLDL, "true2.5null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLI", ModifyArgTest::testConcatDirectLLI, "truenull0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLI", ModifyArgTest::testConcatLVTLLI, "nulltrue-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLD", ModifyArgTest::testConcatDirectLLD, "nulltrue0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLD", ModifyArgTest::testConcatLVTLLD, "truenull2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLL", ModifyArgTest::testConcatDirectLLL, "nullnulltrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLL", ModifyArgTest::testConcatLVTLLL, "truetruenull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIIII", ModifyArgTest::testConcatDirectIIII, "-957256832000");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIIII", ModifyArgTest::testConcatLVTIIII, "0-957256832-957256832-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIIID", ModifyArgTest::testConcatDirectIIID, "0-95725683200.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIIID", ModifyArgTest::testConcatLVTIIID, "-9572568320-9572568322.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIIIL", ModifyArgTest::testConcatDirectIIIL, "00-957256832null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIIIL", ModifyArgTest::testConcatLVTIIIL, "-957256832-9572568320true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIIDI", ModifyArgTest::testConcatDirectIIDI, "000.0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIIDI", ModifyArgTest::testConcatLVTIIDI, "-957256832-9572568322.50");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIIDD", ModifyArgTest::testConcatDirectIIDD, "-95725683200.00.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIIDD", ModifyArgTest::testConcatLVTIIDD, "0-9572568322.52.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIIDL", ModifyArgTest::testConcatDirectIIDL, "0-9572568320.0null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIIDL", ModifyArgTest::testConcatLVTIIDL, "-95725683202.5true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIILI", ModifyArgTest::testConcatDirectIILI, "00true0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIILI", ModifyArgTest::testConcatLVTIILI, "-957256832-957256832null-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIILD", ModifyArgTest::testConcatDirectIILD, "00null2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIILD", ModifyArgTest::testConcatLVTIILD, "-957256832-957256832true0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIILL", ModifyArgTest::testConcatDirectIILL, "-9572568320nullnull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIILL", ModifyArgTest::testConcatLVTIILL, "0-957256832truetrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDII", ModifyArgTest::testConcatDirectIDII, "02.500");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDII", ModifyArgTest::testConcatLVTIDII, "-9572568320.0-957256832-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDID", ModifyArgTest::testConcatDirectIDID, "00.0-9572568320.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDID", ModifyArgTest::testConcatLVTIDID, "-9572568322.502.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDIL", ModifyArgTest::testConcatDirectIDIL, "00.00true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDIL", ModifyArgTest::testConcatLVTIDIL, "-9572568322.5-957256832null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDDI", ModifyArgTest::testConcatDirectIDDI, "-9572568320.00.00");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDDI", ModifyArgTest::testConcatLVTIDDI, "02.52.5-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDDD", ModifyArgTest::testConcatDirectIDDD, "00.00.02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDDD", ModifyArgTest::testConcatLVTIDDD, "-9572568322.52.50.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDDL", ModifyArgTest::testConcatDirectIDDL, "-9572568320.00.0null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDDL", ModifyArgTest::testConcatLVTIDDL, "02.52.5true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDLI", ModifyArgTest::testConcatDirectIDLI, "00.0null-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDLI", ModifyArgTest::testConcatLVTIDLI, "-9572568322.5true0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDLD", ModifyArgTest::testConcatDirectIDLD, "02.5null0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDLD", ModifyArgTest::testConcatLVTIDLD, "-9572568320.0true2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectIDLL", ModifyArgTest::testConcatDirectIDLL, "00.0truenull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTIDLL", ModifyArgTest::testConcatLVTIDLL, "-9572568322.5nulltrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILII", ModifyArgTest::testConcatDirectILII, "0null-9572568320");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILII", ModifyArgTest::testConcatLVTILII, "-957256832true0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILID", ModifyArgTest::testConcatDirectILID, "0null02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILID", ModifyArgTest::testConcatLVTILID, "-957256832true-9572568320.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILIL", ModifyArgTest::testConcatDirectILIL, "-957256832null0null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILIL", ModifyArgTest::testConcatLVTILIL, "0true-957256832true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILDI", ModifyArgTest::testConcatDirectILDI, "0true0.00");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILDI", ModifyArgTest::testConcatLVTILDI, "-957256832null2.5-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILDD", ModifyArgTest::testConcatDirectILDD, "0null2.50.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILDD", ModifyArgTest::testConcatLVTILDD, "-957256832true0.02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILDL", ModifyArgTest::testConcatDirectILDL, "0null0.0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILDL", ModifyArgTest::testConcatLVTILDL, "-957256832true2.5null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILLI", ModifyArgTest::testConcatDirectILLI, "-957256832nullnull0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILLI", ModifyArgTest::testConcatLVTILLI, "0truetrue-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILLD", ModifyArgTest::testConcatDirectILLD, "-957256832nullnull0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILLD", ModifyArgTest::testConcatLVTILLD, "0truetrue2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectILLL", ModifyArgTest::testConcatDirectILLL, "0truenullnull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTILLL", ModifyArgTest::testConcatLVTILLL, "-957256832nulltruetrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDIII", ModifyArgTest::testConcatDirectDIII, "0.00-9572568320");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDIII", ModifyArgTest::testConcatLVTDIII, "2.5-9572568320-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDIID", ModifyArgTest::testConcatDirectDIID, "2.5000.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDIID", ModifyArgTest::testConcatLVTDIID, "0.0-957256832-9572568322.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDIIL", ModifyArgTest::testConcatDirectDIIL, "2.500null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDIIL", ModifyArgTest::testConcatLVTDIIL, "0.0-957256832-957256832true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDIDI", ModifyArgTest::testConcatDirectDIDI, "0.002.50");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDIDI", ModifyArgTest::testConcatLVTDIDI, "2.5-9572568320.0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDIDD", ModifyArgTest::testConcatDirectDIDD, "0.000.02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDIDD", ModifyArgTest::testConcatLVTDIDD, "2.5-9572568322.50.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDIDL", ModifyArgTest::testConcatDirectDIDL, "2.500.0null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDIDL", ModifyArgTest::testConcatLVTDIDL, "0.0-9572568322.5true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDILI", ModifyArgTest::testConcatDirectDILI, "0.00null-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDILI", ModifyArgTest::testConcatLVTDILI, "2.5-957256832true0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDILD", ModifyArgTest::testConcatDirectDILD, "0.00true0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDILD", ModifyArgTest::testConcatLVTDILD, "2.5-957256832null2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDILL", ModifyArgTest::testConcatDirectDILL, "2.50nullnull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDILL", ModifyArgTest::testConcatLVTDILL, "0.0-957256832truetrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDII", ModifyArgTest::testConcatDirectDDII, "2.50.000");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDII", ModifyArgTest::testConcatLVTDDII, "0.02.5-957256832-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDID", ModifyArgTest::testConcatDirectDDID, "0.02.500.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDID", ModifyArgTest::testConcatLVTDDID, "2.50.0-9572568322.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDIL", ModifyArgTest::testConcatDirectDDIL, "0.00.0-957256832null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDIL", ModifyArgTest::testConcatLVTDDIL, "2.52.50true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDDI", ModifyArgTest::testConcatDirectDDDI, "0.00.00.0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDDI", ModifyArgTest::testConcatLVTDDDI, "2.52.52.50");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDDD", ModifyArgTest::testConcatDirectDDDD, "2.50.00.00.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDDD", ModifyArgTest::testConcatLVTDDDD, "0.02.52.52.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDDL", ModifyArgTest::testConcatDirectDDDL, "0.02.50.0null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDDL", ModifyArgTest::testConcatLVTDDDL, "2.50.02.5true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDLI", ModifyArgTest::testConcatDirectDDLI, "0.00.0true0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDLI", ModifyArgTest::testConcatLVTDDLI, "2.52.5null-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDLD", ModifyArgTest::testConcatDirectDDLD, "0.00.0null2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDLD", ModifyArgTest::testConcatLVTDDLD, "2.52.5true0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDDLL", ModifyArgTest::testConcatDirectDDLL, "2.50.0nullnull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDDLL", ModifyArgTest::testConcatLVTDDLL, "0.02.5truetrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLII", ModifyArgTest::testConcatDirectDLII, "2.5null00");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLII", ModifyArgTest::testConcatLVTDLII, "0.0true-957256832-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLID", ModifyArgTest::testConcatDirectDLID, "0.0null-9572568320.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLID", ModifyArgTest::testConcatLVTDLID, "2.5true02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLIL", ModifyArgTest::testConcatDirectDLIL, "0.0null-957256832null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLIL", ModifyArgTest::testConcatLVTDLIL, "2.5true0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLDI", ModifyArgTest::testConcatDirectDLDI, "2.5null0.00");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLDI", ModifyArgTest::testConcatLVTDLDI, "0.0true2.5-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLDD", ModifyArgTest::testConcatDirectDLDD, "0.0true0.00.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLDD", ModifyArgTest::testConcatLVTDLDD, "2.5null2.52.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLDL", ModifyArgTest::testConcatDirectDLDL, "0.0null2.5null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLDL", ModifyArgTest::testConcatLVTDLDL, "2.5true0.0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLLI", ModifyArgTest::testConcatDirectDLLI, "0.0truenull0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLLI", ModifyArgTest::testConcatLVTDLLI, "2.5nulltrue-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLLD", ModifyArgTest::testConcatDirectDLLD, "2.5nullnull0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLLD", ModifyArgTest::testConcatLVTDLLD, "0.0truetrue2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectDLLL", ModifyArgTest::testConcatDirectDLLL, "0.0nullnulltrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTDLLL", ModifyArgTest::testConcatLVTDLLL, "2.5truetruenull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLIII", ModifyArgTest::testConcatDirectLIII, "true000");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLIII", ModifyArgTest::testConcatLVTLIII, "null-957256832-957256832-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLIID", ModifyArgTest::testConcatDirectLIID, "true000.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLIID", ModifyArgTest::testConcatLVTLIID, "null-957256832-9572568322.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLIIL", ModifyArgTest::testConcatDirectLIIL, "true00null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLIIL", ModifyArgTest::testConcatLVTLIIL, "null-957256832-957256832true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLIDI", ModifyArgTest::testConcatDirectLIDI, "null02.50");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLIDI", ModifyArgTest::testConcatLVTLIDI, "true-9572568320.0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLIDD", ModifyArgTest::testConcatDirectLIDD, "null00.02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLIDD", ModifyArgTest::testConcatLVTLIDD, "true-9572568322.50.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLIDL", ModifyArgTest::testConcatDirectLIDL, "null00.0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLIDL", ModifyArgTest::testConcatLVTLIDL, "true-9572568322.5null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLILI", ModifyArgTest::testConcatDirectLILI, "true0null0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLILI", ModifyArgTest::testConcatLVTLILI, "null-957256832true-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLILD", ModifyArgTest::testConcatDirectLILD, "null-957256832null0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLILD", ModifyArgTest::testConcatLVTLILD, "true0true2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLILL", ModifyArgTest::testConcatDirectLILL, "null0truenull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLILL", ModifyArgTest::testConcatLVTLILL, "true-957256832nulltrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDII", ModifyArgTest::testConcatDirectLDII, "null0.00-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDII", ModifyArgTest::testConcatLVTLDII, "true2.5-9572568320");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDID", ModifyArgTest::testConcatDirectLDID, "true0.000.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDID", ModifyArgTest::testConcatLVTLDID, "null2.5-9572568322.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDIL", ModifyArgTest::testConcatDirectLDIL, "null2.50null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDIL", ModifyArgTest::testConcatLVTLDIL, "true0.0-957256832true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDDI", ModifyArgTest::testConcatDirectLDDI, "null2.50.00");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDDI", ModifyArgTest::testConcatLVTLDDI, "true0.02.5-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDDD", ModifyArgTest::testConcatDirectLDDD, "null0.02.50.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDDD", ModifyArgTest::testConcatLVTLDDD, "true2.50.02.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDDL", ModifyArgTest::testConcatDirectLDDL, "true0.00.0null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDDL", ModifyArgTest::testConcatLVTLDDL, "null2.52.5true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDLI", ModifyArgTest::testConcatDirectLDLI, "null2.5null0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDLI", ModifyArgTest::testConcatLVTLDLI, "true0.0true-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDLD", ModifyArgTest::testConcatDirectLDLD, "null0.0true0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDLD", ModifyArgTest::testConcatLVTLDLD, "true2.5null2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLDLL", ModifyArgTest::testConcatDirectLDLL, "null0.0nulltrue");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLDLL", ModifyArgTest::testConcatLVTLDLL, "true2.5truenull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLII", ModifyArgTest::testConcatDirectLLII, "truenull00");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLII", ModifyArgTest::testConcatLVTLLII, "nulltrue-957256832-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLID", ModifyArgTest::testConcatDirectLLID, "nulltrue00.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLID", ModifyArgTest::testConcatLVTLLID, "truenull-9572568322.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLIL", ModifyArgTest::testConcatDirectLLIL, "nullnull-957256832null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLIL", ModifyArgTest::testConcatLVTLLIL, "truetrue0true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLDI", ModifyArgTest::testConcatDirectLLDI, "nullnull0.0-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLDI", ModifyArgTest::testConcatLVTLLDI, "truetrue2.50");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLDD", ModifyArgTest::testConcatDirectLLDD, "truenull0.00.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLDD", ModifyArgTest::testConcatLVTLLDD, "nulltrue2.52.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLDL", ModifyArgTest::testConcatDirectLLDL, "nulltrue0.0null");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLDL", ModifyArgTest::testConcatLVTLLDL, "truenull2.5true");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLLI", ModifyArgTest::testConcatDirectLLLI, "nullnulltrue0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLLI", ModifyArgTest::testConcatLVTLLLI, "truetruenull-957256832");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLLD", ModifyArgTest::testConcatDirectLLLD, "nullnullnull2.5");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLLD", ModifyArgTest::testConcatLVTLLLD, "truetruetrue0.0");
        set.addUnitAssertEquals("ModifyArgTest.testConcatDirectLLLL", ModifyArgTest::testConcatDirectLLLL, "truenullnullnull");
        set.addUnitAssertEquals("ModifyArgTest.testConcatLVTLLLL", ModifyArgTest::testConcatLVTLLLL, "nulltruetruetrue");

        LoggerFactory.getLogger(TestHarness.class).info("ModifyArgTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runModifyReturnValuesTest2(@NotNull TestReport report) {
        TestSet set = new TestSet();

        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$NoConsumeValue");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$NoProvideValue");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$TargetVoid");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$TargetVoidNoConsume");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$TargetVoidRun");

        set.addUnitAssertNotEquals("ModifyReturnValueInvalidTargetInsnTest.TargetReturn", ModifyReturnValueInvalidTargetInsnTest.TargetReturn::getValue, null);
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueInvalidTargetInsnTest$TargetHead");

        set.addUnitAssertNotEquals("ModifyReturnValueVisibilityTest - private", ModifyReturnValueVisibilityTest.VisibilityPrivate::getValue, null);
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueVisibilityTest$VisibilityPackageProtected");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueVisibilityTest$VisibilityPublic");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueVisibilityTest$VisibilityProtected");

        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTypingTest$ConsumeSubclass");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTypingTest$ProvideSuperclass");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTypingTest$ProvideSubclass");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTypingTest$ConsumeSuperclass");

        LoggerFactory.getLogger(TestHarness.class).info("ModifyReturnValuesTest2:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runModifyReturnValuesTest(@NotNull TestReport report) {
        /* following code is generated - do not touch directly. */
        TestSet set = new TestSet();
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteI", ModifyReturnValueTest::identityOverwriteI, -957256832);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteJ", ModifyReturnValueTest::identityOverwriteJ, Long.MAX_VALUE);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteS", ModifyReturnValueTest::identityOverwriteS, ((short) -58));
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteC", ModifyReturnValueTest::identityOverwriteC, 'c');
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteB", ModifyReturnValueTest::identityOverwriteB, ((byte) 126));
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteZ", ModifyReturnValueTest::identityOverwriteZ, true);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteF", ModifyReturnValueTest::identityOverwriteF, 1.2F);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteD", ModifyReturnValueTest::identityOverwriteD, 2.5D);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteNul", ModifyReturnValueTest::identityOverwriteNul, Boolean.TRUE);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteNul2", ModifyReturnValueTest::identityOverwriteNul2, null);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteStr", ModifyReturnValueTest::identityOverwriteStr, "Test2");
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteNulStr", ModifyReturnValueTest::identityOverwriteNulStr, "non-null");
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteNulStr2", ModifyReturnValueTest::identityOverwriteNulStr2, null);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteEmptyStr", ModifyReturnValueTest::identityOverwriteEmptyStr, "");
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteClass", ModifyReturnValueTest::identityOverwriteClass, int.class);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteNulClass", ModifyReturnValueTest::identityOverwriteNulClass, Object.class);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityOverwriteNulClass2", ModifyReturnValueTest::identityOverwriteNulClass2, null);

        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughI", ModifyReturnValueTest::identityPassthroughI, 0);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughJ", ModifyReturnValueTest::identityPassthroughJ, 0L);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughS", ModifyReturnValueTest::identityPassthroughS, ((short) 0));
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughC", ModifyReturnValueTest::identityPassthroughC, '\u0000');
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughB", ModifyReturnValueTest::identityPassthroughB, ((byte) 0));
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughZ", ModifyReturnValueTest::identityPassthroughZ, false);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughF", ModifyReturnValueTest::identityPassthroughF, 0F);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughD", ModifyReturnValueTest::identityPassthroughD, 0D);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughNul", ModifyReturnValueTest::identityPassthroughNul, null);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughNul2", ModifyReturnValueTest::identityPassthroughNul2, Boolean.FALSE);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughStr", ModifyReturnValueTest::identityPassthroughStr, "Test");
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughNulStr", ModifyReturnValueTest::identityPassthroughNulStr, null);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughNulStr2", ModifyReturnValueTest::identityPassthroughNulStr2, "");
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughEmptyStr", ModifyReturnValueTest::identityPassthroughEmptyStr, "non-empty");
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughClass", ModifyReturnValueTest::identityPassthroughClass, void.class);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughNulClass", ModifyReturnValueTest::identityPassthroughNulClass, null);
        set.addUnitAssertEquals("ModifyReturnValueTest.identityPassthroughNulClass2", ModifyReturnValueTest::identityPassthroughNulClass2, Object.class);

        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteI", new ModifyReturnValueTest()::virtualIdentityOverwriteI, -957256832);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteJ", new ModifyReturnValueTest()::virtualIdentityOverwriteJ, Long.MAX_VALUE);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteS", new ModifyReturnValueTest()::virtualIdentityOverwriteS, ((short) -58));
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteC", new ModifyReturnValueTest()::virtualIdentityOverwriteC, 'c');
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteB", new ModifyReturnValueTest()::virtualIdentityOverwriteB, ((byte) 126));
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteZ", new ModifyReturnValueTest()::virtualIdentityOverwriteZ, true);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteF", new ModifyReturnValueTest()::virtualIdentityOverwriteF, 1.2F);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteD", new ModifyReturnValueTest()::virtualIdentityOverwriteD, 2.5D);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteNul", new ModifyReturnValueTest()::virtualIdentityOverwriteNul, Boolean.TRUE);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteNul2", new ModifyReturnValueTest()::virtualIdentityOverwriteNul2, null);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteStr", new ModifyReturnValueTest()::virtualIdentityOverwriteStr, "Test2");
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteNulStr", new ModifyReturnValueTest()::virtualIdentityOverwriteNulStr, "non-null");
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteNulStr2", new ModifyReturnValueTest()::virtualIdentityOverwriteNulStr2, null);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteEmptyStr", new ModifyReturnValueTest()::virtualIdentityOverwriteEmptyStr, "");
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteClass", new ModifyReturnValueTest()::virtualIdentityOverwriteClass, int.class);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteNulClass", new ModifyReturnValueTest()::virtualIdentityOverwriteNulClass, Object.class);
        set.addUnitAssertEquals("ModifyReturnValueTest.virtualIdentityOverwriteNulClass2", new ModifyReturnValueTest()::virtualIdentityOverwriteNulClass2, null);

        set.addUnitAssertEquals("ModifyReturnValueTest.mul3add2I", () -> ModifyReturnValueTest.mul3add2I((int) 1), (int) (((int) 1) * 3 + 2));
        set.addUnitAssertEquals("ModifyReturnValueTest.mul3add2J", () -> ModifyReturnValueTest.mul3add2J((long) 1), (long) (((long) 1) * 3 + 2));
        set.addUnitAssertEquals("ModifyReturnValueTest.mul3add2S", () -> ModifyReturnValueTest.mul3add2S((short) 1), (short) (((short) 1) * 3 + 2));
        set.addUnitAssertEquals("ModifyReturnValueTest.mul3add2C", () -> ModifyReturnValueTest.mul3add2C((char) 1), (char) (((char) 1) * 3 + 2));
        set.addUnitAssertEquals("ModifyReturnValueTest.mul3add2B", () -> ModifyReturnValueTest.mul3add2B((byte) 1), (byte) (((byte) 1) * 3 + 2));
        set.addUnitAssertEquals("ModifyReturnValueTest.mul3add2F", () -> ModifyReturnValueTest.mul3add2F((float) 1), (float) (((float) 1) * 3 + 2));
        set.addUnitAssertEquals("ModifyReturnValueTest.mul3add2D", () -> ModifyReturnValueTest.mul3add2D((double) 1), (double) (((double) 1) * 3 + 2));

        LoggerFactory.getLogger(TestHarness.class).info("ModifyReturnValueTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runRedirectTest(@NotNull TestReport report) {
        TestSet set = new TestSet();
        set.addUnit("RedirectTests.callForeignInstanced", new GenericInvoker()::callForeignInstanced);
        set.addUnit("RedirectTests.callForeignStatically", GenericInvoker::callForeignStatically);
        set.addUnitAssertEquals("RedirectTests.getIntStatic", new GenericInvoker()::getIntStatic, 1);
        set.addUnitAssertEquals("RedirectTests.getIntInstanced", new GenericInvoker()::getIntInstanced, 1);
        set.addUnitAssertEquals("RedirectTests.getObjectStatic0", new GenericInvoker()::getObjectStatic0, 1);
        set.addUnitAssertNotEquals("RedirectTests.getObjectStatic1", new GenericInvoker()::getObjectStatic1, null);
        set.addUnitAssertEquals("RedirectTests.getObjectInstanced0", new GenericInvoker()::getObjectInstanced0, 1);
        set.addUnitAssertNotEquals("RedirectTests.getObjectInstanced1", new GenericInvoker()::getObjectInstanced1, null);
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.redirect.ErroneousInstructionTargetInvoker");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.redirect.ErroneousPublicRedirectInvoker");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.redirect.ErroneousMissingInstanceInvoker");
        LoggerFactory.getLogger(TestHarness.class).info("RedirectTests:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runLocalPrintingTest(TestReport report) {
        TestSet set = new TestSet();
        /*set.addUnit("test - do not use in production", () -> {
            org.stianloader.micromixin.test.j8.targets.LocalPrintingTest.class.toString();
        });/**/
        set.addUnitAssertLocalPrinting("org.stianloader.micromixin.test.j8.targets.LocalPrintingTest",
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_INSTANCE_0,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_STATIC_0,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_0_A,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_0_B,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_1_A,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_1_B,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_2,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_INSTANCE_ARG_3,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_0_A,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_0_B,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_1_A,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_1_B,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_2,
                LocalPrintingWitnesses.LOCAL_PRINTING_TEST_RETURN_STATIC_ARG_3);
        /**/
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.invalid.InvalidPrintAttemptTestA");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.invalid.InvalidPrintAttemptTestB");
        LoggerFactory.getLogger(TestHarness.class).info("LocalPrintingTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    private static void runArgumentCaptureTest(TestReport report) {
        TestSet set = new TestSet();
        set.addUnit("ArgumentCaptureTest.<clinit>", () -> {
            ArgumentCaptureTest.class.toString();
        });
        LoggerFactory.getLogger(TestHarness.class).info("ArgumentCaptureTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runLocalCaputureTest(TestReport report) {
        TestSet set = new TestSet();
        set.addUnitAssertEquals("LocalCaptureTest.returnLocalStatic2", LocalCaptureTest::returnLocalStatic2, 1);
        set.addUnitAssertEquals("LocalCaptureTest.returnLocalInstance2", new LocalCaptureTest()::returnLocalInstance2, 1);
        LoggerFactory.getLogger(TestHarness.class).info("LocalCaptureTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runInjectorRemapTests(TestReport report) {
        TestSet set = new TestSet();
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorInstance0", new InjectorRemapTest()::runInjectorInstance0, 1);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorInstance1", new InjectorRemapTest()::runInjectorInstance1, 1);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorInstance2", new InjectorRemapTest()::runInjectorInstance2, 0);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorInstance3", new InjectorRemapTest()::runInjectorInstance3, 1);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorInstance4", new InjectorRemapTest()::runInjectorInstance4, 1);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorInstance5", new InjectorRemapTest()::runInjectorInstance5, 1);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorInstance6", new InjectorRemapTest()::runInjectorInstance6, NoSuchMethodError.class);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorInstance7", new InjectorRemapTest()::runInjectorInstance7, NoSuchMethodError.class);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorInstance8", new InjectorRemapTest()::runInjectorInstance8, 1);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorInstance9", new InjectorRemapTest()::runInjectorInstance9, 1);
        set.addUnit("InjectorRemapTest.runInjectorInstanceV0", new InjectorRemapTest()::runInjectorInstanceV0);
        set.addUnit("InjectorRemapTest.runInjectorInstanceV1", new InjectorRemapTest()::runInjectorInstanceV1);
        set.addUnit("InjectorRemapTest.runInjectorInstanceV2", new InjectorRemapTest()::runInjectorInstanceV2);
        set.addUnit("InjectorRemapTest.runInjectorInstanceV3", new InjectorRemapTest()::runInjectorInstanceV3);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStatic0", InjectorRemapTest::runInjectorStatic0, NoSuchMethodError.class);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStatic1", InjectorRemapTest::runInjectorStatic1, NoSuchMethodError.class);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorStatic2", InjectorRemapTest::runInjectorStatic2, 0);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStatic3", InjectorRemapTest::runInjectorStatic3, NoSuchMethodError.class);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStatic4", InjectorRemapTest::runInjectorStatic4, NoSuchMethodError.class);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStatic5", InjectorRemapTest::runInjectorStatic5, NoSuchMethodError.class);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorStatic6", InjectorRemapTest::runInjectorStatic6, 1);
        set.addUnitAssertEquals("InjectorRemapTest.runInjectorStatic7", InjectorRemapTest::runInjectorStatic7, 1);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStatic8", InjectorRemapTest::runInjectorStatic8, NoSuchMethodError.class);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStatic9", InjectorRemapTest::runInjectorStatic9, NoSuchMethodError.class);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStaticV0", InjectorRemapTest::runInjectorStaticV0, NoSuchMethodError.class);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStaticV1", InjectorRemapTest::runInjectorStaticV1, NoSuchMethodError.class);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStaticV2", InjectorRemapTest::runInjectorStaticV2, NoSuchMethodError.class);
        set.addUnitExpectThrow("InjectorRemapTest.runInjectorStaticV3", InjectorRemapTest::runInjectorStaticV3, NoSuchMethodError.class);
        LoggerFactory.getLogger(TestHarness.class).info("InjectorRemapTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runClassloadingFailures(TestReport report) {
        TestSet set = new TestSet();
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.invalid.InvalidOverwriteAccessReduction");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.invalid.InvalidOverwriteAliasTest");
        set.addUnitExpectClassloadingFailure("org.stianloader.micromixin.test.j8.targets.invalid.InvalidOverwriteAliasTestB");
        LoggerFactory.getLogger(TestHarness.class).info("ClassloadingFailures:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runOverwriteTests(TestReport report) {
        TestSet set = new TestSet();
        set.addUnitAssertEquals("MixinOverwriteTest.implicitlyOverwrittenMethod", MixinOverwriteTest::acc$implicitlyOverwrittenMethod, 1);
        set.addUnitAssertEquals("MixinOverwriteTest.implicitlyOverwrittenMethodDescMismatch", MixinOverwriteTest::acc$implicitlyOverwrittenMethodDescMismatch, 1);
        set.addUnitAssertEquals("MixinOverwriteTest.explicitlyOverwrittenMethod", MixinOverwriteTest::explicitlyOverwrittenMethod, 1);
        set.addUnitAssertEquals("MixinOverwriteTest.explicitlyProtectedMethod", MixinOverwriteTest::explicitlyProtectedMethod, 1);
        set.addUnitAssertEquals("MixinOverwriteTest.implicitlyOverwrittenMethodDescMismatch", MixinOverwriteTest::explicitlyProtectedMethod, 1);
        set.addUnitAssertEquals("MixinOverwriteTest.explicitlyOverwrittenMethodAliasedPriority", MixinOverwriteTest::acc$explicitlyOverwrittenMethodAliasedPriority, 1);
        set.addUnitAssertEquals("MixinOverwriteTest.explicitlyOverwrittenMethodAliasedMultiA", MixinOverwriteTest::acc$explicitlyOverwrittenMethodAliasedMultiA, 1);
        set.addUnitAssertEquals("MixinOverwriteTest.explicitlyOverwrittenMethodAliasedMultiB", MixinOverwriteTest::acc$explicitlyOverwrittenMethodAliasedMultiB, 0);
        set.addUnitAssertEquals("MixinOverwriteTest.explicitlyOverwrittenMethodAliasedPriorityAlias", MixinOverwriteTest::acc$explicitlyOverwrittenMethodAliasedPriorityAlias, 2);
        LoggerFactory.getLogger(TestHarness.class).info("MixinOverwriteTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runMultiInjectionTests(TestReport report) {
        TestSet set = new TestSet();
        set.addUnitExpectTransformationFailureOrAssert("org.stianloader.micromixin.test.j8.targets.invalid.InjectorStackPosioningTest$IllegalPoison", () -> {
            try {
                IllegalPoison.getValue();
            } catch (NoClassDefFoundError ignore) {
            }

            return InjectorStackPosioningTest.getBehaviour().equals("POPPING");
        });

        set.addUnit("MultiInjectTest.injectionPointA0", MultiInjectTest::injectionPointA0);
        set.addUnit("MultiInjectTest.injectionPointA1", MultiInjectTest::injectionPointA1);
        set.addUnit("MultiInjectTest.injectionPointA2", MultiInjectTest::injectionPointA2);
        set.addUnit("MultiInjectTest.injectionPointA3", MultiInjectTest::injectionPointA3);
        set.addUnitAssertEquals("MultiInjectTest.getInjectionPointACount", MultiInjectTest::getInjectionPointACount, 4);
        set.addUnit("MultiInjectTest.injectionPointB0", MultiInjectTest::injectionPointB0);
        set.addUnit("MultiInjectTest.injectionPointB1", MultiInjectTest::injectionPointB1);
        set.addUnit("MultiInjectTest.injectionPointB2", MultiInjectTest::injectionPointB2);
        set.addUnit("MultiInjectTest.injectionPointB3", MultiInjectTest::injectionPointB3);
        set.addUnitAssertEquals("MultiInjectTest.getInjectionPointBCount", MultiInjectTest::getInjectionPointBCount, 4);
        set.addUnit("MultiInjectTest.injectionPointC0", MultiInjectTest::injectionPointC0);
        set.addUnit("MultiInjectTest.injectionPointC1", MultiInjectTest::injectionPointC1);
        set.addUnit("MultiInjectTest.injectionPointC2", MultiInjectTest::injectionPointC2);
        set.addUnit("MultiInjectTest.injectionPointC3", MultiInjectTest::injectionPointC3);
        set.addUnitAssertEquals("MultiInjectTest.getInjectionPointCCount", MultiInjectTest::getInjectionPointCCount, 0);
        set.addUnit("MultiInjectTest.injectionPointD0", MultiInjectTest::injectionPointD0);
        set.addUnit("MultiInjectTest.injectionPointD1", MultiInjectTest::injectionPointD1);
        set.addUnit("MultiInjectTest.injectionPointD2", MultiInjectTest::injectionPointD2);
        set.addUnit("MultiInjectTest.injectionPointD3", MultiInjectTest::injectionPointD3);
        set.addUnitAssertEquals("MultiInjectTest.getInjectionPointDCount", MultiInjectTest::getInjectionPointDCount, 4);
        set.addUnit("MultiInjectTest.injectionPointE0", MultiInjectTest::injectionPointE0);
        set.addUnit("MultiInjectTest.injectionPointE1", MultiInjectTest::injectionPointE1);
        set.addUnit("MultiInjectTest.injectionPointE2", MultiInjectTest::injectionPointE2);
        set.addUnit("MultiInjectTest.injectionPointE3", MultiInjectTest::injectionPointE3);
        set.addUnitAssertEquals("MultiInjectTest.getInjectionPointECount", MultiInjectTest::getInjectionPointECount, 1);
        set.addUnit("MultiInjectTest.injectionPointF0", MultiInjectTest::injectionPointF0);
        set.addUnit("MultiInjectTest.injectionPointF1", MultiInjectTest::injectionPointF1);
        set.addUnit("MultiInjectTest.injectionPointF2", MultiInjectTest::injectionPointF2);
        set.addUnitAssertEquals("MultiInjectTest.getInjectionPointFCount", MultiInjectTest::getInjectionPointFCount, 2);
        set.addUnit("MultiInjectTest.injectionPointG0", MultiInjectTest::injectionPointG0);
        set.addUnit("MultiInjectTest.injectionPointG1", MultiInjectTest::injectionPointG1);
        set.addUnit("MultiInjectTest.injectionPointG2", MultiInjectTest::injectionPointG2);
        set.addUnitAssertEquals("MultiInjectTest.getInjectionPointGCount", MultiInjectTest::getInjectionPointGCount, 1);
        set.addUnitAssertEquals("MultiInjectTest.injectionPointH0A", MultiInjectTest::injectionPointH0A, 2);
        set.addUnitAssertEquals("MultiInjectTest.injectionPointH0B", MultiInjectTest::injectionPointH0B, 2);
        set.addUnitAssertEquals("MultiInjectTest.injectionPointH1A", MultiInjectTest::injectionPointH1A, 2);
        set.addUnitAssertEquals("MultiInjectTest.injectionPointH1B", MultiInjectTest::injectionPointH1B, 2);
        set.addUnitAssertEquals("MultiInjectTest.injectionPointH2A", MultiInjectTest::injectionPointH2A, 2);
        set.addUnitAssertEquals("MultiInjectTest.injectionPointH2B", MultiInjectTest::injectionPointH2B, 2);
        set.addUnitAssertEquals("MultiInjectTest.injectionPointH3A", MultiInjectTest::injectionPointH3A, 2);
        set.addUnitAssertEquals("MultiInjectTest.injectionPointH3B", MultiInjectTest::injectionPointH3B, 2);

        LoggerFactory.getLogger(TestHarness.class).info("MultiInjectTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runInjectionHeadTests(TestReport report) {
        TestSet set = new TestSet();
        set.addUnit("InjectionHeadTest.expectNoThrowV", InjectionHeadTest::expectNoThrowV);
        set.addUnit("InjectionHeadTest.expectNoThrowB", InjectionHeadTest::expectNoThrowB);
        set.addUnit("InjectionHeadTest.expectNoThrowC", InjectionHeadTest::expectNoThrowC);
        set.addUnit("InjectionHeadTest.expectNoThrowZ", InjectionHeadTest::expectNoThrowZ);
        set.addUnit("InjectionHeadTest.expectNoThrowS", InjectionHeadTest::expectNoThrowS);
        set.addUnit("InjectionHeadTest.expectNoThrowI", InjectionHeadTest::expectNoThrowI);
        set.addUnit("InjectionHeadTest.expectNoThrowJ", InjectionHeadTest::expectNoThrowJ);
        set.addUnit("InjectionHeadTest.expectNoThrowF", InjectionHeadTest::expectNoThrowF);
        set.addUnit("InjectionHeadTest.expectNoThrowD", InjectionHeadTest::expectNoThrowD);
        set.addUnit("InjectionHeadTest.expectNoThrowL", InjectionHeadTest::expectNoThrowL);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowV", InjectionHeadTest::expectThrowV);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowB", InjectionHeadTest::expectThrowB);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowC", InjectionHeadTest::expectThrowC);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowZ", InjectionHeadTest::expectThrowZ);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowS", InjectionHeadTest::expectThrowS);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowI", InjectionHeadTest::expectThrowI);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowJ", InjectionHeadTest::expectThrowJ);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowF", InjectionHeadTest::expectThrowF);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowD", InjectionHeadTest::expectThrowD);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowL", InjectionHeadTest::expectThrowL);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationB", InjectionHeadTest::expectImplicitlyInvalidCancellationB, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationC", InjectionHeadTest::expectImplicitlyInvalidCancellationC, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationD", InjectionHeadTest::expectImplicitlyInvalidCancellationD, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationF", InjectionHeadTest::expectImplicitlyInvalidCancellationF, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationI", InjectionHeadTest::expectImplicitlyInvalidCancellationI, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationJ", InjectionHeadTest::expectImplicitlyInvalidCancellationJ, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationL", InjectionHeadTest::expectImplicitlyInvalidCancellationL, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationS", InjectionHeadTest::expectImplicitlyInvalidCancellationS, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationV", InjectionHeadTest::expectImplicitlyInvalidCancellationV, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationZ", InjectionHeadTest::expectImplicitlyInvalidCancellationZ, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationB", InjectionHeadTest::expectInvalidCancellationB, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationC", InjectionHeadTest::expectInvalidCancellationC, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationD", InjectionHeadTest::expectInvalidCancellationD, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationF", InjectionHeadTest::expectInvalidCancellationF, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationI", InjectionHeadTest::expectInvalidCancellationI, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationJ", InjectionHeadTest::expectInvalidCancellationJ, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationL", InjectionHeadTest::expectInvalidCancellationL, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationS", InjectionHeadTest::expectInvalidCancellationS, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationV", InjectionHeadTest::expectInvalidCancellationV, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationZ", InjectionHeadTest::expectInvalidCancellationZ, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectReturnNondefaultB", InjectionHeadTest::expectReturnNondefaultB, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectReturnNondefaultC", InjectionHeadTest::expectReturnNondefaultC, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectReturnNondefaultD", InjectionHeadTest::expectReturnNondefaultD, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectReturnNondefaultF", InjectionHeadTest::expectReturnNondefaultF, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectReturnNondefaultI", InjectionHeadTest::expectReturnNondefaultI, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectReturnNondefaultJ", InjectionHeadTest::expectReturnNondefaultJ, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectReturnNondefaultL", InjectionHeadTest::expectReturnNondefaultL, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectReturnNondefaultS", InjectionHeadTest::expectReturnNondefaultF, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectReturnNondefaultZ", InjectionHeadTest::expectReturnNondefaultZ, CancellationException.class);
        set.addUnitAssertNotEquals("InjectionHeadTest.expectCancellableReturnNondefaultB", InjectionHeadTest::expectCancellableReturnNondefaultB, (char) 0);
        set.addUnitAssertNotEquals("InjectionHeadTest.expectCancellableReturnNondefaultC", InjectionHeadTest::expectCancellableReturnNondefaultC, (char) 0);
        set.addUnitAssertNotEquals("InjectionHeadTest.expectCancellableReturnNondefaultD", InjectionHeadTest::expectCancellableReturnNondefaultD, 0D);
        set.addUnitAssertNotEquals("InjectionHeadTest.expectCancellableReturnNondefaultF", InjectionHeadTest::expectCancellableReturnNondefaultF, 0F);
        set.addUnitAssertNotEquals("InjectionHeadTest.expectCancellableReturnNondefaultI", InjectionHeadTest::expectCancellableReturnNondefaultI, 0);
        set.addUnitAssertNotEquals("InjectionHeadTest.expectCancellableReturnNondefaultJ", InjectionHeadTest::expectCancellableReturnNondefaultJ, 0L);
        set.addUnitAssertNotEquals("InjectionHeadTest.expectCancellableReturnNondefaultL", InjectionHeadTest::expectCancellableReturnNondefaultL, null);
        set.addUnitAssertNotEquals("InjectionHeadTest.expectCancellableReturnNondefaultS", InjectionHeadTest::expectCancellableReturnNondefaultF, 0F);
        set.addUnitAssertNotEquals("InjectionHeadTest.expectCancellableReturnNondefaultZ", InjectionHeadTest::expectCancellableReturnNondefaultZ, false);
        LoggerFactory.getLogger(TestHarness.class).info("InjectionHeadTest:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }
}
