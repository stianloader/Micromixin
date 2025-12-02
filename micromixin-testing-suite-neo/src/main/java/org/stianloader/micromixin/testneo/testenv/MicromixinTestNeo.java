package org.stianloader.micromixin.testneo.testenv;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.stianloader.micromixin.testneo.testenv.TestReport.ClassReport;
import org.stianloader.micromixin.testneo.testenv.TestReport.MemberReport;
import org.stianloader.micromixin.testneo.testenv.TestReport.TestConstraint;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames;
import org.stianloader.micromixin.testneo.testenv.annotations.AssertMemberNames.AssertMemberName;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectSignaller;
import org.stianloader.micromixin.testneo.testenv.annotations.ExpectedAnnotations;
import org.stianloader.micromixin.testneo.testenv.annotations.InvokeArgument;
import org.stianloader.micromixin.testneo.testenv.annotations.InvokeStaticMethod;
import org.stianloader.micromixin.testneo.testenv.communication.Signaller;
import org.stianloader.micromixin.testneo.testenv.targets.InjectMixinsTarget;
import org.stianloader.micromixin.testneo.testenv.targets.OverwriteMixinsTarget;

public class MicromixinTestNeo {
    @Nullable
    private static Object evaluateArgument(@NotNull InvokeArgument argument) throws Throwable {
        Class<?> type = argument.type();
        if (argument.nullValue() && type != void.class) {
            throw new IllegalStateException("argument.nullValue() is true whilst argument.type() != void.class");
        }

        if (type == int.class) {
            return argument.intValue();
        } else if (type == long.class) {
            return argument.longValue();
        } else if (type == float.class) {
            return argument.floatValue();
        } else if (type == double.class) {
            return argument.doubleValue();
        } else if (type == short.class) {
            return argument.shortValue();
        } else if (type == char.class) {
            return argument.charValue();
        } else if (type == byte.class) {
            return argument.byteValue();
        } else if (type == boolean.class) {
            return argument.booleanValue();
        } else if (type == void.class) {
            if (argument.nullValue()) {
                return null;
            }
            throw new IllegalStateException("argument.type() is void.class whilst argument.nullValue() == false");
        } else {
            if (!Object.class.isAssignableFrom(type)) {
                throw new AssertionError("Internal error in test harness code");
            }

            InvokeStaticMethod invokeMethod = argument.methodValue();
            if (invokeMethod.returnType() != argument.type()) {
                throw new IllegalStateException("Return type does not match argument type");
            }

            return MicromixinTestNeo.evaluateMethodCall(invokeMethod);
        }
    }

    private static void evaluateClass(@NotNull Class<?> transformedTargetClass, @NotNull ClassReport report) {
        List<@NotNull Method> declaredMethods = new ArrayList<>(Arrays.asList(transformedTargetClass.getDeclaredMethods()));

        // Fetch failing cases
        try (InputStream is = transformedTargetClass.getClassLoader().getResourceAsStream(transformedTargetClass.getName().replace(".", "/") + ".class")) {
            ClassReader cr = new ClassReader(is);
            ClassNode node = new ClassNode();
            cr.accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);

            List<AnnotationNode> annotations = node.invisibleAnnotations;
            if (annotations != null) {
                for (AnnotationNode an : node.invisibleAnnotations) {
                    if (!an.desc.equals("Lorg/stianloader/micromixin/testneo/testenv/annotations/IncludeClasses$IncludeFailingClass;")) {
                        continue;
                    }
                    List<Object> values = an.values;
                    if (values == null) {
                        throw new AssertionError();
                    }
                    for (int i = 0; i < values.size(); i += 2) {
                        if (((String) values.get(i)).equals("value")) {
                            for (Object o : (List<?>) values.get(i + 1)) {
                                if (o == null) {
                                    throw new AssertionError();
                                } else if (!(o instanceof Type)) {
                                    throw new AssertionError();
                                }
                                Type type = (Type) o;
                                try (MemberReport mr = new MemberReport(report, "<clinit>/" + type.getClassName(), "()V")) {
                                    boolean loaded = false;
                                    boolean loggingCLFailures = SLF4JLogger.isThreadLoggingClassloadingFailures();
                                    try {
                                        SLF4JLogger.setThreadLoggingClassloadingFailures(false);
                                        transformedTargetClass.getClassLoader().loadClass(type.getInternalName().replace('/', '.'));
                                    } catch (ClassNotFoundException cnfe) {
                                        continue;
                                    } finally {
                                        if (loaded) {
                                            mr.reportFailure(TestConstraint.TRANSFORMATION_FAILURE_EXPECTED);
                                        } else {
                                            mr.reportSucess(TestConstraint.TRANSFORMATION_FAILURE_EXPECTED);
                                        }
                                        SLF4JLogger.setThreadLoggingClassloadingFailures(loggingCLFailures);
                                    }
                                }
                            }
                        } else {
                            SLF4JLogger.error(MicromixinTestNeo.class, "Unknown annotation attribute: " + values.get(i));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read class bytecode for class " + transformedTargetClass.getName(), e);
        }

        for (Method method : declaredMethods) {
            try (MemberReport memberReport = new MemberReport(report, method)) {
                ExpectedAnnotations expectedAnnotations = method.getDeclaredAnnotation(ExpectedAnnotations.class);
                AssertMemberNames memberNames = method.getDeclaredAnnotation(AssertMemberNames.class);
                ExpectSignaller expectSignaller = method.getDeclaredAnnotation(ExpectSignaller.class);

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
                            memberReport.reportFailure(TestConstraint.EXPECTED_ANNOTATIONS_PRESENT, new IllegalStateException("Annotation " + expectedAnnotation + " not present."));
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

                if (expectSignaller != null) {
                    try {
                        Object reciever = null;
                        if (!Modifier.isStatic(method.getModifiers())) {
                            reciever = MicromixinTestNeo.evaluteConstructor(method.getDeclaringClass(), expectSignaller.constructorArgs());
                        } else if (expectSignaller.constructorArgs().length != 0) {
                            throw new IllegalStateException("constructor arguments defined for static method.");
                        }

                        Signaller.resetSignal();
                        MicromixinTestNeo.evaluateMethod(method, reciever, expectSignaller.args());
                        OptionalInt readSignal = Signaller.readSignal();

                        if (readSignal.isEmpty()) {
                            throw new IllegalStateException("Signaller not called.");
                        } else if (readSignal.getAsInt() != expectSignaller.signalValue()) {
                            throw new IllegalStateException("Signal value mismatch (got " + readSignal.getAsInt() + ", expected " + expectSignaller.signalValue() + ")");
                        }

                        memberReport.reportSucess(TestConstraint.SIGNALLER_VALUE);
                    } catch (Throwable t) {
                        if (t instanceof ThreadDeath) {
                            throw (ThreadDeath) t;
                        } else if (t instanceof OutOfMemoryError) {
                            throw (OutOfMemoryError) t;
                        }
                        memberReport.reportFailure(TestConstraint.SIGNALLER_VALUE, t);
                    }
                }
            }
        }
    }

    private static void evaluateClass(@NotNull Class<?> transformedTargetClass, @NotNull TestReport report) {
        try (ClassReport classreport = new ClassReport(report, transformedTargetClass.getName())) {
            MicromixinTestNeo.evaluateClass(transformedTargetClass, classreport);
        }
    }

    @Nullable
    private static Object evaluateMethod(@NotNull Method m, @Nullable Object reciever, @NotNull InvokeArgument @NotNull [] arguments) throws Throwable {
        if (Modifier.isStatic(m.getModifiers()) != (reciever == null)) {
            throw new IllegalArgumentException("Reciever should be null for a static method, or non-null for a non-static method.");
        }

        Object[] args = new Object[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            args[i] = MicromixinTestNeo.evaluateArgument(arguments[i]);
        }

        return m.invoke(reciever, args);
    }

    @Nullable
    private static Object evaluateMethodCall(@NotNull InvokeStaticMethod argument) throws Throwable {
        Class<?> owner = argument.owner();
        Class<?> returnType = argument.returnType();
        String methodName = argument.name();

        Class<?>[] argTypes = new Class<?>[argument.methodArguments().length];
        Object[] args = new Object[argTypes.length];

        for (int i = 0; i < argTypes.length; i++) {
            InvokeStaticMethod.Primitive methodArg = argument.methodArguments()[i];
            argTypes[i] = methodArg.type();
            args[i] = MicromixinTestNeo.evaluateMethodPrimitive(methodArg);
        }

        Method method = owner.getDeclaredMethod(methodName, argTypes);
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("Method[" + method + "] is not static.");
        } else if (method.getReturnType() != returnType) {
            throw new IllegalStateException("Method return type is " + method.getReturnType() + ", which mismatches the declared return type of " + returnType);
        }

        return method.invoke(null, args);
    }

    @Nullable
    private static Object evaluateMethodPrimitive(@NotNull InvokeStaticMethod.Primitive primitive) {
        Class<?> type = primitive.type();

        if (type == int.class) {
            return primitive.intValue();
        } else if (type == long.class) {
            return primitive.longValue();
        } else if (type == float.class) {
            return primitive.floatValue();
        } else if (type == double.class) {
            return primitive.doubleValue();
        } else if (type == short.class) {
            return primitive.shortValue();
        } else if (type == char.class) {
            return primitive.charValue();
        } else if (type == byte.class) {
            return primitive.byteValue();
        } else if (type == boolean.class) {
            return primitive.booleanValue();
        } else if (type == void.class) {
            if (primitive.nullValue()) {
                return null;
            }
            throw new IllegalStateException("argument.type() is void.class whilst argument.nullValue() == false");
        } else {
            throw new IllegalStateException("Unknown/Unsupported primitive type: " + type);
        }
    }

    @NotNull
    private static <T> T evaluteConstructor(Class<T> reciever, @NotNull InvokeArgument @NotNull [] arguments) throws Throwable {
        Class<?>[] argTypes = new Class<?>[arguments.length];
        Object[] args = new Object[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            InvokeArgument arg = arguments[i];
            argTypes[i] = arg.type();
            args[i] = MicromixinTestNeo.evaluateArgument(arg);
        }

        Constructor<T> constructor = reciever.getConstructor(argTypes);

        return constructor.newInstance(args);
    }

    public static void main(String[] args) {
        SLF4JLogger.info(MicromixinTestNeo.class, "Testing environment with CL " + MicromixinTestNeo.class.getClassLoader().getName());

        try (TestReport report = new TestReport(true)) {
            MicromixinTestNeo.evaluateClass(InjectMixinsTarget.class, report);
            MicromixinTestNeo.evaluateClass(OverwriteMixinsTarget.class, report);
        }
    }
}
