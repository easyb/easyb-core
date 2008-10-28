import org.disco.easyb.exception.VerificationException

int initcount = easybResults().getIgnoredScenarioCount()

ignore ~/abc/

scenario "abc", {
    given "bah", {
        bar = 10
    }
    then "it's a lamb", {
        bar.shouldBe 90
    }
}

scenario "123", {
    given "blah", {
        foo = 123
    }
    then "blah blah", {
        foo.shouldBe 123
    }
}


if (easybResults().getIgnoredScenarioCount() < (initcount + 1)) {
    throw new VerificationException('Ignored scenarios count not correct')
}