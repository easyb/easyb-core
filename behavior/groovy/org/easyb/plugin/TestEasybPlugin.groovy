package org.easyb.plugin

import org.easyb.plugin.BasePlugin

class TestEasybPlugin extends BasePlugin {
    static int beforeScenarioCount
    static int afterScenarioCount

    public String getName() {
        return "test"
    }

    def beforeScenario(Binding binding) {
        beforeScenarioCount++
    }

    def afterScenario(Binding binding) {
        afterScenarioCount++
    }
}