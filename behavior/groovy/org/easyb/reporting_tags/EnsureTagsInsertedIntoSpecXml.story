import org.easyb.domain.BehaviorFactory
import org.easyb.report.*
import org.easyb.listener.ResultsAmalgamator

scenario "nested spec", {
  given "a story file with nested scenarios", {
    specBehavior = BehaviorFactory.createBehavior(new File('./behavior/groovy/org/easyb/reporting_tags/InsertReportingTagsIntoSpec.specification'))
  }

  when "the specification is executed", {
    specBehavior.execute()
  }

  and
  when "the reports are written", {
    xmlReportLocation = "./target/reports/InsertReportingTagsInto-report.xml"
    new XmlReportWriter(xmlReportLocation).writeReport(new ResultsAmalgamator(specBehavior))
    txtReportLocation = "./target/reports/InsertReportingTagsInto-report.txt"
    new TxtStoryReportWriter(txtReportLocation).writeReport(new ResultsAmalgamator(specBehavior))
  }

  then "the resulting xml should include 2 jira entries", {
    xmlP = new XmlSlurper()
    xml = xmlP.parse(new File(xmlReportLocation))

    def jira1 = xml.specifications.specification.jira

    jira1.'@id'.shouldBe "CSS-1574"
    jira1.'@description'.shouldBe "This should be in the report"

    def jira2 = xml.specifications.specification.it.jira
    jira2.'@id'.shouldBe "some text"
    jira2.'@description'.shouldBe "some other text"
  }
  and "one entry called exec that should have a result of 2 based on a category calculation", {
    xml.specifications.specification.exec.'@result'.shouldBe "2"
  }
  and "the text report should not mention 2 or jira", {
    contents = new File(txtReportLocation).text

    contents.contains("jira").shouldBe false
    contents.contains("2").shouldBe false

  }
}