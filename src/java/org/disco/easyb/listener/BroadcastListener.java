package org.disco.easyb.listener;

import java.util.ArrayList;
import java.util.List;

import org.disco.easyb.BehaviorStep;
import org.disco.easyb.domain.Behavior;
import org.disco.easyb.result.Result;

public class BroadcastListener implements ExecutionListener {
    List<ExecutionListener> listeners = new ArrayList<ExecutionListener>();

    public void registerListener(ExecutionListener recipient) {
        listeners.add(recipient);
    }

    public void startBehavior(Behavior behavior) {
        for (ExecutionListener listener : listeners) {
            listener.startBehavior(behavior);
        }
    }

    public void stopBehavior(Behavior behavior) {
        for (ExecutionListener listener : listeners) {
            listener.stopBehavior(behavior);
        }
    }

    public void startStep(BehaviorStep step) {
        for (ExecutionListener listener : listeners) {
            listener.startStep(step);
        }
    }

    public void describeStep(String description) {
        for (ExecutionListener listener : listeners) {
            listener.describeStep(description);
        }
    }

    public void completeTesting() {
        for (ExecutionListener listener : listeners) {
            listener.completeTesting();
        }
    }

    public void stopStep() {
        for (ExecutionListener listener : listeners) {
            listener.stopStep();
        }
    }

    public void gotResult(Result result) {
        for (ExecutionListener listener : listeners) {
            listener.gotResult(result);
        }
    }
}
