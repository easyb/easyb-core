package org.disco.easyb.report

import org.disco.easyb.BehaviorBinding
import org.disco.easyb.result.Result
import org.disco.easyb.BehaviorBinding

class TxtStoryReportWriter implements ReportWriter {

  def easybXmlLocation
  def writer

  TxtStoryReportWriter(outputReport, easybXmlLocation) {
    this.easybXmlLocation = easybXmlLocation
    writer = new BufferedWriter(new FileWriter(new File(outputReport.location)))
  }

  void writeReport() {

    def easybXml = new XmlSlurper().parse(new File(easybXmlLocation))

    def count = easybXml.stories.@specifications.toInteger()
    writer.writeLine("${(count > 1) ? "${count} specifications" : " 1 specification"}" + " (including ${easybXml.stories.@pendingspecifications.toInteger()} pending) executed" +
            "${easybXml.stories.@failedspecifications.toInteger() > 0 ? ", but status is failure!" : " successfully"}" +
            "${easybXml.stories.@failedspecifications.toInteger() > 0 ? " Total failures: ${easybXml.stories.@failedspecifications}" : ""}")

    handleElement(easybXml.stories)
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
      case BehaviorBinding.STORY:
        writer.newLine()
        writer.write("${' '.padRight(2)}Story: ${element.@name}")
        break
      case BehaviorBinding.DESCRIPTION:
    	writer.write("${' '.padRight(3)} ${element.text()}")
    	writer.newLine()
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
        writer.write("${' '.padRight(6)}and")
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