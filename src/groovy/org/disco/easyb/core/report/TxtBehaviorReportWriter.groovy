package org.disco.easyb.core.report

import org.disco.easyb.core.listener.SpecificationListener;

public class TxtBehaviorReportWriter implements ReportWriter {

  def report
  def behaviorListener

  TxtBehaviorReportWriter(report, behaviorListener) {
    this.report = report
    this.behaviorListener = behaviorListener
  }

  public void writeReport() {
    // TODO
  }
}
