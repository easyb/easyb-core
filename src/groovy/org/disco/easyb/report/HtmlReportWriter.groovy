package org.disco.easyb.report

import org.disco.easyb.util.BehaviorStepType
import org.disco.easyb.listener.ResultsCollector
import org.disco.easyb.result.Result
import groovy.text.SimpleTemplateEngine

class HtmlReportWriter implements ReportWriter {
  private String location

  HtmlReportWriter(String location) {
    this.location = location
  }

  static getBehaviorResultFailureSummaryClass(long numberOfFailures) {
    (numberOfFailures > 0) ? 'stepResultFailed' : ''
  }

  static getBehaviorResultPendingSummaryClass(long numberOfFailures) {
    (numberOfFailures > 0) ? 'stepResultPending' : ''
  }

  static formatStoryExecutionTime(ResultsCollector results) {
    results.genesisStep.storyExecutionTimeRecursively / 1000f
  }

  static formatSpecificationExecutionTime(ResultsCollector results) {
    results.genesisStep.specificationExecutionTimeRecursively / 1000f
  }

  static formatBehaviorExecutionTime(ResultsCollector results) {
    (results.genesisStep.storyExecutionTimeRecursively + results.genesisStep.specificationExecutionTimeRecursively) / 1000f
  }

  static getRowClass(int rowNum) {
    rowNum % 2 == 0 ? 'primaryRow' : 'secondaryRow'
  }

  static getStepStatusClass(Result stepResult) {
    switch (stepResult?.status) {
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

  static getScenarioRowClass(int scenarioRowNum) {
    scenarioRowNum % 2 == 0 ? 'primaryScenarioRow' : 'secondaryScenarioRow'
  }

  static String formatSpecificationPlainElement(element) {
    String formattedElement
    switch (element.stepType) {
      case BehaviorStepType.SPECIFICATION:
        formattedElement = "<br/>${'&nbsp;'.multiply(2)}Specification: ${element.name}"
        break
      case BehaviorStepType.DESCRIPTION:
        formattedElement = "${'&nbsp;'.multiply(3)}${element.description}<br/>"
        break
      case BehaviorStepType.BEFORE:
        formattedElement = "${'&nbsp;'.multiply(4)}before ${element.name}"
        break
      case BehaviorStepType.IT:
        formattedElement = "${'&nbsp;'.multiply(4)}it ${element.name}"
        break
      case BehaviorStepType.AND:
        formattedElement = "${'&nbsp;'.multiply(4)}and"
        break
      default:
        formattedElement = ""
        //no op to avoid having alerts in story text
        break
    }

    if (element.result?.failed()) {
      formattedElement += "${'&nbsp;'.multiply(1)}<span style='color:#FF8080;'>[FAILURE: ${element.result?.cause()?.getMessage()}]</span>"
    }
    if (element.result?.pending()) {
      formattedElement += "<span style='color:#BABFEE;'>${'&nbsp;'.multiply(1)}[PENDING]</span>"
    }
    return formattedElement
  }

  // TODO need to escape the .name .descriptions, etc.. in case they have special chars?
  static String formatStoryPlainElement(element) {
    String formattedElement
    switch (element.stepType) {
      case BehaviorStepType.STORY:
        formattedElement = "<br/>${'&nbsp;'.multiply(2)}Story: ${element.name}"
        break
      case BehaviorStepType.DESCRIPTION:
        formattedElement = "${'&nbsp;'.multiply(3)}Description: ${element.description}"
        break
      case BehaviorStepType.NARRATIVE:
        formattedElement = "${'&nbsp;'.multiply(3)}Narrative: ${element.description}"
        break
      case BehaviorStepType.NARRATIVE_ROLE:
        formattedElement = "${'&nbsp;'.multiply(6)}As a ${element.name}"
        break
      case BehaviorStepType.NARRATIVE_FEATURE:
        formattedElement = "${'&nbsp;'.multiply(6)}I want ${element.name}"
        break
      case BehaviorStepType.NARRATIVE_BENEFIT:
        formattedElement = "${'&nbsp;'.multiply(6)}So that ${element.name}"
        break
      case BehaviorStepType.SCENARIO:
        formattedElement = "<br/>${'&nbsp;'.multiply(4)}scenario ${element.name}"
        break
      case BehaviorStepType.GIVEN:
        formattedElement = "${'&nbsp;'.multiply(6)}given ${element.name}"
        break
      case BehaviorStepType.WHEN:
        formattedElement = "${'&nbsp;'.multiply(6)}when ${element.name}"
        break
      case BehaviorStepType.THEN:
        formattedElement = "${'&nbsp;'.multiply(6)}then ${element.name}"
        break
      case BehaviorStepType.AND:
        formattedElement = "${'&nbsp;'.multiply(6)}and"
        break
      default:
        formattedElement = ""
        //no op to avoid having alerts in story text
        break
    }

    if (element.result?.failed() && !BehaviorStepType.SCENARIO.equals(element.stepType)) {
      formattedElement += "${'&nbsp;'.multiply(1)}<span style='color:#FF8080;'>[FAILURE: ${element.result?.cause()?.getMessage()}]</span>"
    }

    if (element.result?.pending() && !BehaviorStepType.SCENARIO.equals(element.stepType)) {
      formattedElement += "${'&nbsp;'.multiply(1)}<span style='color:#BABFEE;'>[PENDING]</span>"
    }

    return formattedElement
  }

  static writeListDetails(ResultsCollector results, BufferedWriter reportWriter, String templateFilename) {
    InputStream genericListTemplateInputStream = HtmlReportWriter.class.getClassLoader().getResourceAsStream("resource${File.separator}reports${File.separator}${templateFilename}");
    def templateBinding = ["results": results]
    def templateEngine = new SimpleTemplateEngine()
    def reportTemplate = templateEngine.createTemplate(genericListTemplateInputStream.newReader()).make(templateBinding)
    reportTemplate.writeTo(reportWriter)

  }

  static writeStoriesList(ResultsCollector results, BufferedWriter reportWriter) {
    writeListDetails(results, reportWriter, "easyb_report_stories_list.tmpl")
  }

  static writeSpecificationsList(ResultsCollector results, BufferedWriter reportWriter) {
    writeListDetails(results, reportWriter, "easyb_report_specifications_list.tmpl")
  }

  static writeSpecificationsListPlain(ResultsCollector results, BufferedWriter reportWriter) {
    writeListDetails(results, reportWriter, "easyb_report_specifications_list_plain.tmpl")
  }

  static writeStoriesListPlain(ResultsCollector results, BufferedWriter reportWriter) {
    writeListDetails(results, reportWriter, "easyb_report_stories_list_plain.tmpl")
  }


  public void writeReport(ResultsCollector results) {

    writeResourceToDisk("resource${File.separator}thirdparty${File.separator}prototype", "prototype.js")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img01.gif")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img02.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img03.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img04.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img05.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img06.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_spacer.gif")
    writeResourceToDisk("resource${File.separator}reports", "easyb_report.css")

    InputStream mainTemplateInputStream = this.class.getClassLoader().getResourceAsStream("resource${File.separator}reports${File.separator}easyb_report_main.tmpl");

    def outputReportBufferedWriter = new File(location).newWriter();
    def templateBinding = ["results": results, "reportWriter": outputReportBufferedWriter]
    def templateEngine = new SimpleTemplateEngine()
    def reportTemplate = templateEngine.createTemplate(mainTemplateInputStream.newReader()).make(templateBinding)
    reportTemplate.writeTo(outputReportBufferedWriter)

  }

  private writeResourceToDisk(String resourceLocation, resourceName) {
    InputStream inputStream = this.class.getClassLoader().getResourceAsStream(resourceLocation + File.separator + resourceName);

    if (inputStream != null) {
      FileOutputStream resourceWriter = new FileOutputStream(locationDir + File.separator + resourceName)
      inputStream.eachByte {singleByte ->
        resourceWriter.write(singleByte)
      }
      resourceWriter.close()
    }
  }

  private getLocationDir() {
    if (location.contains("\\")) {
      return location.substring(0, location.lastIndexOf("\\"))
    } else if (location.contains("/")) {
      return location.substring(0, location.lastIndexOf("/"))
    } else {
      return "."
    }
  }

}