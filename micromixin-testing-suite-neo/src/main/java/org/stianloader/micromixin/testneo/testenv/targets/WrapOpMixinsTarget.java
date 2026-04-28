package org.stianloader.micromixin.testneo.testenv.targets;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectSignaller;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectSignaller.ExpectSignals;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.annotations.IncludeClasses.IncludeFailingClass;
import org.stianloader.micromixin.testneo.testenv.annotations.IncludeClasses.IncludePassingClasses;
import org.stianloader.micromixin.testneo.testenv.annotations.InvokeArgument;
import org.stianloader.micromixin.testneo.testenv.communication.Signaller;

@IncludePassingClasses({
    WrapOpMixinsTarget.WrapOpCancellable.class
})
@IncludeFailingClass({
    WrapOpMixinsTarget.WrapOpCancellableNonTrailing.class
})
public class WrapOpMixinsTarget {

    public static class WrapOpCancellable {
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCI")
        @ExpectSignaller(signalValue = 4)
        @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
        public static void testConditionallyUsedCI() {
            ((IntConsumer) Signaller::setSignal).accept(1);
            ((IntConsumer) Signaller::setSignal).accept(3);
            ((IntConsumer) Signaller::setSignal).accept(5);
        }

        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgI")
        @ExpectSignaller(signalValue = 8, args = @InvokeArgument(intValue = 1, type = int.class))
        @ExpectSignaller(signalValue = 2, args = @InvokeArgument(intValue = 3, type = int.class))
        @ExpectedAnnotations({AssertMemberName.class, ExpectSignals.class})
        public static void testConditionallyUsedCIWithArgI(int flagI) {
            ((IntConsumer) Signaller::setSignal).accept(1);
            ((IntConsumer) Signaller::setSignal).accept(3);

            Signaller.setSignal(8);
        }

        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgJ")
        @ExpectSignaller(signalValue = 8, args = @InvokeArgument(longValue = 1, type = long.class))
        @ExpectSignaller(signalValue = 2, args = @InvokeArgument(longValue = 3, type = long.class))
        @ExpectedAnnotations({AssertMemberName.class, ExpectSignals.class})
        public static void testConditionallyUsedCIWithArgJ(long flagJ) {
            ((IntConsumer) Signaller::setSignal).accept(1);
            ((IntConsumer) Signaller::setSignal).accept(3);

            Signaller.setSignal(8);
        }

        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgJ2")
        @ExpectSignaller(signalValue = 8, args = {
                @InvokeArgument(longValue = 1, type = long.class),
                @InvokeArgument(longValue = 4, type = long.class)
        })
        @ExpectSignaller(signalValue = 2, args = {
                @InvokeArgument(longValue = 3, type = long.class),
                @InvokeArgument(longValue = 3, type = long.class)
        })
        @ExpectedAnnotations({AssertMemberName.class, ExpectSignals.class})
        public static void testConditionallyUsedCIWithArgJ2(long flagJ, long flagJ2) {
            ((IntConsumer) Signaller::setSignal).accept(1);

            Signaller.setSignal(8);
        }

        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgJ3")
        @ExpectSignaller(signalValue = 9, args = {
                @InvokeArgument(longValue = 1, type = long.class),
                @InvokeArgument(longValue = 4, type = long.class)
        })
        @ExpectSignaller(signalValue = 2, args = {
                @InvokeArgument(longValue = 3, type = long.class),
                @InvokeArgument(longValue = 3, type = long.class)
        })
        @ExpectedAnnotations({AssertMemberName.class, ExpectSignals.class})
        public static void testConditionallyUsedCIWithArgJ3(long flagJ, long flagJ2) {
            ((IntConsumer) Signaller::setSignal).accept(1);
        }

        @AssertMemberName(constraint = AssertConstraint.IS, value = "testUnusedCI")
        @ExpectSignaller(signalValue = 1)
        @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
        public static void testUnusedCI() {
            ((IntConsumer) Signaller::setSignal).accept(0);
        }

        @AssertMemberName(constraint = AssertConstraint.IS, value = "testUsedCI")
        @ExpectSignaller(signalValue = 2)
        @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
        public static void testUsedCI() {
            ((IntConsumer) Signaller::setSignal).accept(1);
            ((IntConsumer) Signaller::setSignal).accept(4);
        }
    }

    public static class WrapOpCancellableNonTrailing {
        @AssertMemberName(constraint = AssertConstraint.IS, value = "testConditionallyUsedCIWithArgJ")
        @ExpectSignaller(signalValue = 8, args = @InvokeArgument(longValue = 1, type = long.class))
        @ExpectSignaller(signalValue = 2, args = @InvokeArgument(longValue = 3, type = long.class))
        @ExpectedAnnotations({AssertMemberName.class, ExpectSignals.class})
        public static void testConditionallyUsedCIWithArgJ(long flagJ) {
            ((IntConsumer) Signaller::setSignal).accept(1);
            ((IntConsumer) Signaller::setSignal).accept(3);

            Signaller.setSignal(8);
        }
    }

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
