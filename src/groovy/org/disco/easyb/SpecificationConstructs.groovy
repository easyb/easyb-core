package org.disco.easyb

import org.disco.easyb.delegates.EnsuringDelegate
import org.disco.easyb.listener.ExecutionListener
import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

class SpecificationConstructs extends BehaviorConstructs {
    private BehaviorStepStack stepStack = new BehaviorStepStack()
    private def beforeIt

    SpecificationConstructs(ExecutionListener listener) {
        super(listener)
    }

    def before(description, closure) {
        stepStack.startStep(listener, BehaviorStepType.BEFORE, description)
        beforeIt = closure
        stepStack.stopStep(listener)
    }

    def it(spec, closure) {
        stepStack.startStep(listener, BehaviorStepType.IT, spec)
        closure.delegate = new EnsuringDelegate()
        try {
            if (beforeIt != null) {
                beforeIt()
            }
            listener.gotResult(new Result(Result.SUCCEEDED))
            use(BehaviorCategory) {
                closure()
            }
        } catch (Throwable ex) {
            listener.gotResult(new Result(ex))
        }
        stepStack.stopStep(listener)
    }

    def and() {
        stepStack.startStep(listener, BehaviorStepType.AND, "")
        stepStack.stopStep(listener)
    }
}