package org.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.delegates.EnsuringDelegate
import org.easyb.result.Result
import org.easyb.util.BehaviorStepType

class SpecificationKeywords extends BehaviorKeywords {
    private BehaviorStepStack stepStack = new BehaviorStepStack()
    private def beforeIt
    private def afterIt

    SpecificationKeywords(ExecutionListener listener) {
        super(listener)
    }

    def after(description, closure) {
        stepStack.startStep(listener, BehaviorStepType.AFTER, description)
        afterIt = closure
        stepStack.stopStep(listener)
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
            if (afterIt != null) {
                afterIt()
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