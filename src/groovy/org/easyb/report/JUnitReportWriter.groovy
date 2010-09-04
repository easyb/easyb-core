package org.easyb.report

import groovy.xml.MarkupBuilder

import org.easyb.BehaviorStep
import org.easyb.listener.ResultsCollector
import org.easyb.util.BehaviorStepType
import org.easyb.result.Result
import org.easyb.exception.VerificationException
import org.easyb.listener.ResultsAmalgamator
import org.easyb.listener.ResultsReporter
import static org.easyb.util.BehaviorStepType.*

/**
 * Write easyb test results as a JUnit report.
 * Pending tests are reported as skipped.
 * Currently easyb does not record the time taken for stories, so the time iz always zero.
 */
class JUnitReportWriter implements ReportWriter {
	private String location
	private String generatedReportFileName
	private static final String DEFAULT_LOCATION = "target/reports/junit";
	private static final String DEFAULT_JUNIT_ROOT_PACKAGE = "behavior";
	
	public JUnitReportWriter() {
		this(DEFAULT_LOCATION)
	}
	
	public getGeneratedReportFileName() {
		return generatedReportFileName
	}
	
	JUnitReportWriter(String location) {
		this.location = (location != null ? location : DEFAULT_LOCATION);
	}
	
	def void writeReport(ResultsAmalgamator amal) {
		ResultsReporter results = amal.getResultsReporter()
		writeReport(results)
	}
	
	public void writeReport(def results) {
		createReportDirectory()
		writeAReportForEachStoryOrSpecificationIn(results)
	}
	
	private writeAReportForEachStoryOrSpecificationIn(results) {
		results.genesisStep.childSteps.each {
			story -> writeReportFor(story)
		}
	}
	
	private writeReportFor(story) {
		generatedReportFileName = reportFileNameFor(story)
		println "Writing JUnit report to ${generatedReportFileName}"
		
		def writer = new BufferedWriter(new FileWriter(new File(generatedReportFileName)))
		def xml = new MarkupBuilder(writer)
		
		def scenarios = getLowestLevelScenariosFrom(story)
		def totalTests = totalTestsIn(scenarios)
		def successfulTests = totalSuccessfulTestsIn(scenarios)
		def pendingTests = totalPendingTestsIn(scenarios)// results.pendingScenarioCount + results.pendingSpecificationCount
		def failingTests = totalFailingTestsIn(scenarios) //results.failedScenarioCount + results.failedSpecificationCount	
		
		def storyClassname = stripFilenameExtensionFrom(story.context.sourceFile.name)
		def root = DEFAULT_JUNIT_ROOT_PACKAGE
		def qualifiedStoryClassname = "${root}.${storyClassname}"
		def totalStoryDuration = story.executionTotalTimeInMillis / 1000
		
		xml.testsuite(tests:totalTests, 
				results:successfulTests, 
				failures:failingTests, 
				disabled: pendingTests,
				errors: 0, 
				time:totalStoryDuration, 
				name:qualifiedStoryClassname)  {
					
					scenarios.each { scenario ->
						def scenarioDuration = scenario.executionTotalTimeInMillis / 1000
						testcase(classname:qualifiedStoryClassname, name: scenario.name, time:scenarioDuration) {
							if (thisIsAPending(scenario) || thisIsAnIgnored(scenario)) {
								skipped()
							}
							if (thereAreFailuresInThis(scenario)) {
								failure(message: failureMessagesFrom(scenario), scenarioDetailsFrom(scenario))
							}
						}
					}
				}
		
		writer.close()
	}
	
	def createReportDirectory() {
		new File(location).mkdirs()
	}
	
	
	def stripFilenameExtensionFrom(String filename) {
		int dot=filename.lastIndexOf('.');
		if (dot) {
			filename = filename.substring(0,dot);
		}
		return filename
	}
	
	def reportFileNameFor(def story) {
		def storyNameWithoutSpaces = story.name.replaceAll("\\s","_")
		def storyInCamelCaseAndInitialCapital = capitalizedCamelCase(storyNameWithoutSpaces)
		def root = DEFAULT_JUNIT_ROOT_PACKAGE;
		"${location}/TEST-${root}.${storyInCamelCaseAndInitialCapital}.xml"
	} 
	
	
	def capitalizedCamelCase(columnName) {
		def camelCase = columnName.toLowerCase().split('_')
		  .collect { it.substring(0,1).toUpperCase() + it.substring(1) }.join('')
	 }
	  
	def totalTestsIn(def scenarios) {
		scenarios.size()
	}
	
	def totalSuccessfulTestsIn(def scenarios) {
		def success = scenarios.findAll { 
			it.result.succeeded()
		}.size()
	}
	
	def totalFailingTestsIn(def scenarios) {
		scenarios.findAll {
			it.result.failed()
		}.size()
	}
	
	def totalPendingTestsIn(def scenarios) {
		scenarios.findAll { step ->
			(thisIsAPending(step) || thisIsAnIgnored(step))
 		}.size()
	}
	
	def getLowestLevelScenariosFrom(BehaviorStep step) {
		def lowestLevelScenarios = []
		
		if ((step.stepType == BehaviorStepType.SCENARIO) || (step.stepType == BehaviorStepType.IT)) {
			lowestLevelScenarios << step
		}
		def immediateChildScenarios = step.getChildrenOfType(BehaviorStepType.SCENARIO)
		def immediateChildSpecItems = step.getChildrenOfType(BehaviorStepType.IT)
		
		def immediateChildren = immediateChildScenarios + immediateChildSpecItems
		
		immediateChildren.each {scenario ->
			def childScenarios = getLowestLevelScenariosFrom(scenario)
			if (childScenarios) {
				lowestLevelScenarios = lowestLevelScenarios.plus(childScenarios)
			} else {
				lowestLevelScenarios << scenario
			}
		}
		return lowestLevelScenarios
	}
	
	def qualified(def scenarioName) {
		String dispScenarioName = scenarioName.replaceAll(/\s/,"-")
		"org.easyb.${dispScenarioName}"
	}
	
	def thisIsAPending(BehaviorStep step) {
		def pendingChildSteps = step.getBehaviorCountListRecursively([SCENARIO, IT, GIVEN, WHEN, THEN, AND], Result.PENDING)
		(step.result == Result.PENDING) || (pendingChildSteps > 0)
	}
	
	def thisIsAnIgnored(BehaviorStep step) {
		def ignoredChildSteps = step.getBehaviorCountListRecursively([SCENARIO, IT, GIVEN, WHEN, THEN, AND], Result.IGNORED)
		(step.result == Result.IGNORED) || (ignoredChildSteps > 0)
	}

	def thereAreFailuresInThis(BehaviorStep step) {
		failuresInThis(step)
	}
	
	private long failuresInThis(BehaviorStep step) {
		def failingSteps = step.getBehaviorCountListRecursively([SCENARIO, IT, GIVEN, WHEN, THEN, AND], Result.FAILED)
		if (step.result == Result.FAILED)  {
			failingSteps++
		}
		failingSteps
	}
		
	def failureMessagesFrom(BehaviorStep scenario) {
		def failures = ""
		scenario.childSteps.each {step ->
			if (failures) {
				failures += "; "
			}
			failures += failureMessageFor(step)
		}
		return failures
	}
	
	
	def failureMessageFor(BehaviorStep step) {
		def message = ""
		if (step?.result?.failed() && step?.result?.cause) {
			message = """Step "${step.name}" -- ${step.result.cause().message}"""
		}
		return message
	}
	
	private List<BehaviorStep> getBaseStories(BehaviorStep step) {
		
		if (step.stepType in [BehaviorStepType.STORY, BehaviorStepType.SPECIFICATION]) {
			return step
		} else if ( step.getChildSteps()) {
			def firstChild = step.getChildSteps().get(0)
			return getBaseStory(firstChild)
		}
		return null;
	}
	
	def scenarioDetailsFrom(BehaviorStep step) {
		def behaviorType = getGenesisType(step)
		def topLevelStep = getScenarioStep(step)
		describeEachStepDownFrom(topLevelStep,behaviorType)
	}
		
	def describeEachStepDownFrom(BehaviorStep step, BehaviorStepType genesisType) {
		def description = step.format('\n', ' ', genesisType)
		step.childSteps.each { childStep ->		
			if (description) {
				description += '\n'
			}
			description += describeEachStepDownFrom(childStep, genesisType)
		}
		return description
	}
	
	def getScenarioStep(BehaviorStep step) {
		if (step.stepType in [BehaviorStepType.SCENARIO, BehaviorStepType.IT]) {
			step
		} else {
			getScenarioStep(step.parentStep)
		}
	}

	def getGenesisType(BehaviorStep step) {
		if (step.stepType in [BehaviorStepType.STORY, BehaviorStepType.SPECIFICATION]) {
			step.stepType
		} else {
			getGenesisType(step.parentStep)
		}
	}
}