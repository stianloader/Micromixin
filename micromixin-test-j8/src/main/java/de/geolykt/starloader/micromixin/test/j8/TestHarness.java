package de.geolykt.starloader.micromixin.test.j8;

import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

import de.geolykt.starloader.micromixin.test.j8.targets.InjectionHeadTest;

public class TestHarness {

    public static TestReport runAllTests() {
        TestReport report = new TestReport();
        runInjectionReturnTests(report);
        return report;
    }

    public static void runInjectionReturnTests(TestReport report) {
        TestSet set = new TestSet();
        set.addUnit("InjectionHeadTest.expectNoThrowV", InjectionHeadTest::expectNoThrowV);
        set.addUnit("InjectionHeadTest.expectNoThrowC", InjectionHeadTest::expectNoThrowC);
        set.addUnit("InjectionHeadTest.expectNoThrowZ", InjectionHeadTest::expectNoThrowZ);
        set.addUnit("InjectionHeadTest.expectNoThrowS", InjectionHeadTest::expectNoThrowS);
        set.addUnit("InjectionHeadTest.expectNoThrowI", InjectionHeadTest::expectNoThrowI);
        set.addUnit("InjectionHeadTest.expectNoThrowJ", InjectionHeadTest::expectNoThrowJ);
        set.addUnit("InjectionHeadTest.expectNoThrowF", InjectionHeadTest::expectNoThrowF);
        set.addUnit("InjectionHeadTest.expectNoThrowD", InjectionHeadTest::expectNoThrowD);
        set.addUnit("InjectionHeadTest.expectNoThrowL", InjectionHeadTest::expectNoThrowL);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowV", InjectionHeadTest::expectThrowV);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowC", InjectionHeadTest::expectThrowC);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowZ", InjectionHeadTest::expectThrowZ);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowS", InjectionHeadTest::expectThrowS);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowI", InjectionHeadTest::expectThrowI);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowJ", InjectionHeadTest::expectThrowJ);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowF", InjectionHeadTest::expectThrowF);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowD", InjectionHeadTest::expectThrowD);
        set.addUnitExpectThrow("InjectionHeadTest.expectThrowL", InjectionHeadTest::expectThrowL);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationC", InjectionHeadTest::expectImplicitlyInvalidCancellationC, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationD", InjectionHeadTest::expectImplicitlyInvalidCancellationD, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationF", InjectionHeadTest::expectImplicitlyInvalidCancellationF, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationI", InjectionHeadTest::expectImplicitlyInvalidCancellationI, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationJ", InjectionHeadTest::expectImplicitlyInvalidCancellationJ, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationL", InjectionHeadTest::expectImplicitlyInvalidCancellationL, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationS", InjectionHeadTest::expectImplicitlyInvalidCancellationS, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationV", InjectionHeadTest::expectImplicitlyInvalidCancellationV, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectImplicitlyInvalidCancellationZ", InjectionHeadTest::expectImplicitlyInvalidCancellationZ, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationC", InjectionHeadTest::expectInvalidCancellationC, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationD", InjectionHeadTest::expectInvalidCancellationD, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationF", InjectionHeadTest::expectInvalidCancellationF, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationI", InjectionHeadTest::expectInvalidCancellationI, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationJ", InjectionHeadTest::expectInvalidCancellationJ, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationL", InjectionHeadTest::expectInvalidCancellationL, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationS", InjectionHeadTest::expectInvalidCancellationS, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationV", InjectionHeadTest::expectInvalidCancellationV, CancellationException.class);
        set.addUnitExpectThrow("InjectionHeadTest.expectInvalidCancellationZ", InjectionHeadTest::expectInvalidCancellationZ, CancellationException.class);
        set.executeAll(report, LoggerFactory.getLogger(TestHarness.class));
    }
}
