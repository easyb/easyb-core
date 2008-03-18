package org.disco.easyb.core.report

import org.disco.easyb.core.listener.BehaviorListener
import org.disco.easyb.core.report.Report
import groovy.xml.MarkupBuilder

class XmlStoryReportWriter implements ReportWriter {

  def report
  def storyListener

  XmlStoryReportWriter(report, storyListener) {
    this.report = report
    this.storyListener = storyListener
  }

  public void writeReport() {
    Writer writer = new BufferedWriter(new FileWriter(new File(report.location)))

    def builder = new MarkupBuilder(writer)

    // TODO
    writer.close()
  }
  // loop thru all results in their order, test each for failure and include inline, that way the stories stay grouped
  // issue here is that the listener doesn't have a concept of grouping but the builder needs to be nested 

}