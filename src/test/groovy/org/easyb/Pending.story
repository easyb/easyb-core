import org.easyb.exception.VerificationException
import org.easyb.Configuration
import org.easyb.BehaviorRunner
import static org.easyb.BehaviorRunner.getBehaviors

scenario "test scenario 1", {
        given "some context"
        when "something happens"
        then "something should occur"
      }

scenario "Unimplemented stories should be marked as pending", {
    given "a scenario with pending steps", {
        spec = """
      scenario "test scenario 1", {
        given "some context"
        when "something happens"
        then "something should occur"
      }
      scenario "test scenario 1", {
        given "some context", {}
        when "something happens", {}
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
    }
    then "the scenario should be marked as pending", {
        consoleOutput.shouldHave "Pending: 2"
    }
    and "the scenario should not be marked as failing", {
        consoleOutput.shouldHave "Failures: 0"
    }
}
