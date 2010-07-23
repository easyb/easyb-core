package org.easyb.listener;

import org.easyb.BehaviorStep;
import org.easyb.result.Result;
import org.easyb.util.BehaviorStepType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * this centralizes the counting all in one place.
 */

public class ResultsReporter {
  protected BehaviorStep genesisStep;
  private static final List<BehaviorStepType> EXAMINE_FURTHER = Arrays.asList(BehaviorStepType.WHERE, BehaviorStepType.EXECUTE, BehaviorStepType.GENESIS);
  private static final List<BehaviorStepType> LEGAL_BUT_WEIRD = Arrays.asList(BehaviorStepType.WHEN, BehaviorStepType.GIVEN, BehaviorStepType.THEN);
  private static final List<BehaviorStepType> IGNORE = Arrays.asList(BehaviorStepType.DESCRIPTION, BehaviorStepType.NARRATIVE, BehaviorStepType.NARRATIVE_ROLE,
                                                                     BehaviorStepType.NARRATIVE_FEATURE, BehaviorStepType.NARRATIVE_BENEFIT, BehaviorStepType.EXTENSION_POINT); 

  class StoryCollection {
    List<BehaviorStep> scenarios = new ArrayList<BehaviorStep>();
    List<BehaviorStep> afters = new ArrayList<BehaviorStep>();
    List<BehaviorStep> befores = new ArrayList<BehaviorStep>();
    BehaviorStep fakeScenario = null; // holder for random given/then/when in a story which is legal syntax but.. weird.


    private long status(List<BehaviorStep> scenarios, Result.Type status) {
      long count = 0;

      for (BehaviorStep step : scenarios) {
        if (step.getResult() != null && step.getResult().status() == status) {
          count++;
        }
      }

      return count;  //To change body of created methods use File | Settings | File Templates.
    }


    public long storyStatus(Result.Type status) {
      return status(scenarios, status);
    }

    public long typeCount(BehaviorStepType type, Result.Type status) {
      long count = 0;

      for (BehaviorStep step : scenarios) {
        for (BehaviorStep child : step.getChildSteps()) {
          if (child.getStepType() == type && ( child.getResult() != null && child.getResult().status() == status )) {
            count++;
          }
        }
      }

      return count;
    }
  }

  class SpecificationCollection {
    List<BehaviorStep> its = new ArrayList<BehaviorStep>();
    List<BehaviorStep> befores = new ArrayList<BehaviorStep>();
    List<BehaviorStep> afters = new ArrayList<BehaviorStep>();

    private long status(List<BehaviorStep> scenarios, Result.Type status) {
      long count = 0;

      for (BehaviorStep step : scenarios) {
        if (step.getResult() != null && step.getResult().status() == status) {
          count++;
        }
      }

      return count;  //To change body of created methods use File | Settings | File Templates.
    }

    public long specificationStatus(Result.Type status) {
      return status(its, status);
    }
  }

  List<SpecificationCollection> specifications = new ArrayList<SpecificationCollection>();
  List<StoryCollection> stories = new ArrayList<StoryCollection>();

  public ResultsReporter(BehaviorStep g) {
    genesisStep = g;

    spelunk(genesisStep);
  }

  private void spelunk(BehaviorStep step) {

    if (step.getStepType() == BehaviorStepType.STORY) {
      StoryCollection sc = new StoryCollection();
      collectStories(sc, step);
      stories.add(sc);
    } else if (step.getStepType() == BehaviorStepType.SPECIFICATION) {
      SpecificationCollection sc = new SpecificationCollection();
      collectSpecifications(sc, step);
      specifications.add(sc);
    } else if (step.getStepType() == BehaviorStepType.GENESIS) {
      for (BehaviorStep child : step.getChildSteps() ) {
        spelunk(child);
      }
    } else if (!IGNORE.contains(step.getStepType())) {
      throw new RuntimeException( "Unknown step " + step);
    }
  }

  private void collectSpecifications(SpecificationCollection sc, BehaviorStep step) {
    for (BehaviorStep child : step.getChildSteps()) {
      examineSpecificationStep(sc, child);
    }
  }

  private void examineSpecificationStep(SpecificationCollection sc, BehaviorStep child) {
    if (EXAMINE_FURTHER.contains(child.getStepType())) {
      collectSpecifications(sc, child);
    } else if (child.getStepType() == BehaviorStepType.IT) {
      sc.its.add(child);
    } else if (child.getStepType() == BehaviorStepType.BEFORE) {
      sc.befores.add(child);
    } else if (child.getStepType() == BehaviorStepType.AFTER) {
      sc.afters.add(child);
    } else if (!IGNORE.contains(child.getStepType())) {
      throw new RuntimeException( "Unknown step " + child);
    }
  }

  private void collectStories(StoryCollection sc, BehaviorStep step) {
    // if we were to fix the stories so that if before/after were pending, scenarios were as well, we'd do it here.

    for (BehaviorStep child : step.getChildSteps()) {
      examineStoryStep(sc, child);
    }
  }

  private void examineStoryStep(StoryCollection sc, BehaviorStep child) {
    if (EXAMINE_FURTHER.contains(child.getStepType())) {
      collectStories(sc, child);
    } else if (child.getStepType() == BehaviorStepType.SCENARIO) {
      sc.scenarios.add(child);
      fixScenarioStatus(child);
    } else if (child.getStepType() == BehaviorStepType.BEFORE) {
      sc.befores.add(child);
      fixScenarioStatus(child);
    } else if (child.getStepType() == BehaviorStepType.AFTER) {
      sc.afters.add(child);
      fixScenarioStatus(child);
    } else if (LEGAL_BUT_WEIRD.contains(child.getStepType())) {
      if ( sc.fakeScenario == null ) {
        sc.fakeScenario = new BehaviorStep(BehaviorStepType.SCENARIO, "fake");
        sc.scenarios.add(sc.fakeScenario);
      }
      sc.fakeScenario.addChildStep(child);
      fixScenarioStatus(sc.fakeScenario);
    } else if (!IGNORE.contains(child.getStepType())) {
      throw new RuntimeException( "Unknown step " + child);
    }
  }

  private void fixScenarioStatus(BehaviorStep child) {
    // if its ignored, don't care about whats inside it
    if (child.getResult() != null && child.getResult().status() == Result.IGNORED) {
      return;
    }

    // failed trumps pending
    if (fixScenarioStatus(child, Result.FAILED) > 0) {
      child.setResult(new Result(Result.FAILED));
    } else if (fixScenarioStatus(child, Result.PENDING) > 0) {
      child.setResult(new Result(Result.PENDING));
    }
  }

  private int fixScenarioStatus(BehaviorStep step, Result.Type status) {
    int count = 0;

    for (BehaviorStep child : step.getChildSteps()) {
      if (child.getStepType() == BehaviorStepType.BEFORE_EACH || child.getStepType() == BehaviorStepType.AFTER_EACH) {
        count += fixScenarioStatus(child, status);
      } else if (child.getResult() != null && child.getResult().status() == status) {
          count++;
      }
    }

    return count;
  }


  private long specificationStatus(Result.Type status) {
    long count = 0;

    for (SpecificationCollection spec : specifications) {
      count += spec.specificationStatus(status);
    }

    return count;
  }

  private long scenarioStatus(Result.Type status) {
    long count = 0;

    for (StoryCollection story : stories) {
      count += story.storyStatus(status);
    }

    return count;
  }


  public long getFailedSpecificationCount() {
    return specificationStatus(Result.FAILED);
  }

  public long getFailedScenarioCount() {
    return scenarioStatus(Result.FAILED);
  }

  public long getIgnoredScenarioCount() {
    return scenarioStatus(Result.IGNORED);
  }

  public long getSuccessBehaviorCount() {
    return scenarioStatus(Result.SUCCEEDED) + specificationStatus(Result.SUCCEEDED);
  }

  public long getSuccessScenarioCount() {
    return scenarioStatus(Result.SUCCEEDED);
  }

  public long getSuccessSpecificationCount() {
    return specificationStatus(Result.SUCCEEDED);
  }

  public long getPendingSpecificationCount() {
    return specificationStatus(Result.PENDING);
  }

  public long getPendingScenarioCount() {
    return scenarioStatus(Result.PENDING);
  }

  public long getSpecificationCount() {
    long count = 0;

    for (SpecificationCollection spec : specifications) {
      count += spec.its.size();
    }

    return count;
  }

  public long getScenarioCount() {
    long count = 0;

    for (StoryCollection story : stories) {
      count += story.scenarios.size();
    }

    return count;
  }

  public long getBehaviorCount() {
    return getScenarioCount() + getSpecificationCount();
  }

  public long getFailedBehaviorCount() {
    return getFailedScenarioCount() + getFailedSpecificationCount();
  }

  public long getPendingBehaviorCount() {
    return getPendingScenarioCount() + getPendingSpecificationCount();
  }

  public long getTotalBeforeCount(Result.Type type) {
    long count = 0;

    for (SpecificationCollection spec : specifications) {
      count += spec.status(spec.befores, type);
    }

    for (StoryCollection story : stories) {
      count += story.status(story.befores, type);
    }

    return count;
  }

  public long getTotalBeforeEachCount(Result.Type type) {
    long count = 0;

    for (StoryCollection story : stories) {
      count += story.typeCount(BehaviorStepType.BEFORE_EACH, type);
    }

    return count;
  }

  public long getTotalAfterCount(Result.Type type) {
    long count = 0;

    for (SpecificationCollection spec : specifications) {
      count += spec.status(spec.afters, type);
    }

    for (StoryCollection story : stories) {
      count += story.status(story.afters, type);
    }

    return count;
  }

  public long getTotalAfterEachCount(Result.Type type) {
    long count = 0;

    for (StoryCollection story : stories) {
      count += story.typeCount(BehaviorStepType.AFTER_EACH, type);
    }

    return count;
  }

  public long getInReviewScenarioCount() {
    return genesisStep.getInReviewScenarioCountRecursively();
  }

  public BehaviorStep getGenesisStep() {
    return genesisStep;
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
