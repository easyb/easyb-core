import org.easyb.domain.BehaviorFactory
import org.easyb.listener.ResultsCollector
import org.easyb.listener.BroadcastListener
import org.easyb.report.XmlReportWriter
import org.easyb.listener.ResultsAmalgamator

def testSourceDir = System.getProperty("easyb.test.source.dir")
def reportsDir = System.getProperty("easyb.reports.dir")

scenario "Check mapping of narrative elements to XML reports", {
  given "a story file with narrative infos", {
    storyBehavior = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/reporting/xml/NarrativeMappingScenario.story"))
  }

  when "the story is executed", {
    storyBehavior.execute()
  }

  and "the reports are written", {
    xmlReportLocation = "${reportsDir}/NarrativeMappingScenario-report.xml"
    new XmlReportWriter(xmlReportLocation).writeReport(new ResultsAmalgamator(storyBehavior))
  }
  
  then "the resulting file can be read", {
  	xmlFileText = new File(xmlReportLocation).getText()
    xmlFileText.shouldNotBe ""
  }
  
  and "the file contains narrative description \"Should be in narrative description\"", {
  	xmlFileText.contains("Should be in narrative description").shouldBe true
  }

  and "the file contains narrative role \"Should be in narrative role description\"", {
  	xmlFileText.contains("Should be in narrative role description").shouldBe true
  }
  
  and "the file contains narrative feature \"Should be in narrative feature description\"", {
  	xmlFileText.contains("Should be in narrative feature description").shouldBe true
  }
  
  and "the file contains narrative benefit \"Should be in narrative benefit description\"", {
  	xmlFileText.contains("Should be in narrative benefit description").shouldBe true
  }
}