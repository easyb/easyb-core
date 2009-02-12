package org.disco.easyb.plugin

import sun.misc.Service;
import org.disco.easyb.plugin.EasybPlugin

public class PluginLocator {
    EasybPlugin findPluginWithName(String pluginName) {
        for (EasybPlugin each: Service.providers(EasybPlugin)) {
            println each.name
            if (each.name.equals(pluginName)) {
                return each
            }
        }
        throw new RuntimeException("Plugin <${pluginName}> not found")
    }
}
