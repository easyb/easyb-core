package org.disco.easyb.listener;

import org.disco.easyb.BehaviorStep;
import org.disco.easyb.domain.Behavior;
import org.disco.easyb.result.Result;

public interface ExecutionListener {
    void behaviorFileStarting(Behavior behavior);

    void behaviorFileComplete(BehaviorStep currentStep, Behavior behavior);

    void startStep(BehaviorStep step);

    void describeStep(String description);

    void testingComplete();

    void stopStep();

    void gotResult(Result result);
}
