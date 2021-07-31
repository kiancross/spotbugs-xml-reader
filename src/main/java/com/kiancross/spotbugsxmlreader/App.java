/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import java.util.List;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

public class App {
  private static boolean printBugInstances(List<BugInstance> instances, int severityThreshold) {
    boolean error = false;

    for (BugInstance instance: instances) {
      System.out.println(instance.getLogEntry(severityThreshold));
      error = error || instance.isError(severityThreshold);
    }

    return error;
  }

  private static String getVersion() throws IOException {
    InputStream versionStream = App.class.getClassLoader().getResourceAsStream("version.txt");

    String version = "unknown";

    if (versionStream != null) {
      version = new String(versionStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    return version;
  }

  private static boolean handleAlternateFlags(CommandLineInterface cli) throws IOException {
    if (cli.shouldDisplayHelp()) {
      cli.printHelp();
      return true;

    } else if (cli.shouldDisplayVersion()) {
      System.out.println(getVersion());
      return true;
    }

    return false;
  }

  private static boolean handleMainProgramme(CommandLineInterface cli) throws CommandLineInterfaceException, ParserException {
    try {
      Parser parser = Parser.fromFilePath(cli.getReportPath());

      List<BugInstance> bugInstances = parser.getBugInstances();
      int severityThreshold = cli.getErrorSeverityThreshold();

      return !printBugInstances(bugInstances, severityThreshold);

    } catch (FileNotFoundException e) {
      throw new CommandLineInterfaceException(e.getMessage());
    }
  }

  public static void main(String[] args) {
    CommandLineInterface cli = new CommandLineInterface();

    try {

      cli.parse(args);

      if (handleAlternateFlags(cli)) {
        System.exit(0);
      }

      System.exit(handleMainProgramme(cli) ? 0 : 1);

    } catch (CommandLineInterfaceException e) {
      System.err.println(e.getMessage());
      System.out.println("");
      cli.printHelp();

    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
      
    System.exit(1);
  }
}
