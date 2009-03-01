package org.easyb.plugin

interface EasybPlugin {
    String getName()

    void beforeScenario(Binding binding)

    void afterScenario(Binding binding)
}
