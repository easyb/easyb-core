package org.easyb.bdd.reporting.html

import org.easyb.report.HtmlReportHelper



scenario "Converting tabs to HTML", {
    when """we format a text containing tabs:
    text:\t\t\t#text
    formatted:\t#formattedWithTabs""", {
        formattedDescription = HtmlReportHelper.formatStep(text)
    }
    then "the formatted description should expand the tabs with the right number of spaces", {
        formattedDescription.shouldHave formattedWithTabs
    }
    where "text with tabs #text should be formatted as #formattedWithTabs", {
            text = ["\tstarting with a tab",
                    "1\t23456",
                    "12\t3456",
                    "123\t456",
                    "1234\t56",
                    "12345\t6",
            ]

            formattedWithTabs  =     ["<pre><code>starting with a tab<br/></code></pre>",
                    "1&nbsp;&nbsp;&nbsp;23456",
                    "12&nbsp;&nbsp;3456",
                    "123&nbsp;456",
                    "1234&nbsp;&nbsp;&nbsp;&nbsp;56",
                    "12345&nbsp;&nbsp;&nbsp;6"
            ]
    }
}



scenario "Tabs in a multi-line expression", {
    when "we format a text containing line breaks", {
        tabbedText = """values:
a:\t1
bb:\t2
ccc:\t3"""
    }
    and "we format the text", {
        formattedText = HtmlReportHelper.formatStep(tabbedText)
    }
    then "the tabs should take into account the new lines", {
        formattedText.shouldHave "a:&nbsp;&nbsp;1"
        formattedText.shouldHave "bb:&nbsp;2"
        formattedText.shouldHave "ccc:&nbsp;&nbsp;&nbsp;&nbsp;3"
    }
}


scenario "Tabs in a multi-line expression with tabs at the start of lines", {
    when "we format a text containing line breaks", {
        tabbedText = """values:
\ta:\t1
\tbb:\t2
\tccc:\t3"""
    }
    and "we format the text", {
        formattedText = HtmlReportHelper.formatStep(tabbedText)
    }
    then "the tabs should take into account the new lines", {
        formattedText.shouldHave "a:&nbsp;&nbsp;1"
        formattedText.shouldHave "bb:&nbsp;2"
        formattedText.shouldHave "ccc:&nbsp;&nbsp;&nbsp;&nbsp;3"
    }
    and "the markdown code format should be respected", {
        formattedText.shouldHave "<pre><code>"
    }
}
