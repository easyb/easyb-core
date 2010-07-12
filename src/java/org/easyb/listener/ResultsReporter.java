package org.easyb.listener;

import org.easyb.BehaviorStep;
import org.easyb.result.Result;
import org.easyb.util.BehaviorStepType;

import java.util.Arrays;
import java.util.List;


public class ResultsReporter {
  protected BehaviorStep currentStep;
  protected BehaviorStep previousStep;
  protected BehaviorStep genesisStep;
  private static final List<BehaviorStepType> SCENARIO_COUNT_INTO = Arrays.asList(BehaviorStepType.BEFORE, BehaviorStepType.AFTER, BehaviorStepType.BEFORE_EACH,
      BehaviorStepType.AFTER_EACH, BehaviorStepType.SCENARIO, BehaviorStepType.WHERE, BehaviorStepType.EXECUTE);
  private static final List<BehaviorStepType> SPEC_COUNT_INTO = Arrays.asList(BehaviorStepType.BEFORE, BehaviorStepType.AFTER, BehaviorStepType.BEFORE_EACH,
      BehaviorStepType.AFTER_EACH, BehaviorStepType.SPECIFICATION, BehaviorStepType.EXECUTE);

  public ResultsReporter(BehaviorStep g) {
    genesisStep = g;
  }


  public long getFailedSpecificationCount() {
    return findAll(genesisStep, BehaviorStepType.SPECIFICATION, SPEC_COUNT_INTO, Arrays.asList(Result.FAILED) );
//    return genesisStep.getFailedSpecificationCountRecursively();
  }

  private long findAll(BehaviorStep step, BehaviorStepType lookFor, List<BehaviorStepType> countInto, List<Result.Type> matchTypes) {
    long count = 0;

    for ( BehaviorStep s : step.getChildSteps() ) {
      if ( lookFor == s.getStepType() ) {
        count += countUp( s, countInto, matchTypes );
      } else if (countInto.contains(s.getStepType())) // deal with the weird scenario where we start with "scenario" at the top or similar (which should only happen in our own tests)
        count += countUp(step, countInto, matchTypes);
    }

    return count;
  }

  private long countUp(BehaviorStep step, List<BehaviorStepType> countInto, List<Result.Type> matchTypes) {
    long count = 0;

    for ( BehaviorStep s : step.getChildSteps() ) {
      if ( s.getResult() != null && matchTypes.contains( s.getResult().status() ) ) {
        count ++;
      } else if ( countInto.contains(s.getStepType()) ) {
        count += countUp(s, countInto, matchTypes );
      }
    }

    return count;
  }


  public long getFailedScenarioCount() {
    // we really need to find the stories and then all of the scenarios, before, after, before_each, after_each
    return findAll(genesisStep, BehaviorStepType.STORY, SCENARIO_COUNT_INTO, Arrays.asList(Result.FAILED) );
  }

  public long getIgnoredScenarioCount() {
    return findAll(genesisStep, BehaviorStepType.STORY, SCENARIO_COUNT_INTO, Arrays.asList(Result.IGNORED) );
//    return genesisStep.getIgnoredScenarioCountRecursively();
  }

  public long getSuccessBehaviorCount() {
    return findAll(genesisStep, BehaviorStepType.STORY, SCENARIO_COUNT_INTO, Arrays.asList(Result.SUCCEEDED) ) +
      findAll(genesisStep, BehaviorStepType.SPECIFICATION, SCENARIO_COUNT_INTO, Arrays.asList(Result.SUCCEEDED) );
//    return genesisStep.getSuccessBehaviorCountRecursively();
  }

  public long getSuccessScenarioCount() {
//    return genesisStep.getSuccessScenarioCountRecursively();
    return findAll(genesisStep, BehaviorStepType.STORY, SCENARIO_COUNT_INTO, Arrays.asList(Result.SUCCEEDED) );
  }

  public long getSuccessSpecificationCount() {
    return findAll(genesisStep, BehaviorStepType.SPECIFICATION, SCENARIO_COUNT_INTO, Arrays.asList(Result.SUCCEEDED) );
//    return genesisStep.getSuccessSpecificationCountRecursively();
  }

  public long getPendingSpecificationCount() {
    return findAll(genesisStep, BehaviorStepType.SPECIFICATION, SCENARIO_COUNT_INTO, Arrays.asList(Result.PENDING) );
//    return genesisStep.getPendingSpecificationCountRecursively();
  }

  public long getPendingScenarioCount() {
    return findAll(genesisStep, BehaviorStepType.STORY, SCENARIO_COUNT_INTO, Arrays.asList(Result.PENDING) );
//    return genesisStep.getPendingScenarioCountRecursively();
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
    return findAll(genesisStep, BehaviorStepType.STORY, SCENARIO_COUNT_INTO, Arrays.asList(Result.FAILED) ) +
      findAll(genesisStep, BehaviorStepType.SPECIFICATION, SCENARIO_COUNT_INTO, Arrays.asList(Result.FAILED) );
//    return genesisStep.getFailedBehaviorCountRecursively();
  }

  public long getPendingBehaviorCount() {
    return findAll(genesisStep, BehaviorStepType.STORY, SCENARIO_COUNT_INTO, Arrays.asList(Result.PENDING) ) +
      findAll(genesisStep, BehaviorStepType.SPECIFICATION, SCENARIO_COUNT_INTO, Arrays.asList(Result.PENDING) );
//    return genesisStep.getPendingBehaviorCountRecursively();
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
