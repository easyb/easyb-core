package org.easyb.report

import org.easyb.util.BehaviorStepType
import org.easyb.listener.ResultsCollector
import org.easyb.result.Result
import org.easyb.listener.ResultsAmalgamator
import org.easyb.listener.ResultsReporter

public class PrettyPrintReportWriter extends TxtReportWriter {

    private static final String RED = '\u001B[1;31m'; //failed
    private static final String GREEN = '\u001B[1;32m'; //succeeded
    private static final String YELLOW = '\u001B[1;33m'; //ignored
    private static final String CYAN = '\u001B[1;36m'; //pending

    public PrettyPrintReportWriter() {}

    protected BehaviorStepType getGenesisType() {
        return null
    }

    protected Writer getWriter() {
        return new BufferedWriter(new PrintWriter(System.out))
    }

    protected final String getResultsAsText(ResultsReporter results) {
        return null
    }

    @Override
    void writeReport(ResultsAmalgamator amal) {
        ResultsReporter results = amal.getResultsReporter()
      
        Writer writer = getWriter()

        if (results.getScenarioCount() > 0) {
            writeColour(writer, results.getScenarioResultsAsText(), chooseStoryResultsColour(results))
            results.genesisStep.getChildrenOfType(BehaviorStepType.STORY).each {genesisChild ->
                handleElement(writer, genesisChild)
            }
            writer.newLine()
        }

        if (results.getSpecificationCount() > 0) {
            writeColour(writer, results.getSpecificationResultsAsText(), chooseSpecificationResultsColour(results))
            results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).each {genesisChild ->
                handleElement(writer, genesisChild)
            }
            writer.newLine()
        }
        writer.close()
    }

    @Override
    def writeElement(writer, element) {
        def colour = chooseColour(element.result)
        writeColour(writer, element.format('\n', ' ', getGenesisType()), colour)

        def failed = getFailedAndPendingBehaviorsText(element)
        if (failed != null) {
            writeColour(writer, failed, colour)
        }
        writer.newLine()
    }

    def writeColour(writer, message, colour) {
        writer.write(colour.length() == 0 ? message : colour + message + '\u001B[1;0m')
    }

    def chooseStoryResultsColour(results) {
        def colour = ''
        if (isWindows())
            return colour

        if (results.getFailedScenarioCount() > 0) {
            colour = RED
        } else if (results.getPendingScenarioCount() > 0) {
            colour = CYAN
        } else if (results.getIgnoredScenarioCount() > 0) {
            colour = YELLOW
        } else if (results.getScenarioCount() - results.getIgnoredScenarioCount() > 0) {
            colour = GREEN
        }
        return colour
    }

    def chooseSpecificationResultsColour(results) {
        def colour = ''
        if (isWindows())
            return colour

        if (results.getFailedSpecificationCount() > 0) {
            colour = RED
        } else if (results.getPendingSpecificationCount() > 0) {
            colour = CYAN
        } else if (results.getSpecificationCount() > 0) {
            colour = GREEN
        }
        return colour
    }

    def chooseColour(result) {
        def colour = ''
        if (isWindows())
            return colour

        if (result == Result.FAILED || result?.failed()) {
            colour = RED
        } else if (result?.pending()) {
            colour = CYAN
        } else if (result?.ignored()) {
            colour = YELLOW
        } else if (result?.succeeded()) {
            colour = GREEN
        }
        return colour
    }

    def isWindows() {
        System.getProperty("os.name").toLowerCase().matches("mswin|mingw")
    }
}