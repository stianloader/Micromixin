package org.stianloader.micromixin.test.j8.targets;

public class RecursiveHandlerTest {

    public void handlerRedirectVirtual() {
        this.method0();
    }

    private void method0() {
        throw new AssertionError("This method should be overwritten!");
    }
}
