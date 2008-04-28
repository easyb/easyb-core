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
        //printMetrics(behavior, startTime, currentStep, endTime);
        printMetrics(behavior, startTime, this.genesisStep, endTime);
    }

    public void completeTesting() {
        System.out.println("\n" +
            (getBehaviorCount() > 1 ? getBehaviorCount() + " total behaviors run" : "1 behavior run")
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
            if(currentStep.getFailedScenarioCountRecursively() > 0){
                handleFailurePrinting(currentStep);
            }
        } else {
            System.out.println((currentStep.getFailedSpecificationCountRecursively() == 0 ? "" : "FAILURE ") +
                "Specifications run: " + currentStep.getSpecificationCountRecursively() +
                ", Failures: " + currentStep.getFailedSpecificationCountRecursively() +
                ", Pending: " + currentStep.getPendingSpecificationCountRecursively() +
                ", Time Elapsed: " + (endTime - startTime) / 1000f + " sec");
        }
    }

    private void handleFailurePrinting(BehaviorStep currentStep) {
		for(BehaviorStep step : currentStep.getChildSteps()){
			if(step.getStepType().equals(BehaviorStepType.SCENARIO) ||
                    step.getStepType().equals(BehaviorStepType.GENESIS) ||
                    step.getStepType().equals(BehaviorStepType.STORY)){
				handleFailurePrinting(step);
			}else{
				printFailureMessage(step);
			}
		}
	}

    private void printFailureMessage(BehaviorStep istep) {
		if(istep.getResult() != null && istep.getResult().failed()
				&& istep.getResult().cause() != null){
		        System.out.println("\t Message is \""+
		        		istep.getResult().cause().getMessage() +
		        		"\"");
		  }
	}
}
