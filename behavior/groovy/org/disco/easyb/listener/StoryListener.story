package org.disco.easyb.listener

import org.disco.easyb.result.Result
import org.disco.easyb.util.BehaviorStepType

scenario "listener is given all successful results", {

  given "a story listener", {
    listener = new DefaultListener()
  }

  when "a successful result is added", {
    listener.startStep(BehaviorStepType.SCENARIO, "success scenario")
      listener.startStep(BehaviorStepType.THEN, "success then step")
      listener.gotResult(new Result(Result.SUCCEEDED))
      listener.stopStep()
    listener.stopStep()
  }

  then "the count of failed specifications should be 0", {
    listener.failedScenarioCount.shouldBe 0
  }

  and

  // TODO reimplement using successful scenariocount
//  then "the count of successful specifications should be 1", {
//    listener.getSuccessfulSpecificationCount().shouldBe 1
//  }

  and

  then "the count of total scenarios should be 1", {
    listener.getScenarioCount().shouldBe 1
  }

  and

  // TODO reimplement using successful scenariocount
//  then "the total specifications should equal the successful specifications", {
//    listener.getScenarioCount().shouldEqual listener.getSuccessfulSpecificationCount()
//  }

}

scenario "listener is given a single failure", {

  given "a story listener", {
    listener = new DefaultListener()
  }

  when "a failure result is added", {
    listener.startStep(BehaviorStepType.SCENARIO, "success scenario")
      listener.startStep(BehaviorStepType.THEN, "failure then step")
      listener.gotResult(new Result(new Exception("FailureExceptionReason")))
      listener.stopStep()
    listener.stopStep()
  }

  then "the count of failed specifications should be 1", {
    listener.getFailedScenarioCount().shouldBe(1)
  }

  and

  // TODO reimplement using successful scenariocount
//  then "the count of successful specifications should be 0", {
//    listener.getSuccessfulSpecificationCount().is(0)
//  }

  and

  then "the count of total specifications should be 1", {
    listener.getScenarioCount().shouldBe 1
  }

  and

  // TODO reimplement using successful scenariocount
//  then "the total specifications should equal the failed specifications", {
//    listener.getSpecificationCount().shouldBeEqualTo listener.getFailedSpecificationCount()
//  }

}
