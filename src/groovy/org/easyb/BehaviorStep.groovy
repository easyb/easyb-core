package org.easyb

import org.easyb.util.BehaviorStepType
import org.easyb.result.Result

class BehaviorStep implements Serializable {
    BehaviorStepType stepType
    BehaviorStep parentStep
    Result result
    String name
    String description
    long executionStartTime = 0
    long executionFinishTime = 0

    ArrayList<BehaviorStep> childSteps = new ArrayList()

    BehaviorStep(BehaviorStepType inStepType, String inStepName) {
        stepType = inStepType
        name = inStepName
    }

    public List<BehaviorStep> getChildStepsSkipExecute() {
    	if ( childSteps.size() == 1 && childSteps[0].stepType == BehaviorStepType.EXECUTE )
        return childSteps[0].childSteps
      else
        return childSteps
    }

    public BehaviorStepType getStepType() {
      return stepType;
    }

    public void setParentStep(BehaviorStep inParentStep) {
        parentStep = inParentStep
    }

    def addChildStep(BehaviorStep step) {
        childSteps.add(step)
    }

    long getScenarioCountRecursively() {
        return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, null)
    }

    long getIgnoredScenarioCountRecursively() {
        return getBehaviorCountRecursively(BehaviorStepType.SCENARIO, Result.IGNORED)
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
        return ((getSpecificationCountRecursively() + getScenarioCountRecursively()) -
                getIgnoredScenarioCountRecursively())
    }

    long getPendingBehaviorCountRecursively() {
        return getPendingSpecificationCountRecursively() + getPendingScenarioCountRecursively()
    }

    long getFailedBehaviorCountRecursively() {
        return getFailedSpecificationCountRecursively() + getFailedScenarioCountRecursively()
    }

    long getSuccessBehaviorCountRecursively() {
        return getSuccessSpecificationCountRecursively() + getSuccessScenarioCountRecursively()
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
    	if ( childSteps.size() == 1 && childSteps[0].stepType == BehaviorStepType.EXECUTE )
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
                formattedElement = "${spaces}${description}"
                break
            case BehaviorStepType.NARRATIVE_ROLE:
            case BehaviorStepType.NARRATIVE_FEATURE:
            case BehaviorStepType.NARRATIVE_BENEFIT:
            case BehaviorStepType.BEFORE:
            case BehaviorStepType.AFTER:
            case BehaviorStepType.IT:
            case BehaviorStepType.WHEN:
            case BehaviorStepType.GIVEN:
            case BehaviorStepType.THEN:
                formattedElement = "${spaces}${typeFormat} ${name}"
                break
            case BehaviorStepType.AND:
                formattedElement = "${spaces}${typeFormat}"
                break
        }
        return formattedElement;
    }
}