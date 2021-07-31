/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

public class ParserException extends Exception {
  public ParserException(String message, Object... args) {
    super(String.format(message, args));
  }
}
