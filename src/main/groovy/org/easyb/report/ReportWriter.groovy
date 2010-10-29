package org.easyb.report

import org.easyb.listener.ResultsAmalgamator

/**
 * Common interface for easyb reports
 */
interface ReportWriter {
  void writeReport(ResultsAmalgamator results)
}