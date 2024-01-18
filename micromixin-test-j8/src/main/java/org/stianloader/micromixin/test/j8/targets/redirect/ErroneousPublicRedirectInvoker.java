package org.stianloader.micromixin.test.j8.targets.redirect;

public class ErroneousPublicRedirectInvoker {
    public static void invoke() {
        new GenericInvokeTarget().return0Instanced();
    }
}
