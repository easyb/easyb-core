package org.disco.easyb

import org.disco.easyb.BehaviorCategory
import org.disco.easyb.delegates.EnsuringDelegate
import org.disco.easyb.delegates.NarrativeDelegate
import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

class SpecificationBinding {

  static BehaviorStepStack stepStack = new BehaviorStepStack()

  /**
   * This method returns a fully initialized Binding object (or context) that
   * has definitions for methods such as "it" and "given", which are used
   * in the context of behaviors (or stories).
   */
  static Binding getBinding(listener) {

    def binding = new Binding()

    def basicDelegate = basicDelegate()

    def beforeIt

    def pendingClosure = {
      listener.gotResult(new Result(Result.PENDING))
    }


    binding.before = {beforeDescription, closure = {} ->
      stepStack.startStep(listener, BehaviorStepType.BEFORE, beforeDescription)
      beforeIt = closure
      stepStack.stopStep(listener)
    }

    def itClosure = {spec, closure, storyPart ->
      closure.delegate = basicDelegate

      try {
        if (beforeIt != null) {
          beforeIt()
        }
        listener.gotResult(new Result(Result.SUCCEEDED))
        use(BehaviorCategory) {
          closure()
        }
      } catch (ex) {
        listener.gotResult(new Result(ex))
      }
    }

    binding.it = {spec, closure = pendingClosure ->
      stepStack.startStep(listener, BehaviorStepType.IT, spec)
      itClosure(spec, closure, BehaviorStepType.IT)
      stepStack.stopStep(listener)
    }

    binding.and = {
      stepStack.startStep(listener, BehaviorStepType.AND, "")
      stepStack.stopStep(listener)
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
}