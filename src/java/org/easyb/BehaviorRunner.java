package org.easyb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.concurrent.ExecutorService;

import org.easyb.domain.Behavior;
import org.easyb.domain.BehaviorFactory;
import org.easyb.domain.GroovyShellConfiguration;
import org.easyb.listener.BroadcastListener;
import org.easyb.listener.ExecutionListener;
import org.easyb.listener.FailureDetector;
import org.easyb.listener.ResultsCollector;
import org.easyb.report.ReportWriter;

public class BehaviorRunner {
    private Configuration configuration;
    private BroadcastListener broadcastListener = new BroadcastListener();
    private ResultsCollector resultsCollector = new ResultsCollector();
    private FailureDetector failureDetector = new FailureDetector();

    public BehaviorRunner(final Configuration configuration, final ExecutionListener... listeners) {
        this.configuration = configuration;

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
        final ConsoleReporter consoleRpt = configuration.getConsoleReporter();

        //noinspection ConstantConditions
        if (configuration != null) {
            final BehaviorRunner runner = new BehaviorRunner(configuration, consoleRpt);
            try {
                boolean success = runner.runBehaviors(getBehaviors(configuration.getFilePaths(), configuration));
                //the Ant task assumes a return code from the easyb process to
                //determine error or not
                if (!success) {
                    System.exit(-1);
                }
            } catch (Throwable exception) {
                System.err.println("There was an error running your easyb story or specification");
                exception.printStackTrace(System.err);
                System.exit(-1);
            }
        }
    }

    /**
     * @param behaviors collection of files that contain the specifications
     * @return success indicator
     * @throws Exception if unable to write report file
     */
    public boolean runBehaviors(List<Behavior> behaviors) throws Exception {
        boolean wasSuccessful = true;

        List<RunnableBehavior> executedBehaviors = executeBehaviors(behaviors);
        for (RunnableBehavior each : executedBehaviors) {
            if (each.isFailed()) {
                throw each.getFailure();
            }
        }

        broadcastListener.completeTesting();

        for (final ReportWriter report : configuration.getConfiguredReports()) {
            report.writeReport(resultsCollector);
        }

        if (failureDetector.failuresDetected()) {
            wasSuccessful = false;
        }
        return wasSuccessful;
    }

    private List<RunnableBehavior> executeBehaviors(List<Behavior> behaviors) throws InterruptedException {
        ExecutorService executor = configuration.getExecutor();

        List<RunnableBehavior> executedBehaviors = new ArrayList<RunnableBehavior>();
        for (final Behavior behavior : behaviors) {
            RunnableBehavior task = new RunnableBehavior(behavior, broadcastListener);
            executedBehaviors.add(task);
            executor.execute(task);
        }
        executor.shutdown();
        executor.awaitTermination(60, SECONDS);

        return executedBehaviors;
    }

    public static List<Behavior> getBehaviors(final GroovyShellConfiguration groovyShellConfiguration,
        final String[] paths, Configuration config) {
        List<Behavior> behaviors = new ArrayList<Behavior>();
        for (final String path : paths) {
            behaviors.add(BehaviorFactory.createBehavior(groovyShellConfiguration, new File(path), config));
        }
        return Collections.unmodifiableList(behaviors);
    }

    public static List<Behavior> getBehaviors(final GroovyShellConfiguration groovyShellConfiguration,
        final String[] paths) {
        return getBehaviors(groovyShellConfiguration, paths, null);
    }

    /**
     * @param paths locations of the specifications to be loaded
     * @return collection of files where the only element is the file of the spec to be run
     */
    public static List<Behavior> getBehaviors(final String[] paths) {
        return getBehaviors(BehaviorFactory.DEFAULT_GROOVY_SHELL_CONFIG, paths);
    }

    /**
     * @param paths  locations of the specifications to be loaded
     * @param config configuration
     * @return collection of files where the only element is the file of the spec to be run
     */
    public static List<Behavior> getBehaviors(final String[] paths, Configuration config) {
        return getBehaviors(BehaviorFactory.DEFAULT_GROOVY_SHELL_CONFIG, paths, config);
    }
}
