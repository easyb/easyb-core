package org.disco.easyb.ant

import org.apache.tools.ant.types.FileSet

import org.disco.easyb.SpecificationRunner
import org.disco.easyb.core.report.Report

public class SpecificationRunnerTask extends AbstractStoryTask {

  public SpecificationRunnerTask() {
    this(new CommandRunnerImpl())
  }

  public SpecificationRunnerTask(CommandRunner runner) {
    super(SpecificationRunner.class, runner)
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
