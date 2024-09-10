package org.stianloader.micromixin.test.j8;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TestReport {

    public final AtomicInteger testSucceedCount = new AtomicInteger();
    public final AtomicInteger testFailureCount = new AtomicInteger();
    public final Set<Throwable> testFailures = ConcurrentHashMap.newKeySet();

    public Throwable runTest(Runnable x) {
        try {
            x.run();
            this.testSucceedCount.incrementAndGet();
            return null;
        } catch (Throwable t) {
            this.testFailureCount.incrementAndGet();
            this.testFailures.add(t);
            return t;
        }
    }
}
