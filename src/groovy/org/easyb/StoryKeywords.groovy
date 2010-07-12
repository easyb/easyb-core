package org.easyb

import java.util.regex.Pattern
import org.easyb.listener.ExecutionListener
import org.easyb.result.Result
import org.easyb.delegates.EnsuringDelegate
import org.easyb.delegates.PlugableDelegate
import org.easyb.util.BehaviorStepType

/**
 * story keywords just collects and parses the data into steps inside contexts
 */
class StoryKeywords extends BehaviorKeywords {
  def scenariosRun = false
  StoryProcessing storyRunning // not null if story is in flight, used for extension points to pass thru
  def binding

  StoryContext topContext
  StoryContext currentContext
  BehaviorStep currentStep

  def extensionCategories = []

  StoryKeywords(ExecutionListener listener, Binding binding) {
    super(listener)

    topContext = new StoryContext(binding)
    currentContext = topContext
    this.binding = binding
  }

  /**
   * we create a new example step and story context, associate the example data with it and
   * then process the closure within that context. This allows examples within examples within examples as the
   * tester requires.
   *
   * @param description
   * @param data
   * @param closure
   */
  private void processExamplesClosure(description, data, closure) {
    def step = new BehaviorStep(BehaviorStepType.WHERE, description, closure, null)

    StoryContext ctx = new StoryContext(currentContext)

    step.storyContext = ctx
    currentContext = ctx

    ctx.exampleData = data
    ctx.exampleStep = step

    // preserve the step as all subsequent syntax appears underneath the example
    BehaviorStep oldStep = currentStep
    currentStep = step

    try {
      // all new scenarios, etc go into this closure
      closure()
      ctx.parentContext.addStep(currentStep)
    } finally {
      currentContext = ctx.parentContext
      currentStep = oldStep
    }
  }

  /*
  * when we see a "where/examples" clause in a scenario, we
  * 1) remove the scenario from the current context
  * 2) tell the scenario's BehaviorStep parent to remove it as a child
  *
  * This decouples the scenario and leaves it hanging. We then
  *
  * - create a new step (examples)
  * - add the scenario as a child step to the example
  * - add the example step as a child of the scenario's old parent
  * - create a new context
  * - setup the 
  * extract the where clause out, insert it at the same
  * level as the scenario and then move the scenario underneath it.
  */
  private void insertExampleAboveScenario( BehaviorStep scenarioStep, String description, data ) {
    // take scenario out of current context
    currentContext.removeStep(scenarioStep)

    def step = new BehaviorStep(BehaviorStepType.WHERE, description, null, scenarioStep.parentStep)

    // if it is inside a parent (e.g. example), remove it
    if ( scenarioStep.parentStep ) {
      scenarioStep.parentStep.removeChildStep(scenarioStep)
      scenarioStep.parentStep.addChildStep(step)
    }
    
    step.addChildStep(scenarioStep)
    
    currentContext.addStep(step)

    // create a new context for it to go into
    StoryContext ctx = new StoryContext(currentContext)

    // exampleStep should be in the parent context
    step.storyContext = ctx

    ctx.addStep(scenarioStep)

    ctx.exampleData = data
    ctx.exampleStep = step
  }

  def examples(description, data, closure) {
    if (currentStep && currentStep.stepType != BehaviorStepType.WHERE && currentStep.stepType != BehaviorStepType.SCENARIO) {
      throw new IncorrectGrammarException("examples keyword were it should not exist.")
    }

    if (!currentStep) {
      if (closure) {
        // if a closure has been passed, we need to evaluate the closure within the context of a new story context
        // i.e. a new EXAMPLE step gets created
        processExamplesClosure(description, data, closure)
      } else if ( currentContext.exampleData ) {
        // this is an error if it occurs twice!
        throw new IncorrectGrammarException("An attempt has been made to specify example data twice within the same context.")
      } else { // example data for current context
        // otherwise it is example data for the current context. If top level, no problem, if in example step, also no problem
        currentContext.exampleData = data
      }
    } else if ( currentStep.stepType == BehaviorStepType.WHERE ) {
      if ( !closure ) // we already have data for this story context thanks,
        throw new IncorrectGrammarException("examples keyword inside an examples closure must also pass a closure for context")
      
      processExamplesClosure(description, data, closure)
    } else {
      // we are in a scenario, so we create an example node and put the scenario inside it
      insertExampleAboveScenario( currentStep, description, data )
    }
  }

  /**
   * sharedBehavior - this is just a scenario that gets reused and not specifically run.
   *
   * @param description
   * @param closure
   * @return
   */
  def sharedBehavior(description, closure) {
//    println "parse shared behavior ${description}"
    parseScenario(closure, description, BehaviorStepType.SHARED_BEHAVIOR)
  }

  def itBehavesAs(description) {
    addStep(BehaviorStepType.BEHAVES_AS, description, null)
  }

  def pendingClosure = {
    listener.gotResult(new Result(Result.PENDING))
  }

  def before(description, closure) {
    currentContext.beforeScenarios = parseScenario(closure, description, BehaviorStepType.BEFORE)
  }

  def after(description, closure) {
    currentContext.afterScenarios = parseScenario(closure, description, BehaviorStepType.AFTER)
  }

  def beforeEach(description, closure) {
    currentContext.beforeEach = parseScenario(closure, description, BehaviorStepType.BEFORE_EACH)
  }

  def afterEach(description, closure) {
    currentContext.afterEach = parseScenario(closure, description, BehaviorStepType.AFTER_EACH)
  }

  def scenario(scenarioDescription, scenarioClosure) {
    parseScenario(scenarioClosure, scenarioDescription, BehaviorStepType.SCENARIO)
  }

  def parseScenario(scenarioClosure, scenarioDescription, BehaviorStepType type) {
    def scenarioStep = new BehaviorStep(type, scenarioDescription, scenarioClosure, null) // scenarios never have parent steps
    scenarioStep.storyContext = currentContext

    def oldStep = currentStep
    currentStep = scenarioStep

    if (currentContext.ignoreAll || currentContext.ignoreList.contains(scenarioDescription)
        || currentContext.ignoreRegEx?.matcher(scenarioDescription)?.matches()) {
      scenarioStep.ignore = true
    } else if (scenarioClosure == pendingClosure) {
      scenarioStep.pending = true
    } else {
      if (type == BehaviorStepType.SCENARIO)
        currentContext.addStep(scenarioStep)
      else if (type == BehaviorStepType.SHARED_BEHAVIOR)
        currentContext.sharedScenarios[scenarioStep.name] = scenarioStep

      scenarioClosure() // now parse the scenario
    }

    currentStep = oldStep

    return scenarioStep
  }

  def addPlugin(plugin) {
    currentContext.addPlugin plugin
  }

  def replaySteps(executeStory, binding) {
    // this allows the user to run the scenarios before the end of the script if they wish
    if (scenariosRun)
      return

    scenariosRun = true

    storyRunning = new StoryProcessing(extensionCategories)

    try {
      storyRunning.processStory(topContext, executeStory, listener)
    } finally {
      storyRunning = null
    }
  }


  private BehaviorStep addStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    BehaviorStep step = new BehaviorStep(inStepType, inStepName, closure, currentStep)
    step.storyContext = currentContext

    if (closure == pendingClosure)
      step.pending = true

    if (!currentStep)
      currentContext.addStep step
    else
      currentStep.addChildStep step

    return step
  }

  private def addPlugableStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    if (closure != null && closure != pendingClosure)
      closure.delegate = new PlugableDelegate()

    addStep(inStepType, inStepName, closure)
  }

  private def addEnsuringStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    if (closure != null && closure != pendingClosure)
      closure.delegate = new EnsuringDelegate()

    addStep(inStepType, inStepName, closure)
  }

  def given(givenDescription, closure) {
    addPlugableStep(BehaviorStepType.GIVEN, givenDescription, closure)
  }

  def when(whenDescription, closure = {}) {
    addPlugableStep(BehaviorStepType.WHEN, whenDescription, closure)
  }

  def then(spec, closure) {
    addEnsuringStep(BehaviorStepType.THEN, spec, closure)
  }

  def and(description, closure) {
    if (currentStep.lastChildsBehaviorStepType == BehaviorStepType.GIVEN) {
      given(description, closure)
    }
    else if (currentStep.lastChildsBehaviorStepType == BehaviorStepType.WHEN) {
      when(description, closure)
    }
    else if (currentStep.lastChildsBehaviorStepType == BehaviorStepType.THEN) {
      then(description, closure)
    }
  }

  def but(description, closure) {
    and(description, closure)
  }

  def ignoreOn() {
    currentContext.ignoreAll = true
  }

  def ignoreOff() {
    currentContext.ignoreAll = false
  }

  def ignore(scenarios) {
    if (!currentContext.ignoreAll) {
      currentContext.ignoreList = scenarios
    }
  }

  def setIgnoreList(list) {
    currentContext.ignoreList = list
  }

  def ignore(Pattern scenarioPattern) {
    if (!currentContext.ignoreAll) {
      currentContext.ignoreRegEx = scenarioPattern
    }
  }

  def extensionMethod( closure, params ) {
    def ex = new ExtensionPoint(closure:closure, params:params)

    if ( storyRunning )
      storyRunning.executeExtensionMethod( ex )
    else if ( currentStep ) {
      def step = addStep(BehaviorStepType.EXTENSION_POINT, "[extension point]", null)

      step.extensionPoint = ex
    }
  }
}