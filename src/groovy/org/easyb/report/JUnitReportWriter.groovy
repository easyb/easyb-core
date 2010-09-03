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
	private String rootPackage
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
		rootPackage = amal.configuration.junitRootPackage
		writeReport(results)
	}
	
	public void writeReport(def results) {
		BehaviorStep story = getBaseStory(results.genesisStep)
		if (!story) {
			println "STORY NOT FOUND"
			return
		}
		
		createReportDirectory()
		generatedReportFileName = reportFileNameFor(story)
		
		def writer = new BufferedWriter(new FileWriter(new File(generatedReportFileName)))
		def xml = new MarkupBuilder(writer)
		
		def scenarios = getLowestLevelScenariosFrom(story)
		def totalTests = totalTestsIn(scenarios)
		def successfulTests = totalSuccessfulTestsIn(scenarios)
		def pendingTests = results.pendingScenarioCount + results.pendingSpecificationCount
		def failingTests = results.failedScenarioCount + results.failedSpecificationCount		
		def storyClassname = stripFilenameExtensionFrom(story.storyContext.binding.storyFile.name)
		def root = rootPackageOrDefaultIfUndefined()
		
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
						testcase(time:scenarioDuration, classname:qualifiedStoryClassname, name: scenario.name) {
							if (thisIsAPending(scenario) || thisIsAnIngored(scenario)) {
								skipped()
							}
							if (thereAreFailuresInThis(scenario)) {
								failure(message: failureMessagesFrom(scenario))
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
	
	def rootPackageOrDefaultIfUndefined() {		
		(rootPackage) ? rootPackage : DEFAULT_JUNIT_ROOT_PACKAGE
	}
	
	def reportFileNameFor(def story) {
		def storyNameWithoutSpaces = story.name.replaceAll("\\s","_")
		def storyInCamelCaseAndInitialCapital = capitalizedCamelCase(storyNameWithoutSpaces)
		def root = rootPackageOrDefaultIfUndefined();
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
		scenarios.findAll {
			it.result.ignored()
		}.size()
	}
	
	def getLowestLevelScenariosFrom(BehaviorStep story) {
		def lowestLevelScenarios = []
		def immediateChildScenarios = story.getChildrenOfType(BehaviorStepType.SCENARIO)
		immediateChildScenarios.each {scenario ->
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
	
	def thisIsAPending(BehaviorStep scenario) {
		def pendingSteps = scenario.getBehaviorCountListRecursively([SCENARIO, IT, GIVEN, WHEN, THEN, AND], Result.PENDING)
		pendingSteps > 0
	}
	
	def thisIsAnIngored(BehaviorStep scenario) {
		def ignoredSteps = scenario.getBehaviorCountListRecursively([SCENARIO, IT, GIVEN, WHEN, THEN, AND], Result.IGNORED)
		ignoredSteps > 0
	}

	def thereAreFailuresInThis(BehaviorStep scenario) {
		long failureCount = failuresInThis(scenario)
		failureCount > 0
	}
	
	private long failuresInThis(BehaviorStep scenario) {
		def failingSteps = scenario.getBehaviorCountListRecursively([SCENARIO, IT, GIVEN, WHEN, THEN, AND], Result.FAILED)
		failingSteps
	}
		
	def failureMessagesFrom(BehaviorStep scenario) {
		def failures = ""
		scenario.childSteps.each {step ->
			failures += failureMessageFor(step)
		}
		return failures
	}
	
	
	def failureMessageFor(BehaviorStep step) {
		def message = ""
		if (step?.result?.failed() && step?.result?.cause) {
			message = """Step "${step.name}" -- ${step.result.cause().message}\n"""
		}
		return message
	}
	
	private BehaviorStep getBaseStory(BehaviorStep step) {
		
		if (step.stepType == BehaviorStepType.STORY) {
			return step
		} else if ( step.getChildSteps()) {
			def firstChild = step.getChildSteps().get(0)
			return getBaseStory(firstChild)
		}
		return null;
	}
}