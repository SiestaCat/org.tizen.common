package org.tizen.common.file;

import org.tizen.common.util.Assert;

public class PatternFilter {
  protected String pattern = "*";
  
  public String getPattern() {
    return this.pattern;
  }
  
  public void setPattern(String pattern) {
    Assert.notNull(pattern);
    this.pattern = pattern;
  }
}
