package org.disco.easyb

import org.disco.easyb.listener.ExecutionListener
import org.disco.easyb.delegates.NarrativeDelegate
import org.disco.easyb.util.BehaviorStepType

class BehaviorConstructs {
    ExecutionListener listener

    BehaviorConstructs(ExecutionListener listener) {
        this.listener = listener
    }

    def narrative(description, closure) {
        closure.delegate = new NarrativeDelegate()
    }

    def description(description) {
        stepStack.startStep listener, BehaviorStepType.DESCRIPTION, description
        listener.describeStep(description)
        stepStack.stopStep listener
    }
}