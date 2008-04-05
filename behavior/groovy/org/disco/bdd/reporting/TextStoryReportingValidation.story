package org.disco.bdd.reporting

scenario "text reports of single scenario stories with no failures", {
	
	given "an easyb txtstory has been generated with no errors and is single scenario", {
		filetext = new File("./target/valid-report.txt").getText()
	}
	
	then "it should remain valid according to confirmed report syntax", {
		control = new File("./behavior/conf/1Story1ScenarioControl.txt").getText()
		filetext.shouldEqual control
	}
	
}