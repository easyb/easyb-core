package org.easyb.plugin

import sun.misc.Service

class PluginLocator {
    EasybPlugin findPluginWithName(String pluginName) {
        for (EasybPlugin each: Service.providers(EasybPlugin)) {
            if (each.name.equals(pluginName)) {
                return each
            }
        }
        throw new RuntimeException("Plugin <${pluginName}> not found")
    }
}
