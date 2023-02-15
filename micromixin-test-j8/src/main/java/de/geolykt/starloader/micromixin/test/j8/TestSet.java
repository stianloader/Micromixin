package de.geolykt.starloader.micromixin.test.j8;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

public class TestSet {

    private static class TestUnit {
        private final Runnable unit;
        private final String name;

        public TestUnit(String name, Runnable unit) {
            this.name = name;
            this.unit = unit;
        }
    }

    private final List<TestUnit> units = new ArrayList<>();

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

    public void addUnit(String name, Runnable unit) {
        this.units.add(new TestUnit(name, unit));
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
}
