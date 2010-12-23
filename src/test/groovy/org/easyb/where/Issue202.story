package org.easyb.where

import java.util.regex.Matcher
import org.easyb.domain.BehaviorFactory
import org.easyb.report.*
import org.easyb.listener.ResultsAmalgamator

def reportsDir = System.getProperty("easyb.reports.dir")
def testSourceDir = System.getProperty("easyb.test.source.dir")

println "${testSourceDir}/groovy/org/easyb/where/Issue202Example.story"

scenario "txt report are bugged with where clause",{
	given "a story with 1 scenario with 1 where containing 2 values",{

		specBehavior = BehaviorFactory.createBehavior(new File("${testSourceDir}/groovy/org/easyb/where/Issue202Example.story"))
	}
	
	when "the story is executed", {
		specBehavior.execute()
	}
	
	and "the report is generated", {
		txtReportLocation = "${reportsDir}/issue202.txt"
		new TxtStoryReportWriter(txtReportLocation).writeReport(new ResultsAmalgamator(specBehavior))
		report = new File(txtReportLocation).text
		println "***see the report it gives***"
		println report
		println "***end of trace***"
	}
	
	then "summary reports 0 behaviors ignored", {
		(report =~ /\((.*) behaviors were ignored/)[0][1].shouldBe "0"
		
	}
	and "body reports 2 given steps implied by the where clause", {
		matches = (report =~ /being x=(.)/)
    println matches
		ensure (matches[0] == ["being x=1", "1"])
		ensure (matches[1] == ["being x=2", "2"])
	}
	and "summary should report 2 scenarios executed successfully", { // but reports 4 
		(report =~ /(.*) scenarios executed successfully/)[0][1].shouldBe "2"
	}
	and "body shouldn't report 2 ignored steps", { // but it happens! 
		Matcher matches = (report =~ /scenario example \[IGNORED\]/)
		matches.getCount().shouldBe 0
	}
}