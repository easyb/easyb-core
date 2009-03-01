import org.easyb.exception.VerificationException

before "do a one time setup", {
    val = 10
    bar = 10
}

scenario "one scenario, value is 10", {
    when "you add 10 more to it", {
        val += 10
    }
    then "value should be 20", {
        val.shouldBe 20
    }
}

scenario "two scenario, value is now 20", {
    when "you add 3 to it", {
        val += 3
    }
    then "the value should be 23", {
        val.shouldBe 23
    }
}

after "do some clean up", {
    bar += 10
}

if (bar != 20) {
    throw new VerificationException("after clause didn't work")
}