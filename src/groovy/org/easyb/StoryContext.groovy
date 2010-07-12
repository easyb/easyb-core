

/*
Holds the context in which a whole set of stories executes. The reason this is now a class is that examples can have their
own story context in which the whole chain of before/after/before_each/after_each/shared, etc can sit
 */

package org.easyb

import org.easyb.plugin.EasybPlugin
import org.easyb.util.BehaviorStepType;


public class StoryContext {

  public StoryContext(Binding binding) {
    this.binding = binding
    this.parentContext = null
  }

  public StoryContext(StoryContext parentContext) {
    this.binding = parentContext.binding
    this.parentContext = parentContext
  }

  /**
   * all of the scenarios in this context. These get evaluated when the context gets evaluated
   */
  ArrayList<BehaviorStep> steps = new ArrayList<BehaviorStep>()

  /*
   * Allows us to find them by name for shared scenarios
   */
  HashMap<String, BehaviorStep> sharedScenarios = new HashMap<String, BehaviorStep>()

  StoryContext parentContext;

  List<StoryContext> sharedContexts = new ArrayList<StoryContext>();

  /* before step */
  BehaviorStep beforeScenarios
  /* after step */
  BehaviorStep afterScenarios

  /* local context ignores */
  def ignoreAll = false
  def ignoreList = []
  def ignoreRegEx

  BehaviorStep beforeEach
  BehaviorStep afterEach

  ArrayList<EasybPlugin> activePlugins = new ArrayList<EasybPlugin>()
  StoryBinding binding  // this is constant across all contexts

  /* the behavior that represents the example data, output for reporting each time we loop*/
  BehaviorStep exampleStep

  /* map or array data */
  def exampleData

  public void addPlugin(plugin) {
    if (!activePlugins.contains(plugin))
      activePlugins.add(plugin)
  }

  public void notifyPlugins(closure) {
    activePlugins.each { plugin -> 
      closure( plugin, binding )
    }

    if ( parentContext )
      parentContext.notifyPlugins(closure)
  }

  public void addStep(BehaviorStep step) {
    steps.add(step)
  }

  public void removeStep(BehaviorStep step) {
    steps.remove(step)
  }

  public BehaviorStep findSharedScenario(String name) {
    def ss = sharedScenarios[name]

    if ( !ss && parentContext )
      return parentContext.findSharedScenario(name)
    else
      return ss
  }
}