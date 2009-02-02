package org.disco.easyb.report

import org.disco.easyb.listener.ResultsCollector
import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

public abstract class TxtReportWriter implements ReportWriter {
  protected String location;
  protected abstract BehaviorStepType getGenesisType();
  protected abstract Writer getWriter();
  protected abstract String getResultsAsText(ResultsCollector results);
  
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
    if (element.result?.pending()) {
        writer.write(" [PENDING]")
    } else if (element.result == Result.FAILED) {
        writer.newLine()
        writer.newLine()
        writer.write("	Failure -> ${element.name} ${element.description}")
        writer.newLine()
        writer.write("${element.failuremessage}")
    } else {
      switch(element.stepType) {
        case BehaviorStepType.GIVEN:
        case BehaviorStepType.WHEN:
        case BehaviorStepType.THEN:
        case BehaviorStepType.AND:
          if (element.result?.failed()) {
              writer.write(" [FAILURE: ${element.result?.cause()?.getMessage()}]")
          }
          break;
        case BehaviorStepType.SCENARIO:
          if (element.getChildSteps().size == 0) {
              //a scenario w/out child steps is pending or ignored
              if (element.result?.pending()) {
                  writer.write(" [PENDING]")
              } else {
                  //it is ignored
                  writer.write(" [IGNORED]")
              }
          }
          break
      }
    }
    
    writer.newLine()
  }
}