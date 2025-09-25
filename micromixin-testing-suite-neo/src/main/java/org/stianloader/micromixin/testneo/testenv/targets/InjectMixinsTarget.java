package org.stianloader.micromixin.testneo.testenv.targets;

import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectSignaller;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.annotations.InvokeArgument;

public class InjectMixinsTarget {
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
}
