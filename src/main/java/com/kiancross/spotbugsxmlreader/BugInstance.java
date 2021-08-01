/*
 * Copyright (C) 2021 Kian Cross
 */

package com.kiancross.spotbugsxmlreader;

public class BugInstance {
  private String message;
  private String sourcePath;
  private String category;

  private int startLine;
  private int startColumn;
  private int priority;

  public void setMessage(String message) {
    this.message = message;
  }
  
  public void setSourcePath(String sourcePath) {
    this.sourcePath = sourcePath;
  }
  
  public void setCategory(String category) {
    this.category = category;
  }
  
  public void setStartLine(int startLine) {
    this.startLine = startLine;
  }
  
  public void setStartColumn(int startColumn) {
    this.startColumn = startColumn;
  }
  
  public void setPriority(int priority) {
    this.priority = priority;
  }

  /**
   * Get a log file entry representing the bug instance. 
   *
   * @param thresholdSeverity The severity threshold to use for error/warning tags.
   *
   * @return The log file entry.
   */
  public String getLogEntry(int thresholdSeverity) {
    return String.format(
      "(%s) %s [%s] %s",
      getSeverity(thresholdSeverity),
      getSourceIdentifier(),
      category,
      message
    );
  }

  public boolean isError(int thresholdSeverity) {
    return priority <= thresholdSeverity;
  }

  private String getSourceIdentifier() {
    return String.format("%s:%d:%d", sourcePath, startLine, startColumn);
  }

  private String getSeverity(int thresholdSeverity) {
    return isError(thresholdSeverity) ? "ERROR" : "WARNING";
  }
}
