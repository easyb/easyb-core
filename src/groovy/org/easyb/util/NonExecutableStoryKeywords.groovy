package org.easyb.util

import org.easyb.StoryKeywords
import org.easyb.listener.ExecutionListener
import org.easyb.result.Result
import org.easyb.util.BehaviorStepType

class NonExecutableStoryKeywords extends StoryKeywords {

  NonExecutableStoryKeywords(ExecutionListener listener) {
    super(listener)
  }

  def scenario(scenarioDescription, scenarioClosure) {
    runScenario(scenarioClosure, scenarioDescription)
  }

  def runScenario(scenarioClosure, scenarioDescription) {
    stepStack.startStep(listener, BehaviorStepType.SCENARIO, scenarioDescription)
    scenarioClosure()
    listener.gotResult(new Result(Result.IN_REVIEW))
    stepStack.stopStep(listener)
  }

  def given(givenDescription, closure) {
    stepStack.startStep(listener, BehaviorStepType.GIVEN, givenDescription)
    stepStack.stopStep(listener)
  }

  def when(whenDescription, closure = {}) {
    stepStack.startStep(listener, BehaviorStepType.WHEN, whenDescription)
    stepStack.stopStep(listener)
  }

  def then(spec, closure) {
    stepStack.startStep(listener, BehaviorStepType.THEN, spec)
    listener.gotResult(new Result(Result.IN_REVIEW))       
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