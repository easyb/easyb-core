package org.disco.easyb.core.report

import org.disco.easyb.core.listener.SpecificationListener
import org.disco.easyb.SpecificationBinding
import org.disco.easyb.core.util.CamelCaseConverter


class TerseReportWriter implements ReportWriter{

  def report
  def storyListener

  TerseReportWriter(report, storyListener) {
    this.report = report
    this.storyListener = storyListener
  }

  void writeReport(){
	def count = storyListener.getTotalBehaviorCount()
    println  "${(count > 1) ?  "${count} behavior steps run " : " 1 behavior step ran" }" +  
    	"${storyListener.hasBehaviorFailures() ? ", but status is failure!" : "successfully"} " + 
    	"${storyListener.hasBehaviorFailures() ? "Total failures: ${storyListener.failedBehaviorCount}" : " "}"
   	
    storyListener.results.each{ result ->
    
      switch(result.containerName) {
        case SpecificationBinding.STORY_THEN:
          break
        case SpecificationBinding.STORY:
          break
        case SpecificationBinding.STORY_SCENARIO:
          break
        case SpecificationBinding.STORY_GIVEN:
          break
        case SpecificationBinding.STORY_WHEN:
          break
        case SpecificationBinding.AND:
          break  
        case SpecificationBinding.BEHAVIOR_IT:
          break
        default:
          print "! Something unknown made it into the result list"
      }
      if(result.failed()){
    	  def buff = new StringBuffer()
    	  for(i in 1..10){
    	      buff << "\t" + result.cause()?.getStackTrace()[i]
    	      buff << "\t\n"
    	  }
    	  println("	Failure -> " + result.containerName() + " " + new CamelCaseConverter(result.name()).toPhrase() + " in " + result.source)
    	  println("\t" + result.cause().toString())
    	  println(buff.toString())
    	  
      }
    }
  }
}
