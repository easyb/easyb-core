package org.easyb

import org.easyb.StoryKeywords
import org.easyb.listener.ExecutionListener
import org.easyb.plugin.EasybPlugin
import org.easyb.plugin.PluginLocator
import org.easyb.plugin.NullPlugin

class StoryBinding extends Binding {
    StoryKeywords story
    EasybPlugin activePlugin

    def StoryBinding(ExecutionListener listener, EasybPlugin activePlugin) {
        this.story = new StoryKeywords(listener)
        this.activePlugin = activePlugin

        using = {pluginName ->
            activePlugin = new PluginLocator().findPluginWithName(pluginName)
        }

        before = {description = "", closure = {} ->
            story.before(description, closure)
        }

        assumption = before.curry("story assumption")
        assuming = before.curry("story assumes")

        after = {description = "", closure = {} ->
            story.after(description, closure)
        }

        before_each = {description = "", closure = {} ->
            story.beforeEach(description, closure)
        }

        after_each = {description = "", closure = {} ->
            story.afterEach(description, closure)
        }

        scenario = {description, closure = story.pendingClosure ->
            activePlugin.beforeScenario(this)   
            story.scenario(description, closure)
            activePlugin.afterScenario(this)
        }

        then = {spec, closure = story.pendingClosure ->
            activePlugin.beforeThen(this)
            story.then(spec, closure)
            activePlugin.afterThen(this)
        }

        when = {description, closure = {} ->
            activePlugin.beforeWhen(this)
            story.when(description, closure)
            activePlugin.afterWhen(this)
        }

        given = {description, closure = {} ->
            activePlugin.beforeGiven(this)
            story.given(description, closure)
            activePlugin.afterGiven(this)
        }

        and = {description = "", closure = story.pendingClosure ->
            story.and(description, closure)
        }

        narrative = {description = "", closure = {} ->
            story.narrative(description, closure)
        }

        description = {description ->
            story.description description
        }

        easybResults = {
            story.easybResults()
        }

        all = {
            story.all()
        }

        ignore = {Object ... scenarios ->
            if (scenarios.size() == 1) {
                def objscn = scenarios[0]
                try {
                    objscn.call()
                } catch (excep) {
                    if (scenarios[0].getClass() == String) {
                        story.ignore([scenarios[0]])
                    } else {
                        story.ignore(scenarios[0])
                    }
                }
            } else if (scenarios.size() > 1) {
                story.ignore(scenarios as List)
            } else {
                //no argument but this isn't working
            }
        }
        
        shared_behavior = {description, closure = {} ->
            story.sharedBehavior(description, closure)
        }
        
        it_behaves_as = {description ->
            story.itBehavesAs(description)
        }
    }

    def getAt(ArrayList list) {
        this.story.ignoreList = list
    }

    /**
     * This method returns a fully initialized Binding object (or context) that
     * has definitions for methods such as "when" and "given", which are used
     * in the context of stories.
     */
    static StoryBinding getBinding(listener, activePlugin = new NullPlugin()) {
        return new StoryBinding(listener, activePlugin)
    }
}
