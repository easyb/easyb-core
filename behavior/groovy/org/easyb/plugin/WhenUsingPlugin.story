import org.easyb.plugin.TestEasybPlugin

using 'test'

TestEasybPlugin.beforeScenarioCount = 0
TestEasybPlugin.afterScenarioCount = 0

assert TestEasybPlugin.beforeStoryCount == 1

scenario "one", {
    given "context"
    when "event"
    then "outcome"
}

// it should have invoked before and on test plugin once
assert TestEasybPlugin.beforeScenarioCount == 1
assert TestEasybPlugin.afterScenarioCount == 1

scenario "two", {
    given "context"
    when "event"
    then "outcome"
}

// it should have invoked before and on test plugin twice
assert TestEasybPlugin.beforeScenarioCount == 2
assert TestEasybPlugin.afterScenarioCount == 2