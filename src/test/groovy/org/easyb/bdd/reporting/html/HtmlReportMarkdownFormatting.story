package org.easyb.bdd.reporting.html

import org.easyb.report.HtmlReportHelper


scenario "Using bold markdown formatting", {
    given "a step description containing markdown formatting", {
        description = "this line has a section **in bold**"
    }
    when "we format the step description", {
        formattedDescription = HtmlReportHelper.formatStep(description)
    }
    then "the formatted description should contain an HTML line break", {
        formattedDescription.shouldBe "this line has a section <strong>in bold</strong>"
    }
}

scenario "Using italics markdown formatting", {
    given "a step description containing markdown formatting", {
        description = "this line has a section *in italics*"
    }
    when "we format the step description", {
        formattedDescription = HtmlReportHelper.formatStep(description)
    }
    then "the formatted description should contain an HTML line break", {
        formattedDescription.shouldBe "this line has a section <em>in italics</em>"
    }
}


scenario "Using tabs and markdown format", {
    given """a step description containing tabs and markdown like this:
\tname:\tSmith
\tage:\t18""", {
        description =
"""Some values:
\tname:\tSmith
\tage:\t18
"""
    }
    when "we format the step description", {
        formattedDescription = HtmlReportHelper.formatStep(description)
    }
    then "the formatted description should contain tabbed values", {
        println "formattedDescription = $formattedDescription"
        formattedDescription.shouldHave "name:&nbsp;&nbsp;&nbsp;Smith"
        formattedDescription.shouldHave "age:&nbsp;&nbsp;&nbsp;&nbsp;18"
    }
}

