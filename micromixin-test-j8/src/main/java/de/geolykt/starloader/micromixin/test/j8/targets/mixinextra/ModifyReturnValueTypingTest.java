package de.geolykt.starloader.micromixin.test.j8.targets.mixinextra;

public class ModifyReturnValueTypingTest {

    public static class ConsumeSubclass {
        // Expect classloading failure
        public static Object getValue() {
            return "value";
        }
    }

    public static class ProvideSuperclass {
        // Expect classloading failure
        public static String getValue() {
            return "value";
        }
    }

    public static class ProvideSubclass {
        // Expect classloading failure, although Java would in theory allow different behaviour without issues
        public static Object getValue() {
            return "value";
        }
    }

    public static class ConsumeSuperclass {
        // Expect classloading failure, although Java would in theory allow different behaviour without issues
        public static String getValue() {
            return "value";
        }
    }
}
