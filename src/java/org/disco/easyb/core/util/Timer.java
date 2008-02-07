package org.disco.easyb.core.util;

/**
 * @author sevensoft [Ken Brooks]
 */
public class Timer {
    long start = -1L;
    long elapsed = 0L;

    public void start() {
        start = System.currentTimeMillis();
    }

    public void stop() {
        elapsed = System.currentTimeMillis() - start;
    }

    public long elapsedTimeMillis() {
        return elapsed;
    }
}
