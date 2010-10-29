package org.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.plugin.PluginLocator
import org.easyb.plugin.SyntaxExtension


class StoryBinding extends Binding {
  StoryKeywords story
  // where the story is located - necessary for offsets for shared stories
  File storyDirectory

  def addSyntax(SyntaxExtension ext) {
    ext.getSyntax()?.each { methodName, closure ->
      story.binding."$methodName" = { Object[] params ->
        story.extensionMethod(closure, params)
      }
    }

    ext.getExtensionCategories()?.each { cat ->
      story.extensionCategories.add cat
    }
  }

  public replaySteps(boolean executeStory) {
    story.replaySteps(executeStory, this)
  }

  def StoryBinding(ExecutionListener listener, File storyDirectory) {
    this.story = new StoryKeywords(listener, this)
    this.storyDirectory = storyDirectory

    // load all auto syntax adding plugins
    PluginLocator.findAllAutoloadingSyntaxExtensions().each { SyntaxExtension ext ->
      addSyntax(ext)
    }

    where = { description, exampleData, closure = null ->
      if (exampleData != null) {
        story.examples(description, exampleData, closure)
      }
    }

    examples = { description, exampleData, closure = null ->
      if (exampleData != null) {
        story.examples(description, exampleData, closure)
      }
    }

    using = {pluginName, pluginVariableName = null ->
      plugin = new PluginLocator().findPluginWithName(pluginName)

      story.addPlugin(plugin)

      if (pluginVariableName) {
        setProperty(pluginVariableName, plugin)
      }
    }

    extension = { name ->
      addSyntax(PluginLocator.findSyntaxExtensionByName(name))
    }

    shared_stories = { String file ->
      if (!storyDirectory)
      storyDirectory = new File(".")

      if (file.indexOf('.') < 0)
      file += ".shared"

      GroovyShell g = new GroovyShell(story.binding.getClass().getClassLoader(), story.binding);

      File storyFile = new File(storyDirectory, file);

      g.evaluate(storyFile);
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
      story.scenario(description, closure)
    }

    runScenarios = {->      
      story.replaySteps(true, this)
    }

    then = {spec, closure = story.pendingClosure ->
      story.then(spec, closure)
    }

    when = {description, closure = {} ->
      story.when(description, closure)
    }

    given = {description, closure = {} ->
      story.given(description, closure)
    }

    and = {description = "", closure = story.pendingClosure ->
      story.and(description, closure)
    }

    but = {description = "", closure = story.pendingClosure ->
      story.but(description, closure)
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
      story.ignoreOn()
    }

    tags = {
      //nop
    }


    ignore = {Object... scenarios ->
      if (scenarios.size() == 1) {
        def objscn = scenarios[0]

        if (objscn instanceof String)
          story.ignore(objscn)
        else if (objscn == all || !(objscn instanceof Closure)) {
          try {
            objscn.call()
          } catch (excep) {
            if (scenarios[0].getClass() == String) {
              story.ignore([scenarios[0]])
            } else {
              story.ignore(scenarios[0])
            }
          }
        } else if (objscn instanceof Closure) {
          story.ignoreOn()

          try {
            objscn.call()
          } finally {
            story.ignoreOff()
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
  static StoryBinding getBinding(listener, File storyDirectory) {
    return new StoryBinding(listener, storyDirectory)
  }

  static StoryBinding getBinding(listener) {
    return getBinding(listener, null)
  }
}
