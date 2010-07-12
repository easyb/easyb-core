package org.easyb.listener;

import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.result.ReportingTag;
import org.easyb.result.Result;
import org.easyb.util.BehaviorStepType;

public class ResultsCollector implements ExecutionListener {

  protected BehaviorStep currentStep;
  protected BehaviorStep genesisStep = new BehaviorStep(BehaviorStepType.GENESIS, "easyb-genesis");
  protected BehaviorStep previousStep;
  protected Behavior behavior;

  public ResultsCollector() {
    currentStep = genesisStep;
  }
  
  public ResultsReporter getResultsReporter() {
    return new ResultsReporter(genesisStep);
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
    this.behavior = behavior;
  }

  public void stopBehavior(BehaviorStep step, Behavior behavior) {
  }

  public void tag(ReportingTag tag) {
    currentStep.addReportingTag(tag);
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
    if (currentStep.getResult() == null && BehaviorStepType.grossCountableTypes.contains(currentStep.getStepType())) {
      if (currentStep.getChildStepFailureResultCount() > 0) {
        gotResult(new Result(Result.FAILED));
      } else {
        if (currentStep.getChildStepPendingResultCount() > 0) {
          gotResult(new Result(Result.PENDING));
        } else if (currentStep.getChildStepIgnoredResultCount() > 0) {
          gotResult(new Result(Result.IGNORED));
        } else if (currentStep.getChildStepInReviewResultCount() > 0) {
          gotResult(new Result(Result.IN_REVIEW));
        } else {
          gotResult(new Result(Result.SUCCEEDED));
        }
      }
    }

    currentStep.stopExecutionTimer();
    previousStep = currentStep;
    currentStep = currentStep.getParentStep();
  }

  public void startTesting() {
  }
}