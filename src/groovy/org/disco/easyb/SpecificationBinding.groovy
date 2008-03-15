package org.disco.easyb

import org.disco.easyb.core.delegates.EnsuringDelegate
import org.disco.easyb.core.result.Result
import org.disco.easyb.core.delegates.PlugableDelegate
import org.disco.easyb.SpecificationCategory
import org.disco.easyb.core.util.SpecificationStepType

class SpecificationBinding {

  // TODO change to constants when i break the binding into story and behavior bindings
  public static final String STORY = "story"
  public static final String STORY_SCENARIO = "scenario"
  public static final String STORY_GIVEN = "given"
  public static final String STORY_WHEN = "when"
  public static final String STORY_THEN = "then"
  public static final String BEHAVIOR = "behavior"
  public static final String BEHAVIOR_BEFORE = "before"
  public static final String BEHAVIOR_IT = "it"
  public static final String AND = "and"

  /**
	 * This method returns a fully initialized Binding object (or context) that 
	 * has definitions for methods such as "it" and "given", which are used
	 * in the context of behaviors (or stories). 
	 */
  static Binding getBinding(listener) {

    def binding = new Binding()

    def basicDelegate = basicDelegate()
    def givenDelegate = givenDelegate()

    def beforeIt

    def pendingClosure = {
      listener.gotResult(new Result(Result.PENDING))
    }

    binding.scenario = {scenarioDescription, scenarioClosure = {} ->
      listener.startStep(SpecificationStepType.SCENARIO, scenarioDescription)
      scenarioClosure()
      listener.stopStep()
    }

    binding.before = {beforeDescription, closure = {} ->
      listener.startStep(SpecificationStepType.BEFORE, beforeDescription)
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
        use(SpecificationCategory) {
          closure()
        }
      } catch (ex) {
        listener.gotResult(new Result(ex))
      }
    }

    binding.it = {spec, closure = pendingClosure ->
      listener.startStep(SpecificationStepType.IT, spec)
      itClosure(spec, closure, BEHAVIOR_IT)
      listener.stopStep()
    }


    binding.then = {spec, closure = pendingClosure ->
      listener.startStep(SpecificationStepType.THEN, spec)
      itClosure(spec, closure, STORY_THEN)
      listener.stopStep()
    }

    binding.when = {whenDescription, closure = {} ->
      listener.startStep(SpecificationStepType.WHEN, whenDescription)
      closure.delegate = basicDelegate
      closure()
      listener.stopStep()
    }

    binding.given = {givenDescription, closure = {} ->
      listener.startStep(SpecificationStepType.GIVEN, givenDescription)
      closure.delegate = givenDelegate
      closure()
      listener.stopStep()
    }

    binding.and = {
      listener.startStep(SpecificationStepType.AND, "")
      listener.stopStep()
    }

    return binding
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