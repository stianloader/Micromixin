package org.stianloader.micromixin.test.j8;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.stianloader.micromixin.test.j8.localsprinting.LocalPrintingContext;
import org.stianloader.micromixin.test.j8.localsprinting.LocalsPrintingIO;

import net.minestom.server.extras.selfmodification.MinestomRootClassLoader;

public class TestSet {

    @FunctionalInterface
    public static interface ByteSupplier {
        byte getAsByte();
    }

    @FunctionalInterface
    public static interface CharSupplier {
        char getAsChar();
    }

    @FunctionalInterface
    public static interface FloatSupplier {
        float getAsFloat();
    }

    @FunctionalInterface
    public static interface ShortSupplier {
        short getAsShort();
    }

    private static class TestUnit {
        private final String name;
        private final Runnable unit;

        public TestUnit(String name, Runnable unit) {
            this.name = name;
            this.unit = unit;
        }
    }

    private static boolean isVerifyError(Throwable t) {
        if (t == null) {
            return false;
        }
        if (t instanceof VerifyError) {
            return true;
        }
        for (Throwable s : t.getSuppressed()) {
            if (TestSet.isVerifyError(s)) {
                return true;
            }
        }
        return TestSet.isVerifyError(t.getCause());
    }

    private final List<TestUnit> units = new ArrayList<>();

    public void addUnit(String name, Runnable unit) {
        this.units.add(new TestUnit(name, unit));
    }

    public void addUnitAssertEquals(String name, BooleanSupplier unit, boolean value) {
        this.units.add(new TestUnit(name, () -> {
            boolean returned = unit.getAsBoolean();
            if (returned != value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but expected " + value + " to be returned");
            }
        }));
    }

    public void addUnitAssertEquals(String name, ByteSupplier unit, byte value) {
        this.units.add(new TestUnit(name, () -> {
            double returned = unit.getAsByte();
            if (returned != value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but expected " + value + " to be returned");
            }
        }));
    }

    public void addUnitAssertEquals(String name, CharSupplier unit, char value) {
        this.units.add(new TestUnit(name, () -> {
            char returned = unit.getAsChar();
            if (returned != value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but expected " + value + " to be returned");
            }
        }));
    }

    public void addUnitAssertEquals(String name, DoubleSupplier unit, double value) {
        this.units.add(new TestUnit(name, () -> {
            double returned = unit.getAsDouble();
            if (returned != value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but expected " + value + " to be returned");
            }
        }));
    }

    public void addUnitAssertEquals(String name, FloatSupplier unit, float value) {
        this.units.add(new TestUnit(name, () -> {
            float returned = unit.getAsFloat();
            if (returned != value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but expected " + value + " to be returned");
            }
        }));
    }

    public void addUnitAssertEquals(String name, IntSupplier unit, int value) {
        this.units.add(new TestUnit(name, () -> {
            int returned = unit.getAsInt();
            if (returned != value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but expected " + value + " to be returned");
            }
        }));
    }

    public void addUnitAssertEquals(String name, LongSupplier unit, long value) {
        this.units.add(new TestUnit(name, () -> {
            long returned = unit.getAsLong();
            if (returned != value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but expected " + value + " to be returned");
            }
        }));
    }

    public void addUnitAssertEquals(String name, ShortSupplier unit, short value) {
        this.units.add(new TestUnit(name, () -> {
            short returned = unit.getAsShort();
            if (returned != value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but expected " + value + " to be returned");
            }
        }));
    }

    public <T> void addUnitAssertEquals(String name, Supplier<T> unit, T value) {
        this.units.add(new TestUnit(name, () -> {
            T returned = unit.get();
            if (!Objects.equals(returned, value)) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but expected " + value + " to be returned");
            }
        }));
    }

    public void addUnitAssertLocalPrinting(@NotNull String name, @NotNull Runnable unit, String[]... witnessValues) {
        this.addUnit(name, () -> {
            LocalPrintingContext[] tests = LocalsPrintingIO.guardedRead(unit);
            LocalPrintingContext[] witnesses = new LocalPrintingContext[witnessValues.length];
            for (int i = 0; i < witnessValues.length; i++) {
                witnesses[i] = LocalsPrintingIO.parse(witnessValues[i], 0, witnessValues[i].length);
            }
            LocalsPrintingIO.assertEquals(tests, witnesses);
        });
    }

    public void addUnitAssertLocalPrinting(@NotNull String className, String[]... witnessValues) {
        this.addUnitAssertLocalPrinting(className, () -> {
            try {
                TestSet.class.getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new AssertionError(e);
            }
        }, witnessValues);
    }

    public void addUnitAssertNotEquals(String name, BooleanSupplier unit, boolean value) {
        this.units.add(new TestUnit(name, () -> {
            boolean returned = unit.getAsBoolean();
            if (returned == value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but that value should not be returned.");
            }
        }));
    }

    public void addUnitAssertNotEquals(String name, ByteSupplier unit, byte value) {
        this.units.add(new TestUnit(name, () -> {
            double returned = unit.getAsByte();
            if (returned == value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but that value should not be returned.");
            }
        }));
    }

    public void addUnitAssertNotEquals(String name, CharSupplier unit, char value) {
        this.units.add(new TestUnit(name, () -> {
            char returned = unit.getAsChar();
            if (returned == value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but that value should not be returned.");
            }
        }));
    }

    public void addUnitAssertNotEquals(String name, DoubleSupplier unit, double value) {
        this.units.add(new TestUnit(name, () -> {
            double returned = unit.getAsDouble();
            if (returned == value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but that value should not be returned.");
            }
        }));
    }

    public void addUnitAssertNotEquals(String name, FloatSupplier unit, float value) {
        this.units.add(new TestUnit(name, () -> {
            float returned = unit.getAsFloat();
            if (returned == value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but that value should not be returned.");
            }
        }));
    }

    public void addUnitAssertNotEquals(String name, IntSupplier unit, int value) {
        this.units.add(new TestUnit(name, () -> {
            int returned = unit.getAsInt();
            if (returned == value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but that value should not be returned.");
            }
        }));
    }

    public void addUnitAssertNotEquals(String name, LongSupplier unit, long value) {
        this.units.add(new TestUnit(name, () -> {
            long returned = unit.getAsLong();
            if (returned == value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but that value should not be returned.");
            }
        }));
    }

    public void addUnitAssertNotEquals(String name, ShortSupplier unit, short value) {
        this.units.add(new TestUnit(name, () -> {
            short returned = unit.getAsShort();
            if (returned == value) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but that value should not be returned.");
            }
        }));
    }

    public <T> void addUnitAssertNotEquals(String name, Supplier<T> unit, T value) {
        this.units.add(new TestUnit(name, () -> {
            T returned = unit.get();
            if (Objects.equals(returned, value)) {
                throw new AssertionError("Unit " + unit + " returned " + returned + ", but that value should not be returned.");
            }
        }));
    }

    public void addUnitExpectClassloadingFailure(String name) {
        this.units.add(new TestUnit(name, () -> {
            boolean logFailures = MinestomRootClassLoader.getInstance().isThreadLoggingClassloadingFailures();
            try {
                MinestomRootClassLoader.getInstance().setThreadLoggingClassloadingFailures(false);
                TestSet.class.getClassLoader().loadClass(name);
            } catch (ClassNotFoundException cnfe) {
                return;
            } finally {
                MinestomRootClassLoader.getInstance().setThreadLoggingClassloadingFailures(logFailures);
            }
            throw new IllegalStateException("Loaded class " + name + " without a classloading failure, but a failure was anticipated.");
        }));
    }

    public void addUnitExpectThrow(String name, Runnable unit) {
        this.units.add(new TestUnit(name, () -> {
            try {
                unit.run();
            } catch (Throwable t) {
                return;
            }
            throw new AssertionError("Runnable " + unit.toString() + " did not throw");
        }));
    }

    public void addUnitExpectThrow(String name, Runnable unit, Class<? extends Throwable> exception) {
        this.units.add(new TestUnit(name, () -> {
            try {
                unit.run();
            } catch (Throwable t) {
                if (!exception.isInstance(t)) {
                    throw new AssertionError("Runnable " + unit.toString() + " should throw a " + exception.getName() + " but threw a " + t.getClass().getName(), t);
                }
                return;
            }
            throw new AssertionError("Runnable " + unit.toString() + " did not throw");
        }));
    }

    public void addUnitExpectTransformationFailure(String name) {
        this.units.add(new TestUnit(name, () -> {
            boolean logFailures = MinestomRootClassLoader.getInstance().isThreadLoggingClassloadingFailures();
            try {
                MinestomRootClassLoader.getInstance().setThreadLoggingClassloadingFailures(false);
                TestSet.class.getClassLoader().loadClass(name);
            } catch (ClassNotFoundException cnfe) {
                if (TestSet.isVerifyError(cnfe)) {
                    throw new IllegalStateException("Class " + name + " loaded wrongly", cnfe);
                }
                return;
            } finally {
                MinestomRootClassLoader.getInstance().setThreadLoggingClassloadingFailures(logFailures);
            }

            throw new IllegalStateException("Class " + name + " loaded without any issues, but issues were expected!");
        }));
    }

    public void addUnitExpectTransformationFailureOrAssert(String name, BooleanSupplier test) {
        this.units.add(new TestUnit(name, () -> {
            boolean logFailures = MinestomRootClassLoader.getInstance().isThreadLoggingClassloadingFailures();
            try {
                MinestomRootClassLoader.getInstance().setThreadLoggingClassloadingFailures(false);
                TestSet.class.getClassLoader().loadClass(name);
            } catch (ClassNotFoundException cnfe) {
                if (TestSet.isVerifyError(cnfe)) {
                    throw new IllegalStateException("Class " + name + " loaded wrongly", cnfe);
                } else if (test.getAsBoolean()) {
                    throw new IllegalStateException("Class " + name + " passed test but a classloading failure occured", cnfe);
                }
                return;
            } finally {
                MinestomRootClassLoader.getInstance().setThreadLoggingClassloadingFailures(logFailures);
            }

            if (!test.getAsBoolean()) {
                throw new IllegalStateException("Class " + name + " failed to pass test (without CL failure)");
            }
        }));
    }

    public void executeAll(TestReport report, Logger logger) {
        for (TestUnit unit : units) {
            logger.info("  Executing {}", unit.name);
            Throwable t = report.runTest(unit.unit);
            if (t != null) {
                logger.warn("   Failed.", t);
            } else {
                logger.info("   Success.");
            }
        }
    }
}
