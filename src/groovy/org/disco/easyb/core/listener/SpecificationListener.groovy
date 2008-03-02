package org.disco.easyb.core.listener

import org.disco.easyb.core.result.Result
import org.disco.easyb.core.util.SpecificationStepType
import org.disco.easyb.core.SpecificationStep

interface SpecificationListener {

  void startStep(SpecificationStepType specificationStepType, String stepName)
  void stopStep()

  SpecificationStep getGenesisStep()

  long getFailedSpecificationCount()
  long getSuccessfulSpecificationCount()
  long getSpecificationCount()

  //probably expose a method here to get the executionLength
  void gotResult(Result result)
}