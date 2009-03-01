package org.easyb.report

import org.easyb.util.BehaviorStepType
import org.easyb.listener.ResultsCollector

public class TxtSpecificationReportWriter extends TxtReportWriter {
    private static final String DEFAULT_LOC_NAME = "easyb-specification-report.txt";

    public TxtSpecificationReportWriter() {
        this(DEFAULT_LOC_NAME)
    }

    TxtSpecificationReportWriter(String location) {
        this.location = (location != null ? location : DEFAULT_LOC_NAME);
    }

    protected final BehaviorStepType getGenesisType() {
        return BehaviorStepType.SPECIFICATION;
    }

    protected final Writer getWriter() {
        return new BufferedWriter(new FileWriter(new File(location)));
    }

    protected final String getResultsAsText(ResultsCollector results) {
        return results.getSpecificationResultsAsText();
    }
}
