package org.easyb.bdd.reporting.html

import org.easyb.domain.BehaviorFactory
import org.easyb.report.HtmlReportWriter
import org.easyb.listener.ResultsAmalgamator
import org.easyb.Configuration

def testSourceDir = System.getProperty("easyb.test.source.dir")
def reportsDir = System.getProperty("easyb.reports.dir")
 
scenario "a scenario using tags", {
    given "a scenario using tags", {
        story = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/reporting/html/ScenarioWithTags.story"), new Configuration())
    }
    and "the issue management base URL has been specified in the configuration", {
        story.config.issueSystemBaseUrl = "http://my.issues/issue/"
    }
    when "the specification is executed", {
        story.execute()
    }
    and
    when "the reports are written", {
        htmlReportLocation = "${reportsDir}/ScenarioWithExamples-story-report.html"
        new HtmlReportWriter(htmlReportLocation).writeReport(new ResultsAmalgamator(story))
    }
	then "the report should contain a reference to the story tag", {
        htmlReport = new File(htmlReportLocation).text
        htmlReport.shouldHave "123"
    }
}

scenario "a scenario using tags without tags configured", {
    given "a scenario using tags", {
        story = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/reporting/html/ScenarioWithTags.story"))
    }
    when "the specification is executed", {
        story.execute()
    }
    and
    when "the reports are written", {
        htmlReportLocation = "${reportsDir}/ScenarioWithExamples-story-report.html"
        new HtmlReportWriter(htmlReportLocation).writeReport(new ResultsAmalgamator(story))
    }
	then "the report should not contain a reference to the story tag", {
        htmlReport = new File(htmlReportLocation).text
        htmlReport.shouldNotHave "123"
    }
}

scenario "another scenario using tags", {
    given "a scenario using tags", {
        story = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/reporting/html/ScenarioWithMultipleTags.story"), new Configuration())
    }
    and "the issue management base URL has been specified in the configuration", {
        story.config.issueSystemBaseUrl = "http://my.issues/issue/"
    }
    when "the specification is executed", {
        story.execute()
    }
    and
    when "the reports are written", {
        htmlReportLocation = "${reportsDir}/ScenarioWithExamples-story-report.html"
        new HtmlReportWriter(htmlReportLocation).writeReport(new ResultsAmalgamator(story))
    }
	then "the report should contain a reference to the story tag", {
        htmlReport = new File(htmlReportLocation).text
        htmlReport.shouldHave "123"
    }
    and "the report should reference the second tag", {
        htmlReport.shouldHave "456"
    }
}

scenario "a scenario using a tag and displaying an HTML link", {
    given "a scenario using a tag", {
        story = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/reporting/html/ScenarioWithTags.story"), new Configuration())
    }
    and "the issue management base URL has been specified in the configuration", {
        story.config.issueSystemBaseUrl = "http://my.issues/issue/"
    }
    when "the specification is executed", {
        story.execute()
    }
    and
    when "the reports are written", {
        htmlReportLocation = "${reportsDir}/ScenarioWithExamples-story-report.html"
        new HtmlReportWriter(htmlReportLocation).writeReport(new ResultsAmalgamator(story))
    }
	then "the report should contain a link to the corresponding issue", {
        htmlReport = new File(htmlReportLocation).text
        htmlReport.shouldHave "123"
        htmlReport.shouldHave "<a href='http://my.issues/issue/123'>123</a>"
    }
}


scenario "a scenario using a tag and displaying the default column title", {
    given "a scenario using a tag", {
        story = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/reporting/html/ScenarioWithTags.story"), new Configuration())
    }
    and "the issue management base URL has been specified in the configuration", {
        story.config.issueSystemBaseUrl = "http://my.issues/issue/"
    }
    when "the specification is executed", {
        story.execute()
    }
    and
    when "the reports are written", {
        htmlReportLocation = "${reportsDir}/ScenarioWithExamples-story-report.html"
        new HtmlReportWriter(htmlReportLocation).writeReport(new ResultsAmalgamator(story))
    }
	then "the report should have the default column heading", {
        htmlReport.shouldHave "<th>Issues</th>"
    }
}


scenario "a scenario using a tag and displaying an HTML link and a custom title", {
    given "a scenario using a tag", {
        story = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/reporting/html/ScenarioWithTags.story"), new Configuration())
    }
    and "the issue management base URL has been specified in the configuration", {
        story.config.issueSystemBaseUrl = "http://my.issues/issue/"
        story.config.issueSystemHeading = "TRAC"
    }
    when "the specification is executed", {
        story.execute()
    }
    and
    when "the reports are written", {
        htmlReportLocation = "${reportsDir}/ScenarioWithExamples-story-report.html"
        new HtmlReportWriter(htmlReportLocation).writeReport(new ResultsAmalgamator(story))
    }
	then "the report should contain a link to the corresponding issue", {
        htmlReport = new File(htmlReportLocation).text
        htmlReport.shouldHave "123"
        htmlReport.shouldHave "<a href='http://my.issues/issue/123'>123</a>"
    }
    and "the report should have a heading with the provided title", {
        htmlReport.shouldHave "<th>TRAC</th>"
    }
}

  /*
scenario "a scenario using tags and displaying an HTML link", {
    given "a scenario using tags", {
        story = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/bdd/reporting/html/ScenarioWithMultipleTags.story"))//, new Configuration())
    }
    and "the issue management base URL has been specified in the configuration", {
        //story.config.issueSystemBaseUrl = "http://my.issues/issue/"
    }
    when "the specification is executed", {
        story.execute()
    }
    and
    when "the reports are written", {
        htmlReportLocation = "${reportsDir}/ScenarioWithExamples-story-report.html"
        new HtmlReportWriter(htmlReportLocation).writeReport(new ResultsAmalgamator(story))
    }
	then "the report should contain the first issue linl", {
        htmlReport = new File(htmlReportLocation).text
        htmlReport.shouldHave "<a href='http://my.issues/issue/123'>#123</a>"
    }
    and "the report should have the second issue link", {
        htmlReport.shouldHave "<a href='http://my.issues/issue/456'>#456</a>"
    }
}
*/