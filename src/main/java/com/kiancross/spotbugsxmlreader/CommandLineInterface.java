/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class CommandLineInterface {
    
  private static final int defaultErrorThreshold = 2;
  private CommandLine commandLine;
  private Options options;

  public CommandLineInterface() {
    options = getOptions();
  }

  public void parse(String[] args) throws CommandLineInterfaceException {
    CommandLineParser parser = new DefaultParser();

    try {
      commandLine = parser.parse(options, args);

    } catch (ParseException e) {
      throw new CommandLineInterfaceException(e.getMessage());
    }
  }

  public void printHelp() {
    HelpFormatter formatter = new HelpFormatter();

    String header = "Display a SpotBugs XML report on the command line.\n\n";

    String footer = "\n\nPlease report issues at " +
                    "https://github.com/kiancross/spotbugs-xml-reader/issues";

    formatter.printHelp("spotbugs-xml-reader", header, options, footer, true);
  }

  public boolean shouldDisplayHelp() {
    return commandLine.hasOption("h");
  }
  
  public boolean shouldDisplayVersion() {
    return commandLine.hasOption("v");
  }
  
  public int getErrorSeverityThreshold() throws CommandLineInterfaceException {
    String errorThreshold = commandLine.getOptionValue("t");

    if (errorThreshold == null) {
      return defaultErrorThreshold;

    } else {
      try {
        int threshold = Integer.parseInt(errorThreshold);

        if (threshold < 0 || threshold > 3) {
          throw new NumberFormatException();
        }

        return threshold;

      } catch (NumberFormatException e) {
        throw new CommandLineInterfaceException("Invalid argument for `error-threshold`");
      }
    }
  }
  
  public String getReportPath() {
    return commandLine.getOptionValue("f");
  }

  private Options getOptions() {
    Options options = new Options();

    options.addOption(Option.builder("f")
      .longOpt("file")
      .hasArg(true)
      .argName("path")
      .desc("Path to XML report generated by SpotBugs.")
      .required()
      .build());
    
    options.addOption(Option.builder("t")
      .longOpt("error-threshold")
      .hasArg(true)
      .argName("severity")
      .desc(String.format(
              "Entries with a higher severity than this value will be treated as errors. " +
              "Possible values are: 0 (treat all entries as warnings), 1, 2 or 3. " +
              "Default: %d. Note that 1 is most severe and 3 is least severe.",
              defaultErrorThreshold
      )).build());

    options.addOption(Option.builder("h")
      .longOpt("help")
      .desc("Display this help information.")
      .build());
    
    options.addOption(Option.builder("v")
      .longOpt("version")
      .desc("Display the version number.")
      .build());

    return options;
  }
}
