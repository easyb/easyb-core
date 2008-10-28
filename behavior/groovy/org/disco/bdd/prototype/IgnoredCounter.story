import org.disco.easyb.exception.VerificationException

int initcount = easybResults().getIgnoredScenarioCount()

ignore "this scenario will be ignored"


scenario "this scenario will be ignored", {
    given "blah", {
        foo = 10
    }
    when "blah blah", {
        foo += 10
    }
    then "yadida", {
        foo.shouldBe "A farse"
    }
}


scenario "this scenario will NOT be ignored", {
    given "blah", {
        bar = 10
    }
    when "blah blah", {
        bar += 10
    }
    then "yadida", {
        bar.shouldBe 20
    }
}

if (easybResults().getIgnoredScenarioCount() < (initcount + 1)) {
    throw new VerificationException('Ignored scenarios count not correct')
}