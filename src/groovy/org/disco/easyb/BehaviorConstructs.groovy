package org.disco.easyb

import org.disco.easyb.listener.ExecutionListener
import org.disco.easyb.delegates.NarrativeDelegate

class BehaviorConstructs {
    ExecutionListener listener

    BehaviorConstructs(ExecutionListener listener) {
        this.listener = listener
    }

    def narrative(description, closure) {
        closure.delegate = new NarrativeDelegate()
    }

    def description(description) {
        listener.describeStep(description)
    }
}