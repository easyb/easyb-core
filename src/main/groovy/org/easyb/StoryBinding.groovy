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

    where = { source, lineNo, description, exampleData, closure = null ->
      if (exampleData != null) {
        story.examples(description, exampleData, closure, source, lineNo)
      }
    }

    examples = { source, lineNo, description, exampleData, closure = null ->
      if (exampleData != null) {
        story.examples(description, exampleData, closure, source, lineNo)
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

    before = { source, lineNo, description = "", closure = {} ->
      story.before(description, closure, source, lineNo)
    }


    assumption = before.curry("story assumption")
    assuming = before.curry("story assumes")

    after = { source, lineNo, description = "", closure = {} ->
      story.after(description, closure, source, lineNo)
    }

    before_each = { source, lineNo, description = "", closure = {} ->
      story.beforeEach(description, closure, source, lineNo)
    }

    after_each = { source, lineNo, description = "", closure = {} ->
      story.afterEach(description, closure, source, lineNo)
    }

    scenario = { source, lineNo, description, closure = story.pendingClosure ->
      story.scenario(description, closure, source, lineNo)
    }

    runScenarios = {->      
      story.replaySteps(true, this)
    }

    then = { source, lineNo, spec, closure = story.pendingClosure ->
      story.then(spec, closure, source, lineNo)
    }

    when = { source, lineNo, description, closure = {} ->
      story.when(description, closure, source, lineNo)
    }

    given = { source, lineNo, description, closure = {} ->
      story.given(description, closure, source, lineNo)
    }

    and = { source = null, lineNo = 0, description = "", closure = story.pendingClosure ->
      story.and(description, closure, source, lineNo)
    }

    but = { source = null, lineNo = 0, description = "", closure = story.pendingClosure ->
      story.but(description, closure, source, lineNo)
    }

    narrative = { description = "", closure = {} ->
      story.narrative(description, closure)
    }

    description = { description ->
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


    ignore = { Object... scenarios ->
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

    // American spelling
    shared_behavior = {source, lineNo, description, closure = {} ->
      story.sharedBehavior(description, closure, source, lineNo)
    }

    // English spelling
    shared_behaviour = {source, lineNo, description, closure = {} ->
      story.sharedBehavior(description, closure, source, lineNo)
    }

    it_behaves_as = {source, lineNo, description ->
      story.itBehavesAs(description, source, lineNo)
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
