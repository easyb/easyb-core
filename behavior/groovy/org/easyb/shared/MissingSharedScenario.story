import org.easyb.BehaviorRunner
import org.easyb.Configuration
import org.easyb.exception.VerificationException
import static org.easyb.BehaviorRunner.getBehaviors

scenario "missing a shared scenario", {
  given "a scenario", {
    spec = """
      scenario "missing shared scenario", {
  it_behaves_as "missing"
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

  then "we should have a failure", {
    consoleOutput.contains("1 total behavior ran with 1 failure").shouldBe true
  }

  and "it should say behaves as failure", {
    consoleOutput.contains("step BEHAVES_AS \"missing\"").shouldBe true
  }
}
