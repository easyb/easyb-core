package org.easyb.listener;

import org.easyb.domain.Behavior;
import org.easyb.BehaviorStep;
import org.easyb.result.ReportingTag;
import org.easyb.result.Result;

public class ExecutionListenerAdaptor implements ExecutionListener {
    public void startBehavior(Behavior behavior) {
    }

    public void stopBehavior(BehaviorStep step, Behavior behavior) {
    }

    public void tag(ReportingTag tag) {    
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
