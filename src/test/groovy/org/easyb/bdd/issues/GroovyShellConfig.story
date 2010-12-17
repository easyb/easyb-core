package org.easyb.bdd.issues

import org.easyb.BehaviorRunner
import org.easyb.Configuration
import org.easyb.exception.VerificationException

import static org.easyb.BehaviorRunner.getBehaviors
import org.easyb.domain.GroovyShellConfiguration

scenario "Ensure that groovy shell configuration still works", {
    given "a scenario that uses the context variable from a GroovyShellConfiguration", {
        spec = """
      scenario "test scenario 1", {
        given "some context", {
          println 'gsh value is ' + gsh
        }
        when "something happens"
        then "something should occur", {
          gsh.shouldBe 7
        }
      }
    """
    }
    when "the specification is run with easyb", {
        specFile = File.createTempFile('EasybTest', '.story')
        specFile.deleteOnExit()
        specFile.write(spec)

        consoleOutputStream = new ByteArrayOutputStream()

        originalOut = System.out

        GroovyShellConfiguration gsh = new GroovyShellConfiguration();
        gsh.getShellContextVariables().put("gsh", 7);
        try {
            System.setOut(new PrintStream(consoleOutputStream))
            BehaviorRunner runner = new BehaviorRunner(new Configuration())
            runner.runBehaviors(getBehaviors(gsh, specFile.absolutePath))
        } catch (VerificationException expected) {
          expected.printStackTrace()
        } finally {
            System.setOut(originalOut)
        }

        consoleOutput = consoleOutputStream.toString()
        println "-----"
        println "console ouput is ${consoleOutput}"
        println "-----"
    }
    then "the scenario should not be marked as failing", {
        consoleOutput.shouldHave "Failures: 0"
    }
}