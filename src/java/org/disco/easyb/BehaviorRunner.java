package org.disco.easyb;

import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.disco.easyb.listener.DefaultListener;
import org.disco.easyb.listener.BehaviorListener;
import org.disco.easyb.report.EasybXmlReportWriter;
import org.disco.easyb.report.TxtSpecificationReportWriter;
import org.disco.easyb.report.Report;
import org.disco.easyb.report.ReportWriter;
import org.disco.easyb.report.TxtStoryReportWriter;
import org.disco.easyb.util.BehaviorStepType;
import org.disco.easyb.util.ReportFormat;
import org.disco.easyb.util.ReportType;

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
        	(listener.getSpecificationCount() > 1 ? listener.getSpecificationCount()  + " total behaviors run" : "1 behavior run")
        	//outer ternary prints either 1..X failure(s) or no failures
        	//inner ternary determines if more than one failure and makes it plural if so
        	+ (listener.getFailedSpecificationCount() > 0 ? " with " 
        		+ (listener.getFailedSpecificationCount() == 1 ? "1 failure" : listener.getFailedSpecificationCount() + " failures") : " with no failures"));

        produceReports(listener);

        if (listener.getFailedSpecificationCount() > 0) {
            System.exit(-6);
        }
    }

    private void produceReports(BehaviorListener listener) {
        String easybxmlreportlocation = null;
        for (Report report : reports) {
            if (report.getFormat().concat(report.getType()).equals(Report.XML_EASYB)) {
                easybxmlreportlocation = report.getLocation();
                ReportWriter reportWriter = new EasybXmlReportWriter(report, listener);
                reportWriter.writeReport();
            }
        }

        if (reports.size() > 0 && easybxmlreportlocation == null) {
            System.out.println("xmleasyb report is required");
            System.exit(-1);
        }

        for (Report report : reports) {
            if (report.getFormat().concat(report.getType()).equals(Report.XML_EASYB)) {
                //do nothing, report was already run above.
            } else if (report.getFormat().concat(report.getType()).equals(Report.TXT_STORY)) {
                new TxtStoryReportWriter(report, easybxmlreportlocation).writeReport();
            } else if (report.getFormat().concat(report.getType()).equals(Report.TXT_SPECIFICATION)) {
                new TxtSpecificationReportWriter(report, easybxmlreportlocation).writeReport();
            }
        }

    }

    private void executeSpecifications(final Collection<File> behaviorFiles, final BehaviorListener listener) throws IOException {
        for (File behaviorFile : behaviorFiles) {
        	Behavior behavior = null;
            try {
                behavior = BehaviorFactory.createBehavior(behaviorFile);
            } catch(IllegalArgumentException iae) {
                System.out.println(iae.getMessage());
                System.exit(-1);
            }

            long startTime = System.currentTimeMillis();
            System.out.println("Running " + behavior.getPhrase() 
            		+ ((behavior instanceof Story) ? " story" : " specification")
            		+ " (" + behaviorFile.getName() + ")" );
            
            
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
        if(behavior instanceof Story) {
            List<BehaviorStep> scenarioSteps = currentStep.getChildrenOfType(BehaviorStepType.SCENARIO);
            long scenarioFailedCount = 0;
            long scenarioPendingCount = 0;
            for(BehaviorStep behaviorStep : currentStep.getChildrenOfType(BehaviorStepType.SCENARIO)) {
              scenarioFailedCount += behaviorStep.getStepFailureCount();
              scenarioPendingCount += behaviorStep.getStepPendingCount();
            }
            System.out.println((scenarioFailedCount == 0 ? "" : "FAILURE ") +
                    "Scenarios run: " + scenarioSteps.size() +
                    ", Failures: " + scenarioFailedCount +
                    ", Pending: " + scenarioPendingCount +
                    ", Time Elapsed: " + (endTime - startTime) / 1000f + " sec");
        } else {
            System.out.println((currentStep.getChildStepFailureResultCount() == 0 ? "" : "FAILURE ") +
                    "Specifications run: " + currentStep.getChildStepResultCount() +
                    ", Failures: " + currentStep.getChildStepFailureResultCount() +
                    ", Pending: " + currentStep.getChildStepPendingResultCount() + 
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
            throw new IllegalArgumentException("Required Arguments not passed in.");
        }
    }

    private static List<Report> getConfiguredReports(CommandLine line) {

        List<Report> configuredReports = new ArrayList<Report>();
        if (line.hasOption(Report.TXT_STORY)) {
            Report report = new Report();
            report.setFormat(ReportFormat.TXT.format());
            if (line.getOptionValue(Report.TXT_STORY) == null) {
                report.setLocation("easyb-story-report.txt");
            } else {
                report.setLocation(line.getOptionValue(Report.TXT_STORY));
            }
            report.setType(ReportType.STORY.type());

            configuredReports.add(report);
        }

        if (line.hasOption(Report.TXT_SPECIFICATION)) {
            Report report = new Report();
            report.setFormat(ReportFormat.TXT.format());
            if (line.getOptionValue(Report.TXT_SPECIFICATION) == null) {
                report.setLocation("easyb-specification-report.txt");
            } else {
                report.setLocation(line.getOptionValue(Report.TXT_SPECIFICATION));
            }
            report.setType(ReportType.SPECIFICATION.type());

            configuredReports.add(report);
        }

        if (line.hasOption(Report.XML_EASYB)) {
            Report report = new Report();
            report.setFormat(ReportFormat.XML.format());
            if (line.getOptionValue(Report.XML_EASYB) == null) {
                report.setLocation("easyb-report.xml");
            } else {
                report.setLocation(line.getOptionValue(Report.XML_EASYB));
            }
            report.setType(ReportType.EASYB.type());

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
        new HelpFormatter().printHelp("BehaviorRunner my/path/to/MyStory.groovy", options);
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
    private static Options getOptionsForMain() {
        Options options = new Options();

        //noinspection AccessStaticViaInstance
        Option xmleasybreport = OptionBuilder.withArgName("file").hasArg()
            .withDescription("create an easyb report in xml format").create(Report.XML_EASYB);
        options.addOption(xmleasybreport);

        //noinspection AccessStaticViaInstance
        Option storyreport = OptionBuilder.withArgName("file").hasArg()
            .withDescription("create a story report").create(Report.TXT_STORY);
        options.addOption(storyreport);

        //noinspection AccessStaticViaInstance
        Option behaviorreport = OptionBuilder.withArgName("file").hasArg()
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
