package org.easyb.batches

import java.util.concurrent.atomic.AtomicInteger
import org.easyb.domain.Behavior
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Manage running batches of behaviors.
 * For example, running every 1st behavior out of 10.
 */
class BatchManager {
    def behaviorCount = new AtomicInteger(0);
    def registeredBehaviors = new CopyOnWriteArrayList<Behavior>();
    def batchCount;
    def batchNumber;

    def BatchManager(Integer batchCount, Integer batchNumber) {
        this.batchCount = batchCount
        this.batchNumber = batchNumber
    }

    def getCurrentBehaviorNumber() {
        behaviorCount.get();
    }
    
    public boolean shouldExecute (def someBehavior) {
        if (batchCount) {
            def currentCount = behaviorCount.incrementAndGet()
            (currentCount % batchCount == (batchNumber % batchCount))
        } else {
            true
        }
    }
}
