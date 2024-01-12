package de.geolykt.starloader.micromixin.test.j8;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

import de.geolykt.starloader.micromixin.test.j8.localsprinting.LocalPrintingWitnesses;
import de.geolykt.starloader.micromixin.test.j8.targets.ArgumentCaptureTest;
import de.geolykt.starloader.micromixin.test.j8.targets.InjectionHeadTest;
import de.geolykt.starloader.micromixin.test.j8.targets.InjectorRemapTest;
import de.geolykt.starloader.micromixin.test.j8.targets.LocalCaptureTest;
import de.geolykt.starloader.micromixin.test.j8.targets.MixinOverwriteTest;
import de.geolykt.starloader.micromixin.test.j8.targets.MultiInjectTest;
import de.geolykt.starloader.micromixin.test.j8.targets.invalid.InjectorStackPosioningTest;
import de.geolykt.starloader.micromixin.test.j8.targets.invalid.InjectorStackPosioningTest.IllegalPoison;
import de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueInvalidTargetInsnTest;
import de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTest;
import de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueVisibilityTest;
import de.geolykt.starloader.micromixin.test.j8.targets.redirect.GenericInvoker;

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
        return report;
    }

    public static void runModifyReturnValuesTest2(@NotNull TestReport report) {
        TestSet set = new TestSet();

        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$NoConsumeValue");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$NoProvideValue");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$TargetVoid");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$TargetVoidNoConsume");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueSignatureTest$TargetVoidRun");

        set.addUnitAssertNotEquals("ModifyReturnValueInvalidTargetInsnTest.TargetReturn", ModifyReturnValueInvalidTargetInsnTest.TargetReturn::getValue, null);
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueInvalidTargetInsnTest$TargetHead");

        set.addUnitAssertNotEquals("ModifyReturnValueVisibilityTest - private", ModifyReturnValueVisibilityTest.VisibilityPrivate::getValue, null);
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueVisibilityTest$VisibilityPackageProtected");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueVisibilityTest$VisibilityPublic");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueVisibilityTest$VisibilityProtected");

        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTypingTest$ConsumeSubclass");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTypingTest$ProvideSuperclass");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTypingTest$ProvideSubclass");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.mixinextra.ModifyReturnValueTypingTest$ConsumeSuperclass");

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
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.redirect.ErroneousInstructionTargetInvoker");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.redirect.ErroneousPublicRedirectInvoker");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.redirect.ErroneousMissingInstanceInvoker");
        LoggerFactory.getLogger(TestHarness.class).info("RedirectTests:");
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }

    public static void runLocalPrintingTest(TestReport report) {
        TestSet set = new TestSet();
        /*set.addUnit("test - do not use in production", () -> {
            de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest.class.toString();
        });/**/
        set.addUnitAssertLocalPrinting("de.geolykt.starloader.micromixin.test.j8.targets.LocalPrintingTest",
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
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.invalid.InvalidPrintAttemptTestA");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.invalid.InvalidPrintAttemptTestB");
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
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.invalid.InvalidOverwriteAccessReduction");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.invalid.InvalidOverwriteAliasTest");
        set.addUnitExpectClassloadingFailure("de.geolykt.starloader.micromixin.test.j8.targets.invalid.InvalidOverwriteAliasTestB");
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
        set.addUnitExpectTransformationFailureOrAssert("de.geolykt.starloader.micromixin.test.j8.targets.invalid.InjectorStackPosioningTest$IllegalPoison", () -> {
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
