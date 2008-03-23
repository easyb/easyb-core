package org.disco.easyb.report

import groovy.xml.MarkupBuilder
import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.BehaviorStep
import org.disco.easyb.result.Result

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
//       TODO needs better formatting ?
      buff << result.cause()?.getStackTrace()[i]
      buff << "\n"
    }
    buff << "...\n"
    return buff.toString()
  }

  def walkStoryChildren(MarkupBuilder xml, BehaviorStep step) {
    if (step.childSteps.size() == 0) {
      if (step.result == null) {
        xml."${step.stepType.type()}"(name: step.name)
      } else {
        xml."${step.stepType.type()}"(name: step.name, result: step.result.status) {
          if (step.result.failed()) {
            failure(message:step.result.cause()?.getMessage()){
            	stacktrace(buildFailureMessage(step.result))
            }
          }
        }
      }
    } else {
      if(step.stepType == BehaviorStepType.SCENARIO) {
        xml."${step.stepType.type()}"(name: step.name, result: step.result.status) {
          if(step.description){
            xml.description(step.description)
          }
          for (child in step.childSteps) {
            walkStoryChildren(xml, child)
          }
        }
      } else { // assumed to be story now
        def scenarioChildren = step.childSteps.findAll { it.stepType == BehaviorStepType.SCENARIO }
        def failedScenarios = scenarioChildren.inject(0) {count, item -> count + (item.result.status == Result.FAILED ? 1 : 0)}
        def pendingScenarios = scenarioChildren.inject(0) {count, item -> count + (item.result.status == Result.PENDING ? 1 : 0)}

        xml."${step.stepType.type()}"(name: step.name, scenarios: scenarioChildren.size(), failedscenarios: failedScenarios, pendingscenarios: pendingScenarios) {
          if(step.description){
          xml.description(step.description)
          }
          for (child in step.childSteps) {
            walkStoryChildren(xml, child)
          }
        }
      }
    }

  }

  def walkSpecificationChildren(MarkupBuilder xml, BehaviorStep step) {
    if (step.childSteps.size() == 0) {
      if (step.result == null) {
        xml."${step.stepType.type()}"(name: step.name)
      } else {
        xml."${step.stepType.type()}"(name: step.name, result: step.result.status) {
          if (step.result.failed()) {
        	  failure(message:step.result.cause()?.getMessage()){
              	stacktrace(buildFailureMessage(step.result))
              }
          }
        }
      }
    } else {
      def stepSpecifications = step.childSteps.inject(0) {count, item -> count + item.getChildStepResultCount()}
      def stepFailedSpecifications = step.childSteps.inject(0) {count, item -> count + item.getChildStepFailureResultCount()}
      def stepPendingSpecifications = step.childSteps.inject(0) {count, item -> count + item.getChildStepPendingResultCount()}

      xml."${step.stepType.type()}"(name: step.name, specifications: stepSpecifications, failedspecifications: stepFailedSpecifications, pendingspecifications: stepPendingSpecifications) {
    	  if(step.description){
    		xml.description(step.description)
    	  }
    	  for (child in step.childSteps) {
          walkSpecificationChildren(xml, child)
        }
      }
    }

  }

  public void writeReport() {

    Writer writer = new BufferedWriter(new FileWriter(new File(report.location)))


    def xml = new MarkupBuilder(writer)

    def storyList = listener.genesisStep.getChildrenOfType(BehaviorStepType.STORY)
    def scenarioChildren = []
    for(story in storyList) {
      for(storyChild in story.childSteps) {
        if(storyChild.stepType == BehaviorStepType.SCENARIO) {
          scenarioChildren.add(storyChild)
        }
      }
    }
    def failedScenarios = scenarioChildren.inject(0) {count, item -> count + (item.result.status == Result.FAILED ? 1 : 0)}
    def pendingScenarios = scenarioChildren.inject(0) {count, item -> count + (item.result.status == Result.PENDING ? 1 : 0)}

    def specificationChildren = listener.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION)
    def specificationsCount = specificationChildren.inject(0) {count, item -> count + item.getChildStepResultCount()}
    def specificationsFailed = specificationChildren.inject(0) {count, item -> count + item.getChildStepFailureResultCount()}
    def specificationsPending = specificationChildren.inject(0) {count, item -> count + item.getChildStepPendingResultCount()}

    xml.easyb(time: new Date(), totalbehaviors: specificationsCount + scenarioChildren.size(), totalfailedbehaviors: specificationsFailed + failedScenarios, totalpendingbehaviors: specificationsPending + pendingScenarios) {
      stories(scenarios: scenarioChildren.size(), failedscenarios: failedScenarios, pendingscenarios: pendingScenarios) {
        listener.genesisStep.getChildrenOfType(BehaviorStepType.STORY).each {genesisChild ->
          walkStoryChildren(xml, genesisChild)
        }
      }

      specifications(specifications: specificationsCount, failedspecifications: specificationsFailed, pendingspecifications: specificationsPending) {
        listener.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).each {genesisChild ->
          walkSpecificationChildren(xml, genesisChild)
        }
      }
    }
    writer.close()
  }

}