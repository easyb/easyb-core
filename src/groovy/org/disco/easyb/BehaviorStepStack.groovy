package org.disco.easyb

class BehaviorStepStack {
    def steps = []

    def startStep(listener, behaviorType, scenarioDescription) {
        BehaviorStep step = new BehaviorStep(behaviorType, scenarioDescription)
        steps.add(step)
        listener.startStep(step)
    }

    def stopStep(listener) {
        //poping last value of created a stack that
        //never had anything useful in it when it came
        //time to use the "And" logic
        //steps.pop()
        listener.stopStep()
    }

    def lastStep() {
        if (steps.isEmpty()) {
            return null
        }
        else {
            return steps.get(steps.size() - 1)
        }
    }

    String toString() {
        "step stack has ${steps.size()} items in it."
    }
}