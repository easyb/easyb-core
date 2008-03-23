package org.disco.bdd.prototype


scenario "easyb should support and chaining", {
	
	given "an integer value of 10", {
		value1 = 10
	}
	
	and "given another integer value of 5", {
		 value2 = 5
	}
	
	when "the first integer value has 10 added to it", {
		value1 += 10
	}
	
	and "when the second integer value has 12 added to it", {
		value2 += 12
	}
	
	then "the first variable should be equal to 20", {
		value1.shouldBe 20
	}
	
	and "then the second value should be equal to 17", {
		value2.shouldBe 17
	}
}
	
	
	
	