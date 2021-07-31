/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import java.util.List;

public class App {
  private static void printBugInstances(List<BugInstance> instances, int severityThreshold) {
    for (BugInstance instance: instances) {
      System.out.println(instance.getLogEntry(severityThreshold));
    }
  }

  public static void main(String[] args) {
    CommandLineInterface cli = new CommandLineInterface();

    try {

      cli.parse(args);

      Parser parser = Parser.fromFilePath(cli.getReportPath());

      printBugInstances(parser.getBugInstances(), cli.getErrorSeverityThreshold());

    } catch (CommandLineInterfaceException e) {
      System.err.println(e.getMessage());
      System.out.println("");
      cli.printHelp();

    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }
}
