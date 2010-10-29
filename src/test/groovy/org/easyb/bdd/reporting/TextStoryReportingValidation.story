package org.easyb.bdd.reporting

def testSourceDir = System.getProperty("easyb.test.source.dir")
def reportsDir = System.getProperty("easyb.reports.dir")

scenario "text reports of single scenario stories with no failures", {

    given "an easyb txtstory has been generated with no errors and is single scenario", {
        filetext = new File("${reportsDir}/valid-report.txt").getText()
    }

    then "it should remain valid according to confirmed report syntax", {
      filetext.readLines().shouldEqual new File("${testSourceDir}/resources/1Story1ScenarioControl.txt").getText().readLines()
    }

}

scenario "text reports of single scenario stories with failure", {

    given "an easyb txtstory has been generated with errors and is single scenario", {
        filetext = new File("${reportsDir}/valid-report-failure.txt").getText()
    }

    then "it should remain valid according to confirmed report syntax", {
        filetext.readLines().shouldEqual new File("${testSourceDir}/resources/1Story1ScenarioFailureControl.txt").getText().readLines()
    }

}
