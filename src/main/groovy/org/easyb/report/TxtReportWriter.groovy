package org.easyb.report

import org.easyb.listener.ResultsCollector
import org.easyb.result.Result
import org.easyb.util.BehaviorStepType
import org.easyb.listener.ResultsReporter
import org.easyb.listener.ResultsAmalgamator

public abstract class TxtReportWriter implements ReportWriter {
  protected String location;

  protected abstract BehaviorStepType getGenesisType()

  ;

  protected abstract Writer getWriter()

  ;

  protected abstract String getResultsAsText(ResultsReporter results)

  ;

  /**
   *
   */
  void writeReport(ResultsAmalgamator amal) {
    ResultsReporter results = amal.getResultsReporter()
    
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
/*   } else if (element.result?.failed()) {
      return "\n\n  Failure -> ${element.stepType} ${element.description?element.description:""} ${element.result?.cause()?.getMessage()}\n}" */
    } else {
      switch (element.stepType) {
        case BehaviorStepType.GIVEN:
        case BehaviorStepType.WHEN:
        case BehaviorStepType.THEN:
        case BehaviorStepType.AND:
        case BehaviorStepType.IT:
          if (element.result?.failed()) {
            return " [FAILURE: ${element.result?.cause()?.getMessage()}]"
          }
          break;
        case BehaviorStepType.SCENARIO:
        case BehaviorStepType.SPECIFICATION:
          if (element.getChildSteps().size == 0) {
            //a scenario w/out child scenarioSteps is pending or ignored
            return element.result?.pending() ? " [PENDING]" : " [IGNORED]"
          }
          break
      }
    }
  }
}