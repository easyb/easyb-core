package org.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.result.Result
import org.easyb.plugin.PluginLocator

import org.easyb.util.BehaviorStepType
import org.easyb.plugin.ExampleDataParser

public class StoryProcessing {
  private Stack<StoryContext> contextStack = new Stack<StoryContext>();
  private StoryContext currentContext = null
  private BehaviorStep currentStep
  private boolean executeStory
  private ExecutionListener listener
  private int currentIteration
  private def exampleDataParsers
  private def extensionCategories = []
  private def additionalExtensionCategories

  public StoryProcessing(additionalExtensionCategories) {
    exampleDataParsers = PluginLocator.findAllExampleDataParsers()

    extensionCategories.addAll(additionalExtensionCategories)
    extensionCategories.add(BehaviorCategory.class)

    this.additionalExtensionCategories = additionalExtensionCategories
  }

  public def processStory(StoryContext currentContext, boolean executeStory, ExecutionListener listener) {
    this.executeStory = executeStory
    this.listener = listener

    runContext(currentContext, null)
  }

  private def processStoryContext(StoryContext context, BehaviorStep exampleStep) {
    context.steps.each { BehaviorStep step ->
      if (step.stepType == BehaviorStepType.SCENARIO) {
        if (step.ignore) {
          listener.startStep step
          listener.gotResult new Result(Result.IGNORED)
          listener.stopStep()
        } else if (step.pending) {
          listener.startStep step
          step.closure()
          listener.stopStep()
        } else {
          processScenario step, true
        }
      } else if (step.stepType == BehaviorStepType.WHERE) {
        runContext(step.storyContext, step)
      } else {
        processChildStep step
      }
    }
    if ( exampleStep && exampleStep.childSteps.size() > 0 ) {
      exampleStep.childSteps.each { childStep ->
        processChildStep childStep
      }
    }
  }

  private def  processStoryUsingExamples(StoryContext context, BehaviorStep exampleStep) {
    int oldIteration = currentIteration
    currentIteration = 0

    def c = { map ->
      map.each { key, value ->
        context.binding.setProperty key, value
      }

      // don't use the more efficient putAll on variables as it may lead to odd behavior if the field is already in the binding, then
      // get property would always pass back the binding field instead of the variables value

      try {
        processStoryContext(context, exampleStep)
        
        currentIteration ++
      } finally {
        def v = context.binding.variables

        map.keySet().each { key ->
          v.remove key
        }
      }
    }

    for( ExampleDataParser p : exampleDataParsers ) {
      if ( p.processData(context.exampleData, c, context.binding ) )
        break
    }


    currentIteration = oldIteration

  }


  /*
    runs all of the scenarios, befores and afters in this context
   */

  private def runContext(StoryContext context, BehaviorStep exampleStep) {
    if (currentContext != null)
      contextStack.push(currentContext)

    try {
      this.currentContext = context

      // let the plugins know we are starting
      context.notifyPlugins { plugin, binding ->
        plugin.setClassLoader(getClass().getClassLoader())
        plugin.beforeStory(binding)
      }

      if (context.beforeScenarios)
        processScenario(context.beforeScenarios, false)

      try {
        if (!context.exampleData)
          processStoryContext(context, exampleStep)
        else
          processStoryUsingExamples(context, exampleStep)

      } finally {
        if (context.afterScenarios)
          processScenario(context.afterScenarios, false)

        context.notifyPlugins { plugin, binding -> plugin.afterStory(binding) }
      }
    } finally {
      if (contextStack.size())
        currentContext = contextStack.pop()
    }

  }


  private def processSharedScenarios(sharedStep) {
    BehaviorStep shared = currentContext.findSharedScenario(sharedStep.name)

    if (!shared) { // can't find the shared scenario
      listener.startStep(sharedStep)

      sharedStep.result = new Result(Result.FAILED)
      sharedStep.result.description = "Unable to find shared scenario ${sharedStep.name}"

      listener.gotResult sharedStep.result

      listener.stopStep()
    } else {
      processScenario shared, false
    }

//    println "out of shared, back to original"
  }

  private def processChildStep(BehaviorStep childStep) {
    childStep.decodeCurrentName currentIteration
    listener.startStep(childStep)

    // figure out what to actually do
    def action

    if (childStep.pending)
      action = { childStep.replay() }
    else if (!executeStory)
      action = {}
    else if (childStep.stepType == BehaviorStepType.THEN)
      action = {
        use(extensionCategories) {
          childStep.replay()
        }
        childStep.result = new Result(Result.SUCCEEDED)
        listener.gotResult childStep.result
      }
    else // other child steps
      action = {
        use(additionalExtensionCategories) {
          childStep.replay()
        }
        childStep.result = new Result(Result.SUCCEEDED)
        listener.gotResult childStep.result
      }

    // now the plugin's methods are diffuse, so we use the action closure to keep it tidy
    try {
      switch (childStep.stepType) {
        case BehaviorStepType.THEN:
          currentContext.notifyPlugins { plugin, binding -> plugin.beforeThen(binding) }
          action()
          currentContext.notifyPlugins { plugin, binding -> plugin.afterThen(binding) }
          break;
        case BehaviorStepType.WHEN:
          currentContext.notifyPlugins { plugin, binding -> plugin.beforeWhen(binding) }
          action()
          currentContext.notifyPlugins { plugin, binding -> plugin.afterWhen(binding) }
          break;
        case BehaviorStepType.GIVEN:
          currentContext.notifyPlugins { plugin, binding -> plugin.beforeGiven(binding) }
          action()
          currentContext.notifyPlugins { plugin, binding -> plugin.afterGiven(binding) }
          break;
      }


    } catch (Throwable t) { // who knows what could happen, whatever does, its a failure
      childStep.result = new Result(t)
      listener.gotResult childStep.result
    } finally {
      listener.stopStep()
    }
  }

  private def processScenario(BehaviorStep step, isRealScenario) {

    currentStep = step

    step.decodeCurrentName currentIteration

    listener.startStep(step)
    Result result;

    def processing = [BehaviorStepType.GIVEN, BehaviorStepType.WHEN, BehaviorStepType.THEN]

    try {
      if (isRealScenario)
        currentContext.notifyPlugins { plugin, binding -> plugin.beforeScenario(binding) }

      if (isRealScenario && currentContext.beforeEach)
        processScenario(currentContext.beforeEach, false)

      step.childSteps.each { childStep ->
        def oldCurrent = currentStep

        currentStep = childStep

        try {
        if (childStep.stepType == BehaviorStepType.BEHAVES_AS)
          processSharedScenarios(childStep)
        else if ( childStep.stepType == BehaviorStepType.EXTENSION_POINT )
          childStep.extensionPoint.process(step, currentContext.binding, listener)
        else if (processing.contains(childStep.stepType)) {
          processChildStep(childStep)
        } } finally {
          currentStep = oldCurrent
        }
      }

      if (step.childStepFailureResultCount == 0)
        step.result = new Result(Result.SUCCEEDED)
      else {
        step.result = new Result(Result.FAILED)
      }

      listener.gotResult step.result

      if (isRealScenario && currentContext.afterEach)
        processScenario(currentContext.afterEach, false)

    } finally {
      listener.stopStep()

      if (isRealScenario)
        currentContext.notifyPlugins { plugin, binding -> plugin.afterScenario(binding) }
    }
  }

  // chances are this will happen in a given/when/then
  def executeExtensionMethod(ExtensionPoint ex) {
    ex.process(currentStep, currentContext.binding, listener)
  }
}