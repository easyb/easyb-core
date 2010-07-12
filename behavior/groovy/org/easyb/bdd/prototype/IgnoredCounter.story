import org.easyb.exception.VerificationException


before "setup counter", {
  given "we determine the initial ignored scenario count", {
    initcount = easybResults().getIgnoredScenarioCount()
  }
}

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

after "check counter", {
  then "the ignored count should be less than the initial count+1", {
    easybResults().getIgnoredScenarioCount().shouldBeLessThan (initcount + 1)
  }
}