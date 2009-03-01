package org.easyb.listener;

import org.easyb.domain.Behavior;
import org.easyb.BehaviorStep;
import org.easyb.result.Result;

public interface ExecutionListener {
    void startBehavior(Behavior behavior);

    void startStep(BehaviorStep step);

    void describeStep(String description);

    void gotResult(Result result);

    void stopStep();

    void stopBehavior(BehaviorStep step, Behavior behavior);

    void completeTesting();
}
