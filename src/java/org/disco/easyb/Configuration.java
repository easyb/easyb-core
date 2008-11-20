package org.disco.easyb;

import org.disco.easyb.report.ReportWriter;

import java.util.List;

public class Configuration {
    private final String[] filePaths;
    private final List<ReportWriter> configuredReports;
    private boolean stackTraceOn = false;
    private boolean filteredStackTraceOn = false;

    public Configuration(final String[] filePaths, final List<ReportWriter> configuredReports) {
        this.filePaths = filePaths;
        this.configuredReports = configuredReports;
    }

    public Configuration(final String[] filePaths, final List<ReportWriter> configuredReports,
                         final boolean stackTraceOn) {
        this(filePaths, configuredReports);
        this.stackTraceOn = stackTraceOn;
    }
    //yeesh this is odd, think of a better way...
    public Configuration(final String[] filePaths, final List<ReportWriter> configuredReports,
                         final boolean stackTraceOn, final boolean filteredStackTraceOn) {
        this(filePaths, configuredReports);
        this.stackTraceOn = stackTraceOn;
        this.filteredStackTraceOn = filteredStackTraceOn;
    }

    public String[] getFilePaths() {
        return this.filePaths;
    }

    public List<ReportWriter> getConfiguredReports() {
        return this.configuredReports;
    }


    public ConsoleReporter getConsoleReporter() {
        if(this.stackTraceOn){
            if(this.filteredStackTraceOn){
                return new FilteredStackTraceConsoleReporter();
            }else{
                return new StackTraceConsoleReporter();
            }
        }else if(this.filteredStackTraceOn){
                return new FilteredStackTraceConsoleReporter();
        }else{
            return new ConsoleReporter();
        }
    }
}
