package org.stianloader.micromixin.testneo.testenv;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestReport implements AutoCloseable {
    public static class ClassReport implements AutoCloseable {
        @NotNull
        private final String className;
        private boolean closed = false;
        @NotNull
        private final List<@NotNull MemberReport> memberReports = new ArrayList<>();

        public ClassReport(@NotNull TestReport owner, @NotNull String className) {
            if (owner.closed) {
                throw new IllegalStateException("'owner' is closed.");
            }

            this.className = Objects.requireNonNull(className);
            owner.reports.add(this);
        }

        @Override
        public void close() {
            if (this.closed) {
                throw new IllegalStateException("Already closed.");
            }
            for (MemberReport report : this.memberReports) {
                if (!report.closed) {
                    throw new IllegalStateException("An element of the class report is not closed. Affected element: " + report.getPathString());
                }
            }
            this.closed = true;
        }
    }

    public static class MemberReport implements AutoCloseable {
        @NotNull
        private static String buildDesc(@NotNull Method method) {
            StringBuilder sb = new StringBuilder("(");
            for (Class<?> arg : method.getParameterTypes()) {
                sb.append(arg.descriptorString());
            }
            return sb.append(")").append(method.getReturnType().descriptorString()).toString();
        }

        private boolean closed = false;
        @NotNull
        private final Set<@NotNull TestConstraint> failedConstraints = EnumSet.noneOf(TestConstraint.class);
        @NotNull
        private final Map<@NotNull TestConstraint, Throwable> failureCauses = new EnumMap<>(TestConstraint.class);
        @NotNull
        private final String memberDesc;
        @NotNull
        private final String memberName;
        @NotNull
        private final ClassReport owner;
        @NotNull
        private final Set<@NotNull TestConstraint> passedConstraints = EnumSet.noneOf(TestConstraint.class);

        public MemberReport(@NotNull ClassReport owner, @NotNull Method method) {
            this(owner, method.getName(), MemberReport.buildDesc(method));
        }

        public MemberReport(@NotNull ClassReport owner, @NotNull String name, @NotNull String desc) {
            if (owner.closed) {
                throw new IllegalStateException("'owner' is closed.");
            }

            this.owner = owner;
            this.memberName = Objects.requireNonNull(name);
            this.memberDesc = Objects.requireNonNull(desc);
            this.owner.memberReports.add(this);
        }

        @Override
        public void close() {
            if (this.closed) {
                throw new IllegalStateException("Already closed.");
            }
            this.closed = true;
        }

        @Contract(pure = true)
        @NotNull
        public Set<@NotNull TestConstraint> getFailedConstraints() {
            return Collections.unmodifiableSet(this.failedConstraints);
        }

        @Nullable
        public Throwable getFailureCause(@NotNull TestConstraint constraint) {
            return this.failureCauses.get(constraint);
        }

        @Contract(pure = true)
        public int getFailures() {
            return this.failedConstraints.size();
        }

        @NotNull
        @Contract(pure = true)
        public String getPathString() {
            return this.owner.className + "." + this.memberName + ":" + this.memberDesc;
        }

        @Contract(pure = true)
        public int getSuccesses() {
            return this.passedConstraints.size();
        }

        @Contract(pure = true)
        public int getTotalConstraints() {
            return this.failedConstraints.size() + this.passedConstraints.size();
        }

        @Contract(pure = true)
        public boolean hasFailures() {
            return !this.failedConstraints.isEmpty();
        }

        @Contract(pure = false, mutates = "this", value = "null -> fail; !null -> this")
        @NotNull
        public MemberReport reportFailure(@NotNull TestConstraint constraint) {
            return this.reportFailure(constraint, null);
        }

        @Contract(pure = false, mutates = "this", value = "null -> fail; !null -> this")
        @NotNull
        public MemberReport reportFailure(@NotNull TestConstraint constraint, @Nullable Throwable t) {
            if (this.closed) {
                throw new IllegalStateException("Report (" + this.getPathString() + ") already closed");
            } else if (this.passedConstraints.contains(constraint)) {
                throw new IllegalStateException("Report (" + this.getPathString() + ") already contains constraint as passed: " + constraint);
            } else if (this.failedConstraints.contains(constraint)) {
                throw new IllegalStateException("Report (" + this.getPathString() + ") already contains constraint as a failing constraint: " + constraint);
            }
            this.failedConstraints.add(constraint);
            if (t != null) {
                this.failureCauses.put(constraint, t);
            }
            return this;
        }

        @Contract(pure = false, mutates = "this", value = "null -> fail; !null -> this")
        @NotNull
        public MemberReport reportSucess(@NotNull TestConstraint constraint) {
            if (this.closed) {
                throw new IllegalStateException("Report (" + this.getPathString() + ") already closed");
            } else if (this.passedConstraints.contains(constraint)) {
                throw new IllegalStateException("Report (" + this.getPathString() + ") already contains constraint as passed: " + constraint);
            } else if (this.failedConstraints.contains(constraint)) {
                throw new IllegalStateException("Report (" + this.getPathString() + ") already contains constraint as a failing constraint: " + constraint);
            }
            this.passedConstraints.add(constraint);
            return this;
        }
    }

    public static enum TestConstraint {
        EXPECTED_ANNOTATIONS_PRESENT,
        MEMBER_NAME_CONFORMITY,
        SIGNALLER_VALUE,
        TRANSFORMATION_FAILURE_EXPECTED;
    }

    private boolean closed = false;
    private final boolean dumpOnClose;
    @NotNull
    private final List<@NotNull ClassReport> reports = new ArrayList<>();

    public TestReport(boolean dumpOnClose) {
        this.dumpOnClose = dumpOnClose;
    }

    @Override
    public void close() {
        if (this.closed) {
            throw new IllegalStateException("Already closed.");
        }
        for (ClassReport report : this.reports) {
            if (!report.closed) {
                throw new IllegalStateException("An element of the report (class '" + report.className + "') was not closed before the global report was closed.");
            }
        }
        this.closed = true;
        if (this.dumpOnClose) {
            this.dump();
        }
    }

    public void dump() {
        if (!this.closed) {
            throw new IllegalStateException("Report must be closed before it can be dumped. Call #close() first.");
        }

        Set<ClassReport> classes = new TreeSet<>((a, b) -> a.className.compareTo(b.className));
        classes.addAll(this.reports);

        for (ClassReport report : classes) {
            for (MemberReport member : report.memberReports) {
                SLF4JLogger.debug(TestReport.class, "Member '{}'.'{}':'{}' passed following constraints: {}", member.owner.className, member.memberName, member.memberDesc, member.passedConstraints);
            }
        }

        for (ClassReport report : classes) {
            for (MemberReport member : report.memberReports) {
                for (TestConstraint constraint : member.getFailedConstraints()) {
                    String message = "Member " + member.getPathString() + " failed constraint " + constraint;
                    Throwable cause = member.getFailureCause(constraint);
                    if (cause == null) {
                        SLF4JLogger.error(TestReport.class, message);
                    } else {
                        SLF4JLogger.error(TestReport.class, message, cause);
                    }
                }
                if (member.getTotalConstraints() == 0) {
                    SLF4JLogger.warn(TestReport.class, "Member " + member.getPathString() + " has no constraints!");
                }
            }
        }

        SLF4JLogger.info(TestReport.class, "Summary: ");

        for (ClassReport report : classes) {
            int passedCount = 0;
            int totalCount = 0;
            int passedMembers = 0;
            for (MemberReport member : report.memberReports) {
                passedCount += member.getSuccesses();
                totalCount += member.getTotalConstraints();
                if (member.getFailures() == 0 && member.getTotalConstraints() != 0) {
                    passedMembers++;
                }
            }

            String message = "<" + report.className + "> " + passedMembers + "/" + report.memberReports.size() + " members passed (" + passedCount + "/" + totalCount + " constraints passed total)";

            if (passedMembers != report.memberReports.size()) {
                if (passedMembers == 0) {
                    SLF4JLogger.error(TestReport.class, message);
                } else {
                    SLF4JLogger.warn(TestReport.class, message);
                }
            } else if (passedMembers != 0) {
                SLF4JLogger.info(TestReport.class, message);
            } else {
                SLF4JLogger.warn(TestReport.class, message);
            }
        }
    }
}
