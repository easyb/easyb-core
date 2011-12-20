package org.easyb;

import java.io.IOException;

import org.easyb.batches.BatchManager;
import org.easyb.domain.Behavior;

public class RunnableBehavior implements Runnable {
    private Behavior behavior;
    private Exception failure;
    private BatchManager batchManager;

    public RunnableBehavior(Behavior behavior, BatchManager batchManager) {
        this.behavior = behavior;
        this.batchManager = batchManager;
    }

    public void run() {
        try {
            if (shouldExecute(behavior)) {
                behavior.execute();
            }
        } catch (IOException e) {
            failure = e;
        }
    }

    private boolean shouldExecute(Behavior behavior) {
        return batchManager.shouldExecute(behavior);
    }

    public boolean isFailed() {
        return failure != null;
    }

    public Exception getFailure() {
        return failure;
    }
}
