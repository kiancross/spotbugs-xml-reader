/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

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
      new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'></BugCollection>"
        + "<BugCollection version='4.5.0'></BugCollection>"
      ));
    });
  }
  
  @Test void nonExistantProjectThrows() {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream("<BugCollection version='4.5.0'><foo></foo></BugCollection>"));
    });
  }
  
  @Test void duplicateProjectThrows() {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'><Project></Project> <Project></Project></BugCollection>"
      ));
    });
  }
  
  @Test void nonExistantSourcesThrows() {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'><Project> <foo></foo> </Project></BugCollection>"
      ));
    });
  }
  
  @Test void singleSourcesValid() {
    assertDoesNotThrow(() -> {
      new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'><Project> <SrcDir>foo</SrcDir> </Project></BugCollection>"
      ));
    });
  }
  
  @Test void doubleSourcesValid() {
    assertDoesNotThrow(() -> {
      new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'>"
        + "<Project> <SrcDir>foo</SrcDir> <SrcDir>bar</SrcDir> </Project>"
        + "</BugCollection>"
      ));
    });
  }
  
  @Test void noBugsValid() throws ParserException {
    final Parser parser = new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'><Project> <SrcDir>foo</SrcDir> </Project></BugCollection>"
    ));

    assertEquals(0, parser.getBugInstances().size());
  }
  
  @Test void oneBugValid() throws ParserException {
    final Parser parser = new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'>"
        + "  <Project> <SrcDir>foo</SrcDir> </Project>"
        + "  <BugInstance priority='2' category='bar'>"
        + "    <LongMessage>bat</LongMessage>"
        + "    <SourceLine primary='true' start='35' startBytecode='11' relSourcepath='foo'>"
        + "      <Message>bat</Message>"
        + "    </SourceLine>"
        + "  </BugInstance>"
        + "</BugCollection>"
    ));
    
    final List<BugInstance> bugs = parser.getBugInstances();

    assertEquals(1, bugs.size());

    final BugInstance bug = bugs.get(0);

    assertEquals("(WARNING) foo:35:11 [bar] bat", bug.getLogEntry(0));
  }
  
  @Test void noPrimaryThrows() throws ParserException {
    final Parser parser = new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'>"
        + "  <Project> <SrcDir>foo</SrcDir> </Project>"
        + "  <BugInstance priority='2' category='bar'>"
        + "    <LongMessage>bat</LongMessage>"
        + "    <SourceLine start='35' startBytecode='11' relSourcepath='foo'>"
        + "      <Message>bat</Message>"
        + "    </SourceLine>"
        + "  </BugInstance>"
        + "</BugCollection>"
    ));

    assertThrows(ParserException.class, () -> {
      parser.getBugInstances();
    });
  }
  
  @Test void invalidPriorityThrows() throws ParserException {
    final Parser parser = new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'>"
        + "  <Project> <SrcDir>foo</SrcDir> </Project>"
        + "  <BugInstance priority='bad' category='bar'>"
        + "    <LongMessage>bat</LongMessage>"
        + "    <SourceLine primary='true' start='35' startBytecode='11' relSourcepath='foo'>"
        + "      <Message>bat</Message>"
        + "    </SourceLine>"
        + "  </BugInstance>"
        + "</BugCollection>"
    ));

    assertThrows(ParserException.class, () -> {
      parser.getBugInstances();
    });
  }
  
  @Test void invalidStartThrows() throws ParserException {
    final Parser parser = new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'>"
        + "  <Project> <SrcDir>foo</SrcDir> </Project>"
        + "  <BugInstance priority='foo' category='bar'>"
        + "    <LongMessage>bat</LongMessage>"
        + "    <SourceLine primary='true' start='bad' startBytecode='11' relSourcepath='foo'>"
        + "      <Message>bat</Message>"
        + "    </SourceLine>"
        + "  </BugInstance>"
        + "</BugCollection>"
    ));

    assertThrows(ParserException.class, () -> {
      parser.getBugInstances();
    });
  }
  
  @Test void invalidStartBytecodeThrows() throws ParserException {
    final Parser parser = new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'>"
        + "  <Project> <SrcDir>foo</SrcDir> </Project>"
        + "  <BugInstance priority='foo' category='bar'>"
        + "    <LongMessage>bat</LongMessage>"
        + "    <SourceLine primary='true' start='11' startBytecode='bad' relSourcepath='foo'>"
        + "      <Message>bat</Message>"
        + "    </SourceLine>"
        + "  </BugInstance>"
        + "</BugCollection>"
    ));

    assertThrows(ParserException.class, () -> {
      parser.getBugInstances();
    });
  }
  
  @Test void invalidRelSourcepathThrows() throws ParserException {
    final Parser parser = new Parser(getTestInputStream(
        "<BugCollection version='4.5.0'>"
        + "  <Project> <SrcDir>foo</SrcDir> </Project>"
        + "  <BugInstance priority='foo' category='bar'>"
        + "    <LongMessage>bat</LongMessage>"
        + "    <SourceLine primary='true' start='11' startBytecode='10' relSourcepath='bar'>"
        + "      <Message>bat</Message>"
        + "    </SourceLine>"
        + "  </BugInstance>"
        + "</BugCollection>"
    ));

    assertThrows(ParserException.class, () -> {
      parser.getBugInstances();
    });
  }

  @Test void versionLowThrows() throws ParserException {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream(
          "<BugCollection version='3.9.9'>"
          + "  <Project> <SrcDir>foo</SrcDir> </Project>"
          + "  <BugInstance priority='2' category='bar'>"
          + "    <LongMessage>bat</LongMessage>"
          + "    <SourceLine primary='true' start='35' startBytecode='11' relSourcepath='foo'>"
          + "      <Message>bat</Message>"
          + "    </SourceLine>"
          + "  </BugInstance>"
          + "</BugCollection>"
      ));
    });
  }

  @Test void versionHighThrows() throws ParserException {
    assertThrows(ParserException.class, () -> {
      new Parser(getTestInputStream(
          "<BugCollection version='5.0.0'>"
          + "  <Project> <SrcDir>foo</SrcDir> </Project>"
          + "  <BugInstance priority='2' category='bar'>"
          + "    <LongMessage>bat</LongMessage>"
          + "    <SourceLine primary='true' start='35' startBytecode='11' relSourcepath='foo'>"
          + "      <Message>bat</Message>"
          + "    </SourceLine>"
          + "  </BugInstance>"
          + "</BugCollection>"
      ));
    });
  }
}
