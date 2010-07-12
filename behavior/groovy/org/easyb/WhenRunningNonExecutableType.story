import org.easyb.BehaviorRunner
import org.easyb.Configuration
import org.easyb.listener.ConsoleReporterListener
import org.easyb.exception.VerificationException
import static org.easyb.BehaviorRunner.getBehaviors

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
      conf = new Configuration()
      conf.extendedStoryClass = "NonExecutable"
      BehaviorRunner runner = new BehaviorRunner(conf)
      runner.runBehaviors(getBehaviors([specFile.absolutePath] as String[], conf))
    } catch (VerificationException expected) {
    } finally {
      System.setOut(originalOut)
    }

    consoleOutput = consoleOutputStream.toString()
  }

  then "a summary of scenario results should be printed to the console", {
    consoleOutput.contains("Scenarios run: 2, Failures: 0, Pending: 0").shouldBe true
  }

  and "the summary should report aggregate runs and NOT report an error", {
    consoleOutput.contains("2 total behaviors ran with no failures").shouldBe true
  }
}
