package org.easyb.report

import org.easyb.listener.ResultsCollector
import org.easyb.result.Result
import org.easyb.util.BehaviorStepType

public abstract class TxtReportWriter implements ReportWriter {
    protected String location;

    protected abstract BehaviorStepType getGenesisType()

    ;

    protected abstract Writer getWriter()

    ;

    protected abstract String getResultsAsText(ResultsCollector results)

    ;

    /**
     *
     */
    void writeReport(ResultsCollector results) {
        Writer writer = getWriter()
        writer.writeLine(getResultsAsText(results))
        results.genesisStep.getChildrenOfType(getGenesisType()).each {genesisChild ->
            handleElement(writer, genesisChild)
        }

        writer.close()
    }

    /**
     *
     */
    def handleElement(writer, element) {
        writeElement(writer, element)
        element.getChildSteps().each {
            handleElement(writer, it)
        }
    }

    /**
     *
     */
    def writeElement(writer, element) {
        writer.write(element.format('\n', ' ', getGenesisType()))
        writeFailuresAndPending(writer, element)
    }

    def writeFailuresAndPending(writer, element) {
        def failed = getFailedAndPendingBehaviorsText(element)
        if (failed != null) {
            writer.write(failed)
        }
        writer.newLine()
    }

    def getFailedAndPendingBehaviorsText(element) {
        if (element.result?.pending()) {
            return " [PENDING]"
        } else if (element.result == Result.FAILED) {
            return "\n\n	Failure -> ${element.name} ${element.description}\n${element.failuremessage}"
        } else {
            switch (element.stepType) {
                case BehaviorStepType.GIVEN:
                case BehaviorStepType.WHEN:
                case BehaviorStepType.THEN:
                case BehaviorStepType.AND:
                    if (element.result?.failed()) {
                        return " [FAILURE: ${element.result?.cause()?.getMessage()}]"
                    }
                    break;
                case BehaviorStepType.SCENARIO:
                    if (element.getChildSteps().size == 0) {
                        //a scenario w/out child steps is pending or ignored
                        return element.result?.pending() ? " [PENDING]" : " [IGNORED]"
                    }
                    break
            }
        }
    }
}