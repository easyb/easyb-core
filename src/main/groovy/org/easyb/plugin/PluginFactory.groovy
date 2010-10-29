package org.easyb.plugin

public class PluginFactory {
    private PluginLocator locator = new PluginLocator()

    EasybPlugin pluginForStory(String story) {
        EasybPlugin plugin = new NullPlugin()

        story.eachLine {String line ->
            if (line.startsWith('using')) {
                String[] components = line.split("['\"]")
                if (components.length > 1) {
                    plugin = locator.findPluginWithName(components[1])
                }
            }
        }

        return plugin
    }
}