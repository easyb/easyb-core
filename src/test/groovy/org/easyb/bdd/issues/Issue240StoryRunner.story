import org.easyb.domain.BehaviorFactory
import org.easyb.listener.ResultsCollector
import org.easyb.listener.BroadcastListener
import org.easyb.report.HtmlReportWriter
import org.easyb.listener.ResultsAmalgamator

def testSourceDir = System.getProperty("easyb.test.source.dir")
def reportsDir = System.getProperty("easyb.reports.dir")
def storyName = "Issue240StoryToRun"

scenario "Runtime error happens during execution of shared behavior step", {
  given "a story with a shared behavior that could fail", {
    storyBehavior = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/issues/${storyName}.story"))
	System.setProperty("org.easyb.bdd.issues.Issue240", "0")
  }

  when "the story is executed", {
    storyBehavior.execute()
   	System.clearProperty("org.easyb.bdd.issues.Issue240")
  }

  then "the reports can be written without exceptions", {
    htmlReportLocation = "${reportsDir}/${storyName}-report.html"
    new HtmlReportWriter(htmlReportLocation).writeReport(new ResultsAmalgamator(storyBehavior))
  }
  
  then "the resulting report file can be read", {
  	htmlFileText = new File(htmlReportLocation).getText()
    htmlFileText.shouldNotBe ""
  }
  
  and "the file contains step information of the shared behavior", {
  	htmlFileText.contains("then we expect an error").shouldBe true
  	htmlFileText.contains("then we expect something nice").shouldBe true
  	htmlFileText.contains("then we expect nothing").shouldBe true
  }

  and "the file contains exception information", {
  	htmlFileText.contains("java.lang.ArithmeticException: Division by zero").shouldBe true
  }
}