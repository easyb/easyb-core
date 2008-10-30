package org.disco.easyb;

import org.disco.easyb.report.ReportWriter;

import java.util.List;

public class Configuration {
    private final String[] filePaths;
    private final List<ReportWriter> configuredReports;
    private boolean stackTraceOn = false;

    public Configuration(final String[] filePaths, final List<ReportWriter> configuredReports) {
        this.filePaths = filePaths;
        this.configuredReports = configuredReports;
    }

    public Configuration(final String[] filePaths, final List<ReportWriter> configuredReports,
                         boolean stackTraceOn) {
        this(filePaths, configuredReports);
        this.stackTraceOn = stackTraceOn;
    }

    public String[] getFilePaths() {
        return this.filePaths;
    }

    public List<ReportWriter> getConfiguredReports() {
        return this.configuredReports;
    }

    public boolean isStackTraceOn() {
        return this.stackTraceOn;
    }
}
