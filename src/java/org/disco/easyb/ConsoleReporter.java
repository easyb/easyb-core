package org.disco.easyb;

import org.disco.easyb.domain.Behavior;

public class ConsoleReporter implements BehaviorExecutionListener {
    public void startBehavior(Behavior behavior) {
        System.out.println("Running " + behavior.getPhrase() + " " + behavior.getTypeDescriptor()
            + " (" + behavior.getFile().getName() + ")");
    }
}
