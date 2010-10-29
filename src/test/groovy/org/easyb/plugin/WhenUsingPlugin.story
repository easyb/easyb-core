import org.easyb.plugin.TestEasybPlugin

using 'test'

TestEasybPlugin.resetAllCounts()


scenario "just scenario", {
}

// it should have invoked before and on test plugin once
//assert TestEasybPlugin.beforeScenarioCount == 1
//assert TestEasybPlugin.afterScenarioCount == 1
//assert TestEasybPlugin.beforeGivenCount == 0
//assert TestEasybPlugin.afterGivenCount == 0
//assert TestEasybPlugin.beforeWhenCount == 0
//assert TestEasybPlugin.afterWhenCount == 0
//assert TestEasybPlugin.beforeThenCount == 0
//assert TestEasybPlugin.afterThenCount == 0

scenario "just scenario and given", {
    given "context"
}

//assert TestEasybPlugin.beforeScenarioCount == 2
//assert TestEasybPlugin.afterScenarioCount == 2
//assert TestEasybPlugin.beforeGivenCount == 1
//assert TestEasybPlugin.afterGivenCount == 1

scenario "just scenario and when", {
    when "event"
}

//assert TestEasybPlugin.beforeScenarioCount == 3
//assert TestEasybPlugin.afterScenarioCount == 3

scenario "just scenario and then", {
    then "outcome", {
      TestEasybPlugin.beforeWhenCount.shouldBe 1
      TestEasybPlugin.afterWhenCount.shouldBe 1
      TestEasybPlugin.beforeScenarioCount.shouldBe 4
      TestEasybPlugin.afterScenarioCount.shouldBe 3
      TestEasybPlugin.beforeThenCount.shouldBe 1
      TestEasybPlugin.afterThenCount.shouldBe 0
    }
}

scenario "all together", {
    given "context"
    when "event"
    then "outcome"
}
assert TestEasybPlugin.beforeStoryCount == 0
assert TestEasybPlugin.beforeScenarioCount == 0
assert TestEasybPlugin.afterScenarioCount == 0
assert TestEasybPlugin.beforeGivenCount == 0
assert TestEasybPlugin.afterGivenCount == 0
assert TestEasybPlugin.beforeWhenCount == 0
assert TestEasybPlugin.afterWhenCount == 0
assert TestEasybPlugin.beforeThenCount == 0
assert TestEasybPlugin.afterThenCount == 0

runScenarios()

assert TestEasybPlugin.beforeStoryCount == 1
assert TestEasybPlugin.beforeScenarioCount == 5
assert TestEasybPlugin.afterScenarioCount == 5
assert TestEasybPlugin.beforeGivenCount == 2
assert TestEasybPlugin.afterGivenCount == 2
assert TestEasybPlugin.beforeWhenCount == 2
assert TestEasybPlugin.afterWhenCount == 2
assert TestEasybPlugin.beforeThenCount == 2
assert TestEasybPlugin.afterThenCount == 2
