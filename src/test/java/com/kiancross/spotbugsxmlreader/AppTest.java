/*
 * Copyright (C) 2022 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AppTest {
  @Test void emptyThrows() {
    final String[] args = {};
    
    assertThrows(Exception.class, () -> {
      App.main(args);
    });
  }
  
  @Test void nonExistantFileThrows() {
    final String[] args = {"-f", "foo"};
    
    assertThrows(Exception.class, () -> {
      App.main(args);
    });
  }
  
  @Test void helpValid() {
    final String[] args = {"--help"};
    
    assertDoesNotThrow(() -> {
      App.main(args);
    });
  }
  
  @Test void versionValid() {
    final String[] args = {"--version"};
    
    assertDoesNotThrow(() -> {
      App.main(args);
    });
  }
  
  @Test void fromFileWarningValid() {
    final String[] args = {"-f", "src/test/resources/example-report.xml", "-t", "0"};
    
    assertDoesNotThrow(() -> {
      App.main(args);
    });
  }
}
