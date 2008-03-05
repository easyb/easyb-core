package org.disco.easyb.core.report;

import org.disco.easyb.core.listener.SpecificationListener
import groovy.xml.MarkupBuilder
import org.disco.easyb.core.util.CamelCaseConverter

public class XmlBehaviorReportWriter implements ReportWriter {

  Report report
  SpecificationListener behaviorListener

  XmlBehaviorReportWriter(Report report, SpecificationListener behaviorListener) {
    this.report = report
    this.behaviorListener = behaviorListener
  }

  // TODO add the filename camelcaseconverted to the xml structure to help group results
  public void writeReport() {
    Writer writer = new BufferedWriter(new FileWriter(new File(report.location)))

    def builder = new MarkupBuilder(writer)

    builder.EasyBehaviorRun(time: new Date(), totalrun: behaviorListener.methodsVerified, totalfailed: behaviorListener.failures.size()) {
      behaviorListener.successes.each {
        Behavior(name: it.name, result: it.status)
      }
      behaviorListener.failures.each {res ->
        Behavior(name: res.name, result: res.status) {
          FailureMessage(this.buildFailureMessage(res))
        }
      }
    }
    writer.close()
  }

  def buildFailureMessage(result) {
    def buff = new StringBuffer()
    buff << "\t\n"
    for (i in 1..10) {
      buff << "\t" + result.cause()?.getStackTrace()[i]
      buff << "\t\n"
    }
    return result.containerName() + " " + new CamelCaseConverter(result.name()).toPhrase() + ":" + buff.toString()
  }

}
