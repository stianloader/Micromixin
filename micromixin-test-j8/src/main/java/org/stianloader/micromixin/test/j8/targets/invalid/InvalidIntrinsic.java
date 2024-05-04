package org.stianloader.micromixin.test.j8.targets.invalid;

public class InvalidIntrinsic {
    public static class InvalidIntrinsicNoInterface {
        public boolean intrinsicMethodDiscarded() {
            return true;
        }
    }
    public static class InvalidIntrinsicNoInterfacePrefixed {
        public boolean intrinsicMethodDiscarded() {
            return true;
        }
    }
}
