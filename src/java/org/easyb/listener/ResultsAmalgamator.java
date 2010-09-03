package org.easyb.listener;

import org.easyb.BehaviorStep;
import org.easyb.Configuration;
import org.easyb.StoryContext;
import org.easyb.domain.Behavior;
import org.easyb.util.BehaviorStepType;

import java.util.List;

public class ResultsAmalgamator {
  protected BehaviorStep genesisStep = new BehaviorStep(BehaviorStepType.GENESIS, "easyb-genesis");
  protected ResultsReporter results;
  protected Configuration configuration;
  // used in tests
  public ResultsAmalgamator(Behavior... behaviors) {
    this(java.util.Arrays.asList(behaviors), null);
  }

  public ResultsAmalgamator(List<Behavior> behaviors, Configuration configuration) {

    this.configuration = configuration;
    for( Behavior behavior : behaviors ) {
      ResultsCollector result = behavior.getBroadcastListener().getResultsCollector();

      // steal the children of the genesis steps
      for( BehaviorStep step : result.getGenesisStep().getChildSteps() ) {
    	if (step.getStepType() == BehaviorStepType.STORY) {
    		step.setStoryContext(new StoryContext(behavior.getBinding()));
    	}
        genesisStep.addChildStep( step );
      }
    }

    results = new ResultsReporter(genesisStep);
  }

  public boolean failuresDetected() {
    return ( getResultsReporter().getFailedBehaviorCount() > 0 );
  }
  
  public ResultsReporter getResultsReporter() {
    return results; 
  }
}
