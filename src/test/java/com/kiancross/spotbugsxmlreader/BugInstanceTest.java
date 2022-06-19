/*
 * Copyright (C) 2022 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BugInstanceTest {
  private BugInstance bugInstance;

  @BeforeEach void initCommandLineInterface() {
    bugInstance = new BugInstance();
  }

  @Test void isErrorTrue() {
    bugInstance.setPriority(1);
    assertTrue(bugInstance.isError(1));
    assertTrue(bugInstance.isError(2));
    assertTrue(bugInstance.isError(3));
  }
  
  @Test void isErrorFalse() {
    bugInstance.setPriority(2);
    assertFalse(bugInstance.isError(0));
    assertFalse(bugInstance.isError(1));
  }
  
  @Test void getLogEntryWarning() {
    bugInstance.setMessage("foo");
    bugInstance.setSourcePath("bar");
    bugInstance.setCategory("baz");
    bugInstance.setStartLine(10);
    bugInstance.setStartColumn(20);
    bugInstance.setPriority(2);

    assertEquals("(WARNING) bar:10:20 [baz] foo", bugInstance.getLogEntry(0));
  }
  
  @Test void getLogEntryError() {
    bugInstance.setMessage("foo");
    bugInstance.setSourcePath("bar");
    bugInstance.setCategory("baz");
    bugInstance.setStartLine(10);
    bugInstance.setStartColumn(20);
    bugInstance.setPriority(2);

    assertEquals("(ERROR) bar:10:20 [baz] foo", bugInstance.getLogEntry(2));
  }
}

