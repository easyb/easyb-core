package org.easyb.bdd.reporting.junit

import org.easyb.report.JUnitReportWriter
import org.easyb.BehaviorRunner
import org.easyb.Configuration
import org.easyb.exception.VerificationException

import static org.easyb.BehaviorRunner.getBehaviors

def junitReport

scenario "creating a JUnit report with one scenario", {
    given "a scenario", {
        spec = """
      scenario "test scenario 1", {
        given "some context"
        when "something happens"
        then "something should occur", {
          true.shouldBe true
        }
      }
    """
    }
    when "we run the tests and generate a JUnit report", {
        junitReport = generateJUnitReportFrom(spec)
    }
    then "the report should be a valid JUnit report with one test case", {

        junitReport.shouldHave "<testsuite tests='1' results='1' failures='0' disabled='0' errors='0'"
        junitReport.shouldHave "<testcase"
        junitReport.shouldHave "name='test scenario 1'"
        junitReport.shouldHave "</testsuite>"
    }
}

scenario "creating a JUnit report with a failing scenario", {
    given "a failing scenario", {
        spec = """
      scenario "test scenario 1", {
        given "some context"
        when "something happens"
        then "something should occur", {
          true.shouldBe false
        }
      }
    """
    }
    when "we run the tests and generate a JUnit report", {
        junitReport = generateJUnitReportFrom(spec)
    }
    then "the report should be a valid JUnit report with one failing test case", {

        junitReport.shouldHave "<testsuite tests='1' results='0' failures='1' disabled='0' errors='0'"
        junitReport.shouldHave "<testcase"
        junitReport.shouldHave "name='test scenario 1'"
        junitReport.shouldHave "<failure message='Step \"something should occur\" -- expected false but was true"
        junitReport.shouldHave "</testsuite>"
    }
}


scenario "creating a JUnit report with a scenario that throws an exception", {
    given "a failing scenario that throws an exception", {
        spec = """
      scenario "test scenario 1", {
        given "some context", {
           name = null
        }
        when "something happens", {
           name.size()
        }
        then "something should occur", {
          true.shouldBe false
        }
      }
    """
    }
    when "we run the tests and generate a JUnit report", {
        junitReport = generateJUnitReportFrom(spec)
    }
    then "the report should be a valid JUnit report with one error test case", {

        junitReport.shouldHave "<testsuite tests='1' results='0' failures='1' disabled='0' errors='0'"
        junitReport.shouldHave "<testcase"
        junitReport.shouldHave "name='test scenario 1'"
        junitReport.shouldHave "<failure"
        junitReport.shouldHave "</testsuite>"
    }
}

scenario "creating a JUnit report with failures in several steps of a story", {
    given "a scenario with mutiple failing steps", {
        spec = """
      scenario "test scenario 3", {
        given "some context"
        when "something happens"
        then "something should occur", {
          true.shouldBe false
        }
        and "something else should occur", {
          true.shouldBe true
        }
        then "some other thing should occur", {
          x = 1
          x.shouldBe 2
        }
      }
    """
    }
    when "we run the tests and generate a JUnit report", {
        junitReport = generateJUnitReportFrom(spec)
    }
    then "the report should be a valid JUnit report with one failing story", {

        junitReport.shouldHave "<testsuite tests='1' results='0' failures='1' disabled='0' errors='0'"
        junitReport.shouldHave "<testcase"
        junitReport.shouldHave "</testsuite>"
    }
    and "all the failing test steps should appear in the failure message", {

        junitReport.shouldHave "<failure message='Step \"something should occur\" -- expected false but was true"
        junitReport.shouldHave "Step \"some other thing should occur\" -- expected 2 but was 1"
    }

}
    
     
scenario "Pending stories should be marked as skipped", {
    given "a scenario with pending steps", {
        spec = """
      scenario "test scenario 1", {
        given "some context"
        when "something happens"
        then "something should occur"
      }
    """
    }
    when "we run the tests and generate a JUnit report", {
        junitReport = generateJUnitReportFrom(spec)
    }
    then "the report should be a valid JUnit report with one skipped story", {

        junitReport.shouldHave "<testsuite tests='1' results='0' failures='0' disabled='1' errors='0'"
        junitReport.shouldHave "<testcase"
        junitReport.shouldHave "<skipped />"
        junitReport.shouldHave "</testsuite>"
    }

}

scenario "Partially implemented stories should be marked as skipped", {
    given "a scenario with pending and implemented steps", {
        spec = """
      scenario "test scenario 1", {
        given "some context"
        when "something happens", {
           x = 1
        }
        then "something should occur"
      }
    """
    }
    when "we run the tests and generate a JUnit report", {
        junitReport = generateJUnitReportFrom(spec)
    }
    then "the report should be a valid JUnit report with one skipped story", {

        junitReport.shouldHave "<testsuite tests='1' results='0' failures='0' disabled='1' errors='0'"
        junitReport.shouldHave "<testcase"
        junitReport.shouldHave "<skipped />"
        junitReport.shouldHave "</testsuite>"
    }

}


scenario "creating a JUnit report with one specification", {
    given "a specification", {
        spec = """
            before "initialize zipcodevalidator instance", {
                zipvalidate = new ZipCodeValidator();
            }

            it "should deny invalid zip codes", {
                ["221o1", "2210", "22010-121o"].each {zip ->
                    zipvalidate.validate(zip).is false
                }
            }

            it "should accept valid zip codes", {
                ["22101", "22100", "22010-1210"].each {zip ->
                    zipvalidate.validate(zip).shouldBe true
                }
            }"""
    }
    when "we run the tests and generate a JUnit report"
    then "the report should be a valid JUnit report with one test case"
}



scenario "creating a JUnit report with  nested scenarios", {
    given "a scenario with nested scenarios", {
        spec = """
                scenario "parent scenario", {

                  scenario "child scenario", {

                    then "1 should equal 1", {
                      1.shouldEqual 1
                    }

                  }
                }"""
    }
    when "we run the tests and generate a JUnit report", {
        junitReport = generateJUnitReportFrom(spec)
    }
    then "the report should contain test cases for the child scenarios", {
        junitReport.shouldHave "name='child scenario'"
    }
    and "parent scenarios are included in the test count at this stage", {
        junitReport.shouldHave "<testsuite tests='2' results='2' failures='0' disabled='0' errors='0'"
    }
}



scenario "creating a JUnit report with multiple nested scenarios", {
	given "a scenario with nested scenarios", {
		spec = """
				scenario "parent scenario", {

				  scenario "child scenario 1", {

					then "1 should equal 1", {
					  1.shouldEqual 1
					}
				  }
				  scenario "child scenario 2", {

					then "1 should equal 1", {
					  1.shouldEqual 1
					}

				  }
				}"""
	}
	when "we run the tests and generate a JUnit report", {
		junitReport = generateJUnitReportFrom(spec)
	}
	then "the report should contain test cases for the child scenarios", {
		junitReport.shouldHave "name='child scenario 1'"
		junitReport.shouldHave "name='child scenario 2'"
	}
	and "parent scenarios are included in the test count at this stage", {
		junitReport.shouldHave "<testsuite tests='3' results='3' failures='0' disabled='0' errors='0'"
	}
}




scenario "creating a JUnit report with nested scenarios with a failure", {
	given "a scenario with nested scenarios including a failure", {
		spec = """
				scenario "parent scenario", {

				  scenario "child scenario 1", {

					then "1 should equal 1", {
					  1.shouldEqual 1
					}
				  }
				  scenario "child scenario 2", {

					then "1 should equal 1", {
					  1.shouldEqual 2
					}

				  }
				}"""
	}
	when "we run the tests and generate a JUnit report", {
		junitReport = generateJUnitReportFrom(spec)
	}
	then "the report should contain test cases for the child scenarios", {
		junitReport.shouldHave "name='child scenario 1'"
		junitReport.shouldHave "name='child scenario 2'"
	}
	and "only the failing scenario is counted as a failure", {
		junitReport.shouldHave "<testsuite tests='3' results='2' failures='1' disabled='0' errors='0'"
	}
}

scenario "creating a JUnit report with one scenario", {
	given "a scenario", {
		spec = """
	  scenario "test scenario 1", {
		given "some context"
		when "something happens"
		then "something should occur", {
		  true.shouldBe true
		}
	  }
	"""
	}
	when "we run the tests and generate a JUnit report", {
		junitReport = generateJUnitReportFrom(spec)
	}
	then "the report should be a valid JUnit report with one test case", {

		junitReport.shouldHave "<testsuite tests='1' results='1' failures='0' disabled='0' errors='0'"
		junitReport.shouldHave "<testcase"
		junitReport.shouldHave "name='test scenario 1'"
		junitReport.shouldHave "</testsuite>"
	}
}

scenario "Including the classname in the JUnit reports", {
	given "a scenario in a file", {
		storyPath = new File("behavior/groovy/org/easyb/bdd/story/stack/EmptyStackStory.groovy")
	}
	when "we run the tests and generate a JUnit report", {
		junitReport = generateJUnitReportFromStoryIn(storyPath)
	}
	then "the testsuite name should have the story name and the default easyb root package of 'behavior'", {
		junitReport.shouldHave "name='behavior.EmptyStackStory'"
	}
}

scenario "Including the unqualified classname in each test case  in the JUnit reports", {
	given "a scenario in a file", {
		storyPath = new File("behavior/groovy/org/easyb/bdd/story/stack/EmptyStackStory.groovy")
	}
	when "we run the tests and generate a JUnit report", {
		junitReport = generateJUnitReportFromStoryIn(storyPath)
		println "JUNIT REPORT: $junitReport"
	}
	then "the testcase classname should have the story name and the default easyb root package of 'behavior'", {
		junitReport.shouldHave "classname='behavior.EmptyStackStory'"
	}
}

scenario "Configuring the root package name in the JUnit reports", {
	given "a scenario in a file", {
//		storyPath = new File("behavior/groovy/org/easyb/bdd/story/stack/EmptyStackStory.groovy")
	}
	and "the root package name configured as 'easyb'", {
//		configuration = [junitRootPackage:'easyb'] as Configuration
	}
	when "we run the tests and generate a JUnit report", {
//		junitReport = generateJUnitReportFromStoryIn(storyPath, configuration)
	}
	then "the testcase classname should have the story name and the easyb root package of 'easyb'", {
//		junitReport.shouldHave "classname='easyb.EmptyStackStory'"
	}
}

def generateJUnitReportFromStoryIn(def specFile) {
	generateJUnitReportFromStoryIn(specFile, new Configuration())
}

def generateJUnitReportFromStoryIn(def specFile, def configuration) {
	def runner = new BehaviorRunner(configuration)

	try {
		runner.runBehaviors(getBehaviors(specFile.absolutePath))
	} catch (VerificationException expected) {
	}
	def results = runner.resultsAmalgamator.resultsReporter
	def report = new JUnitReportWriter()
	report.writeReport(results)
	def reportFile = report.getGeneratedReportFileName()
	filetext = new File(reportFile).getText()
	return filetext;
}

def generateJUnitReportFrom(def spec) {
    specFile = File.createTempFile('EasybTest', '.story')
    specFile.deleteOnExit()
    specFile.write(spec)
	generateJUnitReportFromStoryIn(specFile)
}