package org.disco.easyb.core

import org.disco.easyb.core.result.Result
import org.disco.easyb.core.util.SpecificationStepType

class BehaviorStep {

  private SpecificationStepType stepType
  private ArrayList<BehaviorStep> childSteps = new ArrayList()
  private BehaviorStep parentStep
  private Result result
  private String name

  BehaviorStep(SpecificationStepType inStepType, String inStepName, BehaviorStep inParentStep) {
    stepType = inStepType
    parentStep = inParentStep
    name = inStepName
  }

  def addChildStep(BehaviorStep inSpecificationStep) {
    childSteps.add(inSpecificationStep)
  }

  // TODO refactor into a getStepCount that can take the type its looking for (Fail, pass, pending)
  long getStepPendingCount() {
    return result != null && result.pending() ? 1 : 0
  }

  long getChildStepSpecificationPendingCount() {
    if (childSteps.size() == 0) {
      return getStepPendingCount()
    } else {
      long childStepPending = 0
      for (childStep in childSteps) {
        childStepPending += childStep.getChildStepSpecificationPendingCount()
      }
      return childStepPending + getStepPendingCount()
    }
  }

  long getStepSuccessCount() {
    return result != null && result.succeeded() ? 1 : 0
  }

  long getChildStepSpecificationSuccessCount() {
    if (childSteps.size() == 0) {
      return getStepSuccessCount()
    } else {
      long childStepSuccess = 0
      for (childStep in childSteps) {
        childStepSuccess += childStep.getChildStepSpecificationSuccessCount()
      }
      return childStepSuccess + getStepSuccessCount()
    }
  }


  long getStepFailureCount() {
    return result != null && result.failed() ? 1 : 0
  }

  long getChildStepSpecificationFailureCount() {
    if (childSteps.size() == 0) {
      return getStepFailureCount()
    } else {
      long childStepFailures = 0
      for (childStep in childSteps) {
        childStepFailures += childStep.getChildStepSpecificationFailureCount()
      }
      return childStepFailures + getStepFailureCount()
    }
  }

  long getStepSpecificationCount() {
    return result != null ? 1 : 0
  }

  long getChildStepSpecificationCount() {
    if (childSteps.size() == 0) {
      return getStepSpecificationCount()
    } else {
      long childStepSpecifications = 0
      for (childStep in childSteps) {
        childStepSpecifications += childStep.getChildStepSpecificationCount()
      }
      return childStepSpecifications + getStepSpecificationCount()
    }
  }

  List<BehaviorStep> getChildrenOfType(SpecificationStepType specificationStoryType) {
    childSteps.findAll {specificationStoryType.equals(it.stepType)}
  }

  BehaviorStep getParentStep() {
    return parentStep
  }

  def setResult(Result inResult) {
    result = inResult
  }

}