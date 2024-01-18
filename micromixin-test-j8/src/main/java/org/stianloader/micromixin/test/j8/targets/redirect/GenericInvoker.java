package org.stianloader.micromixin.test.j8.targets.redirect;

public class GenericInvoker {

    public static void callForeignStatically() {
        new GenericInvokeTarget().assertCalledIV(0);
        new GenericInvokeTarget().assertCalledVV();
        GenericInvokeTarget.assertCalledStaticIV(0);
        GenericInvokeTarget.assertCalledStaticVV();
    }

    public void callForeignInstanced() {
        new GenericInvokeTarget().assertCalledIV(0);
        new GenericInvokeTarget().assertCalledVV();
        GenericInvokeTarget.assertCalledStaticIV(0);
        GenericInvokeTarget.assertCalledStaticVV();
    }

    public int getIntStatic() {
        return GenericInvokeTarget.return0Static();
    }

    public int getIntInstanced() {
        return new GenericInvokeTarget().return0Instanced();
    }

    public Object getObjectStatic0() {
        return GenericInvokeTarget.return0Static();
    }

    public Object getObjectInstanced0() {
        return new GenericInvokeTarget().return0Instanced();
    }

    public Object getObjectStatic1() {
        return GenericInvokeTarget.returnNullStatic();
    }

    public Object getObjectInstanced1() {
        return new GenericInvokeTarget().returnNullInstanced();
    }
}
