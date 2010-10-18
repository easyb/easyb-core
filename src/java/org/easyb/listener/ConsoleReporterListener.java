package org.easyb.listener;


import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.domain.Story;
import org.easyb.listener.ResultsCollector;
import org.easyb.listener.ResultsReporter;
import org.easyb.result.Result;
import org.easyb.util.BehaviorStepType;

import java.util.Arrays;
import java.util.List;

/**
 * this class prints data to the console related to running
 * easyb stories and specifications. This is a user facing class and
 * thus, a lot of effort goes towards human readable messages that
 * convey precise information. note, there is a lot of conditional logic
 * to determine various result altering messages; that is, pending and
 * ignored behaviors change the behavior of reporting output.
 */
public class ConsoleReporterListener extends ResultsCollector {
  private long startTime;

  public void startBehavior(final Behavior behavior) {
    super.startBehavior(behavior);

    System.out.println("Running " + behaviorName(behavior) );
    startTime = System.currentTimeMillis();
  }

  private String behaviorName(Behavior behavior) {
    return behavior.getPhrase() + " " + behavior.getTypeDescriptor()
           + " (" + behavior.getFile().getName() + ")";
  }

  public void stopBehavior(final BehaviorStep currentStep, final Behavior behavior) {
    final long endTime = System.currentTimeMillis();
    printMetrics(behavior, startTime, new ResultsReporter(previousStep), endTime);
  }

  /**
   * this method returns a formatted string that contains the total scenarios run.
   * if there were ignored scenarios, the total number is determined by subtracting
   * the ignored ones; thus, we don't convey that an ignored scenario was "run"
   */
  private String getScenariosRunMessage(final ResultsReporter results) {
    if (results.getIgnoredScenarioCount() > 0) {
      return "Scenarios run: (" +
             +( results.getScenarioCount() - results.getIgnoredScenarioCount() )
             + " of "
             + results.getScenarioCount()
             + ")";
    } else {
      return "Scenarios run: " + results.getScenarioCount();
    }
  }
  
  private void printMetrics(final Behavior behavior, final long startTime,
                            final ResultsReporter results, final long endTime) {
        
    if (behavior instanceof Story) {
      System.out.println(( results.getFailedScenarioCount() == 0 ? "" : "FAILURE " ) +
                         this.getScenariosRunMessage(results) +
                         ", Failures: " + results.getFailedScenarioCount() +
                         ", Pending: " + results.getPendingScenarioCount() +
                         ( results.getIgnoredScenarioCount() > 0 ? ", Ignored: " + results.getIgnoredScenarioCount() : "" ) +
                         ", Time elapsed: " + ( endTime - startTime ) / 1000f + " sec\n");
    } else {
      System.out.println(( results.getFailedSpecificationCount() == 0 ? "" : "FAILURE " ) +
                         "Specifications run: " + results.getSpecificationCount() +
                         ", Failures: " + results.getFailedSpecificationCount() +
                         ", Pending: " + results.getPendingSpecificationCount() +
                         ", Time elapsed: " + ( endTime - startTime ) / 1000f + " sec");
    }

    if (results.getFailedBehaviorCount() > 0) {
      handleFailurePrinting(results.getGenesisStep());
    }
  }

  private static final List<BehaviorStepType> recurseErrorStepTypes = Arrays.asList(BehaviorStepType.SCENARIO,
                                                                                    BehaviorStepType.GENESIS, BehaviorStepType.STORY, BehaviorStepType.SPECIFICATION, BehaviorStepType.EXECUTE,
                                                                                    BehaviorStepType.AFTER, BehaviorStepType.AFTER_EACH, BehaviorStepType.BEFORE, BehaviorStepType.BEFORE_EACH
  );

  private void handleFailurePrinting(final BehaviorStep currentStep) {
    for (BehaviorStep step : currentStep.getChildSteps()) {
      if (recurseErrorStepTypes.contains(step.getStepType())) {
        handleFailurePrinting(step);
      } else {
        printFailureMessage(step);
      }
    }
  }

  private void printFailureMessage(final BehaviorStep istep) {

    if (istep.getResult() != null && istep.getResult().failed()) {
      System.out.println("\t" + istep.getParentStep().getStepType().type() + " \"" + istep.getParentStep().getName() + "\"");
      System.out.println("\tstep " + istep.getStepType().toString() + " \"" + istep.getName() + "\" -- " +
                         ( istep.getResult().cause() != null ? istep.getResult().cause().getMessage() : istep.getResult().description ));
    }
  }
}
