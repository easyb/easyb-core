package org.disco.easyb.plugin

public interface EasybPlugin {
    String getName();

    void beforeScenario(Binding binding);

    void afterScenario(Binding binding);
}
