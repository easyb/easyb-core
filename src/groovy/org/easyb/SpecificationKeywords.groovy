package org.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.delegates.EnsuringDelegate
import org.easyb.result.Result
import org.easyb.util.BehaviorStepType

class SpecificationKeywords extends BehaviorKeywords {
  private BehaviorStepStack stepStack
  private def beforeIt
  private def afterIt
  private def categories = []

  SpecificationKeywords(ExecutionListener listener) {
    super(listener)

    stepStack = new BehaviorStepStack(listener)
    
    categories.add(BehaviorCategory.class)
  }


  def after(description, closure) {
    stepStack.startStep(BehaviorStepType.AFTER, description)
    afterIt = closure
    stepStack.stopStep()
  }

  def before(description, closure) {
    stepStack.startStep(BehaviorStepType.BEFORE, description)
    beforeIt = closure
    stepStack.stopStep()
  }

  def extensionMethod( closure, params, binding ) {
    def ex = new ExtensionPoint(closure:closure, params:params)

    ex.process(stepStack.currentStep, binding, listener)
  }

  def it(spec, closure) {
    stepStack.startStep(BehaviorStepType.IT, spec)
    closure.delegate = new EnsuringDelegate()
    try {
      if (beforeIt != null) {
        beforeIt()
      }
      listener.gotResult(new Result(Result.SUCCEEDED))
      use(categories) {
        closure()
      }
      if (afterIt != null) {
        afterIt()
      }
    } catch (Throwable ex) {
      listener.gotResult(new Result(ex))
    } finally {
      stepStack.stopStep()
    }
  }

  def and() {
    stepStack.startStep(BehaviorStepType.AND, "")
    stepStack.stopStep()
  }
}