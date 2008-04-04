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
    addTarget(behaviorsClassName)
  }

  public void addBehaviors(FileSet fileset) {
    addFilesetTarget(fileset)
  }

  void addConfiguredReport(Report report) {
    addTarget("-${report.type} ${report.location}")
  }

}
