package org.disco.easyb;

import org.disco.easyb.domain.Behavior;
import org.disco.easyb.domain.BehaviorFactory;
import org.disco.easyb.domain.GroovyShellConfiguration;
import org.disco.easyb.exception.VerificationException;
import org.disco.easyb.listener.BroadcastListener;
import org.disco.easyb.listener.ExecutionListener;
import org.disco.easyb.listener.FailureDetector;
import org.disco.easyb.listener.ResultsCollector;
import org.disco.easyb.report.ReportWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BehaviorRunner {
    private List<ReportWriter> reports;
    private BroadcastListener broadcastListener = new BroadcastListener();
    private ResultsCollector resultsCollector = new ResultsCollector();
    private FailureDetector failureDetector = new FailureDetector();

    public BehaviorRunner(final ExecutionListener... listeners) {
        this(null, listeners);
    }

    public BehaviorRunner(final List<ReportWriter> reports, final ExecutionListener... listeners) {
        this.reports = reports;

        broadcastListener.registerListener(resultsCollector);
        broadcastListener.registerListener(failureDetector);

        for (final ExecutionListener listener : listeners) {
            broadcastListener.registerListener(listener);
        }
    }

    /**
     * @param args the command line arguments
     *             usage is:
     *             <p/>
     *             java BehaviorRunner my/path/to/spec/MyStory.groovy -txtstory ./reports/story-report.txt
     *             <p/>
     *             You don't need to pass in the file name for the report either-- if no
     *             path is present, then the runner will create a report in the current directory
     *             with a default filename following this convention:
     *             easyb-<type>-report.<format> (for reports of either story or specification)
     *             easyb-report.<format> (for reports that contain both)
     *             <p/>
     *             Multiple specifications can be passed in on the command line
     *             <p/>
     *             java BehaviorRunner my/path/to/spec/MyStory.groovy my/path/to/spec/AnotherStory.groovy
     */
    public static void main(final String[] args) {
        final Configuration configuration = new ConsoleConfigurator().configure(args);
        final ConsoleReporter consoleRpt = new ConsoleReporter();
        consoleRpt.setConfiguration(configuration);

        if (configuration != null) {
            final BehaviorRunner runner = new BehaviorRunner(configuration.getConfiguredReports(),
                    consoleRpt);
            try {
                runner.runBehavior(getBehaviors(configuration.getFilePaths()));
            } catch (Throwable exception) {
                System.err.println("There was an error running your easyb story or specification");
                exception.printStackTrace(System.err);
                System.exit(-1);
            }
        }
    }

    /**
     * @param behaviors collection of files that contain the specifications
     * @throws Exception if unable to write report file
     */
    public void runBehavior(List<Behavior> behaviors) throws Exception {
        for (final Behavior behavior : behaviors) {
            behavior.execute(broadcastListener);
        }

        broadcastListener.completeTesting();

        for (final ReportWriter report : reports) {
            report.writeReport(resultsCollector);
        }

        if (failureDetector.failuresDetected()) {
            throw new VerificationException("There were specification failures");
        }
    }

    public static List<Behavior> getBehaviors(final GroovyShellConfiguration groovyShellConfiguration,
                                              final String[] paths) {
        List<Behavior> behaviors = new ArrayList<Behavior>();
        for (final String path : paths) {
            behaviors.add(BehaviorFactory.createBehavior(groovyShellConfiguration, new File(path)));
        }
        return Collections.unmodifiableList(behaviors);
    }

    /**
     * @param paths locations of the specifications to be loaded
     * @return collection of files where the only element is the file of the spec to be run
     */
    public static List<Behavior> getBehaviors(final String[] paths) {
        return getBehaviors(BehaviorFactory.DEFAULT_GROOVY_SHELL_CONFIG, paths);
    }
}
