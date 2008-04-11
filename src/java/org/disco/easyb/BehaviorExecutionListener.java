package org.disco.easyb;

import org.disco.easyb.domain.Behavior;

public interface BehaviorExecutionListener {
    void behaviorFileStarting(Behavior behavior);

    void behaviorFileComplete(BehaviorStep currentStep, Behavior behavior);
}
