package org.disco.easyb;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.disco.easyb.report.Report;

public class Configuration {
    public final CommandLine commandLine;
    public final List<Report> configuredReports;

    public Configuration(CommandLine commandLine, List<Report> configuredReports) {
        this.commandLine = commandLine;
        this.configuredReports = configuredReports;
    }
}
