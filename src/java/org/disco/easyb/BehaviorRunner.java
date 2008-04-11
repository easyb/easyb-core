package org.disco.easyb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.disco.easyb.domain.Behavior;
import org.disco.easyb.domain.BehaviorFactory;
import org.disco.easyb.report.Report;
import org.disco.easyb.report.TxtSpecificationReportWriter;
import org.disco.easyb.report.TxtStoryReportWriter;
import org.disco.easyb.report.XmlReportWriter;
import org.disco.easyb.listener.ExecutionListener;
import org.disco.easyb.listener.BroadcastListener;
import org.disco.easyb.listener.FailureDetector;
import org.disco.easyb.listener.ResultsCollector;

/**
 * usage is:
 * <p/>
 * java BehaviorRunner my/path/to/spec/MyStory.groovy -txtstory ./reports/story-report.txt
 * <p/>
 * You don't need to pass in the file name for the report either-- if no
 * path is present, then the runner will create a report in the current directory
 * with a default filename following this convention: easyb-<type>-report.<format>
 * <p/>
 * Multiple specifications can be passed in on the command line
 * <p/>
 * java BehaviorRunner my/path/to/spec/MyStory.groovy my/path/to/spec/AnotherStory.groovy
 */
public class BehaviorRunner {
    private List<Report> reports;
    private BroadcastListener broadcastListener;
    private ResultsCollector resultsCollector;
    private FailureDetector failureDetector;

    public BehaviorRunner(ExecutionListener executionListener) {
        this(executionListener, null);
    }

    public BehaviorRunner(ExecutionListener executionListener, List<Report> reports) {
        this.reports = addDefaultReports(reports);

        resultsCollector = new ResultsCollector();
        failureDetector = new FailureDetector();

        broadcastListener = new BroadcastListener();
        broadcastListener.registerListener(resultsCollector);
        broadcastListener.registerListener(failureDetector);
        broadcastListener.registerListener(executionListener);
    }

    /**
     * @param specs collection of files that contain the specifications
     * @throws Exception if unable to write report file
     */
    public void runBehavior(Collection<File> specs) throws Exception {

        executeBehaviors(specs);

        broadcastListener.testingComplete();

        produceReports(resultsCollector);

        if (failureDetector.failuresDetected()) {
            System.exit(-6);
        }
    }

    /**
     * @param listener Listener to receive specification events
     */
    private void produceReports(ExecutionListener listener) {

        for (Report report : reports) {
            if (report.getType().equals(Report.EASYB_TYPE)) {
                new XmlReportWriter(report, listener).writeReport();
            } else if (report.getType().equals(Report.STORY_TYPE)) {
                new TxtStoryReportWriter(report, listener).writeReport();
            } else if (report.getType().equals(Report.SPECIFICATION_TYPE)) {
                new TxtSpecificationReportWriter(report, listener).writeReport();
            }
        }
    }

    /**
     * @param behaviors Specifications to run
     * @throws IOException IO exception running groovy script
     */
    private void executeBehaviors(final Collection<File> behaviors) throws IOException {
        for (File behaviorFile : behaviors) {
            Behavior behavior = null;
            try {
                behavior = BehaviorFactory.createBehavior(behaviorFile);
            } catch (IllegalArgumentException iae) {
                System.out.println(iae.getMessage());
                System.exit(-1);
            }

            broadcastListener.behaviorFileStarting(behavior);

            BehaviorStep results = behavior.execute(broadcastListener);

            broadcastListener.behaviorFileComplete(results, behavior);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Configuration configuration = new ConsoleConfigurator().configure(args);
        if (configuration != null) {
            BehaviorRunner runner = new BehaviorRunner(new ConsoleReporter(), configuration.configuredReports);
            try {
                runner.runBehavior(getFileCollection(configuration.commandLine.getArgs()));
            }
            catch (Exception e) {
                System.err.println("There was an error running the script");
                e.printStackTrace(System.err);
                System.exit(-6);
            }
        }
    }

    private List<Report> addDefaultReports
        (List<Report> userConfiguredReports) {
        List<Report> configuredReports = new ArrayList<Report>();

        if (userConfiguredReports != null) {
            configuredReports.addAll(userConfiguredReports);
        }

        return configuredReports;
    }

    /**
     * @param paths locations of the specifications to be loaded
     * @return collection of files where the only element is the file of the spec to be run
     */
    private static Collection<File> getFileCollection
        (String[] paths) {
        Collection<File> coll = new ArrayList<File>();
        for (String path : paths) {
            coll.add(new File(path));
        }
        return coll;
    }
}
