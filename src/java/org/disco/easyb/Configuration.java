package org.disco.easyb;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.disco.easyb.report.ReportWriter;

public class Configuration {
    public final CommandLine commandLine;
    public final List<ReportWriter> configuredReports;

    public Configuration(final CommandLine commandLine, final List<ReportWriter> configuredReports) {
        this.commandLine = commandLine;
        this.configuredReports = configuredReports;
    }
}
