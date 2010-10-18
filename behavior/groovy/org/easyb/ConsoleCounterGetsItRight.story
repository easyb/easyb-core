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
        before "before", { given "some condition", {} }
        after "after", { then "some condition", {} }
        before_each "before each", { given "some condition", {} }
        after_each "after each", { then "some condition", {} }
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
      //println  "[[[$consoleOutput]]]"
    }
    then "the scenario should be marked as pending", {
        consoleOutput.shouldHave "Pending: 2"
    }
    and "the scenario should not be marked as failing", {
        consoleOutput.shouldHave "Failures: 0"
    }
//    and "there should be 2 before_eaches", {
//      consoleOutput.shouldHave "Before Eachs run: 2,"
//    }
//  and "there should be 2 after_eaches", {
//    consoleOutput.shouldHave "After Eachs run: 2,"
//  }
//  and "there should be 1 before", {
//    consoleOutput.shouldHave "Befores run: 1,"
//  }
//  and "there should be 1 after", {
//    consoleOutput.shouldHave "Afters run: 1,"
//  }
  and "there should be two scenarios run", {
    consoleOutput.shouldHave "Scenarios run: 2,"
  }

}
