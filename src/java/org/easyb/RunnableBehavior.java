package org.easyb;

import java.io.IOException;

import org.easyb.domain.Behavior;
import org.easyb.listener.BroadcastListener;

public class RunnableBehavior implements Runnable {
    private Behavior behavior;
    private BroadcastListener broadcastListener;
    private Exception failure;

    public RunnableBehavior(Behavior behavior, BroadcastListener broadcastListener) {
        this.behavior = behavior;
        this.broadcastListener = broadcastListener;
    }

    public void run() {
        try {
            behavior.execute(broadcastListener);
        } catch (IOException e) {
            failure = e;
        }
    }

    public boolean isFailed() {
        return failure != null;
    }

    public Exception getFailure() {
        return failure;
    }
}
