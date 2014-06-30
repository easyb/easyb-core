package org.easyb.bdd.reporting.xml

narrative "Should be in narrative description", {
    as_a "Should be in narrative role description"
    i_want "Should be in narrative feature description"
    so_that "Should be in narrative benefit description"
}

scenario "This scenario is in a story that contains a narrative for testing XML report mapping", {
	def dada

	given "We initialize a variable", {
		dada = true
	}
	then "its value has the expected value", {
		dada.shouldBe true
	}
}