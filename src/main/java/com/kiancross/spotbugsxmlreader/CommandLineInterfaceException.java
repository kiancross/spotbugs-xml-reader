/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

public class CommandLineInterfaceException extends Exception {
  public CommandLineInterfaceException(String message, Object... args) {
    super(String.format(message, args));
  }
}
