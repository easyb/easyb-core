package org.disco.easyb.core.report

import org.disco.easyb.core.listener.SpecificationListener
import org.disco.easyb.core.report.Report
import groovy.xml.MarkupBuilder
import org.disco.easyb.core.util.SpecificationStepType
import org.disco.easyb.core.SpecificationStep

class EasybXmlReportWriter implements ReportWriter {

  def report
  def listener

  EasybXmlReportWriter(inReport, inListener) {
    listener = inListener
    report = inReport
  }

  def buildFailureMessage(result){
    def buff = new StringBuffer()
    for(i in 1..10){
      // TODO needs better formatting
      buff << result.cause()?.getStackTrace()[i]
      buff << "\n"
    }
    return buff.toString()
  }

  def walkChildren(MarkupBuilder xml, SpecificationStep step) {
    if(step.childSteps.size() == 0) {
      if(step.result == null) {
        xml."${step.stepType.type()}"(name:step.name)
      } else {
        if(step.result.failed()) {
          xml."${step.stepType.type()}"(name:step.name, result:step.result.status) {
            FailureMessage(buildFailureMessage(step.result))
          }
        } else {
          xml."${step.stepType.type()}"(name:step.name, result:step.result.status)

        }
      }
    } else {
      for(child in step.childSteps) {
        walkChildren(xml, child)
      }
    }

  }

  public void writeReport() {

    Writer writer = new BufferedWriter(new FileWriter(new File(report.location)))


    def xml = new MarkupBuilder(writer)

	  xml.EasybRun(time:new Date(), totalspecifications:listener.getSpecificationCount(), totalfailedspecifications:listener.getFailedSpecificationCount()){
      stories { // TODO add metrics at this level
        listener.genesisStep.childSteps.each { genesisChild ->
          if(SpecificationStepType.STORY.equals(genesisChild.stepType)) {
            "${genesisChild.stepType.type()}"(name:genesisChild.name) {
              walkChildren(xml, genesisChild)
            }
          }
        }
      }
      behaviors {
        // TODO
      }
      // TODO should we have a misc? grouping.. for things that aren't in a Story.groovy or a Behavior.groovy.. etc?

	  }
	  writer.close()
  }

}