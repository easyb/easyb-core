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
                xml."${step.stepType.type()}"(name: step.name, result: step.result.status, executionTime: step.executionTotalTimeInMillis) {
                    if (step.description) {
                        xml.description(step.description)
                    }
                    for (child in step.childSteps) {
                        walkStoryChildren(xml, child)
                    }
                }
            } else if (step.stepType == BehaviorStepType.NARRATIVE) {
                xml."${step.stepType.type()}"(description: step.name) {
                  for (child in step.childSteps) {
                    xml."${child.stepType.type()}"(description: step.name)
                  }
                }
            } else { // assumed to be story now
                xml."${step.stepType.type()}"(name: step.name, scenarios: step.scenarioCountRecursively, failedscenarios: step.failedScenarioCountRecursively, pendingscenarios: step.pendingScenarioCountRecursively, executionTime: step.executionTotalTimeInMillis) {
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
                xml."${step.stepType.type()}"(name: step.name, result: step.result.status, executionTime: step.executionTotalTimeInMillis) {
                    if (step.result.failed()) {
                        failure(message: step.result.cause()?.getMessage()) {
                            if (!(step.result.cause instanceof VerificationException))
                                stacktrace(buildFailureMessage(step.result))
                        }
                    }
                }
            }
        } else {
            xml."${step.stepType.type()}"(name: step.name, specifications: step.specificationCountRecursively, failedspecifications: step.failedSpecificationCountRecursively, pendingspecifications: step.pendingSpecificationCountRecursively, executionTime: step.executionTotalTimeInMillis) {
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

        xml.easyb(time: new Date(), totalbehaviors: results.behaviorCount, totalfailedbehaviors: results.failedBehaviorCount, totalpendingbehaviors: results.pendingBehaviorCount, executionTime: results.genesisStep.storyExecutionTimeRecursively + results.genesisStep.specificationExecutionTimeRecursively) {
            stories(scenarios: results.scenarioCount, failedscenarios: results.failedScenarioCount, pendingscenarios: results.pendingScenarioCount, executionTime: results.genesisStep.storyExecutionTimeRecursively) {
                results.genesisStep.getChildrenOfType(BehaviorStepType.STORY).each {genesisChild ->
                    walkStoryChildren(xml, genesisChild)
                }
            }

            specifications(specifications: results.specificationCount, failedspecifications: results.failedSpecificationCount, pendingspecifications: results.pendingSpecificationCount, executionTime: results.genesisStep.specificationExecutionTimeRecursively) {
                results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).each {genesisChild ->
                    walkSpecificationChildren(xml, genesisChild)
                }
            }
        }
        writer.close()
    }
}