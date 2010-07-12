package org.easyb.report

import org.easyb.util.BehaviorStepType
import org.easyb.listener.ResultsCollector
import org.easyb.listener.ResultsReporter

class TxtStoryReportWriter extends TxtReportWriter {
    private static final String DEFAULT_LOC_NAME = "easyb-story-report.txt";

    public TxtStoryReportWriter() {
        this(DEFAULT_LOC_NAME)
    }

    public TxtStoryReportWriter(final String location) {
        this.location = (location != null ? location : DEFAULT_LOC_NAME);
    }

    protected final BehaviorStepType getGenesisType() {
        return BehaviorStepType.STORY;
    }

    protected final Writer getWriter() {
        return new BufferedWriter(new FileWriter(new File(location)));
    }

    protected final String getResultsAsText(ResultsReporter results) {
        return results.getScenarioResultsAsText();
    }

    private String getTotalPendingCountMessage(final results) {
        def messge = new StringBuffer()
        if (results.pendingScenarioCount > 0) {
            messge << (" (including ")
            messge << (results.pendingScenarioCount == 1 ? "1 pending behavior" : results.pendingScenarioCount + " pending behaviors")
            if (results.getIgnoredScenarioCount() > 0) {
                messge << " and " + (results.getIgnoredScenarioCount() == 1 ? " 1 behavior ignored" : results.getIgnoredScenarioCount() + " ignored behaviors")
            }
            messge << ")"
        }
        return messge
    }

    private String getIgnoredCount(final results) {
        if (results.pendingScenarioCount == 0) {
            return " (" + (results.getIgnoredScenarioCount() == 1 ? "1 behavior was ignored" : results.getIgnoredScenarioCount() + " behaviors were ignored") + ")"
        } else {
            return ""
        }
    }
}