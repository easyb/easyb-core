package org.disco.easyb.report

import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.listener.ResultsCollector

public class TxtSpecificationReportWriter implements ReportWriter {
    String location

    TxtSpecificationReportWriter(String location) {
        this.location = location
    }

    /**
     *
     */
    void writeReport(ResultsCollector results) {
        Writer writer = new BufferedWriter(new FileWriter(new File(location)))
        def count = results.getSpecificationCount()
        writer.writeLine("${(count == 1) ? "1 specification" : "${count} specifications"}" +
                " (including ${results.getPendingSpecificationCount()} pending) executed" +
                "${results.getFailedSpecificationCount().toInteger() > 0 ? ", but status is failure!" : " successfully"}" +
                "${results.getFailedSpecificationCount().toInteger() > 0 ? " Total failures: ${results.getFailedSpecificationCount()}" : ""}")
        results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).each {genesisChild ->
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
        switch (element.stepType) {
            case BehaviorStepType.SPECIFICATION:
                writer.newLine()
                writer.write("${' '.padRight(2)}Specification: ${element.name}")
                break
            case BehaviorStepType.DESCRIPTION:
                writer.write("${' '.padRight(3)} ${element.description}")
                writer.newLine()
                break
            case BehaviorStepType.BEFORE:
                writer.write("${' '.padRight(4)}before ${element.name}")
                break
            case BehaviorStepType.IT:
                writer.write("${' '.padRight(4)}it ${element.name}")
                break
            case BehaviorStepType.AND:
                writer.write("${' '.padRight(4)}and")
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
        if (element.result?.pending()) {
            writer.write(" [PENDING]")
        }
        writer.newLine()

    }
}
