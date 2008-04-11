package org.disco.easyb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.disco.easyb.domain.Behavior;
import org.disco.easyb.domain.BehaviorFactory;
import org.disco.easyb.domain.Story;
import org.disco.easyb.listener.DefaultStepListener;
import org.disco.easyb.listener.StepListener;
import org.disco.easyb.report.Report;
import org.disco.easyb.report.TxtSpecificationReportWriter;
import org.disco.easyb.report.TxtStoryReportWriter;
import org.disco.easyb.report.XmlReportWriter;

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
    private BehaviorExecutionListener executionListener;

    public BehaviorRunner(BehaviorExecutionListener executionListener) {
        this(executionListener, null);
    }

    public BehaviorRunner(BehaviorExecutionListener executionListener, List<Report> reports) {
        this.executionListener = executionListener;
        this.reports = addDefaultReports(reports);
    }

    /**
     * @param specs collection of files that contain the specifications
     * @throws Exception if unable to write report file
     */
    public void runBehavior(Collection<File> specs) throws Exception {

        StepListener listener = new DefaultStepListener();

        executeBehaviors(specs, listener);

        System.out.println("\n" +
            //prints "1 behavior run" or "x behaviors run"
            (listener.getBehaviorCount() > 1 ? listener.getBehaviorCount() + " total behaviors run" : "1 behavior run")
            //outer ternary prints either 1..X failure(s) or no failures
            //inner ternary determines if more than one failure and makes it plural if so
            + (listener.getFailedBehaviorCount() > 0 ? " with "
            + (listener.getFailedBehaviorCount() == 1 ? "1 failure" : listener.getFailedBehaviorCount() + " failures") : " with no failures"));

        produceReports(listener);

        if (listener.getFailedBehaviorCount() > 0) {
            System.exit(-6);
        }
    }

    /**
     * @param listener Listener to receive specification events
     */
    private void produceReports(StepListener listener) {

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
     * @param listener  Listener to receive specification events
     * @throws IOException IO exception running groovy script
     */
    private void executeBehaviors(final Collection<File> behaviors, final StepListener listener) throws IOException {
        for (File behaviorFile : behaviors) {
            Behavior behavior = null;
            try {
                behavior = BehaviorFactory.createBehavior(behaviorFile);
            } catch (IllegalArgumentException iae) {
                System.out.println(iae.getMessage());
                System.exit(-1);
            }

            long startTime = System.currentTimeMillis();
            executionListener.startBehavior(behavior);

            BehaviorStep currentStep = behavior.execute(listener);
            listener.stopStep();

            long endTime = System.currentTimeMillis();

            printMetrics(behavior, startTime, currentStep, endTime);
        }
    }

    private void printMetrics(Behavior behavior, long startTime, BehaviorStep currentStep, long endTime) {
        if (behavior instanceof Story) {
            System.out.println((currentStep.getFailedScenarioCountRecursively() == 0 ? "" : "FAILURE ") +
                "Scenarios run: " + currentStep.getScenarioCountRecursively() +
                ", Failures: " + currentStep.getFailedScenarioCountRecursively() +
                ", Pending: " + currentStep.getPendingScenarioCountRecursively() +
                ", Time Elapsed: " + (endTime - startTime) / 1000f + " sec");
        } else {
            System.out.println((currentStep.getFailedSpecificationCountRecursively() == 0 ? "" : "FAILURE ") +
                "Specifications run: " + currentStep.getSpecificationCountRecursively() +
                ", Failures: " + currentStep.getFailedSpecificationCountRecursively() +
                ", Pending: " + currentStep.getPendingSpecificationCountRecursively() +
                ", Time Elapsed: " + (endTime - startTime) / 1000f + " sec");
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
