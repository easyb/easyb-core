package org.disco.easyb.report

import groovy.xml.MarkupBuilder
import org.disco.easyb.BehaviorStep
import org.disco.easyb.exception.VerificationException
import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.listener.ResultsCollector

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

    public void writeReport(ResultsCollector results) {

      Writer writer = new BufferedWriter(new FileWriter(new File(location)))

      def html = new MarkupBuilder(writer)

      writer << '<?xml version="1.0" encoding="UTF-8"?>\r\n'
      writer << '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">\r\n'

      html.html(xmlns:'http://www.w3.org/1999/xhtml', 'xml:lang':'en', lang:'en') {
        head {
          title('easyb-report')
          style(type:'text/css',
              '''
              body {
                background-color: white;
                background-position: bottom;
                background-attachment: fixed;
                color: #6F6F6F;
                font-family: 'Lucida Sans', 'Helvetica', 'Sans-serif', 'sans';
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
                th('Time')
              }
            }
            tbody {
              tr(class:'primaryRow') {
                td(results.behaviorCount)
                td(results.failedBehaviorCount)
                td(results.pendingBehaviorCount)
                td('TODO add in the time')
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
                th('Time')
              }
            }
            tbody {
              tr(class:'primaryRow') {
                td(results.genesisStep.getChildrenOfType(BehaviorStepType.STORY).size())
                td(results.scenarioCount)
                td(results.failedScenarioCount)
                td(results.pendingScenarioCount)
                td('TODO put in the time for stories run')
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
                th('Time')
              }
            }
            tbody {
              // TODO here we need to walk the children and spit out storyname, scenarios, failed, time
              // TODO but there is a problem since some scenarios,etc aren't in a story
                tr(class:'primaryRow') {
                  td(results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).size())
                  td(results.failedSpecificationCount)
                  td(results.pendingSpecificationCount)
                  td('TODO put in time for this spec')
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
                th('Time')
              }
            }
            tbody {
              int rowNum = 0;
              results.genesisStep.getChildrenOfType(BehaviorStepType.STORY).each { storyStep ->
              // TODO here we need to walk the children and spit out storyname, scenarios, failed, time
              // TODO but there is a problem since some scenarios,etc aren't in a story
                tr(class:getRowClass(rowNum)) {
                  td(storyStep.name)
                  td(storyStep.scenarioCountRecursively)
                  td(storyStep.failedScenarioCountRecursively)
                  td(storyStep.pendingScenarioCountRecursively)
                  td('TODO put in time for this story')
                }
                rowNum++
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
                th('Time')
              }
            }
            tbody {
              int rowNum = 0;
              results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).each { specificationStep ->
              // TODO here we need to walk the children and spit out storyname, scenarios, failed, time
              // TODO but there is a problem since some scenarios,etc aren't in a story
                tr(class:getRowClass(rowNum)) {
                  td(specificationStep.name)
                  td(specificationStep.specificationCountRecursively)
                  td(specificationStep.failedSpecificationCountRecursively)
                  td(specificationStep.pendingSpecificationCountRecursively)
                  td('TODO put in time for this spec')
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