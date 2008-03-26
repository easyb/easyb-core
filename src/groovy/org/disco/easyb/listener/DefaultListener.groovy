package org.disco.easyb.listener;

import org.disco.easyb.BehaviorStep
import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.result.Result

class DefaultListener implements BehaviorListener {

  private BehaviorStep currentStep
  private BehaviorStep genesisStep = new BehaviorStep(BehaviorStepType.GENESIS, "easyb-genesis", null)
  private BehaviorStep previousStep
  
  DefaultListener() {
    currentStep = genesisStep
  }

  long getFailedSpecificationCount() {
    return genesisStep.getFailedSpecificationCountRecursively()
  }

  long getFailedScenarioCount() {
    return genesisStep.getFailedScenarioCountRecursively()
  }

  long getSuccessBehaviorCount() {
    return genesisStep.getSuccessBehaviorCountRecursively()
  }

  long getSuccessScenarioCount() {
    return genesisStep.getSuccessScenarioCountRecursively()
  }

  long getSuccessSpecificationCount() {
    return genesisStep.getSuccessSpecificationCountRecursively()
  }

  long getPendingSpecificationCount() {
    return genesisStep.getPendingSpecificationCountRecursively()
  }

  long getPendingScenarioCount() {
    return genesisStep.getPendingScenarioCountRecursively()
  }

  long getSpecificationCount() {
    return genesisStep.getSpecificationCountRecursively()
  }

  long getScenarioCount() {
    return genesisStep.getScenarioCountRecursively()
  }

  long getBehaviorCount() {
    return genesisStep.getBehaviorCountRecursively()
  }

  long getFailedBehaviorCount() {
    return genesisStep.getFailedBehaviorCountRecursively()
  }

  long getPendingBehaviorCount() {
    return genesisStep.getPendingBehaviorCountRecursively()
  }
  
  BehaviorStep getGenesisStep() {
    return genesisStep
  }
  
  BehaviorStep getCurrentStep() {
	return currentStep
  }
  
  BehaviorStep getPreviousStep() {
    return previousStep
  }
 
  public void gotResult(result) {
    currentStep.setResult(result)
  }

  // TODO instead of startStep and stopStep we should do a decorator instead
  public BehaviorStep startStep(BehaviorStepType specificationStepType, String stepName) {
    BehaviorStep specificationStep = new BehaviorStep(specificationStepType, stepName, currentStep);
    currentStep.addChildStep(specificationStep)
    currentStep = specificationStep
    return specificationStep
  }

  public void stopStep() {

    if(BehaviorStepType.SCENARIO == currentStep.stepType) {
      if(currentStep.childStepFailureResultCount > 0) {
        gotResult(new Result(Result.FAILED))
      } else {
        if(currentStep.childStepPendingResultCount > 0) {
          gotResult(new Result(Result.PENDING))
        } else {
          gotResult(new Result(Result.SUCCEEDED))
        }
      }
    }

    // TODO set the metrics for the current step prior to popping it??
    previousStep = currentStep
    currentStep = currentStep.parentStep
  }

}