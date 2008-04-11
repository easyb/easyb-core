package org.disco.easyb;

import org.disco.easyb.domain.Behavior;
import org.disco.easyb.domain.Story;

public class ConsoleReporter implements BehaviorExecutionListener {
    private long startTime;

    public void behaviorFileStarting(Behavior behavior) {
        System.out.println("Running " + behavior.getPhrase() + " " + behavior.getTypeDescriptor()
            + " (" + behavior.getFile().getName() + ")");
        startTime = System.currentTimeMillis();
    }

    public void behaviorFileComplete(BehaviorStep currentStep, Behavior behavior) {
        long endTime = System.currentTimeMillis();
        printMetrics(behavior, startTime, currentStep, endTime);
    }

    private void printMetrics(Behavior behavior, long startTime, BehaviorStep currentStep, long endTime) {
        if (behavior instanceof Story) {
            System.out.println((currentStep.getFailedScenarioCountRecursively() == 0 ? "" : "FAILURE ") +
                "Scenarios run: " + currentStep.getScenarioCountRecursively() +
                ", Failures: " + currentStep.getFailedScenarioCountRecursively() +
                ", Pending: " + currentStep.getPendingScenarioCountRecursively() +
                ", Time Elapsed: " + (endTime - startTime) / 1000f + " sec");
        } else {
            System.out.println((currentStep.getFailedSpecificationCountRecursively() == 0 ? "" : "FAILURE ") +
                "Specifications run: " + currentStep.getSpecificationCountRecursively() +
                ", Failures: " + currentStep.getFailedSpecificationCountRecursively() +
                ", Pending: " + currentStep.getPendingSpecificationCountRecursively() +
                ", Time Elapsed: " + (endTime - startTime) / 1000f + " sec");
        }
    }
}
