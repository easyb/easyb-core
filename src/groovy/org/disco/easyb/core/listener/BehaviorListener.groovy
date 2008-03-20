package org.disco.easyb.core.listener

import org.disco.easyb.core.result.Result
import org.disco.easyb.core.util.BehaviorStepType
import org.disco.easyb.core.BehaviorStep

interface BehaviorListener {

  BehaviorStep startStep(BehaviorStepType specificationStepType, String stepName)
  void stopStep()

  BehaviorStep getGenesisStep()

  long getFailedSpecificationCount()
  long getSuccessfulSpecificationCount()
  long getSpecificationCount()

  //probably expose a method here to get the executionLength
  void gotResult(result)
}