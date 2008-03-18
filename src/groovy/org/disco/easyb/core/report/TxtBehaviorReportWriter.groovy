package org.disco.easyb.core.report

import org.disco.easyb.BehaviorBinding
import org.disco.easyb.core.result.Result
import org.disco.easyb.BehaviorBinding

public class TxtBehaviorReportWriter implements ReportWriter {

  def easybXmlLocation
  def writer

  TxtBehaviorReportWriter(outputReport, easybXmlLocation) {
    this.easybXmlLocation = easybXmlLocation
    writer = new BufferedWriter(new FileWriter(new File(outputReport.location)))
  }

  void writeReport() {

    def easybXml = new XmlSlurper().parse(new File(easybXmlLocation))

    def count = easybXml.behaviors.@specifications.toInteger()
    writer.writeLine("${(count > 1) ? "${count} specifications" : " 1 specification"}" + " (including ${easybXml.behaviors.@pendingspecifications.toInteger()} pending) executed" +
            "${easybXml.behaviors.@failedspecifications.toInteger() > 0 ? ", but status is failure!" : " successfully"}" +
            "${easybXml.behaviors.@failedspecifications.toInteger() > 0 ? " Total failures: ${easybXml.behaviors.@failedspecifications}" : ""}")

    handleElement(easybXml.behaviors)
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
      case BehaviorBinding.BEHAVIOR:
        writer.newLine()
        writer.write("${' '.padRight(2)}Behavior: ${element.@name}")
        break
      case BehaviorBinding.BEHAVIOR_BEFORE:
        writer.write("${' '.padRight(4)}before ${element.@name}")
        break
      case BehaviorBinding.BEHAVIOR_IT:
        writer.write("${' '.padRight(4)}it ${element.@name}")
        break
      case BehaviorBinding.STORY_SCENARIO:
        writer.newLine()
        writer.write("${' '.padRight(4)}scenario ${element.@name}")
        break
      case BehaviorBinding.STORY_GIVEN:
        writer.write("${' '.padRight(6)}given ${element.@name}")
        break
      case BehaviorBinding.STORY_WHEN:
        writer.write("${' '.padRight(6)}when ${element.@name}")
        break
      case BehaviorBinding.STORY_THEN:
        writer.write("${' '.padRight(6)}then ${element.@name}")
        break
      case BehaviorBinding.AND:
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
