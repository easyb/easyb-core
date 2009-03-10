package org.easyb.plugin

import org.easyb.plugin.BasePlugin

class TestEasybPlugin extends BasePlugin {
    static int beforeScenarioCount
    static int afterScenarioCount
    static int beforeStoryCount

    public String getName() {
        return "test"
    }

    def beforeStory(Binding binding) {
        beforeStoryCount++
    }

    def beforeScenario(Binding binding) {
        beforeScenarioCount++
    }

    def afterScenario(Binding binding) {
        afterScenarioCount++
    }
}