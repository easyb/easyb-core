package org.easyb.report

import org.easyb.listener.ResultsCollector

/**
 * Common interface for easyb reports
 */
interface ReportWriter {
    void writeReport(ResultsCollector results)
}