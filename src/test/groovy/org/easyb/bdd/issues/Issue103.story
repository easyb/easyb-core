import org.easyb.exception.VerificationException
import java.util.logging.Logger

Logger log = Logger.getLogger("Issue103.story")
def var = ""
def addInBefore = "In before"
def addInBeforeEach = " + in each before"
def addInGiven = " + in given"
def addInAfter = " + in after"
def addInAfterEach = " + in each after"

before "[Before block", {
    when "info to var is added in before block", {
        var += addInBefore
        log.info("In before block '$var'")
    }
}

before_each "[Before_each block", {
    when "info to var is added in before each block", {
        var += addInBeforeEach
        log.info("In before_each block '$var'")
    }
}

after "After block]", {
    when "info to var is added in after block", {
        var += addInAfter
        log.info("In after block '$var'")
    }
}

after_each "After each block]", {
    when "info to var is added in after each block", {
        var += addInAfterEach
        log.info("In after_each block '$var'")
    }
}

scenario "Validate setup and teardown", {
    given "var is changed", {
        var += addInGiven
    }
    then "the variable should be set with value from before block" , {
        log.info("In then block '$var'")
        var.shouldBe addInBefore + addInBeforeEach + addInGiven
    }
}
