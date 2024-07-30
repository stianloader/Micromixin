package org.stianloader.micromixin.test.j8.targets;

import org.stianloader.micromixin.test.j8.MutableInt;

public class ConstructorInjectionTest {

    public static class IllegalSuperconstructorRedirect extends MutableInt {
        public IllegalSuperconstructorRedirect() {
            super(2);
        }
    }

    public static class IllegalSuperconstructorRedirect2 extends MutableInt {
        public IllegalSuperconstructorRedirect2() {
            super(2);
        }
    }

    public static class ModifySuperconstructorArg extends MutableInt {
        public ModifySuperconstructorArg() {
            super(2);
        }
    }
}
