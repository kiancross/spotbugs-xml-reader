/*
 * Copyright (C) 2022 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandLineInterfaceExceptionTest {
  @Test void message() {
    final CommandLineInterfaceException exception
        = new CommandLineInterfaceException("-%s-", "foo");
    assertEquals("-foo-", exception.getMessage());
  }
}

