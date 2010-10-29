package org.easyb.report

import org.easyb.listener.ResultsCollector
import org.easyb.result.Result
import groovy.text.SimpleTemplateEngine
import org.easyb.util.BehaviorStepType
import org.easyb.listener.ResultsReporter

class HtmlReportHelper {

    private HtmlReportHelper() {
    }

    static getBehaviorResultFailureSummaryClass(long numberOfFailures) {
        (numberOfFailures > 0) ? 'stepResultFailed' : ''
    }

    static getBehaviorResultPendingSummaryClass(long numberOfFailures) {
        (numberOfFailures > 0) ? 'stepResultPending' : ''
    }

    static formatStoryExecutionTime(ResultsReporter results) {
        results.genesisStep.storyExecutionTimeRecursively / 1000f
    }

    static formatSpecificationExecutionTime(ResultsReporter results) {
        results.genesisStep.specificationExecutionTimeRecursively / 1000f
    }

    static formatBehaviorExecutionTime(ResultsReporter results) {
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
        String formattedElement = element.format('<br/>', '&nbsp;', BehaviorStepType.SPECIFICATION);

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
        String formattedElement = element.format('<br/>', '&nbsp;', BehaviorStepType.STORY);

        if (element.result?.failed() && !BehaviorStepType.SCENARIO.equals(element.stepType)) {
            formattedElement += "${'&nbsp;'.multiply(1)}<span style='color:#FF8080;'>[FAILURE: ${element.result?.cause()?.getMessage()}]</span>"
        }

        if (element.result?.pending() && (!BehaviorStepType.SCENARIO.equals(element.stepType)
                || element.getChildSteps().isEmpty())) {
            formattedElement += "${'&nbsp;'.multiply(1)}<span style='color:#BABFEE;'>[PENDING]</span>"
        }

        return formattedElement
    }

    static writeListDetails(ResultsReporter results, BufferedWriter reportWriter, String templateFilename) {
        InputStream genericListTemplateInputStream = HtmlReportHelper.class.getClassLoader().getResourceAsStream("reports/${templateFilename}");
        def templateBinding = ["results": results]
        def templateEngine = new SimpleTemplateEngine()
        def reportTemplate = templateEngine.createTemplate(genericListTemplateInputStream.newReader()).make(templateBinding)
        reportTemplate.writeTo(reportWriter)

    }

    static writeStoriesList(ResultsReporter results, BufferedWriter reportWriter) {
        writeListDetails(results, reportWriter, "easyb_report_stories_list.tmpl")
    }

    static writeSpecificationsList(ResultsReporter results, BufferedWriter reportWriter) {
        writeListDetails(results, reportWriter, "easyb_report_specifications_list.tmpl")
    }

    static writeSpecificationsListPlain(ResultsReporter results, BufferedWriter reportWriter) {
        writeListDetails(results, reportWriter, "easyb_report_specifications_list_plain.tmpl")
    }

    static writeStoriesListPlain(ResultsReporter results, BufferedWriter reportWriter) {
        writeListDetails(results, reportWriter, "easyb_report_stories_list_plain.tmpl")
    }

    static String formatHtmlReportElement(results, behaviourStepType) {
        def html = ''
        results.genesisStep.getChildrenOfType(behaviourStepType).each {child ->
            html += handleHtmlElement(child, behaviourStepType)
        }
        return html
    }

    static String handleHtmlElement(element, behaviourStepType) {
        if (behaviourStepType.equals(BehaviorStepType.STORY)) {
            return handleHtmlStoryElement(element)
        } else if (behaviourStepType.equals(BehaviorStepType.SPECIFICATION)) {
            return handleHtmlSpecificationElement(element)
        }
    }

    static String handleHtmlStoryElement(element) {
        def plainElement = "<br/>${formatStoryPlainElement(element)}"
        element.getChildSteps().each {
            plainElement += handleHtmlStoryElement(it)
        }
        return plainElement
    }

    static String handleHtmlSpecificationElement(element) {
        def plainElement = "<br/>${formatSpecificationPlainElement(element)}"
        element.getChildSteps().each {
            plainElement += handleHtmlSpecificationElement(it)
        }
        return plainElement
    }


}