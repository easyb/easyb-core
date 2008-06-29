package org.disco.easyb.report

import org.disco.easyb.listener.ResultsCollector
import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

class TxtStoryReportWriter implements ReportWriter {
    private String location
    private static final String DEFAULT_LOC_NAME = "easyb-story-report.txt";

    public TxtStoryReportWriter() {
        this(DEFAULT_LOC_NAME)
    }

    public TxtStoryReportWriter(String location) {
        this.location = (location != null ? location : DEFAULT_LOC_NAME);
    }

    /**
     * Render a text story report
     */
    void writeReport(ResultsCollector results) {
        Writer writer = new BufferedWriter(new FileWriter(new File(location)))
        def count = results.scenarioCount
        writer.writeLine("${(count > 1) ? "${count} scenarios" : " 1 scenario"}" +
                " (including ${results.pendingScenarioCount} pending) executed" +
                "${results.failedScenarioCount.toInteger() > 0 ? ", but status is failure!" : " successfully"}" +
                "${results.failedScenarioCount.toInteger() > 0 ? " Total failures: ${results.failedScenarioCount}" : ""}")
        results.genesisStep.getChildrenOfType(BehaviorStepType.STORY).each {genesisChild ->
            handleElement(writer, genesisChild)
        }

        writer.close()
    }

    /**
     *
     */
    def handleElement(writer, element) {
        writeStep(writer, element)
        element.getChildSteps().each {
            writeStep(writer, it)
        }
    }

    /**
     *
     */
    def writeStep(writer, element) {

        switch (element.stepType) {
            case BehaviorStepType.STORY:
                writer.newLine()
                writer.write("${' '.padRight(2)}Story: ${element.name}")
                break
            case BehaviorStepType.DESCRIPTION:
                writer.write("${' '.padRight(3)}Description: ${element.description}")
                break
            case BehaviorStepType.NARRATIVE:
                writer.write("${' '.padRight(3)}Narrative: ${element.description}")
                element.getChildSteps().each {
                   writer.newLine()
                    switch (it.stepType) {
                       case BehaviorStepType.NARRATIVE_ROLE:
                            writer.write("${' '.padRight(6)}As a ${it.name}")
                            break
                        case BehaviorStepType.NARRATIVE_FEATURE:
                            writer.write("${' '.padRight(6)}I want ${it.name}")
                            break
                        case BehaviorStepType.NARRATIVE_BENEFIT:
                            writer.write("${' '.padRight(6)}So that ${it.name}")
                            break
                    }
                }
                break
            case BehaviorStepType.SCENARIO:
                writer.newLine()
                writer.write("${' '.padRight(4)}scenario ${element.name}")
                element.getChildSteps().each {
                    writer.newLine()
                    switch (it.stepType) {
                        case BehaviorStepType.GIVEN:
                            writer.write("${' '.padRight(6)}given ${it.name}")
                            break
                        case BehaviorStepType.WHEN:
                            writer.write("${' '.padRight(6)}when ${it.name}")
                            break
                        case BehaviorStepType.THEN:
                            writer.write("${' '.padRight(6)}then ${it.name}")
                            break
                        case BehaviorStepType.AND:
                            writer.write("${' '.padRight(6)}and")
                            break
                        default:
                            //no op to avoid having alerts in story text
                            break
                    } //end it.stepType switch
                    if (it.result?.pending()) {
                        writer.write(" [PENDING]")
                    } else if (it.result?.failed()) {
                        writer.write(" [FAILURE: ${it.result?.cause()?.getMessage()}]")
                    }
                }//end child steps
                if (element.getChildSteps().size == 0) {
                    //a scenario w/out child steps is pending
                    writer.write(" [PENDING]")
                }
                break
            case BehaviorStepType.GIVEN:
                writer.write("${' '.padRight(6)}given ${element.name}")
                break
            case BehaviorStepType.WHEN:
                writer.write("${' '.padRight(6)}when ${element.name}")
                break
            case BehaviorStepType.THEN:
                writer.write("${' '.padRight(6)}then ${element.name}")
                break
            case BehaviorStepType.AND:
                writer.write("${' '.padRight(6)}and")
                break
            default:
                //no op to avoid having alerts in story text
                break
        }

        if (element.result == Result.FAILED) {
            writer.newLine()
            writer.newLine()
            writer.write("	Failure -> ${element.name} ${element.description}")
            writer.newLine()
            writer.write("${element.failuremessage}")
        }
        if (element.result == Result.PENDING) {
            writer.write(" [PENDING]")
        }
        writer.newLine()

    }
}