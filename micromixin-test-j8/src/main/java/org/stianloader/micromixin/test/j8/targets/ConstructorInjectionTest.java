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

    public static class InjectHeadStatic extends MutableInt {
        public static int callerCounter;
        public InjectHeadStatic(int value) {
            super(value);
        }
    }

    public static class InjectHeadVirtual extends MutableInt {
        public static int callerCounter;
        public InjectHeadVirtual(int value) {
            super(value);
        }
    }

    public static class InjectReturnStatic extends MutableInt {
        public static int callerCounter;
        public InjectReturnStatic(int value) {
            super(value);
        }
    }

    public static class InjectReturnVirtual extends MutableInt {
        public static int callerCounter;
        public InjectReturnVirtual(int value) {
            super(value);
        }
    }

    public static class InjectTailStatic extends MutableInt {
        public static int callerCounter;
        public InjectTailStatic(int value) {
            super(value);
        }
    }

    public static class InjectTailVirtual extends MutableInt {
        public static int callerCounter;
        public InjectTailVirtual(int value) {
            super(value);
        }
    }

    public static class ModifySuperconstructorArg extends MutableInt {
        public ModifySuperconstructorArg() {
            super(2);
        }
    }
}
