package org.stianloader.micromixin.testneo.testenv.targets;

import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectSignaller;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.annotations.IncludeClasses.IncludeFailingClass;
import org.stianloader.micromixin.testneo.testenv.annotations.InvokeArgument;
import org.stianloader.micromixin.testneo.testenv.communication.Signaller;

@IncludeFailingClass(InjectMixinsTarget.WrongCallbackInfoClass.class)
public class InjectMixinsTarget {
    public static class WrongCallbackInfoClass {
        @ExpectSignaller(signalValue = 1)
        @AssertMemberName(constraint = AssertConstraint.IS, value = "wrongCallbackInfoClass")
        public final boolean wrongCallbackInfoClass() {
            return true;
        }
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "invoke")
    @ExpectSignaller(
         args = {
             @InvokeArgument(type = long.class, longValue = 3),
             @InvokeArgument(type = double.class, doubleValue  = 1.75D),
             @InvokeArgument(type = double.class, doubleValue = 0.25D)
         },
         signalValue = 1
    )
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public final void invoke(long var1, double var3, double var5) {
        // Replicating https://discord.com/channels/868569240398082068/868569240398082071/1420371588221767680
        // (from the stianloader discord server, note that this is the pure-java equivalent. The
        // real issue lies with Kotlin, apparently)
    }

    @AssertMemberName(constraint = AssertConstraint.IS, value = "testJIII")
    @ExpectSignaller(
         args = {
             @InvokeArgument(type = long.class, longValue = 3),
             @InvokeArgument(type = int.class, intValue = 1),
             @InvokeArgument(type = int.class, intValue = 1),
             @InvokeArgument(type = int.class, intValue = 9)
         },
         signalValue = 1
    )
    @ExpectedAnnotations({AssertMemberName.class, ExpectSignaller.class})
    public final void testJIII(long var1, int var3, int var4, int var5) {
        byte var11;
        if (var3 == 0) {
           var11 = 0;
        } else if (var3 == 1) {
           var11 = 1;
        } else if (var3 == 2) {
           var11 = 2;
        } else if (var3 == 3) {
           var11 = 3;
        } else if (var3 == 4) {
           var11 = 4;
        } else {
           var11 = -1;
        }

        Signaller.setSignal(var11);
    }
}
