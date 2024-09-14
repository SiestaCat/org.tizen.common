package org.tizen.common.file.filter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.file.Filter;
import org.tizen.common.file.PatternFilter;

public class WildCardFilter extends PatternFilter implements Filter {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  public WildCardFilter() {}
  
  public WildCardFilter(String pattern) {
    setPattern(pattern);
  }
  
  public boolean accept(String cwd, String path) {
    this.logger.trace("Current working directory :{}, Path :{}", cwd, path);
    String name = path;
    this.logger.trace("Name :{}", name);
    return FilenameUtils.wildcardMatch(name, getPattern());
  }
  
  public String toString() {
    return String.valueOf(WildcardFileFilter.class.getName()) + "[" + getPattern() + "]";
  }
}
