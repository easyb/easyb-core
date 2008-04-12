package org.disco.easyb.report

import org.disco.easyb.listener.ResultsCollector

/**
 * Common interface for easyb reports
 */
interface ReportWriter {
  void writeReport(ResultsCollector results)
}