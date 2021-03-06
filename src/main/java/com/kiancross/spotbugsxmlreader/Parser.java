/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor.Version;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Parser {
  private Element root;
  private String[] sourceDirectories;

  /**
   * Construct a parser from an {@link InputStream}.
   *
   * @param input An {@link InputStream} containing XML.
   *
   * @throws ParserException Thrown if there is a syntax error in the XML.
   */
  public Parser(InputStream input) throws ParserException {
    Document document = getDocument(input);
    root = document.getDocumentElement();

    checkVersion(root);

    Element project = getElementByTagName(root, "Project");
    sourceDirectories = getSourceDirectories(project);
  }

  /**
   * Construct a parser from a file path.
   *
   * @param path Path to XML file.
   *
   * @return The parser instance.
   *
   * @throws FileNotFoundException Thrown if the given file path does not exist.
   *
   * @throws ParserException Thrown if there is a syntax error in the given XML file.
   */
  public static Parser fromFilePath(String path) throws FileNotFoundException, ParserException {
    File file = new File(path);
    InputStream stream = new FileInputStream(file);
    return new Parser(stream);
  }

  /**
   * Get all bug instances from the parsed XML file.
   *
   * @return List of {@link BugInstance} from the XML file.
   *
   * @throws ParserException Thrown if there are any semantic erorrs in the XML file.
   */
  public List<BugInstance> getBugInstances() throws ParserException {
    List<Element> elements = getElementsByTagName(root, "BugInstance");

    int numberOfBugInstances = elements.size();

    List<BugInstance> bugInstances = new ArrayList<BugInstance>(numberOfBugInstances);

    for (int i = 0; i < numberOfBugInstances; i++) {
      BugInstance instance = getBugInstance(elements.get(i));
      bugInstances.add(instance);
    }

    return bugInstances;
  }

  private void checkVersion(Element bugCollection) throws ParserException {
    if (!bugCollection.hasAttribute("version")) {
      throw new ParserException("Missing `version` attribute on `BugCollection`.");
    }

    final Version version = Version.parse(bugCollection.getAttribute("version"));

    if (
        version.compareTo(Version.parse("4.0.0")) < 0
        || version.compareTo(Version.parse("5.0.0")) >= 0
    ) {
      throw new ParserException(
        "Unsupported SpotBugs version `%s`. Supported versions: 4.0.0 <= v < 5.0.0.",
        version.toString()
      );
    }
  }

  private BugInstance getBugInstance(Element parent) throws ParserException {
    String message = getElementByTagName(parent, "LongMessage").getTextContent();

    Element sourceElement = getSourceElement(parent);

    String relativeSourcePath = sourceElement.getAttribute("relSourcepath");
    String category = parent.getAttribute("category");

    String path = relativeToAbsolutePath(relativeSourcePath);

    try {
      int startLine = Integer.parseInt(sourceElement.getAttribute("start"));
      int startColumn = Integer.parseInt(sourceElement.getAttribute("startBytecode"));
      int priority = Integer.parseInt(parent.getAttribute("priority"));
    
      BugInstance instance = new BugInstance();
    
      instance.setMessage(message);
      instance.setSourcePath(path);
      instance.setCategory(category);
      instance.setStartLine(startLine);
      instance.setStartColumn(startColumn);
      instance.setPriority(priority);

      return instance;

    } catch (NumberFormatException e) {
      throw new ParserException("Error when parsing integer: %s", e.getMessage());
    }
  }

  private Element getSourceElement(Element bugInstance) throws ParserException {
    List<Element> elements = getElementsByTagName(bugInstance, "SourceLine");

    for (Element element: elements) {
      if (element.hasAttribute("primary")) {
        return element;
      }
    }

    throw new ParserException("At least one `SourceLine` must have `primary` attribute.");
  }

  private String relativeToAbsolutePath(String relative) throws ParserException {
    String[] parts = relative.split(Pattern.quote(File.separator));

    String start = parts[0];

    for (int i = 0; i < sourceDirectories.length; i++) {
      if (sourceDirectories[i].endsWith(start)) {
        parts[0] = "";
        return Paths.get(sourceDirectories[i], parts).toString();
      }
    }

    throw new ParserException("Source path not included in source paths");
  }

  private String[] getSourceDirectories(Element project) throws ParserException {
    List<Element> sourceElements = getSourceDirectoryElements(project);

    int numberOfSources = sourceElements.size();

    String[] sourcePaths = new String[numberOfSources];

    for (int i = 0; i < numberOfSources; i++) {
      Node node = sourceElements.get(i);
      sourcePaths[i] = node.getTextContent();
    }

    return sourcePaths;
  }
  
  private List<Element> getSourceDirectoryElements(Element project) throws ParserException {
    List<Element> sourceElements = getElementsByTagName(project, "SrcDir");

    if (sourceElements.size() == 0) {
      throw new ParserException(
          "XML document should contain at least a single `SrcDir` node inside `Project`."
      );
    }

    return sourceElements;
  }

  private Element getElementByTagName(Element parent, String tagName) throws ParserException {
    List<Element> elements = getElementsByTagName(parent, tagName);

    if (elements.size() != 1) {
      throw new ParserException("There must be exactly one `%s` tag.", tagName);
    }

    return elements.get(0);
  }
  
  private List<Element> getElementsByTagName(Element parent, String tagName) throws
      ParserException {

    Node child = parent.getFirstChild();
    List<Element> elements = new ArrayList<Element>();
   
    while (child != null) {
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) child;

        if (element.getTagName().equals(tagName)) {
          elements.add(element);
        }
      }
        
      child = child.getNextSibling();
    }
      
    return elements;
  }

  private Document getDocument(InputStream input) throws ParserException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(input);
      document.getDocumentElement().normalize();

      return document;

    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new ParserException("Syntax error in XML: %s", e.getMessage());
    }
  }
}
