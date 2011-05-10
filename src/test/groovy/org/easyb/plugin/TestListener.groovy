package org.easyb.plugin

import org.easyb.listener.ExecutionListenerAdaptor
import org.easyb.domain.Behavior
import org.easyb.BehaviorStep
import org.easyb.util.BehaviorStepType
import org.easyb.exception.VetoStepException

class TestListener extends ExecutionListenerAdaptor {
    @Override
    void startBehavior(Behavior behavior) {
        super.startBehavior(behavior)
    }

    @Override
    void stopBehavior(BehaviorStep step, Behavior behavior) {
        super.stopBehavior(step, behavior)
    }

    @Override
    void startStep(BehaviorStep step) {
        if (step.stepType == BehaviorStepType.WHEN) {
            println "INTERCEPTING WHEN IN LISTENER $this"
           // throw new VetoStepException("Thou shall not pass!")
        }
        super.startStep(step)
    }

    @Override
    void stopStep() {
        super.stopStep()
    }


}
