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
  private ResultsReporter results;


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
    printMetrics(behavior, startTime, previousStep, endTime);
  }

  /**
   * this method returns a formatted string that contains the total scenarios run.
   * if there were ignored scenarios, the total number is determined by subtracting
   * the ignored ones; thus, we don't convey that an ignored scenario was "run"
   */
  private String getScenariosRunMessage(final BehaviorStep step) {
    if (step.getIgnoredScenarioCountRecursively() > 0) {
      return "Scenarios run: (" +
             +( step.getScenarioCountRecursively() - step.getIgnoredScenarioCountRecursively() )
             + " of "
             + step.getScenarioCountRecursively()
             + ")";
    } else {
      return "Scenarios run: " + step.getScenarioCountRecursively();
    }
  }

  private String getStepRunMessage(final String name, final BehaviorStepType type, final BehaviorStep step) {
    long aftersTotal = step.getBehaviorCountRecursively(type, null);
    long afters = step.getBehaviorCountRecursively(type, Result.FAILED);

    return name + "'s run: " + aftersTotal + ", Failures: " + afters;
  }


  private void printMetrics(final Behavior behavior, final long startTime,
                            final BehaviorStep currentStep, final long endTime) {
    System.out.print(behaviorName(behavior) + ": ");
    if (behavior instanceof Story) {
      System.out.println(( currentStep.getFailedScenarioCountRecursively() == 0 ? "" : "FAILURE " ) +
                         this.getScenariosRunMessage(currentStep) +
                         ", Failures: " + currentStep.getFailedScenarioCountRecursively() +
                         ", Pending: " + currentStep.getPendingScenarioCountRecursively() +
                         ( currentStep.getIgnoredScenarioCountRecursively() > 0 ? ", Ignored: " + currentStep.getIgnoredScenarioCountRecursively() : "" ) +
                         ", Time elapsed: " + ( endTime - startTime ) / 1000f + " sec\n" +
                         getStepRunMessage("Before", BehaviorStepType.BEFORE, currentStep) + "\n" +
                         getStepRunMessage("After", BehaviorStepType.AFTER, currentStep) + "\n" +
                         getStepRunMessage("Before Scenario", BehaviorStepType.BEFORE_EACH, currentStep) + "\n" +
                         getStepRunMessage("After Scenario", BehaviorStepType.AFTER_EACH, currentStep) + "\n"
      );
    } else {
      System.out.println(( currentStep.getFailedSpecificationCountRecursively() == 0 ? "" : "FAILURE " ) +
                         "Specifications run: " + currentStep.getSpecificationCountRecursively() +
                         ", Failures: " + currentStep.getFailedSpecificationCountRecursively() +
                         ", Pending: " + currentStep.getPendingSpecificationCountRecursively() +
                         ", Time elapsed: " + ( endTime - startTime ) / 1000f + " sec");
    }
    if (currentStep.getBehaviorCountListRecursively(BehaviorStepType.grossCountableTypes, Result.FAILED) > 0) {
      handleFailurePrinting(currentStep);
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
