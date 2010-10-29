package org.easyb

import org.easyb.result.Result
import org.easyb.util.BehaviorStepType
import org.easyb.util.TextDecoder
import org.easyb.result.ReportingTag

public class BehaviorStep implements Serializable {
  BehaviorStepType stepType
  BehaviorStep parentStep
  Result result
  String name
  Map context

  String currentStepName //
  String description
  long executionStartTime = 0
  long executionFinishTime = 0
  Closure closure
  boolean ignore
  boolean pending
  StoryContext storyContext
  private TextDecoder textDecoder;
  private List<ReportingTag> tags;

  ArrayList<BehaviorStep> childSteps = new ArrayList<BehaviorStep>()
  ExtensionPoint extensionPoint; // if behavior step is extension point type, will have one of these


  BehaviorStep(BehaviorStepType inStepType, String inStepName, Closure closure, BehaviorStep parent) {
    stepType = inStepType
    setName( inStepName )
    this.closure = closure
    this.parentStep = parent
  }

  BehaviorStep(BehaviorStepType inStepType, String inStepName) {
    this(inStepType, inStepName, null, null)
  }

  public void setName( String name ) {
    if ( name.indexOf('#') >= 0 )
      textDecoder = new TextDecoder(name)

    this.name = name;
  }

  public void decodeCurrentName(int iteration) {
    Binding binding = storyContext.binding
    
    binding.setProperty "iterationCount", iteration
    binding.setProperty "easybStep", this

    if ( textDecoder == null ) {
      binding.setProperty "stepName", name
    } else {
      currentStepName = textDecoder.replace(binding, parentStep)
      binding.setProperty "stepName", currentStepName
    }
  }

  public String getName() {
    if ( textDecoder != null && currentStepName != null )
      return currentStepName
    else
      return name
  }

  /**
   * lazy add tags
   *
   * @param tag - a reporting tag
   */
  public void addReportingTag(ReportingTag tag) {
    if ( tags == null )
      tags = new ArrayList<ReportingTag>()

    tags.add(tag)
  }

  def cloneStep(BehaviorStep into) {
    into.description = description
    into.closure = closure
    into.name = name
  }


  def replay() {
    if ( closure != null ) {
      executionStartTime = System.currentTimeMillis()
      closure()
      executionFinishTime = System.currentTimeMillis()
    }
  }

  public List<BehaviorStep> getChildStepsSkipExecute() {
    if (childSteps.size() == 1 && childSteps[0].stepType == BehaviorStepType.EXECUTE)
    return childSteps[0].childSteps
    else
      return childSteps
  }

  public void removeChildStep(BehaviorStep step) {
    childSteps.remove(step)
    step.parentStep = null
  }

  public BehaviorStepType getStepType() {
    return stepType;
  }

  public void setParentStep(BehaviorStep inParentStep) {
    parentStep = inParentStep
  }

  def BehaviorStepType getLastChildsBehaviorStepType() {
    if ( childSteps.size() > 0 )
      return childSteps[childSteps.size()-1].stepType
    else
      return null;
  }

  def addChildStep(BehaviorStep step) {
    childSteps.add(step)
    step.parentStep = this
  }

  long getStoryExecutionTimeRecursively() {
    return getBehaviorExecutionTimeRecursively(BehaviorStepType.STORY)
  }

  long getSpecificationExecutionTimeRecursively() {
    return getBehaviorExecutionTimeRecursively(BehaviorStepType.SPECIFICATION)
  }

  long getBehaviorExecutionTimeRecursively(type) {
    long executionTime = 0

    if ((type == stepType)) {
      executionTime += (executionFinishTime - executionStartTime)
    }

    for (childStep in childSteps) {
      executionTime += childStep.getBehaviorExecutionTimeRecursively(type)
    }
    return executionTime
  }


  long getBehaviorCountListRecursively(types, resultStatus) {
    long behaviorCount = 0

    if (resultStatus == null) {
      if (types.contains(stepType)) {
        behaviorCount++
      }
    } else if ((types.contains(stepType)) && (resultStatus == result?.status)) {
      behaviorCount++
    }

    childSteps.each { childStep ->
      behaviorCount += childStep.getBehaviorCountListRecursively(types, resultStatus)
    }

    return behaviorCount
  }

  long getBehaviorCountRecursively(type, resultStatus) {
    long behaviorCount = 0

    if (resultStatus == null) {
      if (type == stepType) {
        behaviorCount++
      }
    } else if ((type == stepType) && (resultStatus == result?.status)) {
      behaviorCount++
    }

    for (childStep in childSteps) {
      behaviorCount += childStep.getBehaviorCountRecursively(type, resultStatus)
    }
    
    return behaviorCount
  }


  List<BehaviorStep> getChildrenOfType(BehaviorStepType behaviorStepType) {
    if (childSteps.size() == 1 && childSteps[0].stepType == BehaviorStepType.EXECUTE)
    return childSteps[0].childSteps.findAll {behaviorStepType.equals(it.stepType)}
    else
      return childSteps.findAll {behaviorStepType.equals(it.stepType)}
  }

  BehaviorStep getParentStep() {
    return parentStep
  }

  def setResult(inResult) {
    result = inResult
  }

  def setDescription(inDescription) {
    description = inDescription
  }

  def startExecutionTimer() {
    executionStartTime = System.currentTimeMillis();
  }

  def stopExecutionTimer() {
    executionFinishTime = System.currentTimeMillis()
  }

  public long getExecutionTotalTimeInMillis() {
    return executionFinishTime - executionStartTime
  }

  public boolean equals(Object other) {
    if (other == null) {
      return false
    }

    return name.equals(other.name)
  }

  public int hashCode() {
    return name.hashCode();
  }

  public String toString() {
    "BehaviorStep Type: ${stepType.toString()}"
  }

  /**
   * Writes the step as a formated text string.
   * @param lineSeparator , breaks the report lines
   * @param spaceSeparator , formats the behavior step padding
   * @param genesisType , BehaviorStepType.SPECIFICATION or BehaviorStepType.STORY
   *
   */
  public String format(String lineSeparator, String spaceSeparator, BehaviorStepType genesisType) {
    String formattedElement
    int softTabs = stepType.getSoftTabs(genesisType)
    String typeFormat = stepType.format(genesisType)
    String spaces = spaceSeparator.multiply(softTabs)
    switch (stepType) {
      case BehaviorStepType.STORY:
      case BehaviorStepType.SCENARIO:
      case BehaviorStepType.SPECIFICATION:
        formattedElement = "${lineSeparator}${spaces}${typeFormat} ${name}"
        break
      case BehaviorStepType.EXECUTE:
        formattedElement = ""
        break
      case BehaviorStepType.DESCRIPTION:
        formattedElement = "${spaces}${description}${genesisType.equals(BehaviorStepType.STORY) ? "" : lineSeparator}"
        break
      case BehaviorStepType.NARRATIVE:
        formattedElement = "${lineSeparator}${spaces}${description}"
        break
      case BehaviorStepType.NARRATIVE_ROLE:
      case BehaviorStepType.NARRATIVE_FEATURE:
      case BehaviorStepType.NARRATIVE_BENEFIT:
      case BehaviorStepType.BEFORE:
      case BehaviorStepType.AFTER:
      case BehaviorStepType.WHERE:
      case BehaviorStepType.BEFORE_EACH:
      case BehaviorStepType.AFTER_EACH:
      case BehaviorStepType.IT:
      case BehaviorStepType.WHEN:
      case BehaviorStepType.GIVEN:
      case BehaviorStepType.THEN:
        formattedElement = "${spaces}${typeFormat} ${name}"
        break
      case BehaviorStepType.EXTENSION_POINT:
        break
      case BehaviorStepType.AND:
        formattedElement = "${spaces}${typeFormat}"
        break
      case BehaviorStepType.BEHAVES_AS:
        formattedElement = "${spaces} behaves as ${name}"
        break
      case BehaviorStepType.SHARED_BEHAVIOR:
        formattedElement = "${spaces} shared behavior ${name}"
        break
    }
    return formattedElement;
  }
}