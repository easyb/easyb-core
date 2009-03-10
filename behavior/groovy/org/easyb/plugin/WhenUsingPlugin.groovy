using 'test'

TestEasybPlugin.beforeScenarioCount = 0
TestEasybPlugin.afterScenarioCount = 0

scenario "one", {
    given "context"
    when "event"
    then "outcome"
}

it 'should have invoked before and on test plugin once', {
    TestEasybPlugin.beforeScenarioCount.shouldBe 1
    TestEasybPlugin.afterScenarioCount.shouldBe 1
}

scenario "two", {
    given "context"
    when "event"
    then "outcome"
}

it 'should have invoked before and on test plugin twice', {
    TestEasybPlugin.beforeScenarioCount.shouldBe 0
    TestEasybPlugin.afterScenarioCount.shouldBe 0
}