package org.disco.easyb

class BehaviorStepStack {
  def steps = []

  def startStep(listener, behaviorType, scenarioDescription) {
    BehaviorStep step = new BehaviorStep(behaviorType, scenarioDescription)
    steps.add(step)
    listener.startStep(step)
  }

  def stopStep(listener) {
    steps.pop()
    listener.stopStep()
  }

  def lastStep() {
    if (steps.isEmpty())
      return null
    else
      return steps.get(steps.size() - 1)
  }
}