package org.disco.easyb.ant

import org.apache.tools.ant.types.FileSet

import org.disco.easyb.BehaviorRunner
import org.disco.easyb.report.Report

public class BehaviorRunnerTask extends AbstractStoryTask {

  public BehaviorRunnerTask() {
    this(new CommandRunnerImpl())
  }

  public BehaviorRunnerTask(CommandRunner runner) {
    super(BehaviorRunner.class, runner)
  }

  public void setBehaviorsClassName(String behaviorsClassName) {
    super.addTarget(behaviorsClassName)
  }

  public void addBehaviors(FileSet fileset) {
    super.addFilesetTarget(fileset)
  }

  void addConfiguredReport(Report report) {
    super.addTarget("-${report.format} ${report.location}")
  }

}
