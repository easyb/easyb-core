package org.disco.easyb.report

import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

public class TxtSpecificationReportWriter implements ReportWriter {

  def listener
  def report

  TxtSpecificationReportWriter(report, listener){
	  this.report = report
	  this.listener = listener
  }
  /**
   * 
   */
  void writeReport() {
    Writer writer = new BufferedWriter(new FileWriter(new File(report.location)))
	  def count = listener.getSpecificationCount()
	  writer.writeLine("${(count > 1) ? "${count} specifications" : " 1 specification"}" + 
			     " (including ${listener.getPendingSpecificationCount()} pending) executed" +
	            "${listener.getFailedSpecificationCount().toInteger() > 0 ? ", but status is failure!" : " successfully"}" +
	            "${listener.getFailedSpecificationCount().toInteger() > 0 ? " Total failures: ${listener.getFailedSpecificationCount()}" : ""}")
	 listener.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).each { genesisChild ->
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
