package org.easyb;

import org.easyb.listener.ConsoleReporterListener;
import org.easyb.report.ReportWriter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Configuration {
    private final String[] filePaths;
    private final List<ReportWriter> configuredReports;
    private boolean stackTraceOn = false;
    private boolean filteredStackTraceOn = false;
    private String extendedStoryClass;
    private boolean parallel = false;
    private String failureFile;
    private boolean isFailureFile = false;
    private String[] tags;
    private String junitRootPackage;

    public Configuration() {
        this(new String[]{}, Collections.<ReportWriter>emptyList());
    }

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

    public Configuration(final String[] filePaths, final List<ReportWriter> configuredReports,
                         final boolean stackTraceOn, final boolean filteredStackTraceOn,
                         final String extendedStoryClassName, final boolean parallel) {
        this(filePaths, configuredReports, stackTraceOn, filteredStackTraceOn);
        this.extendedStoryClass = extendedStoryClassName;
        this.parallel = parallel;
    }

    public Configuration(final String[] filePaths, final List<ReportWriter> configuredReports,
                         final boolean stackTraceOn, final boolean filteredStackTraceOn,
                         final String extendedStoryClassName, final boolean parallel, final boolean isFailureFile,
                         final String failureFile) {
        this(filePaths, configuredReports, stackTraceOn, filteredStackTraceOn, extendedStoryClassName, parallel);
        this.failureFile = failureFile;
        this.isFailureFile = isFailureFile;
    }

    public Configuration(final String[] filePaths, final List<ReportWriter> configuredReports,
                         final boolean stackTraceOn, final boolean filteredStackTraceOn,
                         final String extendedStoryClassName, final boolean parallel, final boolean isFailureFile,
                         final String failureFile, final String[] tags, String rootPackage) {
        this(filePaths, configuredReports, stackTraceOn, filteredStackTraceOn,
                extendedStoryClassName, parallel, isFailureFile, failureFile);
        this.tags = tags;
        this.junitRootPackage = rootPackage;
    }

    public String[] getTags(){
        return this.tags;
    }

    public String getExtendedStoryClass() {
        return extendedStoryClass;
    }

    public String[] getFilePaths() {
        return this.filePaths;
    }

    public List<ReportWriter> getConfiguredReports() {
        return this.configuredReports;
    }

    public boolean isParallel() {
        return this.parallel;
    }
    
    public String getJunitRootPackage() {
    	return junitRootPackage;
    }

    public ConsoleReporterListener getConsoleReporter() {
        if (this.stackTraceOn) {
            if (this.filteredStackTraceOn) {
                return new FilteredStackTraceConsoleReporter();
            } else {
                return new StackTraceConsoleReporter();
            }
        } else if (this.filteredStackTraceOn) {
            return new FilteredStackTraceConsoleReporter();
        } else if (this.isFailureFile) {
            return new FailureFileConsoleReporter(this.failureFile);
        } else {
            return new ConsoleReporterListener();
        }
    }

    public ExecutorService getExecutor() {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

        if (parallel) {
            return new ThreadPoolExecutor(10, 10, 60L, SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());
        } else {
            return new ThreadPoolExecutor(1, 1, 60L, SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());
        }
    }
}
