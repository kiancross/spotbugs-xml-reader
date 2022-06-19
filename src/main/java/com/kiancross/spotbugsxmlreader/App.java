/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Entry class to application.
 */
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

    // If (for whatever reason) the version.txt file does
    // not exist, we fall back gracefully to 'unknown'.
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

  private static boolean handleMainProgramme(CommandLineInterface cli) throws
      CommandLineInterfaceException, ParserException {

    try {
      Parser parser = Parser.fromFilePath(cli.getReportPath());

      List<BugInstance> bugInstances = parser.getBugInstances();
      int severityThreshold = cli.getErrorSeverityThreshold();

      return !printBugInstances(bugInstances, severityThreshold);

    } catch (FileNotFoundException e) {
      throw new CommandLineInterfaceException(e.getMessage());
    }
  }

  /**
   * Entry function to application.
   *
   * @param args Arguments passed from command line.
   */
  public static void main(String[] args) throws Exception {
    CommandLineInterface cli = new CommandLineInterface();

    try {

      cli.parse(args);

      if (handleAlternateFlags(cli)) {
        return;
      }

      if (handleMainProgramme(cli)) {
        return;
      }

    } catch (CommandLineInterfaceException e) {
      System.err.println(e.getMessage());
      System.out.println("");
      cli.printHelp();
    }
      
    throw new Exception();
  }
}
