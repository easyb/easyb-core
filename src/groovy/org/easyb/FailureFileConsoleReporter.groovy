package org.easyb

import org.easyb.BehaviorStep
import org.easyb.ConsoleReporter
import org.easyb.domain.Behavior

class FailureFileConsoleReporter extends ConsoleReporter {
  def failures = []
  def outfile

  FailureFileConsoleReporter(final outfile) {
    super()
    this.outfile = outfile ?: "failure-files.txt"
  }

  public void stopBehavior(final BehaviorStep currentStep, final Behavior behavior) {
    super.stopBehavior(currentStep, behavior);
    if ((this.previousStep.getFailedScenarioCountRecursively() > 0) ||
            (this.previousStep.getFailedSpecificationCountRecursively() > 0)) {
      this.failures << behavior.getFile().path
    }
  }


  public void completeTesting() {
    super.completeTesting()
    if (this.failures.size() > 0) {
      new File(outfile).withPrintWriter { file ->
        this.failures.each {
          file.println(it)
        }
      }
    }
  }
}