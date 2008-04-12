package org.disco.easyb.listener;

import org.disco.easyb.BehaviorStep;
import org.disco.easyb.domain.Behavior;
import org.disco.easyb.result.Result;

public interface ExecutionListener {
    void startBehavior(Behavior behavior);

    void startStep(BehaviorStep step);

    void describeStep(String description);

    void gotResult(Result result);

    void stopStep();

    void stopBehavior(BehaviorStep currentStep, Behavior behavior);

    void completeTesting();
}
