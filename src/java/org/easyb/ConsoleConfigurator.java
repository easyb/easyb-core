package org.easyb;

import org.apache.commons.cli.*;

import static org.apache.commons.cli.OptionBuilder.withArgName;
import static org.apache.commons.cli.OptionBuilder.withDescription;

import org.easyb.report.*;
import org.easyb.util.BehaviorFileToPathsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsoleConfigurator {
    private static final String TXT_SPECIFICATION = "txtspecification";
    private static final String TXT_STORY = "txtstory";
    private static final String XML_EASYB = "xml";
    private static final String HTML_EASYB = "html";
    private static final String EXCEPTION_STACK = "e";
    private static final String FILTER_EXCEPTION_STACK = "ef";


    private static final String NOEXECUTE_STORY_DESCRIPTION = "creates report without running any stories";
    private static final String NOEXECUTE_STORY = "ne";
    private static final String PRETTY_PRINT = "prettyprint";
    private static final String HTML_DESCRIPTION = "create an easyb report in html format";
    private static final String XML_DESCRIPTION = "create an easyb report in xml format";
    private static final String STORY_DESCRIPTION = "create a story report";
    private static final String BEHAVIOR_DESRCIPTION = "create a behavior report";
    private static final String STACKTRACE_DESCRIPTION = "prints stacktrace information";
    private static final String FILTERED_STACKTRACE_DESCRIPTION = "prints filtered stacktrace information";
    private static final String PARALLEL = "parallel";
    private static final String PARALLEL_DESCRIPTION = "run specifications in parallel";
    private static final String PRETTY_PRINT_DESCRIPTION = "prints colored behaviors";

    private static final String BEHAVIOR_FILE = "f";
    private static final String BEHAVIOR_FILE_DESCRIPTION = "run behaviors listed in a file";
    private static final String FAILURE_BEHAVIOR_FILE = "outfail";
    private static final String FAILURE_BEHAVIOR_FILE_DESCRIPTION = "caputure failed behaviors in a file " +
            "(for processing at a later point -- see the -f option)";

    private static final String TAG = "tags";
    private static final String TAG_DESCRIPTION = "run behaviors with tag marker";


    public Configuration configure(final String[] args) {
        final Options options = getOptionsForMain();

        try {
            CommandLine commandLine = getCommandLineForMain(args, options);
            validateArguments(commandLine);
            String extendedStoryClzz = getExtendedStoryClass(commandLine);
            String[] paths = convertArgsToPaths(commandLine);

            return new Configuration(paths,
                    getConfiguredReports(commandLine), commandLine.hasOption(EXCEPTION_STACK),
                    commandLine.hasOption(FILTER_EXCEPTION_STACK), extendedStoryClzz, isParallel(commandLine),
                    isFailureFile(commandLine), commandLine.getOptionValue(FAILURE_BEHAVIOR_FILE),
                    getTags(commandLine));

        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getMessage());
            handleHelpForMain(options);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
            handleHelpForMain(options);
        }
        return null;
    }

    private String[] getTags(CommandLine cmdLine) {
        if (cmdLine.hasOption(TAG)) {
            return cmdLine.getOptionValue(TAG).split(",");
        } else {
            return null;
        }
    }

    /**
     * @param cmdLine
     * @return
     */
    private String[] convertArgsToPaths(CommandLine cmdLine) {
        if (cmdLine.hasOption(BEHAVIOR_FILE)) {
            String file = cmdLine.getOptionValue(BEHAVIOR_FILE);
            BehaviorFileToPathsBuilder builder = new BehaviorFileToPathsBuilder();
            return builder.buildPaths(((file != null) ? file : "failure-files.txt"), cmdLine.getArgs());
        } else {
            return cmdLine.getArgs();
        }
    }

    private String getExtendedStoryClass(CommandLine commandLine) {
        String extendedStoryClzz = null;
        if (commandLine.hasOption(NOEXECUTE_STORY)) {
            extendedStoryClzz = "NonExecutableStory";
        }
        return extendedStoryClzz;
    }

    private boolean isFailureFile(CommandLine cmdLine) {
        return cmdLine.hasOption(FAILURE_BEHAVIOR_FILE);
    }

    private boolean isParallel(CommandLine commandLine) {
        return commandLine.hasOption(PARALLEL);
    }

    /**
     * @param args    command line arguments passed into main
     * @param options options that are available to this specification runner
     * @return representation of command line arguments passed in that match the available options
     * @throws org.apache.commons.cli.ParseException
     *          if there are any problems encountered while parsing the command line tokens
     */
    private static CommandLine getCommandLineForMain(final String[] args, final Options options) throws ParseException {
        final CommandLineParser commandLineParser = new GnuParser();
        return commandLineParser.parse(options, args);
    }

    private static void validateArguments(final CommandLine commandLine) throws IllegalArgumentException {
        if (commandLine.getArgs().length == 0) {
            if (!commandLine.hasOption(BEHAVIOR_FILE)) {
                throw new IllegalArgumentException("Required arguments missing. At a minimum, " +
                        "you must provide a path to a behavior for easyb to run or provide the -f flag pointing to" +
                        " a file containing behaviors.");
            }
        }
    }

    /**
     * @param line command line to scan for reports
     * @return immutable list of ReportWriter objects
     */
    private static List<ReportWriter> getConfiguredReports(final CommandLine line) {
        List<ReportWriter> configuredReports = new ArrayList<ReportWriter>();
        if (line.hasOption(TXT_STORY)) {
            configuredReports.add(new TxtStoryReportWriter(line.getOptionValue(TXT_STORY)));
        }

        if (line.hasOption(TXT_SPECIFICATION)) {
            configuredReports.add(new TxtSpecificationReportWriter(line.getOptionValue(TXT_SPECIFICATION)));
        }

        if (line.hasOption(XML_EASYB)) {
            configuredReports.add(new XmlReportWriter(line.getOptionValue(XML_EASYB)));
        }

        if (line.hasOption(HTML_EASYB)) {
            configuredReports.add(new HtmlReportWriter(line.getOptionValue(HTML_EASYB)));
        }
        if (line.hasOption(PRETTY_PRINT)) {
            configuredReports.add(new PrettyPrintReportWriter());
        }
        return Collections.unmodifiableList(configuredReports);
    }

    /**
     * @param options options that are available to this specification runner
     */
    private static void handleHelpForMain(final Options options) {
        new HelpFormatter().printHelp("BehaviorRunner my/path/to/MyFile.story or provide the -f flag", options);
    }

    /**
     * @return representation of a collection of Option objects, which describe the possible options for a command-line.
     */
    @SuppressWarnings({"static-access", "AccessStaticViaInstance"})
    private static Options getOptionsForMain() {
        final Options options = new Options();

        options.addOption(withDescription(HTML_DESCRIPTION).hasOptionalArg().create(HTML_EASYB));
        options.addOption(withDescription(XML_DESCRIPTION).hasOptionalArg().create(XML_EASYB));
        options.addOption(withDescription(STORY_DESCRIPTION).hasOptionalArg().create(TXT_STORY));
        options.addOption(withDescription(BEHAVIOR_DESRCIPTION).hasOptionalArg().create(TXT_SPECIFICATION));
        options.addOption(withDescription(STACKTRACE_DESCRIPTION).hasOptionalArg().create(EXCEPTION_STACK));
        options.addOption(withDescription(FILTERED_STACKTRACE_DESCRIPTION).hasOptionalArg().create(FILTER_EXCEPTION_STACK));
        options.addOption(withArgName(NOEXECUTE_STORY).withDescription(NOEXECUTE_STORY_DESCRIPTION).create(NOEXECUTE_STORY));
        options.addOption(withDescription(PRETTY_PRINT_DESCRIPTION).hasOptionalArg().create(PRETTY_PRINT));
        options.addOption(withArgName(PARALLEL).withDescription(PARALLEL_DESCRIPTION).create(PARALLEL));
        options.addOption(withDescription(BEHAVIOR_FILE_DESCRIPTION).hasOptionalArg().create(BEHAVIOR_FILE));
        options.addOption(withDescription(FAILURE_BEHAVIOR_FILE_DESCRIPTION).hasOptionalArg().create(FAILURE_BEHAVIOR_FILE));
        options.addOption(withDescription(TAG_DESCRIPTION).hasOptionalArg().create(TAG));

        return options;
    }
}
