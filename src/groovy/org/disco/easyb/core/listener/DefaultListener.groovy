package org.disco.easyb.core.listener;

import org.disco.easyb.core.result.Result
import org.disco.easyb.core.SpecificationStep
import org.disco.easyb.core.util.SpecificationStepType

class DefaultListener implements SpecificationListener{

  // TODO possibly create the top level step when the listener is created and set that as the current step.
  private SpecificationStep currentStep
  private SpecificationStep genesisStep = new SpecificationStep(SpecificationStepType.GENESIS, "easyb-genesis", null)

  DefaultListener() {
    currentStep = genesisStep
  }


  long getFailedSpecificationCount() {
    return genesisStep.getChildStepSpecificationFailureCount()
  }

  long getSuccessfulSpecificationCount() {
    return genesisStep.getChildStepSpecificationSuccessCount()
  }

  long getSpecificationCount() {
    return genesisStep.getChildStepSpecificationCount()
  }

  SpecificationStep getGenesisStep() {
    return genesisStep
  }

  public void gotResult(Result result) {
    currentStep.setResult(result)
  }

  public void setSpecificationName(String name){
    this.specName = name
  }


  // TODO instead of startStep and stopStep we should do a decorator instead
  public void startStep(SpecificationStepType specificationStepType, String stepName) {
    SpecificationStep specificationStep = new SpecificationStep(specificationStepType, stepName, currentStep);
    currentStep.addChildStep(specificationStep)
    currentStep = specificationStep
  }

  public void stopStep() {
    // TODO set the metrics for the current step prior to popping it??
    currentStep = currentStep.parentStep
  }
  
}