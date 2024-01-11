package de.geolykt.starloader.micromixin.test.j8.targets.mixinextra;

public class ModifyReturnValueInvalidTargetInsnTest {

    public static class TargetHead {
        // Expect classloading failure
        public static Object getValue() {
            return null;
        }
    }

    public static class TargetReturn {
        // Expect non-null return value
        public static Object getValue() {
            return null;
        }
    }

}
