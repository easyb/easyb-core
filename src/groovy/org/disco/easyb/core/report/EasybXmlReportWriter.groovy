package org.disco.easyb.core.report

import groovy.xml.MarkupBuilder
import org.disco.easyb.core.util.BehaviorStepType
import org.disco.easyb.core.BehaviorStep
import org.disco.easyb.core.BehaviorStep
import org.disco.easyb.core.util.BehaviorStepType

class EasybXmlReportWriter implements ReportWriter {

  def report
  def listener

  EasybXmlReportWriter(inReport, inListener) {
    listener = inListener
    report = inReport
  }

  def buildFailureMessage(result) {
    def buff = new StringBuffer()
    for (i in 1..10) {
      // TODO needs better formatting ?
      buff << result.cause()?.getStackTrace()[i]
      buff << "\n"
    }
    return buff.toString()
  }

  def walkChildren(MarkupBuilder xml, BehaviorStep step) {
    if (step.childSteps.size() == 0) {
      if (step.result == null) {
        xml."${step.stepType.type()}"(name: step.name)
      } else {
        xml."${step.stepType.type()}"(name: step.name, result: step.result.status) {
          if (step.result.failed()) {
            failuremessage(buildFailureMessage(step.result))
          }
        }
      }
    } else {
      def stepSpecifications = step.childSteps.inject(0) {count, item -> count + item.getChildStepSpecificationCount()}
      def stepFailedSpecifications = step.childSteps.inject(0) {count, item -> count + item.getChildStepSpecificationFailureCount()}
      def stepPendingSpecifications = step.childSteps.inject(0) {count, item -> count + item.getChildStepSpecificationPendingCount()}

      xml."${step.stepType.type()}"(name: step.name, specifications: stepSpecifications, failedspecifications: stepFailedSpecifications, pendingspecifications: stepPendingSpecifications) {
        for (child in step.childSteps) {
          walkChildren(xml, child)
        }
      }
    }

  }

  public void writeReport() {

    Writer writer = new BufferedWriter(new FileWriter(new File(report.location)))


    def xml = new MarkupBuilder(writer)

    xml.EasybRun(time: new Date(), totalspecifications: listener.getSpecificationCount(), totalfailedspecifications: listener.getFailedSpecificationCount(), totalpendingspecifications: listener.getPendingSpecificationCount()) {
      def storyChildren = listener.genesisStep.getChildrenOfType(BehaviorStepType.STORY)
      def storyChildrenSpecifications = storyChildren.inject(0) {count, item -> count + item.getChildStepSpecificationCount()}
      def storyChildrenFailedSpecifications = storyChildren.inject(0) {count, item -> count + item.getChildStepSpecificationFailureCount()}
      def storyChildrenPendingSpecifications = storyChildren.inject(0) {count, item -> count + item.getChildStepSpecificationPendingCount()}
      stories(specifications: storyChildrenSpecifications, failedspecifications: storyChildrenFailedSpecifications, pendingspecifications: storyChildrenPendingSpecifications) {
        listener.genesisStep.getChildrenOfType(BehaviorStepType.STORY).each {genesisChild ->
          walkChildren(xml, genesisChild)
        }
      }
      def behaviorChildren = listener.genesisStep.getChildrenOfType(BehaviorStepType.BEHAVIOR)
      def behaviorChildrenSpecifications = behaviorChildren.inject(0) {count, item -> count + item.getChildStepSpecificationCount()}
      def behaviorChildrenFailedSpecifications = behaviorChildren.inject(0) {count, item -> count + item.getChildStepSpecificationFailureCount()}
      def behaviorChildrenPendingSpecifications = behaviorChildren.inject(0) {count, item -> count + item.getChildStepSpecificationPendingCount()}
      behaviors(specifications: behaviorChildrenSpecifications, failedspecifications: behaviorChildrenFailedSpecifications, pendingspecifications: behaviorChildrenPendingSpecifications) {
        listener.genesisStep.getChildrenOfType(BehaviorStepType.BEHAVIOR).each {genesisChild ->
          walkChildren(xml, genesisChild)
        }
      }
    }
    writer.close()
  }

}