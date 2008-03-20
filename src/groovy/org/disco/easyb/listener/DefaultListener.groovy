package org.disco.easyb.listener;

import org.disco.easyb.result.Result
import org.disco.easyb.BehaviorStep
import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.util.BehaviorStepType

class DefaultListener implements BehaviorListener {

  private BehaviorStep currentStep
  private BehaviorStep genesisStep = new BehaviorStep(BehaviorStepType.GENESIS, "easyb-genesis", null)

  DefaultListener() {
    currentStep = genesisStep
  }

  long getFailedSpecificationCount() {
    return genesisStep.getChildStepSpecificationFailureCount()
  }

  long getSuccessfulSpecificationCount() {
    return genesisStep.getChildStepSpecificationSuccessCount()
  }

  long getPendingSpecificationCount() {
    return genesisStep.getChildStepSpecificationPendingCount()
  }

  long getSpecificationCount() {
    return genesisStep.getChildStepSpecificationCount()
  }

  BehaviorStep getGenesisStep() {
    return genesisStep
  }
  
  BehaviorStep getCurrentStep() {
	return currentStep
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
    // TODO set the metrics for the current step prior to popping it??
    currentStep = currentStep.parentStep
  }

}