package org.disco.easyb;

import groovy.lang.GroovyShell;

import java.io.File;
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

import org.disco.easyb.core.listener.DefaultListener;
import org.disco.easyb.core.listener.SpecificationListener;
import org.disco.easyb.core.report.Report;
import org.disco.easyb.core.report.ReportWriter;
import org.disco.easyb.core.report.TerseReportWriter;
import org.disco.easyb.core.report.TxtStoryReportWriter;
import org.disco.easyb.core.report.XmlBehaviorReportWriter;
import org.disco.easyb.core.result.Result;
import org.disco.easyb.core.util.CamelCaseConverter;
import org.disco.easyb.core.util.ReportFormat;
import org.disco.easyb.core.util.ReportType;


/**
 * usage is:
 * <p/>
 * java SpecificationRunner my/path/to/spec/MyStory.groovy -txtstory ./reports/story-report.txt
 * <p/>
 * You don't need to pass in the file name for the report either-- if no
 * path is present, then the runner will create a report in the current directory
 * with a default filename following this convention: easyb-<type>-report.<format>
 * <p/>
 * Multiple specifications can be passed in on the command line
 * <p/>
 * java SpecificationRunner my/path/to/spec/MyStory.groovy my/path/to/spec/AnotherStory.groovy
 */
public class SpecificationRunner {


  List<Report> reports;

  public SpecificationRunner() {
    this(null);
  }

  public SpecificationRunner(List<Report> reports) {
    this.reports = addDefaultReports(reports);
  }

  /**
   * @param specs collection of files that contain the specifications
   * @throws Exception if unable to write report file
   * @return BehaviorListener has status about failures and successes
   */
  public List<SpecificationListener> runSpecification(Collection<File> specs) throws Exception {

    List<SpecificationListener> listeners = new ArrayList<SpecificationListener>();

    SpecificationListener listener = new DefaultListener();

    for (File file : specs) {
    	listener.setSpecificationName(file.getName());
        if (file.getName().contains("Story.groovy")) {
        	listener.gotResult(new Result(new CamelCaseConverter(file.getName().substring(0, file.getName().indexOf("Story.groovy"))).toPhrase(), SpecificationBinding.STORY, Result.SUCCEEDED));
          new GroovyShell(SpecificationBinding.getBinding(listener)).evaluate(file);
        } else {
        	if(!file.getName().contains("Behavior.groovy")){
        		System.out.println("You should consider ending your specification file (" +
        			file.getName() + ") with either Story or Behavior. " + 
        			"See easyb documentation for more details. ");
        	}
        	new GroovyShell(SpecificationBinding.getBinding(listener)).evaluate(file);
        } 
    }

    for(Report report : reports) {
      ReportWriter reportWriter;
      if(report.getFormat().concat(report.getType()).equals(Report.TXT_STORY)) {
        reportWriter = new TxtStoryReportWriter(report, listener);
      } else if(report.getFormat().concat(report.getType()).equals(Report.XML_BEHAVIOR)) {
        reportWriter = new XmlBehaviorReportWriter(report, listener);
      } else {
        reportWriter = new TerseReportWriter(report, listener);
      }

      reportWriter.writeReport();
    }

    listeners.add(listener);
    return listeners;
  }


  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Options options = getOptionsForMain();


    try {
        CommandLine commandLine = getCommandLineForMain(args, options);
        validateArguments(commandLine);

        SpecificationRunner runner = new SpecificationRunner(getConfiguredReports(commandLine));

        List<SpecificationListener> listeners = runner.runSpecification(getFileCollection(commandLine.getArgs()));

        notifyAndExitOnSpecificationFailures(listeners);

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
    if(commandLine.getArgs().length == 0) {
      throw new IllegalArgumentException("Required Arguments not passed in.");
    }
  }

  private static void notifyAndExitOnSpecificationFailures(List<SpecificationListener> listeners) {
    int totalSpecificationFailures = 0;
    for(SpecificationListener listener : listeners) {
      if(listener.hasBehaviorFailures()) {
        totalSpecificationFailures++;
      }
    }
    if(totalSpecificationFailures > 0) {
      System.out.println("specification failures detected!");
      System.exit(-6);
    }
  }

  private static List<Report> getConfiguredReports(CommandLine line) {

    List<Report> configuredReports = new ArrayList<Report>();
    if (line.hasOption(Report.XML_BEHAVIOR)) {
      Report report = new Report();
      report.setFormat(ReportFormat.XML.format());
      if(line.getOptionValue(Report.XML_BEHAVIOR) == null) {
        report.setLocation("easyb-behavior-report.xml");
      } else {
        report.setLocation(line.getOptionValue(Report.XML_BEHAVIOR));
      }
      report.setType(ReportType.BEHAVIOR.type());

      configuredReports.add(report);
    }

    if(line.hasOption(Report.TXT_STORY)) {
      Report report = new Report();
      report.setFormat(ReportFormat.TXT.format());
      if(line.getOptionValue(Report.TXT_STORY) == null) {
        report.setLocation("easyb-story-report.txt");
      } else {
        report.setLocation(line.getOptionValue(Report.TXT_STORY));
      }
      report.setType(ReportType.STORY.type());

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
    new HelpFormatter().printHelp("SpecificationRunner my/path/to/MyStory.groovy",options);
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
    Option xmlbehaviorreport = OptionBuilder.withArgName("file").hasArg()
        .withDescription("create a behavior report in xml format").create(Report.XML_BEHAVIOR);
    options.addOption(xmlbehaviorreport);

    //noinspection AccessStaticViaInstance
    Option storyreport = OptionBuilder.withArgName("file").hasArg()
        .withDescription("create a story report").create(Report.TXT_STORY);
    options.addOption(storyreport);

    return options;

  }

  private List<Report> addDefaultReports(List<Report> userConfiguredReports) {
    List<Report> configuredReports = new ArrayList<Report>();

    if(userConfiguredReports != null) {
      configuredReports.addAll(userConfiguredReports);
    }

    Report terseStoryReport = new Report();
    terseStoryReport.setFormat(ReportFormat.TERSE.format());
    terseStoryReport.setLocation("screen");
    terseStoryReport.setType(ReportType.STORY.type());
    configuredReports.add(terseStoryReport);

    return configuredReports;
  }

}
