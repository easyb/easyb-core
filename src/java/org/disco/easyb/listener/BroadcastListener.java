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

    public void behaviorFileStarting(Behavior behavior) {
        for (ExecutionListener listener : listeners) {
            listener.behaviorFileStarting(behavior);
        }
    }

    public void behaviorFileComplete(BehaviorStep currentStep, Behavior behavior) {
        for (ExecutionListener listener : listeners) {
            listener.behaviorFileComplete(currentStep, behavior);
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

    public void testingComplete() {
        for (ExecutionListener listener : listeners) {
            listener.testingComplete();
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
