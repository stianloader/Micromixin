package org.stianloader.micromixin.test.j8.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Desc;
import org.stianloader.micromixin.annotations.CanonicalOverwrite;
import org.stianloader.micromixin.test.j8.targets.CanonicalOverwriteTest;

public class CanonicalOverwriteMixins {
    @Mixin(CanonicalOverwriteTest.OverwriteExplicitDiffer.class)
    private static class OverwriteExplicitDiffer implements CanonicalOverwriteTest.CanonicalOverwriteInterfaceI {
        @CanonicalOverwrite(method = "invokeMethodIOverwritten")
        @Override
        public int invokeMethodI() {
            return 1;
        }
    }

    @Mixin(CanonicalOverwriteTest.OverwriteExplicitIdentity.class)
    private static class OverwriteExplicitIdentity implements CanonicalOverwriteTest.CanonicalOverwriteInterface {
        @CanonicalOverwrite(target = @Desc("invokeMethodOne"))
        @Override
        public void invokeMethodOne() {
            // NOP
        }
    }

    @Mixin(CanonicalOverwriteTest.OverwriteImplicit.class)
    private static class OverwriteImplicit implements CanonicalOverwriteTest.CanonicalOverwriteInterface {
        @CanonicalOverwrite
        @Override
        public void invokeMethodOne() {
            // NOP
        }
    }

    @Mixin(CanonicalOverwriteTest.OverwriteImplicitLVT.class)
    private static class OverwriteImplicitLVT implements CanonicalOverwriteTest.CanonicalOverwriteInterfaceLVT {
        @CanonicalOverwrite
        @Override
        public int invokeMethodLVT(int arg0, int arg1) {
            Integer hashcode = Integer.valueOf(((CanonicalOverwriteTest.CanonicalOverwriteInterfaceLVT) this).hashCode());
            if ((hashcode ^ hashcode.intValue()) != 0) {
                throw new AssertionError();
            }

            long sum = arg0 + arg1;
            return (int) sum;
        }
    }

    @Mixin(CanonicalOverwriteTest.OverwriteInterfaceMissing.class)
    private static class OverwriteInterfaceMissing {
        @CanonicalOverwrite
        public void invokeMethodOne() {
            // NOP
        }
    }

    @Mixin(CanonicalOverwriteTest.OverwriteTargetNotPresentExplicit.class)
    private static class OverwriteTargetNotPresentExplicit implements CanonicalOverwriteTest.CanonicalOverwriteInterface {
        @CanonicalOverwrite(method = "notAMethod")
        @Override
        public void invokeMethodOne() {
            // NOP
        }
    }

    @Mixin(CanonicalOverwriteTest.OverwriteTargetNotPresentImplicit.class)
    private static class OverwriteTargetNotPresentImplicit implements CanonicalOverwriteTest.CanonicalOverwriteInterface {
        @CanonicalOverwrite
        @Override
        public void invokeMethodOne() {
            // NOP
        }
    }
}
