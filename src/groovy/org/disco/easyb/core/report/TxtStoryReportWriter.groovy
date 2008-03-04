package org.disco.easyb.core.report

import org.disco.easyb.SpecificationBinding
import org.disco.easyb.core.result.Result

class TxtStoryReportWriter implements ReportWriter {

  def easybXmlLocation
  def storyListener
  def writer

  TxtStoryReportWriter(outputReport, easybXmlLocation) {
    this.easybXmlLocation = easybXmlLocation
    writer = new BufferedWriter(new FileWriter(new File(outputReport.location)))
  }

  void writeReport() {
    def easybXml = new XmlSlurper().parse(new File(easybXmlLocation))
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
      case SpecificationBinding.STORY:
        writer.newLine()
        writer.write("${' '.padRight(2)}Story: ${element.@name}")
        break
      case SpecificationBinding.STORY_SCENARIO:
        writer.newLine()
        writer.write("${' '.padRight(4)}scenario ${element.@name}")
        break
      case SpecificationBinding.STORY_GIVEN:
        writer.write("${' '.padRight(6)}given ${element.@name}")
        break
      case SpecificationBinding.STORY_WHEN:
        writer.write("${' '.padRight(6)}when ${element.@name}")
        break
      case SpecificationBinding.STORY_THEN:
        writer.write("${' '.padRight(6)}then ${element.@name}")
        break
      case SpecificationBinding.AND:
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
    writer.newLine()

  }
}