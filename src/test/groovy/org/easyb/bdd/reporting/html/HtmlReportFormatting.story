package org.easyb.bdd.reporting.html

import org.easyb.report.HtmlReportHelper

scenario "Converting line breaks to HTML", {
	given "a step description containing a line break", {
		description = "this line\nhas a linebreak"
	}
	when "we format the step description", {
		formattedDescription = HtmlReportHelper.formatStep(description)
	}
	then "the formatted description should contain an HTML line break", {
		formattedDescription.shouldBe "this line<br/>has a linebreak"
	}
}

scenario "Converting Windows line breaks to HTML", {
	given "a step description containing a line break", {
		description = "this line\r\nhas a linebreak"
	}
	when "we format the step description", {
		formattedDescription = HtmlReportHelper.formatStep(description)
	}
	then "the formatted description should contain an HTML line break", {
		formattedDescription.shouldBe "this line<br/>has a linebreak"
	}
}


scenario "Converting old Mac OS line breaks to HTML", {
	given "a step description containing a line break", {
		description = "this line\rhas a linebreak"
	}
	when "we format the step description", {
		formattedDescription = HtmlReportHelper.formatStep(description)
	}
	then "the formatted description should contain an HTML line break", {
		formattedDescription.shouldBe "this line<br/>has a linebreak"
	}
}