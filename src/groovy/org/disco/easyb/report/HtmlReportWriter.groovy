package org.disco.easyb.report

import groovy.xml.MarkupBuilder
import org.disco.easyb.BehaviorStep
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


    private getLocationDir() {
      String locationDir
      if(location.indexOf("\\") != -1) {
        locationDir = location.substring(0, location.lastIndexOf("\\"))
      } else if(location.indexOf("/") != -1) {
        locationDir = location.substring(0, location.lastIndexOf("/"))
      } else {
        locationDir = "."
      }
      return locationDir
    }

    private writeResourceToDisk(String resourceLocation, resourceName) {
      InputStream inputStream = this.class.getClassLoader().getResourceAsStream(resourceLocation + File.separator + resourceName);

      String locationDir = getLocationDir()

      if(inputStream != null) {
        FileOutputStream resourceWriter = new FileOutputStream(locationDir + File.separator + resourceName)
        inputStream.eachByte { singleByte ->
          resourceWriter.write(singleByte)
        }
        resourceWriter.close()
      }


    }

    private getResourceFilename(resourceName) {
      return getLocationDir() + File.separator + resourceName
    }

    def handleSpecificationPlainElement(MarkupBuilder html, element) {
        writeSpecificationPlainElement(html, element)
        element.getChildSteps().each {
            writeSpecificationPlainElement(html, it)
        }
    }

  def writeSpecificationPlainElement(html, element) {
      switch (element.stepType) {
          case BehaviorStepType.SPECIFICATION:
            html.br()
            html.yieldUnescaped "${'&nbsp;'.multiply(2)}"
            html.yield "Specification: ${element.name}"
            break
          case BehaviorStepType.DESCRIPTION:
            html.yieldUnescaped "${'&nbsp;'.multiply(3)}"
            html.yield "${element.description}"
            html.br()
            break
          case BehaviorStepType.BEFORE:
            html.yieldUnescaped "${'&nbsp;'.multiply(4)}"
            html.yield "before ${element.name}"
              break
          case BehaviorStepType.IT:
            html.yieldUnescaped "${'&nbsp;'.multiply(4)}"
            html.yield "it ${element.name}"
              break
          case BehaviorStepType.AND:
            html.yieldUnescaped "${'&nbsp;'.multiply(4)}"
            html.yield "and"
              break
          default:
              //no op to avoid having alerts in story text
              break
      }

      if (element.result == Result.FAILED) {
        html.br()
        html.br()
        html.yield "	Failure -> ${element.name} ${element.description}"
        html.br()
        html.yield "${element.failuremessage}"
      }
      if (element.result?.pending()) {
        html.yield " [PENDING]"
      }
    html.br()
  }


    public void writeReport(ResultsCollector results) {

      Writer writer = new BufferedWriter(new FileWriter(new File(location)))

      writeResourceToDisk("resource/thirdparty/prototype", "prototype.js")
      writeResourceToDisk("resource/reports", "easyb_img01.gif")
      writeResourceToDisk("resource/reports", "easyb_img02.jpg")
      writeResourceToDisk("resource/reports", "easyb_img03.jpg")
      writeResourceToDisk("resource/reports", "easyb_img04.jpg")
      writeResourceToDisk("resource/reports", "easyb_img05.jpg")
      writeResourceToDisk("resource/reports", "easyb_img06.jpg")
      writeResourceToDisk("resource/reports", "easyb_spacer.gif")
      writeResourceToDisk("resource/reports", "easyb_report.css")


      def html = new MarkupBuilder(writer)

      writer << '<?xml version="1.0" encoding="UTF-8"?>\n'
      writer << '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">\n'

      html.html(xmlns:'http://www.w3.org/1999/xhtml', 'xml:lang':'en', lang:'en') {
        head {
          meta("http-equiv":"content-type", content:"text/html; charset=utf-8")
          title('easyb-report')
          meta(name:"keywords", content:"BDD, behavior driven development, java, java bdd, groovy, groovy bdd, groovy behavior driven development, java behavior driven development, ruby, rspec, easyb, easy bdd")
          meta(name:"description", content:"easyb is story verification framework built in the spirit of behavior driven development.")
          link(href:"easyb_report.css", rel:"stylesheet", type:"text/css")
          script(src:"prototype.js", type:'text/javascript', '')
          script(type:'text/javascript',
                  '''
                  function showOnlyContent(contentDiv) {
                    $('Summaries').hide();
                    $('StoriesList').hide();
                    $('SpecificationsList').hide();
                    $('SpecificationsListPlain').hide();
                    $(contentDiv).show();
                  }
                  function toggleScenariosForStory(storyNumber) {
                    $('scenarios_for_story_' + storyNumber).toggle();
                    return false;
                  }
                  function toggleComponentsForSpecification(specificationNumber) {
                    $('components_for_specification_' + specificationNumber).toggle();
                    return false;
                  }

                  ''')
        }
        body {
          yieldUnescaped '\n<!-- start header -->\n'
          div(id:"header") {
            h1 {
              a(href:"http://www.easyb.org") {
                span("easyb")
              }
            }
            h2 {
              yieldUnescaped "&nbsp;&nbsp; -- BDD in java can't get any easier"
            }
          }
          yieldUnescaped '\n<!-- end header -->\n'
          yieldUnescaped '\n\n<!-- start page -->\n'
          div(id:'page') {
            yieldUnescaped '\n<!-- start content -->\n'
            div(id:'content') {
              div(class:'post') {
                div(class:'entry') {
                  div(id:'Summaries') {
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
                          tr(class:'primaryRow') {
                            td(results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).size())
                            td(class:getBehaviorResultFailureSummaryClass(results.failedSpecificationCount), results.failedSpecificationCount)
                            td(class:getBehaviorResultPendingSummaryClass(results.pendingSpecificationCount),results.pendingSpecificationCount)
                            td(results.genesisStep.specificationExecutionTimeRecursively / 1000f)
                          }
                      }
                    }
                  }
                  div(id:'StoriesList', style:"display:none;") {
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
                                        if (componentStep.result?.failed()) {
                                          tr {
                                            td(colspan:'3', style:'color:red; padding-left: 1cm;') {
                                              strong(componentStep.result.cause()?.getMessage())
                                              br()
                                              for (i in 1..10) {
                                                yield "" + componentStep.result.cause()?.getStackTrace()[i]
                                                br()
                                              }
                                            }
                                          }
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
                  }
                  div(id:'SpecificationsList', style:"display:none;") {
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
                                      if (componentStep.result?.failed()) {
                                        tr {
                                          td(colspan:'2', style:'color:red; padding-left: 1cm;') {
                                            strong(componentStep.result.cause()?.getMessage())
                                            br()
                                            for (i in 1..10) {
                                              yield "" + componentStep.result.cause()?.getStackTrace()[i]
                                              br()
                                            }
                                          }
                                        }
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
                  div(id:'SpecificationsListPlain', style:"display:none;") {
                    a(name:"SpecificationsListPlain")
                    h2('Specifications List Plain')

                    def specificationCount = results.specificationCount
                    div(
                        """${(specificationCount > 1) ? "${specificationCount} specifications" : " 1 specification"}
                             (including ${results.getPendingSpecificationCount()} pending) executed
                            ${results.getFailedSpecificationCount().toInteger() > 0 ? ", but status is failure!" : " successfully"}
                            ${results.getFailedSpecificationCount().toInteger() > 0 ? " Total failures: ${results.getFailedSpecificationCount()}" : ""}
                        """)

                    div() {
                      results.genesisStep.getChildrenOfType(BehaviorStepType.SPECIFICATION).each {genesisChild ->
                        handleSpecificationPlainElement(html, genesisChild)
                      }
                    }


                  }
                }
              }
            }
            yieldUnescaped '\n<!-- end content -->\n'

            yieldUnescaped '\n\n<!-- start sidebar -->\n'
            div(id:'sidebar') {
              ul {
                li {
                  h2("Sections")
                  ul {
                    li {
                      a(href:"#", onclick:"showOnlyContent('Summaries')", "Summary")
                    }
                    li {
                      a(href:"#", onclick:"showOnlyContent('StoriesList')", "Stories List")
                    }
                    li {
                      a(href:"#", onclick:"showOnlyContent('SpecificationsList')", "Specifications List")
                    }
                    li {
                      a(href:"#", onclick:"showOnlyContent('SpecificationsListPlain')", "Specifications List Plain")
                    }
                  }
                }
              }
              div(style:"clear: both;") {
                yieldUnescaped "&nbsp;"
              }
            }
            yieldUnescaped '\n<!-- end sidebar -->\n'

          }
          yieldUnescaped '\n<!-- end page -->\n'
          div(id:'footer')
        }

      }

        writer.close()
    }
}