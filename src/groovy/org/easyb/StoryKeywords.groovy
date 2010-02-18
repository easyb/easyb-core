package org.easyb

import java.util.regex.Pattern
import org.easyb.listener.ExecutionListener
import org.easyb.result.Result
import org.easyb.delegates.EnsuringDelegate
import org.easyb.delegates.PlugableDelegate
import org.easyb.util.BehaviorStepType

class StoryKeywords extends BehaviorKeywords {
    def stepStack = new BehaviorStepStack()
    def beforeScenario
    def afterScenario

    //TODO: add these too listener/reporting
    def beforeDone = false
    def afterDone = false

    def ignoreAll = false
    def ignoreList = []
    def ignoreRegEx
    
    def sharedBeaviors = [:]

    StoryKeywords(ExecutionListener listener) {
        super(listener)
    }
    
    def sharedBehavior(description, closure) {
        sharedBeaviors.put(description, closure)
    }
    
    def itBehavesAs(description) {
        if (sharedBeaviors.containsKey(description)) {
            def closure = sharedBeaviors.get(description)
            closure()
        }
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
        if (!afterDone) {
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
        if (!ignoreAll && !this.ignoreList.contains(scenarioDescription)
                && !this.ignoreRegEx?.matcher(scenarioDescription)?.matches()) {
            runScenario(scenarioClosure, scenarioDescription)
        } else {
            stepStack.startStep(listener, BehaviorStepType.SCENARIO, scenarioDescription)
            listener.gotResult new Result(Result.IGNORED)
            stepStack.stopStep(listener)
        }
    }

    def runScenario(scenarioClosure, scenarioDescription) {
        stepStack.startStep(listener, BehaviorStepType.SCENARIO, scenarioDescription)
        if (beforeScenario != null) {
            beforeScenario()
        }

        def initialsteps = stepStack.steps.size()

        scenarioClosure()

        if (initialsteps.equals(stepStack.steps.size())) {
            listener.gotResult(new Result(Result.PENDING))
        }

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

    def but(description, closure) {
      and(description, closure)
    }

    def all() {
        this.ignoreAll = true
    }

    def ignore(scenarios) {
        if (!this.ignoreAll) {
            this.ignoreList = scenarios
        }
    }

    def ignore(Pattern scenarioPattern) {
        if (!this.ignoreAll) {
            this.ignoreRegEx = scenarioPattern
        }
    }
    
}