package org.disco.easyb.core.report

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
      // TODO needs better formatting ?
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
          xml."${step.stepType.type()}"(name:step.name, result:step.result.status) {
            if(step.result.failed()) {
                  FailureMessage(buildFailureMessage(step.result))
            }
          }
      }
    } else {
      xml."${step.stepType.type()}"(name:step.name) {
        for(child in step.childSteps) {
          walkChildren(xml, child)
        }
      }
    }

  }

  public void writeReport() {

    Writer writer = new BufferedWriter(new FileWriter(new File(report.location)))


    def xml = new MarkupBuilder(writer)

	  xml.EasybRun(time:new Date(), totalspecifications:listener.getSpecificationCount(), totalfailedspecifications:listener.getFailedSpecificationCount()){
      def storyChildren = listener.genesisStep.getChildrenOfType(SpecificationStepType.STORY)
      def storyChildrenTotalSpecifications = storyChildren.inject(0) { count, item -> count + item.getChildStepSpecificationCount() }
      def storyChildrenTotalFailedSpecifications = storyChildren.inject(0) { count, item -> count + item.getChildStepSpecificationFailureCount() }
      stories (totalspecifications:storyChildrenTotalSpecifications, totalfailedspecifications:storyChildrenTotalFailedSpecifications) { // TODO add metrics at this level
        listener.genesisStep.getChildrenOfType(SpecificationStepType.STORY).each { genesisChild ->
          walkChildren(xml, genesisChild)
        }
      }
      def behaviorChildren = listener.genesisStep.getChildrenOfType(SpecificationStepType.BEHAVIOR)
      def behaviorChildrenTotalSpecifications = behaviorChildren.inject(0) { count, item -> count + item.getChildStepSpecificationCount() }
      def behaviorChildrenTotalFailedSpecifications = behaviorChildren.inject(0) { count, item -> count + item.getChildStepSpecificationFailureCount() }
      behaviors (totalspecifications:behaviorChildrenTotalSpecifications, totalfailedspecifications:behaviorChildrenTotalFailedSpecifications) {
        listener.genesisStep.getChildrenOfType(SpecificationStepType.BEHAVIOR).each { genesisChild ->
          walkChildren(xml, genesisChild)
        }
      }
      // TODO should we have a misc? grouping.. for things that aren't in a Story.groovy or a Behavior.groovy.. etc?

	  }
	  writer.close()
  }

}