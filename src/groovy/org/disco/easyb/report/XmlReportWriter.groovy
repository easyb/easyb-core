package org.disco.easyb.report

import groovy.xml.MarkupBuilder
import org.disco.easyb.BehaviorStep
import org.disco.easyb.exception.VerificationException
import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.listener.ResultsCollector

class XmlReportWriter implements ReportWriter {
  private String location

  XmlReportWriter(String location) {
    this.location = location
  }

  def buildFailureMessage(result) {
    def buff = new StringBuffer()
    for (i in 1..10) {
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
            failure(message: step.result.cause()?.getMessage()) {
              if (!(step.result.cause instanceof VerificationException)) {
                stacktrace(buildFailureMessage(step.result))
              }
            }
          }
        }
      }
    } else {
      if (step.stepType == BehaviorStepType.SCENARIO) {
        xml."${step.stepType.type()}"(name: step.name, result: step.result.status) {
          if (step.description) {
            xml.description(step.description)
          }
          for (child in step.childSteps) {
            walkStoryChildren(xml, child)
          }
        }
      } else { // assumed to be story now
        xml."${step.stepType.type()}"(name: step.name, scenarios: step.scenarioCountRecursively, failedscenarios: step.failedScenarioCountRecursively, pendingscenarios: step.pendingScenarioCountRecursively) {
          if (step.description) {
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
            failure(message: step.result.cause()?.getMessage()) {
              if (!(step.result.cause instanceof VerificationException))
                stacktrace(buildFailureMessage(step.result))
            }
          }
        }
      }
    } else {
      xml."${step.stepType.type()}"(name: step.name, specifications: step.specificationCountRecursively, failedspecifications: step.failedSpecificationCountRecursively, pendingspecifications: step.pendingSpecificationCountRecursively) {
        if (step.description) {
          xml.description(step.description)
        }
        for (child in step.childSteps) {
          walkSpecificationChildren(xml, child)
        }
      }
    }

  }

  public void writeReport(ResultsCollector results) {
    Writer writer = new BufferedWriter(new FileWriter(new File(location)))


    def xml = new MarkupBuilder(writer)

    xml.easyb(time: new Date(), totalbehaviors: results.behaviorCount, totalfailedbehaviors: results.failedBehaviorCount, totalpendingbehaviors: results.pendingBehaviorCount) {
      stories(scenarios: results.scenarioCount, failedscenarios: results.failedScenarioCount, pendingscenarios: results.pendingScenarioCount) {
        results.genesisStep.getChildrenOfType(BehaviorStepType.STORY).each {genesisChild ->
          walkStoryChildren(xml, genesisChild)
        }
      }

      specifications(specifications: results.specificationCount, failedspecifications: results.failedSpecificationCount, pendingspecifications: results.pendingSpecificationCount) {
        results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).each {genesisChild ->
          walkSpecificationChildren(xml, genesisChild)
        }
      }
    }
    writer.close()
  }
}