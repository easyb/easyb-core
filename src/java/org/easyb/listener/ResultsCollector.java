package org.easyb.listener;

import org.easyb.BehaviorStep;
import org.easyb.result.Result;
import org.easyb.domain.Behavior;
import org.easyb.util.BehaviorStepType;

public class ResultsCollector implements ExecutionListener {

    protected BehaviorStep currentStep;
    protected BehaviorStep genesisStep = new BehaviorStep(BehaviorStepType.GENESIS, "easyb-genesis");
    protected BehaviorStep previousStep;

    public ResultsCollector() {
        currentStep = genesisStep;
    }

    public long getFailedSpecificationCount() {
        return genesisStep.getFailedSpecificationCountRecursively();
    }

    public long getFailedScenarioCount() {
        return genesisStep.getFailedScenarioCountRecursively();
    }

    public long getIgnoredScenarioCount() {
        return genesisStep.getIgnoredScenarioCountRecursively();
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

    public synchronized void startStep(final BehaviorStep step) {
        BehaviorStep clone = new BehaviorStep(step.getStepType(), step.getName());
        clone.startExecutionTimer();
        clone.setParentStep(currentStep);
        currentStep.addChildStep(clone);
        currentStep = clone;
    }

    public void describeStep(final String description) {
        currentStep.setDescription(description);
    }

    public void completeTesting() {
    }

    public synchronized void stopStep() {
        if (BehaviorStepType.SCENARIO == currentStep.getStepType()) {
            if (currentStep.getChildStepFailureResultCount() > 0) {
                gotResult(new Result(Result.FAILED));
            } else {
                if (currentStep.getChildStepPendingResultCount() > 0) {
                    gotResult(new Result(Result.PENDING));
                } else if (currentStep.getChildStepIgnoredResultCount() > 0) {
                    gotResult(new Result(Result.IGNORED));
                } else {
                    gotResult(new Result(Result.SUCCEEDED));
                }
            }
        }

        currentStep.stopExecutionTimer();
        previousStep = currentStep;
        currentStep = currentStep.getParentStep();
    }

    public String getSpecificationResultsAsText() {
        return (getSpecificationCount() == 1 ? "1 specification" : getSpecificationCount() + " specifications") +
                (getPendingSpecificationCount() > 0 ? " (including " + getPendingSpecificationCount() + " pending)" : "") + " executed" +
                (getFailedSpecificationCount() > 0 ? ", but status is failure! Total failures: " + getFailedSpecificationCount() : " successfully.");
    }

    public String getScenarioResultsAsText() {
        long scenariosExecuted = getScenarioCount() - getIgnoredScenarioCount();
        return (scenariosExecuted == 1 ? "1 scenario" : scenariosExecuted +
                (getIgnoredScenarioCount() > 0 ? " of " + getScenarioCount() : "") + " scenarios") +
                (getPendingScenarioCount() > 0 ? " (including " + getPendingScenarioCount() + " pending)" : "") + " executed" +
                (getFailedScenarioCount() > 0 ? ", but status is failure! Total failures: " + getFailedScenarioCount() : " successfully.");
    }
}