package org.disco.easyb

import org.disco.easyb.delegates.EnsuringDelegate
import org.disco.easyb.result.Result
import org.disco.easyb.delegates.PlugableDelegate
import org.disco.easyb.BehaviorCategory
import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.delegates.NarrativeDelegate

class SpecificationBinding {

  public static final String SPECIFICATION = "specification"
  public static final String SPECIFICATION_BEFORE = "before"
  public static final String SPECIFICATION_IT = "it"
  public static final String AND = "and"
  public static final String DESCRIPTION = "description"

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
      listener.startStep(BehaviorStepType.BEFORE, beforeDescription)
      beforeIt = closure
      listener.stopStep()
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
      listener.startStep(BehaviorStepType.IT, spec)
      itClosure(spec, closure, SPECIFICATION_IT)
      listener.stopStep()
    }

    binding.and = {
      listener.startStep(BehaviorStepType.AND, "")
      listener.stopStep()
    }

    binding.narrative = { storydescript = "", closure = {} ->
    	closure.delegate = narrativeDelegate()
    }

    binding.description = { description ->
    	listener.getCurrentStep().setDescription(description)
    }

    return binding
  }

  private static narrativeDelegate(){
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