package org.tizen.common.core.command.file;

import org.tizen.common.FactoryWithArgument;
import org.tizen.common.core.command.AbstractCommand;
import org.tizen.common.core.command.ExecutionContext;
import org.tizen.common.core.command.Executor;
import org.tizen.common.file.Filter;
import org.tizen.common.file.SimpleFileFilter;
import org.tizen.common.file.filter.WildCardFilterFactory;
import org.tizen.common.util.Assert;

public class FileHandlingCommand<T> extends AbstractCommand<T> {
  protected FactoryWithArgument<Filter, String> filterFactory = new WildCardFilterFactory();
  
  protected SimpleFileFilter filter = new SimpleFileFilter(true);
  
  protected String path;
  
  public void setFilterFactory(FactoryWithArgument<Filter, String> factory) {
    this.filterFactory = factory;
    this.logger.debug("Configured filter factory :{}", factory);
  }
  
  public void setFilter(SimpleFileFilter filter) {
    Assert.notNull(filter);
    this.filter = filter;
    this.logger.debug("Configured filter :{}", filter);
  }
  
  public void setIncludes(String[] includes) {
    this.filter.clearIncludes();
    if (includes != null) {
      byte b;
      int i;
      String[] arrayOfString;
      for (i = (arrayOfString = includes).length, b = 0; b < i; ) {
        String include = arrayOfString[b];
        this.filter.addIncludes(new Filter[] { this.filterFactory.create(include) });
        b++;
      } 
    } 
  }
  
  public void setExcludes(String[] excludes) {
    this.filter.clearExcludes();
    if (excludes != null) {
      byte b;
      int i;
      String[] arrayOfString;
      for (i = (arrayOfString = excludes).length, b = 0; b < i; ) {
        String exclude = arrayOfString[b];
        this.filter.addExcludes(new Filter[] { this.filterFactory.create(exclude) });
        b++;
      } 
    } 
  }
  
  public String getPath() {
    return this.path;
  }
  
  public void setPath(String path) {
    this.path = path;
  }
  
  public void run(Executor executor, ExecutionContext context) throws Exception {
    throw new UnsupportedOperationException();
  }
  
  public void undo(Executor executor, ExecutionContext context) throws Exception {
    throw new UnsupportedOperationException();
  }
}
