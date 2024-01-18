package org.stianloader.micromixin.test.j8.targets.redirect;

public class ErroneousInstructionTargetInvoker {

    public void invoke() {
        GenericInvokeTarget.assertCalledStaticVV();
    }
}
