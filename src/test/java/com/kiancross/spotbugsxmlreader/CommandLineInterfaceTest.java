/*
 * Copyright (C) 2022 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  
  @Test void helpShortNoThrows() {
    final String[] args = {"-h"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
  }
  
  @Test void helpLongNoThrows() {
    final String[] args = {"--help"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
  }
  
  @Test void versionShortNoThrows() {
    final String[] args = {"-v"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
  }
  
  @Test void versionLongNoThrows() {
    final String[] args = {"--version"};

    assertDoesNotThrow(() -> {
      commandLineInterface.parse(args);
    });
  }
}
