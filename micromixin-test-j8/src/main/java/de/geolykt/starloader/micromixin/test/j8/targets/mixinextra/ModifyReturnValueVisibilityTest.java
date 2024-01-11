package de.geolykt.starloader.micromixin.test.j8.targets.mixinextra;

public class ModifyReturnValueVisibilityTest {
    public static class VisibilityPackageProtected {
        // Expect classloading failure
        public static Object getValue() {
            return null;
        }
    }

    public static class VisibilityPublic {
        // Expect classloading failure
        public static Object getValue() {
            return null;
        }
    }

    public static class VisibilityProtected {
        // Expect classloading failure
        public static Object getValue() {
            return null;
        }
    }

    public static class VisibilityPrivate {
        // Expect non-null value
        public static Object getValue() {
            return null;
        }
    }
}
