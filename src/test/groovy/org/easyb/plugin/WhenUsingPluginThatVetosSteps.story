package org.easyb.plugin

using 'testveto'

TestVetoingEasybPlugin.resetAllCounts()

assert TestVetoingEasybPlugin.beforeStoryCount == 0
assert TestVetoingEasybPlugin.beforeScenarioCount == 0
assert TestVetoingEasybPlugin.afterScenarioCount == 0
assert TestVetoingEasybPlugin.beforeGivenCount == 0
assert TestVetoingEasybPlugin.afterGivenCount == 0
assert TestVetoingEasybPlugin.beforeWhenCount == 0
assert TestVetoingEasybPlugin.afterWhenCount == 0
assert TestVetoingEasybPlugin.beforeThenCount == 0
assert TestVetoingEasybPlugin.afterThenCount == 0


scenario "all together", {
    given "context", {
    }
    when "event", {
    }
    then "outcome", {
    }
    and "another outcome", {
    }
}

runScenarios()
/*
assert TestVetoingEasybPlugin.beforeStoryCount == 1
assert TestVetoingEasybPlugin.beforeScenarioCount == 1
assert TestVetoingEasybPlugin.afterScenarioCount == 1
assert TestVetoingEasybPlugin.beforeGivenCount == 1
assert TestVetoingEasybPlugin.afterGivenCount == 1
assert TestVetoingEasybPlugin.beforeWhenCount == 1
assert TestVetoingEasybPlugin.afterWhenCount == 0
assert TestVetoingEasybPlugin.beforeThenCount == 2
assert TestVetoingEasybPlugin.afterThenCount == 2
*/