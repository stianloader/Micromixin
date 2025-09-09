package org.stianloader.micromixin.testneo.testenv;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.jetbrains.annotations.NotNull;
import org.stianloader.micromixin.testneo.testenv.TestReport.ClassReport;
import org.stianloader.micromixin.testneo.testenv.TestReport.MemberReport;
import org.stianloader.micromixin.testneo.testenv.TestReport.TestConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.targets.OverwriteMixinsTarget;

public class MicromixinTestNeo {
    public static void main(String[] args) {
        SLF4JLogger.info(MicromixinTestNeo.class, "Testing environment with CL " + MicromixinTestNeo.class.getClassLoader().getName());

        try (TestReport report = new TestReport(true)) {
            MicromixinTestNeo.evaluateClass(OverwriteMixinsTarget.class, report);
        }
    }

    private static void evaluateClass(@NotNull Class<?> transformedTargetClass, @NotNull TestReport report) {
        try (ClassReport classreport = new ClassReport(report, transformedTargetClass.getName())) {
            MicromixinTestNeo.evaluateClass(transformedTargetClass, classreport);
        }
    }

    private static void evaluateClass(@NotNull Class<?> transformedTargetClass, @NotNull ClassReport report) {
        for (Method method : transformedTargetClass.getDeclaredMethods()) {
            try (MemberReport memberReport = new MemberReport(report, method)) {
                ExpectedAnnotations expectedAnnotations = method.getDeclaredAnnotation(ExpectedAnnotations.class);
                AssertMemberNames memberNames = method.getDeclaredAnnotation(AssertMemberNames.class);

                {
                    AssertMemberName assertName = method.getDeclaredAnnotation(AssertMemberName.class);
                    if (assertName != null) {
                        memberNames = new AssertMemberNames() {
                            @Override
                            public Class<? extends Annotation> annotationType() {
                                return AssertMemberNames.class;
                            }

                            @Override
                            @NotNull
                            public AssertMemberName @NotNull[] value() {
                                return new @NotNull AssertMemberName[] {
                                    assertName
                                };
                            }
                        };
                    }
                }

                if (expectedAnnotations != null) {
                    boolean annotationsPresent = true;

                    for (Class<? extends Annotation> expectedAnnotation : expectedAnnotations.value()) {
                        if (method.getDeclaredAnnotation(expectedAnnotation) == null) {
                            memberReport.reportFailure(TestConstraint.EXPECTED_ANNOTATIONS_PRESENT);
                            annotationsPresent = false;
                            break;
                        }
                    }

                    if (annotationsPresent) {
                        memberReport.reportSucess(TestConstraint.EXPECTED_ANNOTATIONS_PRESENT);
                    }
                }

                if (memberNames != null) {
                    boolean success = true;

                    for (AssertMemberName memberNameConstraint : memberNames.value()) {
                        if (!AssertMemberNames.AssertConstraint.test(method.getName(), memberNameConstraint)) {
                            success = false;
                            break;
                        }
                    }

                    if (success) {
                        memberReport.reportSucess(TestConstraint.MEMBER_NAME_CONFORMITY);
                    } else {
                        memberReport.reportFailure(TestConstraint.MEMBER_NAME_CONFORMITY);
                    }
                }
            }
        }
    }
}
