package org.easyb.plugin

import org.easyb.exception.VetoStepException
import org.easyb.listener.ListenerFactory
import org.easyb.listener.ListenerBuilder
import org.easyb.listener.ExecutionListener

class TestVetoingEasybPlugin extends BasePlugin {

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

    TestVetoingEasybPlugin() {
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
        return "testveto"
    }

    def beforeStory(Binding binding) {
        ListenerFactory.registerBuilder(new ListenerBuilder() {
            TestListener testListener = new TestListener();
            ExecutionListener get() {
                return testListener
            }

        });
    }

    @Override def afterStory(Binding binding) {
        ListenerFactory.deregisterExecutionListener(TestListener.class)
        return super.afterStory(binding)    //To change body of overridden methods use File | Settings | File Templates.
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