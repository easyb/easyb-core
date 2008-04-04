package org.disco.bdd.reporting

scenario "text reports of single scenario stories with no failures", {
	
	given "an easyb txtstory has been generated with no errors and is single scenario", {
		filetext = new File("./target/valid-report.txt").getText()
	}
	
	then "it should remain valid according to confirmed report syntax", {
		/*filetext.shouldBe """ 1 scenario (including 0 pending) executed successfully

		  Story: and chained feature

		    scenario easyb should support and chaining
		      given an integer value of 10
		      given given another integer value of 5
		      when the first integer value has 10 added to it
		      when when the second integer value has 12 added to it
		      then the first variable should be equal to 20
		      then then the second value should be equal to 17
		"""
		*/
	}
	
}