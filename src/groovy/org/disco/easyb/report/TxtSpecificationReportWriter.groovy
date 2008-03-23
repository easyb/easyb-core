package org.disco.easyb.report

import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

public class TxtSpecificationReportWriter implements ReportWriter {

  def easybXmlLocation
  def writer

  TxtSpecificationReportWriter(outputReport, easybXmlLocation) {
    this.easybXmlLocation = easybXmlLocation
    writer = new BufferedWriter(new FileWriter(new File(outputReport.location)))
  }

  void writeReport() {
    def easybXml = new XmlSlurper().parse(new File(easybXmlLocation))

    def count = easybXml.specifications.@specifications.toInteger()
    writer.writeLine("${(count > 1) ? "${count} specifications" : " 1 specification"}" + " (including ${easybXml.specifications.@pendingspecifications.toInteger()} pending) executed" +
            "${easybXml.specifications.@failedspecifications.toInteger() > 0 ? ", but status is failure!" : " successfully"}" +
            "${easybXml.specifications.@failedspecifications.toInteger() > 0 ? " Total failures: ${easybXml.specifications.@failedspecifications}" : ""}")

    handleElement(easybXml.specifications)
    writer.close()
  }

  def handleElement(element) {
    writeElement(element)
    element.children().each {
      handleElement(it)
    }
  }

  def writeElement(element) {
    switch (element.name()) {
      case BehaviorStepType.SPECIFICATION.type():
        writer.newLine()
        writer.write("${' '.padRight(2)}Specification: ${element.@name}")
        break
      case BehaviorStepType.DESCRIPTION.type():
      	writer.write("${' '.padRight(3)} ${element.text()}")
      	writer.newLine()
      	break
      case BehaviorStepType.BEFORE.type():
        writer.write("${' '.padRight(4)}before ${element.@name}")
        break
      case BehaviorStepType.IT.type():
        writer.write("${' '.padRight(4)}it ${element.@name}")
        break
      case BehaviorStepType.AND.type():
        writer.write("${' '.padRight(4)}and")
        break
      default:
        //no op to avoid having alerts in story text
        break
    }

    if (element.@result == Result.FAILED) {
      writer.newLine()
      writer.newLine()
      writer.write("	Failure -> ${element.name()} ${element.@name}")
      writer.newLine()
      writer.write("${element.failuremessage}")
    }
    if (element.@result == Result.PENDING) {
      writer.write(" [PENDING]")
    }
    writer.newLine()

  }
}
