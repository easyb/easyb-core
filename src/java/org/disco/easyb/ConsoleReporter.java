package org.disco.easyb;

import org.disco.easyb.domain.Behavior;
import org.disco.easyb.domain.Story;
import org.disco.easyb.listener.ResultsCollector;
import org.disco.easyb.util.BehaviorStepType;

public class ConsoleReporter extends ResultsCollector {
    private long startTime;

    public void startBehavior(Behavior behavior) {
        System.out.println("Running " + behavior.getPhrase() + " " + behavior.getTypeDescriptor()
                + " (" + behavior.getFile().getName() + ")");
        startTime = System.currentTimeMillis();
    }

    public void stopBehavior(BehaviorStep currentStep, Behavior behavior) {
        long endTime = System.currentTimeMillis();
        printMetrics(behavior, startTime, this.getPreviousStep(), endTime);
    }

    public void completeTesting() {
        System.out.println("\n" +
                (getBehaviorCount() > 1 ? getBehaviorCount() + " total behaviors run" : "1 behavior run")
                + (getPendingBehaviorCount() > 0 ? " (including "
                + (getPendingBehaviorCount() == 1 ? "1 pending behavior)" : getPendingBehaviorCount() + " pending behaviors)") : "")
                + (getFailedBehaviorCount() > 0 ? " with "
                + (getFailedBehaviorCount() == 1 ? "1 failure" : getFailedBehaviorCount() + " failures") : " with no failures"));
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
        if ((currentStep.getFailedSpecificationCountRecursively() > 0) ||
                (currentStep.getFailedScenarioCountRecursively() > 0)) {
            handleFailurePrinting(currentStep);
        }
    }

    private void handleFailurePrinting(BehaviorStep currentStep) {
        for (BehaviorStep step : currentStep.getChildSteps()) {
            if (step.getStepType().equals(BehaviorStepType.SCENARIO) ||
                    step.getStepType().equals(BehaviorStepType.GENESIS) ||
                    step.getStepType().equals(BehaviorStepType.STORY) ||
                    step.getStepType().equals(BehaviorStepType.SPECIFICATION)) {
                handleFailurePrinting(step);
            } else {
                printFailureMessage(step);
            }
        }
    }

    private void printFailureMessage(BehaviorStep istep) {
        if (istep.getResult() != null && istep.getResult().failed()
                && istep.getResult().cause() != null) {
            System.out.println("\t\"" + istep.getName() + "\" -- " +
                    istep.getResult().cause().getMessage());
        }
    }
}
