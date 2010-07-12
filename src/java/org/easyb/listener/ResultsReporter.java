package org.easyb.listener;

import org.easyb.BehaviorStep;
import org.easyb.result.Result;


public class ResultsReporter {
  protected BehaviorStep currentStep;
  protected BehaviorStep previousStep;
  protected BehaviorStep genesisStep;

  public ResultsReporter(BehaviorStep g) {
    genesisStep = g;
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

  public long getInReviewScenarioCount() {
    return genesisStep.getInReviewScenarioCountRecursively();
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

  public String getSpecificationResultsAsText() {
    return ( getSpecificationCount() == 1 ? "1 specification" : getSpecificationCount() + " specifications" ) +
           ( getPendingSpecificationCount() > 0 ? " (including " + getPendingSpecificationCount() + " pending)" : "" ) + " executed" +
           ( getFailedSpecificationCount() > 0 ? ", but status is failure! Total failures: " + getFailedSpecificationCount() : " successfully." );
  }

  public String getScenarioResultsAsText() {
    if (getInReviewScenarioCount() > 0) {
      return getInReviewScenarioCount() + " scenarios in review";
    } else {
      long scenariosExecuted = getScenarioCount() - getIgnoredScenarioCount();
      return ( scenariosExecuted == 1 ? "1 scenario" : scenariosExecuted +
                                                       ( getIgnoredScenarioCount() > 0 ? " of " + getScenarioCount() : "" ) + " scenarios" ) +
             ( getPendingScenarioCount() > 0 ? " (including " + getPendingScenarioCount() + " pending)" : "" ) + " executed" +
             ( getFailedScenarioCount() > 0 ? ", but status is failure! Total failures: " + getFailedScenarioCount() : " successfully." );
    }
  }

}
