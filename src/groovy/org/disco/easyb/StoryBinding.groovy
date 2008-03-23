package org.disco.easyb

import org.disco.easyb.delegates.EnsuringDelegate
import org.disco.easyb.result.Result
import org.disco.easyb.delegates.PlugableDelegate
import org.disco.easyb.BehaviorCategory
import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.delegates.NarrativeDelegate
import org.disco.easyb.BehaviorStep
class StoryBinding {

  /**
	 * This method returns a fully initialized Binding object (or context) that
	 * has definitions for methods such as "when" and "given", which are used
	 * in the context of stories.
	 */
  static Binding getBinding(listener) {

    def binding = new Binding()

    def basicDelegate = basicDelegate()
    def givenDelegate = givenDelegate()

    def pendingClosure = {
      listener.gotResult(new Result(Result.PENDING))
    }

    binding.scenario = {scenarioDescription, scenarioClosure = {} ->
      listener.startStep(BehaviorStepType.SCENARIO, scenarioDescription)
      scenarioClosure()

      if(listener.currentStep.childStepFailureResultCount > 0) {
        listener.gotResult(new Result(Result.FAILED))
      } else {
        if(listener.currentStep.childStepPendingResultCount > 0) {
          listener.gotResult(new Result(Result.PENDING))
        } else {
          listener.gotResult(new Result(Result.SUCCEEDED))
        }
      }
      listener.stopStep()
    }

    def thenClosure = {spec, closure, storyPart ->
      closure.delegate = basicDelegate

      try {
        listener.gotResult(new Result(Result.SUCCEEDED))
        use(BehaviorCategory) {
          closure()
        }
      } catch (ex) {
        listener.gotResult(new Result(ex))
      }
    }
    
    def _thenClos = {spec, closure = pendingClosure ->
      listener.startStep(BehaviorStepType.THEN, spec)
      thenClosure(spec, closure, BehaviorStepType.THEN)
      listener.stopStep()
    }
    
    binding.then = {spec, closure = pendingClosure ->
      _thenClos(spec, closure)
    }

    def _whenClos = {whenDescription, closure = {} ->
      listener.startStep(BehaviorStepType.WHEN, whenDescription)
      closure.delegate = basicDelegate
      closure()
      listener.stopStep()
    } 
    
    binding.when = {whenDescription, closure = {} ->
      _whenClos(whenDescription, closure)
    }

    def _givenClos = {givenDescription, closure = {} ->
      listener.startStep(BehaviorStepType.GIVEN, givenDescription)
      closure.delegate = givenDelegate
      closure()
      listener.stopStep()
    }
    
    binding.given = {givenDescription, closure = {} ->
      _givenClos(givenDescription, closure)
    }

    binding.and = { description = "", closure = {} ->
      if(listener.getPreviousStep().stepType == BehaviorStepType.GIVEN){
    	  _givenClos(description, closure)
      }else if(listener.getPreviousStep().stepType == BehaviorStepType.WHEN){
    	  _whenClos(description, closure)
      }else if(listener.getPreviousStep().stepType == BehaviorStepType.THEN){
    	  _thenClos(description, closure)
      }else{
    	  listener.startStep(BehaviorStepType.AND, "")
    	  listener.stopStep()
      }
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