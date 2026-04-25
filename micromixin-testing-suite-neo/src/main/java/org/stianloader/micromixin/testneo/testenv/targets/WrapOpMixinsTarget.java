package org.stianloader.micromixin.testneo.testenv.targets;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectSignaller;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.communication.Signaller;

public class WrapOpMixinsTarget {

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testWrapOpSI")
    @ExpectSignaller(signalValue = 1)
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public static void testWrapOpSI() {
        ((IntConsumer) Signaller::setSignal).accept(0);
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testWrapOpSIO")
    @ExpectSignaller(signalValue = 1)
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public static void testWrapOpSIO() {
        Consumer<Integer> signaller = signal -> Signaller.setSignal(signal);
        signaller.accept(0);
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testWrapOpSS")
    @ExpectSignaller(signalValue = 1)
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public static void testWrapOpSS() {
        Signaller.setSignal(0);
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testWrapOpVI")
    @ExpectSignaller(signalValue = 1)
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public void testWrapOpVI() {
        ((IntConsumer) Signaller::setSignal).accept(0);
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testWrapOpVIO")
    @ExpectSignaller(signalValue = 1)
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public void testWrapOpVIO() {
        Consumer<Integer> signaller = signal -> Signaller.setSignal(signal);
        signaller.accept(0);
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testWrapOpVS")
    @ExpectSignaller(signalValue = 1)
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public void testWrapOpVS() {
        Signaller.setSignal(0);
    }
}
