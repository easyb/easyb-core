package org.disco.easyb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import groovy.lang.GroovyShell;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.disco.easyb.listener.BehaviorListener;
import org.disco.easyb.listener.DefaultListener;
import org.disco.easyb.report.Report;
import org.disco.easyb.report.TxtSpecificationReportWriter;
import org.disco.easyb.report.TxtStoryReportWriter;
import org.disco.easyb.report.XmlReportWriter;
import org.disco.easyb.util.BehaviorStepType;

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

    List<Report> reports;

    public BehaviorRunner() {
        this(null);
    }

    public BehaviorRunner(List<Report> reports) {
        this.reports = addDefaultReports(reports);
    }

    /**
     * @param specs collection of files that contain the specifications
     * @throws Exception if unable to write report file
     */
    public void runBehavior(Collection<File> specs) throws Exception {

        BehaviorListener listener = new DefaultListener();

        executeSpecifications(specs, listener);

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
     *
     * @param listener
     */
    private void produceReports(BehaviorListener listener) {

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
     *
     * @param behaviorFiles
     * @param listener
     * @throws IOException
     */
    private void executeSpecifications(final Collection<File> behaviorFiles, final BehaviorListener listener) throws IOException {
        for (File behaviorFile : behaviorFiles) {
            Behavior behavior = null;
            try {
                behavior = BehaviorFactory.createBehavior(behaviorFile);
            } catch (IllegalArgumentException iae) {
                System.out.println(iae.getMessage());
                System.exit(-1);
            }

            long startTime = System.currentTimeMillis();
            System.out.println("Running " + behavior.getPhrase()
                + ((behavior instanceof Story) ? " story" : " specification")
                + " (" + behaviorFile.getName() + ")");

            BehaviorStep currentStep;
            if (behavior instanceof Story) {
                currentStep = listener.startStep(BehaviorStepType.STORY, behavior.getPhrase());
                new GroovyShell(StoryBinding.getBinding(listener)).evaluate(behaviorFile);
            } else {
                currentStep = listener.startStep(BehaviorStepType.SPECIFICATION, behavior.getPhrase());
                new GroovyShell(SpecificationBinding.getBinding(listener)).evaluate(behaviorFile);
            }
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
        Options options = getOptionsForMain();

        try {
            CommandLine commandLine = getCommandLineForMain(args, options);
            validateArguments(commandLine);
            BehaviorRunner runner = new BehaviorRunner(getConfiguredReports(commandLine));
            runner.runBehavior(getFileCollection(commandLine.getArgs()));
        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getMessage());
            handleHelpForMain(options);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
            handleHelpForMain(options);
        } catch (Exception e) {
            System.err.println("There was an error running the script");
            e.printStackTrace(System.err);
            System.exit(-6);
        }
    }

    private static void validateArguments(CommandLine commandLine) throws IllegalArgumentException {
        if (commandLine.getArgs().length == 0) {
            throw new IllegalArgumentException("Required arguments missing.");
        }
    }

    private static List<Report> getConfiguredReports(CommandLine line) {

        List<Report> configuredReports = new ArrayList<Report>();
        if (line.hasOption(Report.TXT_STORY)) {
            Report report = Report.build(Report.TXT_STORY, line.getOptionValue(Report.TXT_STORY));
            configuredReports.add(report);
        }

        if (line.hasOption(Report.TXT_SPECIFICATION)) {
            Report report = Report.build(Report.TXT_SPECIFICATION, line.getOptionValue(Report.TXT_SPECIFICATION));
            configuredReports.add(report);
        }

        if (line.hasOption(Report.XML_EASYB)) {
            Report report = Report.build(Report.EASYB_TYPE, line.getOptionValue(Report.XML_EASYB));
            configuredReports.add(report);
        }
        return configuredReports;
    }

    /**
     * @param paths locations of the specifications to be loaded
     * @return collection of files where the only element is the file of the spec to be run
     */
    private static Collection<File> getFileCollection(String[] paths) {
        Collection<File> coll = new ArrayList<File>();
        for (String path : paths) {
            coll.add(new File(path));
        }
        return coll;
    }

    /**
     * @param options options that are available to this specification runner
     */
    private static void handleHelpForMain(Options options) {
        new HelpFormatter().printHelp("BehaviorRunner my/path/to/MyFile.groovy", options);
    }

    /**
     * @param args    command line arguments passed into main
     * @param options options that are available to this specification runner
     * @return representation of command line arguments passed in that match the available options
     * @throws ParseException if there are any problems encountered while parsing the command line tokens
     */
    private static CommandLine getCommandLineForMain(String[] args, Options options) throws ParseException {
        CommandLineParser commandLineParser = new GnuParser();
        return commandLineParser.parse(options, args);
    }

    /**
     * @return representation of a collection of Option objects, which describe the possible options for a command-line.
     */
    @SuppressWarnings("static-access")
    private static Options getOptionsForMain() {
        Options options = new Options();

        //noinspection AccessStaticViaInstance
        Option xmleasybreport = OptionBuilder.withArgName("file").hasOptionalArg()
            .withDescription("create an easyb report in xml format").create(Report.XML_EASYB);
        options.addOption(xmleasybreport);
        //noinspection AccessStaticViaInstance
        Option storyreport = OptionBuilder.withArgName("file").hasOptionalArg()
            .withDescription("create a story report").create(Report.TXT_STORY);
        options.addOption(storyreport);
        //noinspection AccessStaticViaInstance
        Option behaviorreport = OptionBuilder.withArgName("file").hasOptionalArg()
            .withDescription("create a behavior report").create(Report.TXT_SPECIFICATION);
        options.addOption(behaviorreport);

        return options;
    }

    private List<Report> addDefaultReports(List<Report> userConfiguredReports) {
        List<Report> configuredReports = new ArrayList<Report>();

        if (userConfiguredReports != null) {
            configuredReports.addAll(userConfiguredReports);
        }

        return configuredReports;
    }
}
