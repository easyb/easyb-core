package org.disco.easyb;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.disco.easyb.report.Report;

public class ConsoleConfigurator {
    public Configuration configure(String[] args) {
        Options options = getOptionsForMain();

        try {
            CommandLine commandLine = getCommandLineForMain(args, options);
            validateArguments(commandLine);
            return new Configuration(commandLine, getConfiguredReports(commandLine));
        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getMessage());
            handleHelpForMain(options);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
            handleHelpForMain(options);
        }

        // TODO: Find another way to communicate that configuration was not successful
        return null;
    }

    /**
     * @param args    command line arguments passed into main
     * @param options options that are available to this specification runner
     * @return representation of command line arguments passed in that match the available options
     * @throws org.apache.commons.cli.ParseException
     *          if there are any problems encountered while parsing the command line tokens
     */
    private static CommandLine getCommandLineForMain(String[] args, Options options) throws ParseException {
        CommandLineParser commandLineParser = new GnuParser();
        return commandLineParser.parse(options, args);
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
     * @param options options that are available to this specification runner
     */
    private static void handleHelpForMain(Options options) {
        new HelpFormatter().printHelp("BehaviorRunner my/path/to/MyFile.groovy", options);
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
}
