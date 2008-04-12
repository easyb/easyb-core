package org.disco.easyb.listener;

import org.disco.easyb.BehaviorStep;
import org.disco.easyb.domain.Behavior;
import org.disco.easyb.result.Result;

public class ExecutionListenerAdaptor implements ExecutionListener {
    public void startBehavior(Behavior behavior) {
    }

    public void stopBehavior(BehaviorStep currentStep, Behavior behavior) {
    }

    public void startStep(BehaviorStep step) {
    }

    public void describeStep(String description) {
    }

    public void stopStep() {
    }

    public void gotResult(Result result) {
    }

    public void completeTesting() {
    }
}
