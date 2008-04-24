package org.disco.easyb.listener;

import org.disco.easyb.domain.Behavior;
import org.disco.easyb.result.Result;
import org.disco.easyb.util.BehaviorStepType;
import org.disco.easyb.BehaviorStep;

public class ResultsCollector implements ExecutionListener {

    private BehaviorStep currentStep;
    private BehaviorStep genesisStep = new BehaviorStep(BehaviorStepType.GENESIS, "easyb-genesis");
    private BehaviorStep previousStep;

    public ResultsCollector() {
        currentStep = genesisStep;
    }

    public long getFailedSpecificationCount() {
        return genesisStep.getFailedSpecificationCountRecursively();
    }

    public long getFailedScenarioCount() {
        return genesisStep.getFailedScenarioCountRecursively();
    }

    public long getSuccessBehaviorCount() {
        return genesisStep.getSuccessBehaviorCountRecursively();
    }

    public long getSuccessScenarioCount() {
        return genesisStep.getSuccessScenarioCountRecursively();
    }

    public long getSuccessSpecificationCount() {
        return genesisStep.getSuccessSpecificationCountRecursively();
    }

    public long getPendingSpecificationCount() {
        return genesisStep.getPendingSpecificationCountRecursively();
    }

    public long getPendingScenarioCount() {
        return genesisStep.getPendingScenarioCountRecursively();
    }

    public long getSpecificationCount() {
        return genesisStep.getSpecificationCountRecursively();
    }

    public long getScenarioCount() {
        return genesisStep.getScenarioCountRecursively();
    }

    public long getBehaviorCount() {
        return genesisStep.getBehaviorCountRecursively();
    }

    public long getFailedBehaviorCount() {
        return genesisStep.getFailedBehaviorCountRecursively();
    }

    public long getPendingBehaviorCount() {
        return genesisStep.getPendingBehaviorCountRecursively();
    }

    public BehaviorStep getGenesisStep() {
        return genesisStep;
    }

    public BehaviorStep getCurrentStep() {
        return currentStep;
    }

    public BehaviorStep getPreviousStep() {
        return previousStep;
    }

    public void gotResult(Result result) {
        currentStep.setResult(result);
    }

    public void startBehavior(Behavior behavior) {
    }

    public void stopBehavior(BehaviorStep step, Behavior behavior) {
    }

    public void startStep(BehaviorStep step) {
        step.setParentStep(currentStep);
        currentStep.addChildStep(step);
        currentStep = step;
    }

    public void describeStep(String description) {
     currentStep.setDescription(description);   
    }

    public void completeTesting() {
    }

    public void stopStep() {

        if (BehaviorStepType.SCENARIO == currentStep.stepType) {
            if (currentStep.getChildStepFailureResultCount() > 0) {
                gotResult(new Result(Result.FAILED));
            } else {
                if (currentStep.getChildStepPendingResultCount() > 0) {
                    gotResult(new Result(Result.PENDING));
                } else {
                    gotResult(new Result(Result.SUCCEEDED));
                }
            }
        }

        previousStep = currentStep;
        currentStep = currentStep.parentStep;
    }
}