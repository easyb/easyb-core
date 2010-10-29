package org.easyb.plugin

import org.easyb.plugin.BasePlugin

class TestEasybPlugin extends BasePlugin {
    static resetAllCounts() {
        beforeScenarioCount = 0
        afterScenarioCount = 0
        beforeGivenCount = 0
        afterGivenCount = 0
        beforeWhenCount = 0
        afterWhenCount = 0
        beforeThenCount = 0
        afterThenCount = 0
    }
    
    static int beforeScenarioCount
    static int afterScenarioCount
    static int beforeGivenCount
    static int afterGivenCount
    static int beforeWhenCount
    static int afterWhenCount
    static int beforeThenCount
    static int afterThenCount
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

    def beforeGiven(Binding binding) {
        beforeGivenCount++
    }

    def afterGiven(Binding binding) {
        afterGivenCount++
    }

    def beforeWhen(Binding binding) {
        beforeWhenCount++
    }

    def afterWhen(Binding binding) {
        afterWhenCount++
    }

    def beforeThen(Binding binding) {
        beforeThenCount++
    }
    
    def afterThen(Binding binding) {
        afterThenCount++
    }


}