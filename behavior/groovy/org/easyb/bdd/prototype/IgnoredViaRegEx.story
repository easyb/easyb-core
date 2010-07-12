import org.easyb.exception.VerificationException

before "setup counter", {
  given "we determine the initial ignored scenario count", {
    initcount = easybResults().getIgnoredScenarioCount()
  }
}


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

after "check counter", {
  then "the ignored count should be less than the initial count+1", {
    easybResults().getIgnoredScenarioCount().shouldBeLessThan (initcount + 1)
  }
}