package org.easyb.bdd.reporting.junit

import org.easyb.report.JUnitReportWriter
import org.easyb.BehaviorRunner
import org.easyb.Configuration
import org.easyb.exception.VerificationException

import static org.easyb.BehaviorRunner.getBehaviors

scenario "Unimplemented stories should be marked as pending", {
    given "a scenario with pending steps", {
        spec = """
      scenario "test scenario 1", {
        given "some context"
        when "something happens"
        then "something should occur"
      }
    """
    }
    when "the specification is run with easyb", {
        specFile = File.createTempFile('EasybTest', '.story')
        specFile.deleteOnExit()
        specFile.write(spec)

        consoleOutputStream = new ByteArrayOutputStream()

        originalOut = System.out
        try {
            System.setOut(new PrintStream(consoleOutputStream))
            BehaviorRunner runner = new BehaviorRunner(new Configuration())
            runner.runBehaviors(getBehaviors(specFile.absolutePath))
        } catch (VerificationException expected) {
        } finally {
            System.setOut(originalOut)
        }

        consoleOutput = consoleOutputStream.toString()
        println "console ouput is ${consoleOutput}"
    }
    then "the scenario should be marked as pending", {
        consoleOutput.shouldHave "Pending: 1"
    }
    and "the scenario should not be marked as failing", {
        consoleOutput.shouldHave "Failures: 0"
    }
}

scenario "Partially implemented stories should be marked as pending", {
    given "a scenario with some pending steps", {
        spec = """
      scenario "test scenario 1", {
        given "some context", {
            x = 1
        }
        when "something happens", {
            y = x + 1
        }
        then "something should occur"
      }
    """
    }
    when "the specification is run with easyb", {
        specFile = File.createTempFile('EasybTest', '.story')
        specFile.deleteOnExit()
        specFile.write(spec)

        consoleOutputStream = new ByteArrayOutputStream()

        originalOut = System.out
        try {
            System.setOut(new PrintStream(consoleOutputStream))
            BehaviorRunner runner = new BehaviorRunner(new Configuration())
            runner.runBehaviors(getBehaviors(specFile.absolutePath))
        } catch (VerificationException expected) {
        } finally {
            System.setOut(originalOut)
        }

        consoleOutput = consoleOutputStream.toString()
        println "console ouput is ${consoleOutput}"
    }
    then "the scenario should be marked as pending", {
        consoleOutput.shouldHave "Pending: 1"
    }
    and "the scenario should not be marked as failing", {
        consoleOutput.shouldHave "Failures: 0"
    }
}