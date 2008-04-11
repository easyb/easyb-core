package org.disco.easyb

import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

class BehaviorStep {

  BehaviorStepType stepType
  ArrayList<BehaviorStep> childSteps = new ArrayList()
  private BehaviorStep parentStep
  private Result result
  private String name
  private String description

  BehaviorStep(BehaviorStepType inStepType, String inStepName, BehaviorStep inParentStep) {
    stepType = inStepType
    parentStep = inParentStep
    name = inStepName
  }

  def addChildStep(BehaviorStep step) {
    childSteps.add(step)
  }

  long getScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, null)
  }

  long getPendingScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, Result.PENDING)
  }

  long getFailedScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, Result.FAILED)
  }

  long getSuccessScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, Result.SUCCEEDED)
  }

  long getSpecificationCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.IT, null)
  }

  long getPendingSpecificationCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.IT, Result.PENDING)
  }

  long getFailedSpecificationCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.IT, Result.FAILED)
  }

  long getSuccessSpecificationCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.IT, Result.SUCCEEDED)
  }


  long getBehaviorCountRecursively() {
    return getSpecificationCountRecursively() + getScenarioCountRecursively()
  }

  long getPendingBehaviorCountRecursively() {
    return getPendingSpecificationCountRecursively() + getPendingScenarioCountRecursively()
  }

  long getFailedBehaviorCountRecursively() {
    return getFailedSpecificationCountRecursively() + getFailedScenarioCountRecursively()
  }

  long getSuccessBehaviorCountRecursively() {
    return getSuccessSpecificationCountRecursively() + getSuccessScenarioCountRecursively()
  }

  long getBehaviorCountRecursively(type, resultStatus) {
    long behaviorCount = 0

    if (resultStatus == null) {
      if (type == stepType) {
        behaviorCount++
      }
    } else if ((type == stepType) && (resultStatus == result?.status)) {
      behaviorCount++
    }

    for (childStep in childSteps) {
      behaviorCount += childStep.getBehaviorCountRecursively(type, resultStatus)
    }
    return behaviorCount
  }

  // TODO refactor into a getStepCount that can take the type its looking for (Fail, pass, pending)
  long getStepPendingCount() {
    return result != null && result.pending() ? 1 : 0
  }

  long getChildStepPendingResultCount() {
    if (childSteps.size() == 0) {
      return getStepPendingCount()
    } else {
      long childStepPending = 0
      for (childStep in childSteps) {
        childStepPending += childStep.getChildStepPendingResultCount()
      }
      return childStepPending + getStepPendingCount()
    }
  }

  long getStepSuccessCount() {
    return result != null && result.succeeded() ? 1 : 0
  }

  long getChildStepSuccessResultCount() {
    if (childSteps.size() == 0) {
      return getStepSuccessCount()
    } else {
      long childStepSuccess = 0
      for (childStep in childSteps) {
        childStepSuccess += childStep.getChildStepSuccessResultCount()
      }
      return childStepSuccess + getStepSuccessCount()
    }
  }


  long getStepFailureCount() {
    return result != null && result.failed() ? 1 : 0
  }

  long getChildStepFailureResultCount() {
    if (childSteps.size() == 0) {
      return getStepFailureCount()
    } else {
      long childStepFailures = 0
      for (childStep in childSteps) {
        childStepFailures += childStep.getChildStepFailureResultCount()
      }
      return childStepFailures + getStepFailureCount()
    }
  }

  long getStepResultCount() {
    return result != null ? 1 : 0
  }

  long getChildStepResultCount() {
    if (childSteps.size() == 0) {
      return getStepResultCount()
    } else {
      long childStepResults = 0
      for (childStep in childSteps) {
        childStepResults += childStep.getChildStepResultCount()
      }
      return childStepResults + getStepResultCount()
    }
  }

  List<BehaviorStep> getChildrenOfType(BehaviorStepType behaviorStepType) {
    childSteps.findAll {behaviorStepType.equals(it.stepType)}
  }

  BehaviorStep getParentStep() {
    return parentStep
  }

  def setResult(inResult) {
    result = inResult
  }

  def setDescription(inDescription) {
    description = inDescription
  }
}