package org.disco.easyb.core.report

import org.disco.easyb.core.listener.SpecificationListener
import org.disco.easyb.SpecificationBinding
import org.disco.easyb.core.util.CamelCaseConverter

class TxtStoryReportWriter implements ReportWriter{

  def report
  def storyListener

  TxtStoryReportWriter(report, storyListener) {
    this.report = report
    this.storyListener = storyListener
  }

  void writeReport(){

    def writer = new BufferedWriter(new FileWriter(new File(report.location)))
    //writer.writeLine("Stories Overall Status [${storyListener.hasBehaviorFailures() ? "FAIL" : "PASS"}] : Total Failures [${storyListener.failedBehaviorCount}]")
    
    def count = storyListener.getTotalBehaviorCount()
    writer.writeLine("${(count > 1) ?  "${count} behavior steps executed " : " 1 behavior step run" }" +  
    	"${storyListener.hasBehaviorFailures() ? ", but status is failure!" : "successfully"}" + 
    	"${storyListener.hasBehaviorFailures() ? " Total failures: ${storyListener.failedBehaviorCount}" : ""}")
   	
    
    storyListener.results.each{ result ->
    
      switch(result.containerName) {
        case SpecificationBinding.STORY:
          writer.write("${' '.padRight(2)}Story: ${result.name}")
          break
        case SpecificationBinding.STORY_SCENARIO:
          writer.write("${' '.padRight(4)}scenario ${result.name}")
          break
        case SpecificationBinding.STORY_GIVEN:
          writer.write("${' '.padRight(6)}given ${result.name}")
          break        
        case SpecificationBinding.STORY_WHEN:
          writer.write("${' '.padRight(6)}when ${result.name}")
          break
        case SpecificationBinding.STORY_THEN:
          writer.write("${' '.padRight(6)}then ${result.name}")
          break        
        case SpecificationBinding.AND:
          writer.write("${' '.padRight(6)}and")
          break
        default:
          //no op to avoid having alerts in story text
          break
          //writer.write("! Something unknown made it into the result list")
          //writer.write(" **** " + result.containerName)
      }
      if(result.failed()){
    	  writer.newLine()
    	  writer.newLine()
    	  def buff = new StringBuffer()
    	  buff << "\t\n"
    	  for(i in 1..10){
    	      buff << "\t\t" + result.cause()?.getStackTrace()[i]
    	      buff << "\t\n"
    	  }
    	  writer.write("	Failure -> " + result.containerName() + " " + new CamelCaseConverter(result.name()).toPhrase() + " in " + result.source)
    	  writer.newLine()
    	  writer.write("\t\t" + result.cause().toString())
    	  writer.write(buff.toString())
      }
      //hack needed to ignore its
      if(!result.containerName.equals("it")){
      	writer.newLine()
      }
    }
    writer.close()
  }
}