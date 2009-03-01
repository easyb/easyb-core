import org.easyb.BehaviorRunner
import static org.easyb.BehaviorRunner.getBehaviors
import org.easyb.ConsoleReporter
import org.easyb.Configuration
import org.easyb.exception.VerificationException

scenario "realtime console output", {
    given "a specification", {
        spec = """
      scenario "test scenario", {
        given "some context"
        when "something happens"
        then "something should occur", {
          true.shouldBe false
        }
      }
      scenario "another test scenario", {
        given "some context"
        when "something happens"
        then "something should occur", {
          true.shouldBe true
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
        try {
            System.setOut(new PrintStream(consoleOutputStream))
            consolerpt = new ConsoleReporter()
            //consolerpt.configuration = new Configuration(null, null)
            BehaviorRunner runner = new BehaviorRunner([], consolerpt)
            runner.runBehavior(getBehaviors(specFile.absolutePath))
        } catch (VerificationException expected) {
        } finally {
            System.setOut(originalOut)
        }

        consoleOutput = consoleOutputStream.toString()
    }
    
    then "a summary of scenario results should be printed to the console", {
        consoleOutput.contains("Scenarios run: 2, Failures: 1, Pending: 0").shouldBe true
    }

    and "the summary should name the error", {
        consoleOutput.contains("something should occur").shouldBe true
    }

    and "the summary should report the details of the error", {
        consoleOutput.contains("expected false but was true").shouldBe true
    }

    and "the summary should report aggregate runs and 1 failure", {
        consoleOutput.contains("2 total behaviors ran with 1 failure").shouldBe true
    }
}
