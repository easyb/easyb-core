package org.disco.easyb.plugin

class NullPlugin implements EasybPlugin {
    String getName() {
        return "nothing";
    }

    void beforeScenario(Binding binding) {

    }

    void afterScenario(Binding binding) {

    }
}
