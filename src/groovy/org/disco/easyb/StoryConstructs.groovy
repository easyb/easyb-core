package org.disco.easyb

import org.disco.easyb.delegates.EnsuringDelegate
import org.disco.easyb.delegates.PlugableDelegate
import org.disco.easyb.listener.ExecutionListener
import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

class StoryConstructs extends BehaviorConstructs {
    def stepStack = new BehaviorStepStack()
    def beforeScenario
    def afterScenario

    //TODO: add these too listener/reporting
    def beforeDone = false
    def afterDone = false

    StoryConstructs(ExecutionListener listener) {
        super(listener)
    }

    def pendingClosure = {
        listener.gotResult(new Result(Result.PENDING))
    }

    def before(description, beforeClosure) {
        if (!beforeDone) {
            beforeClosure()
            beforeDone = true
        }
    }

    def after(description, afterClosure) {
        if (!afterClosure) {
            afterClosure()
            afterDone = true
        }
    }

    def beforeEach(description, closure) {
        beforeScenario = closure
    }

    def afterEach(description, closure) {
        afterScenario = closure
    }

    def scenario(scenarioDescription, scenarioClosure) {
        stepStack.startStep(listener, BehaviorStepType.SCENARIO, scenarioDescription)
        if (beforeScenario != null) {
            beforeScenario()
        }
        scenarioClosure()
        if (afterScenario != null) {
            afterScenario()
        }
        stepStack.stopStep(listener)
    }

    def given(givenDescription, closure) {
        stepStack.startStep(listener, BehaviorStepType.GIVEN, givenDescription)
        closure.delegate = new PlugableDelegate()
        try {
            closure()
        } catch (Throwable t) {
            listener.gotResult(new Result(t))
        }
        stepStack.stopStep(listener)
    }

    def when(whenDescription, closure = {}) {
        stepStack.startStep(listener, BehaviorStepType.WHEN, whenDescription)
        closure.delegate = new EnsuringDelegate()
        try {
            closure()
        } catch (Throwable t) {
            listener.gotResult(new Result(t))
        }
        stepStack.stopStep(listener)
    }

    def then(spec, closure) {
        stepStack.startStep(listener, BehaviorStepType.THEN, spec)
        closure.delegate = new EnsuringDelegate()
        try {
            use(BehaviorCategory) {
                closure()
            }
            if (!closure.is(pendingClosure)) {
                listener.gotResult(new Result(Result.SUCCEEDED))
            }
        } catch (Throwable t) {
            listener.gotResult(new Result(t))
        }
        stepStack.stopStep(listener)
    }

    def and(description, closure) {
        if (stepStack.lastStep().stepType == BehaviorStepType.GIVEN) {
            given(description, closure)
        } else if (stepStack.lastStep().stepType == BehaviorStepType.WHEN) {
            when(description, closure)
        } else if (stepStack.lastStep().stepType == BehaviorStepType.THEN) {
            then(description, closure)
        } else {
            stepStack.startStep(listener, BehaviorStepType.AND, "")
            stepStack.stopStep(listener)
        }
    }
}