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

  long getScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, null)
  }

  long getIgnoredScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, Result.IGNORED)
  }

  long getInReviewScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, Result.IN_REVIEW)
  }

  long getPendingScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, Result.PENDING)
  }

  long getFailedScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, Result.FAILED)
  }

  long getSuccessScenarioCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, Result.SUCCEEDED)
  }

  long getSpecificationCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.IT, null)
  }

  long getPendingSpecificationCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.IT, Result.PENDING)
  }

  long getFailedSpecificationCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.IT, Result.FAILED)
  }

  long getSuccessSpecificationCountRecursively() {
    return getBehaviorCountRecursively(BehaviorStepType.IT, Result.SUCCEEDED)
  }


  long getBehaviorCountRecursively() {
    return (getBehaviorCountListRecursively(BehaviorStepType.grossCountableTypes, Result.SUCCEEDED) +
        getBehaviorCountListRecursively(BehaviorStepType.grossCountableTypes, Result.FAILED) )
  }



  long getPendingBehaviorCountRecursively() {
    return getBehaviorCountListRecursively(BehaviorStepType.grossCountableTypes, Result.PENDING)
  }

  long getFailedBehaviorCountRecursively() {
    return getBehaviorCountListRecursively(BehaviorStepType.grossCountableTypes, Result.FAILED)
  }

  long getSuccessBehaviorCountRecursively() {
    return getBehaviorCountListRecursively(BehaviorStepType.grossCountableTypes, Result.SUCCEEDED)
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

  // TODO refactor into a getStepCount that can take the type its looking for (Fail, pass, pending)
  long getStepPendingCount() {
    return result != null && result.pending() ? 1 : 0
  }

  long getChildStepPendingResultCount() {
    if (childSteps.size() == 0) {
      return getStepPendingCount()
    } else {
      long childStepPending = 0
      for (childStep in childSteps) {
        childStepPending += childStep.getChildStepPendingResultCount()
      }
      return childStepPending + getStepPendingCount()
    }
  }


  long getStepInReviewCount() {
    return result != null && result.inReview() ? 1 : 0
  }


  long getChildStepInReviewResultCount() {
    if (childSteps.size() == 0) {
      return getStepInReviewCount()
    } else {
      long childStepInReview = 0
      for (childStep in childSteps) {
        childStepInReview += childStep.getChildStepInReviewResultCount()
      }
      return childStepInReview + getStepInReviewCount()
    }
  }


  long getStepIgnoredCount() {
    return result != null && result.ignored() ? 1 : 0
  }


  long getChildStepIgnoredResultCount() {
    if (childSteps.size() == 0) {
      return getStepIgnoredCount()
    } else {
      long childStepIgnored = 0
      for (childStep in childSteps) {
        childStepIgnored += childStep.getChildStepIgnoredResultCount()
      }
      return childStepIgnored + getStepIgnoredCount()
    }
  }


  long getStepSuccessCount() {
    return result != null && result.succeeded() ? 1 : 0
  }

  long getChildStepSuccessResultCount() {
    if (childSteps.size() == 0) {
      return getStepSuccessCount()
    } else {
      long childStepSuccess = 0
      for (childStep in childSteps) {
        childStepSuccess += childStep.getChildStepSuccessResultCount()
      }
      return childStepSuccess + getStepSuccessCount()
    }
  }


  long getStepFailureCount() {
    return result != null && result.failed() ? 1 : 0
  }

  public long getChildStepFailureResultCount() {
    if (childSteps.size() == 0) {
      return getStepFailureCount()
    } else {
      long childStepFailures = 0
      for (childStep in childSteps) {
        childStepFailures += childStep.getChildStepFailureResultCount()
      }
      return childStepFailures + getStepFailureCount()
    }
  }

  long getStepResultCount() {
    return result != null ? 1 : 0
  }

  long getChildStepResultCount() {
    if (childSteps.size() == 0) {
      return getStepResultCount()
    } else {
      long childStepResults = 0
      for (childStep in childSteps) {
        childStepResults += childStep.getChildStepResultCount()
      }
      return childStepResults + getStepResultCount()
    }
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