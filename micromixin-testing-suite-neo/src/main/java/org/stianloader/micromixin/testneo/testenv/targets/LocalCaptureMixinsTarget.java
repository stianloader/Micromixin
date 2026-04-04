package org.stianloader.micromixin.testneo.testenv.targets;

import java.util.function.IntConsumer;

import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectSignaller;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.annotations.IncludeClasses.IncludeFailingClass;
import org.stianloader.micromixin.testneo.testenv.annotations.InvokeArgument;
import org.stianloader.micromixin.testneo.testenv.communication.Signaller;

@IncludeFailingClass({
    LocalCaptureMixinsTarget.NoArgsCaptureInLocalCapture.class,
    LocalCaptureMixinsTarget.NoArgsCaptureInLocalCaptureUnderflow.class,
    LocalCaptureMixinsTarget.NoArgsOverflow.class
})
public class LocalCaptureMixinsTarget {

    public static class NoArgsCaptureInLocalCapture {
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testArgsNoArgCapture")
        @ExpectedAnnotations({AssertMemberName.class })
        public final void testArgsNoArgCapture(long a, double b, double c) {
            IntConsumer signalSetter = Signaller::setSignal;
            signalSetter.accept((int) (a + b + c));
        }
    }

    public static class NoArgsCaptureInLocalCaptureUnderflow {
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testArgsNoArgCaptureUnderflow")
        @ExpectSignaller(
                args = {
                    @InvokeArgument(type = long.class, longValue = 3),
                    @InvokeArgument(type = double.class, doubleValue  = 1.75D),
                    @InvokeArgument(type = double.class, doubleValue = 0.25D)
                },
                signalValue = 1
           )
        @ExpectedAnnotations({AssertMemberName.class })
        public final void testArgsNoArgCaptureUnderflow(long a, double b, double c) {
            IntConsumer signalSetter = Signaller::setSignal;
            signalSetter.accept((int) (a + b + c));
        }
    }

    public static class NoArgsOverflow {
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testNoArgsOverflow")
        @ExpectSignaller(signalValue = 1)
        @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
        public final void testNoArgsOverflow() {
            IntConsumer signalSetter = Signaller::setSignal;
            signalSetter.accept(0);
        }
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testArgCapture")
    @ExpectSignaller(
         args = {
             @InvokeArgument(type = long.class, longValue = 3),
             @InvokeArgument(type = double.class, doubleValue  = 1.75D),
             @InvokeArgument(type = double.class, doubleValue = 0.25D)
         },
         signalValue = 1
    )
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public final void testArgCapture(long a, double b, double c) {
        IntConsumer signalSetter = Signaller::setSignal;
        signalSetter.accept((int) (a + b + c));
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testArgCaptureUnderflow")
    @ExpectSignaller(
         args = {
             @InvokeArgument(type = long.class, longValue = 3),
             @InvokeArgument(type = double.class, doubleValue  = 1.75D),
             @InvokeArgument(type = double.class, doubleValue = 0.25D)
         },
         signalValue = 1
    )
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public final void testArgCaptureUnderflow(long a, double b, double c) {
        IntConsumer signalSetter = Signaller::setSignal;
        signalSetter.accept((int) (a + b + c));
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testNoArgs")
    @ExpectSignaller(signalValue = 1)
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public final void testNoArgs() {
        IntConsumer signalSetter = Signaller::setSignal;
        signalSetter.accept(0);
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testNoArgsUnderflow")
    @ExpectSignaller(signalValue = 1)
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public final void testNoArgsUnderflow() {
        IntConsumer signalSetter = Signaller::setSignal;
        signalSetter.accept(0);
    }
}
