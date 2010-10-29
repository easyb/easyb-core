package org.easyb.listener;

import java.util.List;

import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.util.BehaviorStepType;

public class ResultsAmalgamator {
  protected BehaviorStep genesisStep = new BehaviorStep(BehaviorStepType.GENESIS, "easyb-genesis");
  protected ResultsReporter results;
  // used in tests
  public ResultsAmalgamator(Behavior... behaviors) {
    this(java.util.Arrays.asList(behaviors));
  }

  public ResultsAmalgamator(List<Behavior> behaviors) {

    for( Behavior behavior : behaviors ) {
      ResultsCollector result = behavior.getBroadcastListener().getResultsCollector();

      // steal the children of the genesis steps
      for( BehaviorStep step : result.getGenesisStep().getChildSteps() ) {
  		step.setContext(behavior.getBinding().getVariables());
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
