import org.easyb.plugin.EasybPlugin
import org.easyb.plugin.PluginFactory
import org.easyb.plugin.NullPlugin

narrative 'plugin discovery', {
    as_a 'easyb developer'
    i_want 'to discover the plugin for a story from the story text'
    so_that 'I can invokve the appropriate lifecycle methods which surround story execution'
}

PluginFactory factory = new PluginFactory()

scenario 'no plugin', {
    given 'a story which does not contain a using declaration', {
        story = "scenario 'foo'"
    }
    when 'determining plugin for story', {
        plugin = factory.pluginForStory(story)
    }
    then 'a null plugin instance is returned', {
        plugin.shouldBeA NullPlugin
    }
}

scenario 'valid plugin', {
    given 'a story which starts with a valid using declaration for the test plugin', {
        story = "using 'test'\nscenario 'foo'"
    }
    when 'determining plugin for story', {
        plugin = factory.pluginForStory(story)
    }
    then 'a test plugin instance is returned', {
        plugin.shouldBeA EasybPlugin
    }
}

scenario 'invalid plugin', {
    given 'a story which starts with an invalid using declaration', {
        story = "using 'unknown'\nscenario 'foo'"
    }
    when 'determining plugin for story'
    then 'should throw an exception', {
        ensureThrows(RuntimeException) {
            plugin = factory.pluginForStory(story)
            plugin.shouldBeA NullPlugin
        }
    }
}