/*
 * Copyright (C) 2022 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandLineInterfaceTest {
  private CommandLineInterface commandLineInterface;

  @BeforeEach void initCommandLineInterface() {
    commandLineInterface = new CommandLineInterface();
  }

  @Test void noArgsThrows() {
    final String[] args = {};

    assertThrows(CommandLineInterfaceException.class, () -> {
      commandLineInterface.parse(args);
    });
  }
  
  @Test void helpShort() {
    final String[] args = {"-h"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });

    assertTrue(commandLineInterface.shouldDisplayHelp());
  }
  
  @Test void helpLong() {
    final String[] args = {"--help"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
    
    assertTrue(commandLineInterface.shouldDisplayHelp());
  }
  
  @Test void printHelp() {
    assertDoesNotThrow(() -> {
      commandLineInterface.printHelp();
    });
  }
  
  @Test void versionShort() {
    final String[] args = {"-v"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
    
    assertTrue(commandLineInterface.shouldDisplayVersion());
  }
  
  @Test void versionLong() {
    final String[] args = {"--version"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
    
    assertTrue(commandLineInterface.shouldDisplayVersion());
  }
  
  @Test void fileShortNoValueThrows() {
    final String[] args = {"-f"};

    assertThrows(CommandLineInterfaceException.class, () -> {
      commandLineInterface.parse(args);
    });
  }
  
  @Test void fileLongNoValueThrows() {
    final String[] args = {"--file"};

    assertThrows(CommandLineInterfaceException.class, () -> {
      commandLineInterface.parse(args);
    });
  }
  
  @Test void fileValueCorrect() {
    final String[] args = {"-f", "foo"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });

    assertEquals("foo", commandLineInterface.getReportPath());
  }
  
  @Test void thresholdShortNoValueThrows() {
    final String[] args = {"-f", "foo", "-t"};

    assertThrows(CommandLineInterfaceException.class, () -> {
      commandLineInterface.parse(args);
    });
  }
  
  @Test void thresholdLongNoValueThrows() {
    final String[] args = {"-f", "foo", "--error-threshold"};

    assertThrows(CommandLineInterfaceException.class, () -> {
      commandLineInterface.parse(args);
    });
  }
  
  @Test void thresholdValueCorrect() {
    final String[] args = {"-f", "foo", "-t", "2"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
    
    assertDoesNotThrow(() -> {
      assertEquals(2, commandLineInterface.getErrorSeverityThreshold());
    });
  }
  
  @Test void defualtThresholdValueCorrect() {
    final String[] args = {"-f", "foo"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
    
    assertDoesNotThrow(() -> {
      assertEquals(2, commandLineInterface.getErrorSeverityThreshold());
    });
  }
  
  @Test void thresholdValueStringThrows() {
    final String[] args = {"-f", "foo", "-t", "foo"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
    
    assertThrows(CommandLineInterfaceException.class, () -> {
      commandLineInterface.getErrorSeverityThreshold();
    });
  }
  
  @Test void thresholdValueLowThrows() {
    final String[] args = {"-f", "foo", "-t", "-1"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
    
    assertThrows(CommandLineInterfaceException.class, () -> {
      commandLineInterface.getErrorSeverityThreshold();
    });
  }
  
  @Test void thresholdValueHighThrows() {
    final String[] args = {"-f", "foo", "-t", "4"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
    
    assertThrows(CommandLineInterfaceException.class, () -> {
      commandLineInterface.getErrorSeverityThreshold();
    });
  }
  
  @Test void noFileThrows() {
    final String[] args = {"-t", "0"};

    assertThrows(CommandLineInterfaceException.class, () -> {
      commandLineInterface.parse(args);
    });
  }
}
