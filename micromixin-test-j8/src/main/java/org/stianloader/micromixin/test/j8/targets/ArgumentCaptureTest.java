package org.stianloader.micromixin.test.j8.targets;

public class ArgumentCaptureTest {

    public boolean captureModifyConstant(boolean arg) {
        return false;
    }

    public int captureModifyConstantMulti0(int argA, int argB) {
        return 0;
    }

    public long captureModifyConstantMulti1(int argA, int argB) {
        return 0;
    }

    public int captureModifyConstantMulti2(long argA, int argB) {
        return 0;
    }

    public int captureModifyConstantMulti3(int argA, long argB) {
        return 0;
    }

    public int captureModifyConstantMulti4(long argA, long argB) {
        return 0;
    }

    public long captureModifyConstantMulti5(int argA, long argB) {
        return 0;
    }

    public boolean captureModifyReturnValue(boolean arg) {
        return false;
    }

    public int captureModifyReturnValueMulti0(int argA, int argB) {
        return 0;
    }

    public long captureModifyReturnValueMulti1(int argA, int argB) {
        return 0;
    }

    public int captureModifyReturnValueMulti2(long argA, int argB) {
        return 0;
    }

    public int captureModifyReturnValueMulti3(int argA, long argB) {
        return 0;
    }

    public int captureModifyReturnValueMulti4(long argA, long argB) {
        return 0;
    }

    public long captureModifyReturnValueMulti5(int argA, long argB) {
        return 0;
    }

    public void captureNaught(int argument) {
        // NOP
    }

    public void captureOne(int argument) {
        // NOP
    }
}
