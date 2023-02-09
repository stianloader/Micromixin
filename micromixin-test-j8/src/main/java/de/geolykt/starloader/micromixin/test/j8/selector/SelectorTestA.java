package de.geolykt.starloader.micromixin.test.j8.selector;

public class SelectorTestA {

    public void targetMethod8() {
    }
    public void targetMethod2(String[] args) {
    }
    public void targetMethod2() {
    }
    public void targetMethod0() {
    }
    public void targetMethod4() {
    }
    public void targetMultiA() {
        System.out.println("Divider");
    }
    public void targetMultiDuplicated() {
    }
    public void constantProvider0() {
        System.out.println("MyConstant");
        System.out.println("MyConstant");
        System.err.println("MyConstant");
    }
    public void constantProvider1() {
        System.out.println("MyConstant");
        System.out.println("MyConstant");
        System.err.println("MyConstant");
    }
    public void constantProvider0(String[] args) {
        System.out.println("MyConstant" + args);
        System.out.println("MyConstant" + args);
        System.err.println("MyConstant" + args);
        System.err.println("MyConstant" + args);
    }
    public void constantProvider5() {
        System.out.println("MyConstantA");
        System.out.println("MyConstantB");
        System.err.println("MyConstantC");
    }
}
