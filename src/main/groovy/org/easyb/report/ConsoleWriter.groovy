/*
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: Jun 30, 2010
 * Time: 6:49:53 PM
 */
package org.easyb.report

import org.easyb.listener.ResultsAmalgamator
import org.easyb.listener.ResultsReporter

public class ConsoleWriter {
  ResultsReporter results;
  
  public void write(ResultsAmalgamator amal) {
    this.results = amal.getResultsReporter()

    if (results.getBehaviorCount() == 0) {
      System.out.println( "No behaviors were run");
    } else {
      System.out.println("\n" +
                         this.getTotalRanCountMessage() +
                         this.getTotalPendingCountMessage() +
                         ( results.getFailedBehaviorCount() > 0 ? " with " +
                                                                  ( results.getFailedBehaviorCount() == 1 ? "1 failure" : results.getFailedBehaviorCount() + " failures" ) : " with no failures" ) +
                         this.getCompletedIgnoredMesage());
    }
  }

  /**
   * this method returned a formatted string the total pending count and
   * some information regarding the ignored count
   * example strings:
   * 3 of 9 behaviors ran with no failures (6 behaviors were ignored)
   * 21 of 27 behaviors ran (including 9 pending behaviors and 6 ignored behaviors) with no failures
   */
  private String getTotalPendingCountMessage() {
    if (results.getPendingBehaviorCount() > 0) {
      String messge = " (including " +
                      ( results.getPendingBehaviorCount() == 1 ? "1 pending behavior" : results.getPendingBehaviorCount() + " pending behaviors" );
      if (results.getIgnoredScenarioCount() > 0) {
        messge +=
          ( results.getIgnoredScenarioCount() > 0 ? ""
                                                    + ( results.getIgnoredScenarioCount() == 1 ? " with 1 behavior ignored)" : " and " + results.getIgnoredScenarioCount()
                                                                                                                               + " ignored behaviors" ) : "" );
      }
      return messge + ")";
    } else {
      return "";
    }
  }

  /**
   * this method only returns the ignored count if there are NO pending messages
   * as the ignored count is included in the pending message to make the
   * output more human readable
   */
  private String getCompletedIgnoredMesage() {
    if (results.getPendingBehaviorCount() > 0) {
      return "";
    } else {
      return ( results.getIgnoredScenarioCount() > 0 ? " ("
                                                       + ( results.getIgnoredScenarioCount() == 1 ? "1 behavior was ignored)" : results.getIgnoredScenarioCount() + " behaviors were ignored)" ) : "" );
    }
  }

  /**
   * this method formats the total run behaviors -- like with @getScenariosRunMessage,
   * this method calculates the total by subtracting any ignored scenarios
   */
  private String getTotalRanCountMessage() {
    if (results.getIgnoredScenarioCount() > 0) {
      return results.getBehaviorCount() + " of " + ( results.getBehaviorCount() + results.getIgnoredScenarioCount() ) + " behaviors ran";
    } else {
      return ( results.getBehaviorCount() > 1 ? results.getBehaviorCount() + " total behaviors ran" : "1 behavior ran" );
    }
  }


}