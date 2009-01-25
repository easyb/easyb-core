package org.disco.easyb;

import org.apache.commons.cli.*;
import org.disco.easyb.report.*;

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

    private static final String EXTENDED_STORY_CLASS = "extstory";

    public Configuration configure(final String[] args) {
        final Options options = getOptionsForMain();

        try {
            CommandLine commandLine = getCommandLineForMain(args, options);
            validateArguments(commandLine);

            String extendedStoryClzz = getExtendedStoryClass(commandLine);

            return new Configuration(commandLine.getArgs(),
                    getConfiguredReports(commandLine), commandLine.hasOption(EXCEPTION_STACK),
                    commandLine.hasOption(FILTER_EXCEPTION_STACK), extendedStoryClzz);

        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getMessage());
            handleHelpForMain(options);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
            handleHelpForMain(options);
        }

        return null;
    }

    private String getExtendedStoryClass(CommandLine commandLine) {
        String extendedStoryClzz = null;
        if (commandLine.hasOption(EXTENDED_STORY_CLASS)) {
            extendedStoryClzz = commandLine.getOptionValue(EXTENDED_STORY_CLASS);
        }
        return extendedStoryClzz;
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
            throw new IllegalArgumentException("Required arguments missing. At a minimum, " +
                    "you must provide a path to a behavior for easyb to run.");
        }
    }

    /**
     * @param line
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
        return Collections.unmodifiableList(configuredReports);
    }

    /**
     * @param options options that are available to this specification runner
     */
    private static void handleHelpForMain(final Options options) {
        new HelpFormatter().printHelp("BehaviorRunner my/path/to/MyFile.story", options);
    }

    /**
     * @return representation of a collection of Option objects, which describe the possible options for a command-line.
     */
    @SuppressWarnings("static-access")
    private static Options getOptionsForMain() {
        final Options options = new Options();

        //noinspection AccessStaticViaInstance
        final Option htmleasybreport = OptionBuilder.withArgName("file").hasOptionalArg()
                .withDescription("create an easyb report in html format").create(HTML_EASYB);
        options.addOption(htmleasybreport);
        //noinspection AccessStaticViaInstance
        final Option xmleasybreport = OptionBuilder.withArgName("file").hasOptionalArg()
                .withDescription("create an easyb report in xml format").create(XML_EASYB);
        options.addOption(xmleasybreport);
        //noinspection AccessStaticViaInstance
        final Option storyreport = OptionBuilder.withArgName("file").hasOptionalArg()
                .withDescription("create a story report").create(TXT_STORY);
        options.addOption(storyreport);
        //noinspection AccessStaticViaInstance
        final Option behaviorreport = OptionBuilder.withArgName("file").hasOptionalArg()
                .withDescription("create a behavior report").create(TXT_SPECIFICATION);
        options.addOption(behaviorreport);


        final Option stacktrace = new Option(EXCEPTION_STACK, "prints stacktrace");
        stacktrace.setRequired(false);
        options.addOption(stacktrace);

        final Option filteredstacktrace = new Option(FILTER_EXCEPTION_STACK, "prints stacktrace");
        stacktrace.setRequired(false);
        options.addOption(filteredstacktrace);


        //noinspection AccessStaticViaInstance
        final Option extendedStoryClass = OptionBuilder.withArgName("class").hasOptionalArg()
                .withDescription("use an extended story class").create(EXTENDED_STORY_CLASS);
        options.addOption(extendedStoryClass);

        return options;
    }
}
