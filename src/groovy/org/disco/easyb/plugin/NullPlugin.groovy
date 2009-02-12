package org.disco.easyb.plugin

import org.disco.easyb.plugin.EasybPlugin

public class NullPlugin implements EasybPlugin {
    public String getName() {
        return "nothing";
    }

    public void beforeScenario(Binding binding) {

    }

    public void afterScenario(Binding binding) {

    }
}
