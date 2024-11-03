package org.stianloader.micromixin.test.j8.targets;

public class CanonicalOverwriteTest {

    public static interface CanonicalOverwriteInterface {
        void invokeMethodOne();
    }

    public static interface CanonicalOverwriteInterfaceI {
        int invokeMethodI();
    }

    public static interface CanonicalOverwriteInterfaceLVT {
        int invokeMethodLVT(int arg0, int arg1);
    }

    public static class OverwriteExplicitDiffer {
        public int invokeMethodIOverwritten() {
            return 0;
        }
    }

    public static class OverwriteExplicitIdentity {
        public void invokeMethodOne() {
            throw new AssertionError("Did not overwrite.");
        }
    }

    public static class OverwriteImplicit {
        public void invokeMethodOne() {
            throw new AssertionError("Did not overwrite.");
        }
    }

    public static class OverwriteImplicitLVT {
        public int invokeMethodLVT(int arg0, int arg1) {
            throw new AssertionError("Did not overwrite");
        }
    }

    public static class OverwriteInterfaceMissing {
        public void invokeMethodOne() {
            // NOP
        }
    }

    public static class OverwriteTargetNotPresentExplicit {
    }

    public static class OverwriteTargetNotPresentImplicit {
    }
}
