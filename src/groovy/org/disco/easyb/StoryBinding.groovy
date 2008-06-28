package org.disco.easyb

import org.disco.easyb.BehaviorCategory
import org.disco.easyb.delegates.EnsuringDelegate
import org.disco.easyb.delegates.NarrativeDelegate
import org.disco.easyb.delegates.PlugableDelegate
import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

class StoryBinding {

    /**
     * This method returns a fully initialized Binding object (or context) that
     * has definitions for methods such as "when" and "given", which are used
     * in the context of stories.
     */
    static Binding getBinding(listener) {
        def stepStack = new BehaviorStepStack()
        def binding = new Binding()
        def beforeScenario
        def afterScenario
        def basicDelegate = basicDelegate()
        def givenDelegate = givenDelegate()

        def pendingClosure = {
            listener.gotResult(new Result(Result.PENDING))
        }

        //TODO: add these too listener/reporting
        def beforeDone = false
        def afterDone = false

        binding.before = {description = "", beforeClosure = {} ->
            if (!beforeDone) {
                beforeClosure()
                beforeDone = true
            }
        }

        binding.assumption = binding.before.curry("story assumption")
        binding.assuming = binding.before.curry("story assumes")

        binding.after = {description = "", afterClosure = {} ->
            if (!afterClosure) {
                afterClosure()
                afterDone = true
            }
        }

        //todo: this signature requires fixtures have a description-- consider removing 1st param
        binding.before_each = {description = "", closure = {} ->
            beforeScenario = closure
        }

        binding.after_each = {description = "", closure = {} ->
            afterScenario = closure
        }

        binding.scenario = {scenarioDescription, scenarioClosure = pendingClosure ->
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

        def thenClosure = {spec, closure ->
            closure.delegate = basicDelegate

            try {
                use(BehaviorCategory) {
                    closure()
                }
                if(!closure.is(pendingClosure)){
                    listener.gotResult(new Result(Result.SUCCEEDED))
                }
            } catch (Throwable t) {
                listener.gotResult(new Result(t))
            }
        }

        def _thenClos = {spec, closure = pendingClosure ->
            stepStack.startStep(listener, BehaviorStepType.THEN, spec)
            thenClosure(spec, closure)
            stepStack.stopStep(listener)
        }

        binding.then = {spec, closure = pendingClosure ->
            _thenClos(spec, closure)
        }

        def _whenClos = {whenDescription, closure = {} ->
            stepStack.startStep(listener, BehaviorStepType.WHEN, whenDescription)
            closure.delegate = basicDelegate
            try {
                closure()
            } catch (Throwable t) {
                listener.gotResult(new Result(t))
            }
            stepStack.stopStep(listener)
        }

        binding.when = {whenDescription, closure = {} ->
            _whenClos(whenDescription, closure)
        }

        def _givenClos = {givenDescription, closure = {} ->
            stepStack.startStep(listener, BehaviorStepType.GIVEN, givenDescription)
            closure.delegate = givenDelegate
            try {
                closure()
            } catch (Throwable t) {
                listener.gotResult(new Result(t))
            }
            stepStack.stopStep(listener)
        }

        binding.given = {givenDescription, closure = {} ->
            _givenClos(givenDescription, closure)
        }

        binding.and = {description = "", closure = {} ->
            if (stepStack.lastStep().stepType == BehaviorStepType.GIVEN) {
                _givenClos(description, closure)
            } else if (stepStack.lastStep().stepType == BehaviorStepType.WHEN) {
                _whenClos(description, closure)
            } else if (stepStack.lastStep().stepType == BehaviorStepType.THEN) {
                _thenClos(description, closure)
            } else {
                stepStack.startStep(listener, BehaviorStepType.AND, "")
                stepStack.stopStep(listener)
            }
        }

        binding.narrative = {storydescript = "", closure = {} ->
            closure.delegate = narrativeDelegate()
        }

        binding.description = {description ->
            listener.describeStep(description)
        }

        return binding
    }

    private static narrativeDelegate() {
        return new NarrativeDelegate()
    }

    /**
     * The easy delegate handles "it", "then", and "when"
     * Currently, this delegate isn't plug and play.
     */
    private static basicDelegate() {
        return new EnsuringDelegate()
    }

    /**
     * The "given" delegate supports plug-ins, consequently,
     * the flex guys are utlized. Currently, there is a DbUnit
     * "given" plug-in and it is envisioned that there could be
     * others like Selenium.
     */
    private static givenDelegate() {
        return new PlugableDelegate()
    }
}
