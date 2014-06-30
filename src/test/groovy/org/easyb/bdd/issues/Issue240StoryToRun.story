package org.easyb.bdd.issues

shared_behavior "Division wonderland", {
  then "we expect an error", {
	(10 / divisor).shouldBe expectedResult
  }
  and "we expect something nice", {
  	true.shouldBe true
  }
}

scenario "Call a shared behaviour and die ...", {
	given "We set up our expectations", {
    	divisor = System.getProperty("org.easyb.bdd.issues.Issue240", "10").toInteger()
    	expectedResult = 1
	}
	it_behaves_as "Division wonderland"
	
	then "we expect nothing", {
		expectedResult.shouldBe 1
	}
}
