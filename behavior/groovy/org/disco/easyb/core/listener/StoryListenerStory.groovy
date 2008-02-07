package org.disco.easyb.core.listener

import org.disco.easyb.core.result.Result

scenario "listener is given all successful results", {

  given "a story listener",{
    listener = new DefaultListener()
  }

  when "a successful result is added", {
    result = new Result("irrelevant", "irrelevant", Result.SUCCEEDED)
    listener.gotResult(result)
  }

  then "it should have no failures", {
    listener.hasBehaviorFailures().is false
  }

  and

  then "the count of failed specifications should be 0", {
    listener.getFailedBehaviorCount().shouldBe 0
  }

  and

  then "the count of successful specifications should be 1", {
   listener.getSuccessfulBehaviorCount().shouldBe 1
  }

  and

  then "the count of total specifications should be 1", {
   listener.getTotalBehaviorCount().shouldBe 1
  }

  and

  then "the total specifications should equal the successful specifications", {
    listener.getTotalBehaviorCount().shouldEqual listener.getSuccessfulBehaviorCount()
  }

}

scenario "listener is given a single failure", {

  given "a story listener", {
    listener = new DefaultListener()
  }

  when "a failure result is added", {
    result = new Result("irrelevant", "irrelevant", new Exception("FailureExceptionReason"))
    listener.gotResult(result)
  }

  then "it should have failures", {
    listener.hasBehaviorFailures().is(true)
  }

  and

  then "the count of failed specifications should be 1", {
   listener.getFailedBehaviorCount().shouldBe(1)
  }

  and

  then "the count of successful specifications should be 0", {
    listener.getSuccessfulBehaviorCount().is(0)
  }

  and

  then "the count of total specifications should be 1", {
   listener.getTotalBehaviorCount().shouldBe 1
  }

  and

  then "the total specifications should equal the failed specifications", {
    listener.getTotalBehaviorCount().shouldBeEqualTo listener.getFailedBehaviorCount()
  }

}
