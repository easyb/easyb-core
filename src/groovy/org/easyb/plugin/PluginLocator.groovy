package org.easyb.plugin

import sun.misc.Service;

class PluginLocator {
    EasybPlugin findPluginWithName(String pluginName) {
        for (EasybPlugin plugin: Service.providers(EasybPlugin)) {
            if (plugin.name.equals(pluginName)) {
                return plugin
            }
        }
        throw new RuntimeException("Plugin <${pluginName}> not found")
    }
}
