package org.disco.easyb.report

import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType
import quicktime.app.spaces.Listener


class TxtStoryReportWriter implements ReportWriter {

  def listener
  def report
    
  TxtStoryReportWriter(report, listener){
	  this.report = report
	  this.listener = listener
  }
  /**
   * 
   */
  void writeReport() {
	  Writer writer = new BufferedWriter(new FileWriter(new File(report.location)))
	  def count = listener.scenarioCount
	  writer.writeLine("${(count > 1) ? "${count} scenarios" : " 1 scenario"}" + 
			     " (including ${listener.pendingScenarioCount} pending) executed" +
	            "${listener.failedScenarioCount.toInteger() > 0 ? ", but status is failure!" : " successfully"}" +
	            "${listener.failedScenarioCount.toInteger() > 0 ? " Total failures: ${listener.failedScenarioCount}" : ""}")
	 listener.genesisStep.getChildrenOfType(BehaviorStepType.STORY).each { genesisChild ->
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
      	writer.write("${' '.padRight(3)} ${element.description}")
      	writer.newLine()
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
         }
         if (it.result?.pending()) {
             writer.write(" [PENDING]")
         }else if(it.result?.failed()){
        	 writer.write(" [FAILURE: ${it.result?.cause()?.getMessage()}]")
         }
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