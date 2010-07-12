package org.easyb.listener

import org.easyb.result.Result
import org.easyb.util.BehaviorStepType
import org.easyb.BehaviorStep

scenario "listener is given all successful results", {

    given "a story listener", {
        listener = new ResultsCollector()
    }

    when "a successful result is added", {
        listener.startStep(new BehaviorStep(BehaviorStepType.SCENARIO, "success scenario"))
        listener.startStep(new BehaviorStep(BehaviorStepType.THEN, "success then step"))
        listener.gotResult(new Result(Result.SUCCEEDED))
        listener.stopStep()
        listener.stopStep()
        results = listener.getResultsReporter()
    }

    then "the count of failed specifications should be 0", {
        results.failedScenarioCount.shouldBe 0
    }

    and

    then "the count of successful specifications should be 1", {
        results.getSuccessScenarioCount().shouldBe 1
    }

    and

    then "the count of total scenarios should be 1", {
        results.getScenarioCount().shouldBe 1
    }

    and

    then "the total specifications should equal the successful specifications", {
        results.getScenarioCount().shouldEqual results.getSuccessScenarioCount()
    }

}

scenario "listener is given a single failure", {

    given "a story listener", {
        listener = new ResultsCollector()
    }

    when "a failure result is added", {
        listener.startStep(new BehaviorStep(BehaviorStepType.SCENARIO, "success scenario"))
        listener.startStep(new BehaviorStep(BehaviorStepType.THEN, "failure then step"))
        listener.gotResult(new Result(new Exception("FailureExceptionReason")))
        listener.stopStep()
        listener.stopStep()
        results = listener.getResultsReporter()
    }

    then "the count of failed scenarios should be 1", {
        results.getFailedScenarioCount().shouldBe(1)
    }

    and

    then "the count of successful scenarios should be 0", {
        results.getSuccessScenarioCount().is(0)
    }

    and

    then "the count of total scenarios should be 1", {
        results.getScenarioCount().shouldBe 1
    }

    and

    then "the total scenarios should equal the failed scenario", {
        results.getScenarioCount().shouldBeEqualTo results.getFailedScenarioCount()
    }

}
