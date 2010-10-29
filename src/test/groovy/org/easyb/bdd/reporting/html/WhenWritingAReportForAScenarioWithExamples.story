package org.easyb.bdd.reporting.html

import org.easyb.domain.BehaviorFactory
import org.easyb.report.HtmlReportWriter
import org.easyb.listener.ResultsAmalgamator

def testSourceDir = System.getProperty("easyb.test.source.dir")
def reportsDir = System.getProperty("easyb.reports.dir")
 
//ignore "a scenario using examples"
scenario "a scenario using examples", {
    given "a scenario using examples", {
        story = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/reporting/html/ScenarioWithExamples.story"))
    }

    when "the specification is executed", {
        story.execute()
    }
    and
    when "the reports are written", {
        htmlReportLocation = "${reportsDir}/ScenarioWithExamples-story-report.html"
        new HtmlReportWriter(htmlReportLocation).writeReport(new ResultsAmalgamator(story))
    }
	then "the report should not contain unexpanded references to properties with null values", {
		def report = new File(htmlReportLocation).text
		report.shouldNotHave "#favoriteColor"
	}
    and "the report should not contain unexpanded references to inexistant properties", {
        def report = new File(htmlReportLocation).text
        report.shouldNotHave "#unknownField"
    }

}