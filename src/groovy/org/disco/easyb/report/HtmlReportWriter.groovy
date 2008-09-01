package org.disco.easyb.report

import groovy.xml.MarkupBuilder
import org.disco.easyb.BehaviorStep
import org.disco.easyb.exception.VerificationException
import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.listener.ResultsCollector
import org.disco.easyb.result.Result

class HtmlReportWriter implements ReportWriter {
    private String location

    HtmlReportWriter(String location) {
        this.location = location
    }


    def walkStoryChildrenForList(MarkupBuilder html, BehaviorStep step) {
        if (step.childSteps.size() == 0) {
        } else {
            if (step.stepType == BehaviorStepType.SCENARIO) {
            } else { // assumed to be story now
              html.
                xml."${step.stepType.type()}"(name: step.name, scenarios: step.scenarioCountRecursively, failedscenarios: step.failedScenarioCountRecursively, pendingscenarios: step.pendingScenarioCountRecursively) {
                    if (step.description) {
                        xml.description(step.description)
                    }
                    for (child in step.childSteps) {
                        walkStoryChildren(xml, child)
                    }
                }
            }
        }

    }

    private getRowClass(int rowNum) {
      rowNum % 2 == 0 ? 'primaryRow' : 'secondaryRow'
    }

    private getScenarioRowClass(int scenarioRowNum) {
      scenarioRowNum % 2 == 0 ? 'primaryScenarioRow' : 'secondaryScenarioRow'
    }

    private getStepStatusClass(Result stepResult) {
      switch(stepResult?.status) {
        case Result.SUCCEEDED:
          return 'stepResultSuccess'
          break
        case Result.FAILED:
          return 'stepResultFailed'
          break
        case Result.PENDING:
          return 'stepResultPending'
          break
        default:
          return ''
      }
    }

    private getBehaviorResultFailureSummaryClass(long numberOfFailures) {
      (numberOfFailures > 0) ? 'stepResultFailed' : ''
    }

    private getBehaviorResultPendingSummaryClass(long numberOfFailures) {
      (numberOfFailures > 0) ? 'stepResultPending' : ''
    }

    public void writeReport(ResultsCollector results) {

      Writer writer = new BufferedWriter(new FileWriter(new File(location)))

      def html = new MarkupBuilder(writer)

      writer << '<?xml version="1.0" encoding="UTF-8"?>\n'
      writer << '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">\n'

      html.html(xmlns:'http://www.w3.org/1999/xhtml', 'xml:lang':'en', lang:'en') {
        head {
          title('easyb-report')
          script(src:'prototype.js', type:'text/javascript', '')
          script(type:'text/javascript',
                  '''
                  function toggleScenariosForStory(storyNumber) {
                    $('scenarios_for_story_' + storyNumber).toggle();
                    return false;
                  }
                  function toggleComponentsForSpecification(specificationNumber) {
                    $('components_for_specification_' + specificationNumber).toggle();
                    return false;
                  }

                  ''')
          style(type:'text/css',
              '''
              body {
                background-color: white;
                background-position: bottom;
                background-attachment: fixed;
                color: #6F6F6F;
                font-family: 'Lucida Sans', 'Helvetica', 'Sans-serif', 'sans', serif;
                font-size: 8pt;
                line-height: 1.8em;
                height: 99%;
                padding: 0;
                margin: 0;
              }

              h1,h2,h3,h4,h5,h6 {
                color: red;
              }

              h2 {
                font-size: 1.25em;
              }

              table.componentsForSpecificationTable {
                width:100%;
              }
              table.scenariosForStoriesTable {
                width:100%;
              }

              th {
                background: #C3C3C3;
                color: white;
                font-weight: bold;
              }

              th,td {
                padding-left: 5px;
                padding-right: 5px;
              }

              tr.primaryRow {
                background: #F5F5F5;
              }

              tr.secondaryRow {
                background: #E6E6E5;
              }

              tr.primaryScenarioRow {
                background: #C3D4E5;
              }

              tr.secondaryScenarioRow {
                background: #E2EAF3;
              }
              td.stepResultFailed {
                background: #FF8080;
                color: white;
              }
              td.stepResultSuccess {
              }
              td.stepResultPending {
                background: #BABFEE;
                color: white;
              }

              tr.scenariosForStory {
                background: #F5FFF5;
              }
              tr.componentsForSpecification {
                background: #F5FFF5;
              }

              ''')
        }
        body {
          a(name:'Summary')
          h2('Summary')
          table {
            thead {
              tr {
                th('Behaviors')
                th('Failed')
                th('Pending')
                th('Time (sec)')
              }
            }
            tbody {
              tr(class:'primaryRow') {
                td(results.behaviorCount)
                td(class:getBehaviorResultFailureSummaryClass(results.failedBehaviorCount),results.failedBehaviorCount)
                td(class:getBehaviorResultPendingSummaryClass(results.pendingBehaviorCount),results.pendingBehaviorCount)
                td((results.genesisStep.storyExecutionTimeRecursively + results.genesisStep.specificationExecutionTimeRecursively)/ 1000f )
              }
            }
          }
          a(name:"StoriesSummary")
          h2('Stories Summary')
          table {
            thead {
              tr {
                th('Stories')
                th('Scenarios')
                th('Failed')
                th('Pending')
                th('Time (sec)')
              }
            }
            tbody {
              tr(class:'primaryRow') {
                td(results.genesisStep.getChildrenOfType(BehaviorStepType.STORY).size())
                td(results.scenarioCount)
                td(class:getBehaviorResultFailureSummaryClass(results.failedScenarioCount),results.failedScenarioCount)
                td(class:getBehaviorResultPendingSummaryClass(results.pendingScenarioCount),results.pendingScenarioCount)
                td(results.genesisStep.storyExecutionTimeRecursively / 1000f)
              }
            }
          }
          a(name:"SpecificationsSummary")
          h2('Specifications Summary')
          table {
            thead {
              tr {
                th('Specifications')
                th('Failed')
                th('Pending')
                th('Time (sec)')
              }
            }
            tbody {
              // TODO here we need to walk the children and spit out storyname, scenarios, failed, time
              // TODO but there is a problem since some scenarios,etc aren't in a story
                tr(class:'primaryRow') {
                  td(results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).size())
                  td(class:getBehaviorResultFailureSummaryClass(results.failedSpecificationCount), results.failedSpecificationCount)
                  td(class:getBehaviorResultPendingSummaryClass(results.pendingSpecificationCount),results.pendingSpecificationCount)
                  td(results.genesisStep.specificationExecutionTimeRecursively / 1000f)
                }
            }
          }
          a(name:"StoriesList")
          h2('Stories List')
          table {
            thead {
              tr {
                th('Story')
                th('Scenarios')
                th('Failed')
                th('Pending')
                th('Time (sec)')
              }
            }
            tbody {

              int storyRowNum = 0;
              results.genesisStep.getChildrenOfType(BehaviorStepType.STORY).each { storyStep ->
              // TODO here we need to walk the children and spit out storyname, scenarios, failed, time
              // TODO but there is a problem since some scenarios,etc aren't in a story
                int scenarioChildrenCount = storyStep.getChildrenOfType(BehaviorStepType.SCENARIO).size

                tr(class:getRowClass(storyRowNum)) {
                  td{
                    if(scenarioChildrenCount > 0) {
                      a(href:"#", onclick:"return toggleScenariosForStory(${storyRowNum});", storyStep.name)
                    } else {
                        a(storyStep.name)
                    }
                  }
                  td(storyStep.scenarioCountRecursively)
                  td(class:getBehaviorResultFailureSummaryClass(storyStep.failedScenarioCountRecursively), storyStep.failedScenarioCountRecursively)
                  td(class:getBehaviorResultPendingSummaryClass(storyStep.pendingScenarioCountRecursively), storyStep.pendingScenarioCountRecursively)
                  td(storyStep.executionTotalTimeInMillis / 1000f)
                }

                if(scenarioChildrenCount > 0) {
                  tr(id:"scenarios_for_story_${storyRowNum}", class:"scenariosForStory", style:"display:none;") {
                    td(colspan:'5') {
                      table(class:'scenariosForStoriesTable') {
                        tbody {
                          int scenarioRowNum = 0;
                          storyStep.getChildrenOfType(BehaviorStepType.SCENARIO).each { scenarioStep ->
                            tr(class:getScenarioRowClass(scenarioRowNum)) {
                              td(title:"Scenario", scenarioStep.name)
                              td(title:"Result", class:getStepStatusClass(scenarioStep.result), scenarioStep.result.status)
                              td(title:"Time (sec)", scenarioStep.executionTotalTimeInMillis / 1000f)
                            }
                            if(scenarioStep.childSteps.size > 0) {
                              scenarioStep.childSteps.each { componentStep ->
                              tr(class:"scenarioComponents") {
                                          td("${componentStep.stepType.type} ${componentStep.name}")
                                          td(class:getStepStatusClass(componentStep.result), componentStep.result?.status)
                                          td()
                                }
                              }
                            }

                          scenarioRowNum++
                          }
                        }
                      }
                    }
                  }
                }


                storyRowNum++
              }

            }
          }
          a(name:"SpecificationsList")
          h2('Specifications List')
          table {
            thead {
              tr {
                th('Name')
                th('Specifications')
                th('Failed')
                th('Pending')
                th('Time (sec)')
              }
            }
            tbody {
              int rowNum = 0;
              results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).each { specificationStep ->
              // TODO here we need to walk the children and spit out storyname, scenarios, failed, time
              // TODO but there is a problem since some scenarios,etc aren't in a story
                tr(class:getRowClass(rowNum)) {
                  td{
                    if(specificationStep.childSteps.size > 0) {
                      a(href:"#", onclick:"return toggleComponentsForSpecification(${rowNum});", specificationStep.name)
                    } else {
                        a(specificationStep.name)
                    }
                  }
                  td(specificationStep.specificationCountRecursively)
                  td(class:getBehaviorResultFailureSummaryClass(specificationStep.failedSpecificationCountRecursively), specificationStep.failedSpecificationCountRecursively)
                  td(class:getBehaviorResultPendingSummaryClass(specificationStep.pendingSpecificationCountRecursively), specificationStep.pendingSpecificationCountRecursively)
                  td(specificationStep.executionTotalTimeInMillis / 1000f)
                }
                if(specificationStep.childSteps.size > 0) {
                  tr(id:"components_for_specification_${rowNum}", class:"componentsForSpecification", style:"display:none;"){
                    td(colspan:'5') {
                      table(class:'componentsForSpecificationTable') {
                        tbody {
                          specificationStep.childSteps.each { componentStep ->
                            tr {
                              td("${componentStep.stepType.type} ${componentStep.name}")
                              td(class:getStepStatusClass(componentStep.result), componentStep.result?.status, style:'text-align:right;')
                            }
                          }
                        }
                      }
                    }
                  }
                }
                rowNum++
              }

            }
          }


        }

      }

        writer.close()
    }
}