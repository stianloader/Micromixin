package de.geolykt.starloader.micromixin.test.j8.targets.mixinextra;

public class ModifyReturnValueSignatureTest {

    public static class NoConsumeValue {
        // Expect classloading failure
        public static Object getValue() {
            return null;
        }
    }

    public static class NoProvideValue {
        // Expect classloading failure
        public static Object getValue() {
            return null;
        }
    }

    public static class TargetVoid {
        // Expect classloading failure
        public static void getValue() {
            return;
        }
    }

    public static class TargetVoidNoConsume {
        // Expect classloading failure
        public static void getValue() {
            return;
        }
    }

    public static class TargetVoidRun {
        // Expect classloading failure
        public static void getValue() {
            return;
        }
    }

}
