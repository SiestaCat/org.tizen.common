package org.tizen.common.file;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.CollectionUtil;

public class SimpleFileFilter implements Filter {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected List<Filter> includes = new ArrayList<>();
  
  protected List<Filter> excludes = new ArrayList<>();
  
  protected boolean bDefault = false;
  
  public SimpleFileFilter() {
    this(false);
  }
  
  public SimpleFileFilter(boolean bDefault) {
    this.bDefault = bDefault;
  }
  
  public void setDefault(boolean bDefault) {
    this.bDefault = bDefault;
  }
  
  public void clearIncludes() {
    this.includes.clear();
  }
  
  public void setIncludes(Filter... filters) {
    clearIncludes();
    addIncludes(filters);
  }
  
  public void addIncludes(Filter... filters) {
    byte b;
    int i;
    Filter[] arrayOfFilter;
    for (i = (arrayOfFilter = filters).length, b = 0; b < i; ) {
      Filter filter = arrayOfFilter[b];
      this.includes.add(filter);
      b++;
    } 
    this.logger.debug("Include filters[{}] added", CollectionUtil.toString(filters));
  }
  
  public void clearExcludes() {
    this.excludes.clear();
  }
  
  public void setExcludes(Filter... filters) {
    clearExcludes();
    addExcludes(filters);
  }
  
  public void addExcludes(Filter... filters) {
    byte b;
    int i;
    Filter[] arrayOfFilter;
    for (i = (arrayOfFilter = filters).length, b = 0; b < i; ) {
      Filter filter = arrayOfFilter[b];
      this.excludes.add(filter);
      b++;
    } 
    this.logger.trace("Excludes filters[{}] added", CollectionUtil.toString(filters));
  }
  
  public void removeExcludes(Filter... filters) {
    this.excludes = removeFilters(this.excludes, filters);
  }
  
  private List<Filter> removeFilters(List<Filter> savedFilters, Filter... removeFilters) {
    byte b;
    int i;
    Filter[] arrayOfFilter;
    for (i = (arrayOfFilter = removeFilters).length, b = 0; b < i; ) {
      Filter filter = arrayOfFilter[b];
      savedFilters.remove(filter);
      b++;
    } 
    return savedFilters;
  }
  
  public void removeIncludes(Filter... filters) {
    this.includes = removeFilters(this.includes, filters);
  }
  
  public boolean accept(String cwd, String path) {
    this.logger.trace("Current working directory :{}, Path :{}", cwd, path);
    if (this.excludes.isEmpty() && this.includes.isEmpty())
      return this.bDefault; 
    this.logger.trace("Including filters :{}", this.includes);
    for (Filter filter : this.includes) {
      if (filter.accept(cwd, path)) {
        this.logger.debug("Accept because of {}", filter);
        return true;
      } 
    } 
    if (this.excludes.isEmpty())
      return false; 
    this.logger.trace("Excluding filters :{}", this.excludes);
    for (Filter filter : this.excludes) {
      if (filter.accept(cwd, path)) {
        this.logger.debug("Deny because of {}", filter);
        return false;
      } 
    } 
    if (this.includes.isEmpty())
      return true; 
    return this.bDefault;
  }
  
  public String toString() {
    return String.valueOf(getClass().getSimpleName()) + "[" + this.includes.size() + "/" + this.excludes.size() + "]";
  }
  
  public Filter[] getExcludes() {
    return (this.excludes != null) ? this.excludes.<Filter>toArray(new Filter[0]) : null;
  }
  
  public Filter[] getIncludes() {
    return (this.includes != null) ? this.includes.<Filter>toArray(new Filter[0]) : null;
  }
}
