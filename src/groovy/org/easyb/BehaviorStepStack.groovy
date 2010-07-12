package org.easyb

import org.easyb.listener.ExecutionListener

class BehaviorStepStack {
  def ExecutionListener listener
  def BehaviorStep currentStep

  public BehaviorStepStack(ExecutionListener listener) {
    this.listener = listener
  }

  def startStep( behaviorType, scenarioDescription) {
    currentStep = new BehaviorStep (behaviorType, scenarioDescription, null, null)
    listener.startStep(currentStep)
  }

  def lastStep() {
    return currentStep
  }

  def stopStep() {
    this.listener.stopStep()
  }


  def replaySteps() {
    throw new RuntimeException("don't create me")
  }

  String toString() {
    "step stack has ${steps.size ()} items in it."
  }
}