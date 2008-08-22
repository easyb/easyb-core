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
import org.disco.easyb.report.ReportWriter;
import org.disco.easyb.report.TxtSpecificationReportWriter;
import org.disco.easyb.report.TxtStoryReportWriter;
import org.disco.easyb.report.XmlReportWriter;
import org.disco.easyb.report.HtmlReportWriter;

public class ConsoleConfigurator {
    private static final String TXT_SPECIFICATION = "txtspecification";
    private static final String TXT_STORY = "txtstory";
    private static final String XML_EASYB = "xml";
    private static final String HTML_EASYB = "html";

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

    private static List<ReportWriter> getConfiguredReports(CommandLine line) {
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
        return configuredReports;
    }

    /**
     * @param options options that are available to this specification runner
     */
    private static void handleHelpForMain(Options options) {
        new HelpFormatter().printHelp("BehaviorRunner my/path/to/MyFile.story", options);
    }

    /**
     * @return representation of a collection of Option objects, which describe the possible options for a command-line.
     */
    @SuppressWarnings("static-access")
    private static Options getOptionsForMain() {
        Options options = new Options();

        //noinspection AccessStaticViaInstance
        Option htmleasybreport = OptionBuilder.withArgName("file").hasOptionalArg()
            .withDescription("create an easyb report in html format").create(HTML_EASYB);
        options.addOption(htmleasybreport);
        //noinspection AccessStaticViaInstance
        Option xmleasybreport = OptionBuilder.withArgName("file").hasOptionalArg()
            .withDescription("create an easyb report in xml format").create(XML_EASYB);
        options.addOption(xmleasybreport);
        //noinspection AccessStaticViaInstance
        Option storyreport = OptionBuilder.withArgName("file").hasOptionalArg()
            .withDescription("create a story report").create(TXT_STORY);
        options.addOption(storyreport);
        //noinspection AccessStaticViaInstance
        Option behaviorreport = OptionBuilder.withArgName("file").hasOptionalArg()
            .withDescription("create a behavior report").create(TXT_SPECIFICATION);
        options.addOption(behaviorreport);

        return options;
    }
}
