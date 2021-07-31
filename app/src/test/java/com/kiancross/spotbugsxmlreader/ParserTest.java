/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

class ParserTest {
  private InputStream getTestInputStream(String data) {
    return new ByteArrayInputStream(data.getBytes());
  }

  @Test void nonExistantPathThrows() {
    assertThrows(FileNotFoundException.class, () -> {
      Parser.fromFilePath("foo");
    });
  }
  
  @Test void nonExistantBugCollectionThrows() {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream("<foo></foo>"));
    });
  }
  
  @Test void duplicateBugCollectionThrows() {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream("<BugCollection></BugCollection> <BugCollection></BugCollection>"));
    });
  }
  
  @Test void nonExistantProjectThrows() {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream("<BugCollection><foo></foo></BugCollection>"));
    });
  }
  
  @Test void duplicateProjectThrows() {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream("<BugCollection><Project></Project> <Project></Project></BugCollection>"));
    });
  }
  
  @Test void nonExistantSourcesThrows() {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream("<BugCollection><Project> <foo></foo> </Project></BugCollection>"));
    });
  }
  
  @Test void singleSourcesValid() {
    assertDoesNotThrow(() -> {
      new Parser(getTestInputStream("<BugCollection><Project> <SrcDir>foo</SrcDir> </Project></BugCollection>"));
    });
  }
  
  @Test void doubleSourcesValid() {
    assertDoesNotThrow(() -> {
      new Parser(getTestInputStream("<BugCollection><Project> <SrcDir>foo</SrcDir> <SrcDir></SrcDir> </Project></BugCollection>"));
    });
  }
}
