package org.easyb.bdd.prototype

import org.easyb.plugin.BasePlugin

class PrototypePlugin extends BasePlugin {
    String getName() {
        return "prototype";
    }

    def beforeScenario(Binding binding) {
        binding._foo_ = "test"
    }
}