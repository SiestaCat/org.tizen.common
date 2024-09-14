package org.tizen.common.config.loader;

import org.tizen.common.config.Loader;
import org.tizen.common.util.Assert;

public abstract class AbstractLoader implements Loader {
  protected final Source src;
  
  public AbstractLoader(Source src) {
    this.src = src;
    Assert.notNull(src);
  }
  
  protected Source getSource() {
    return this.src;
  }
}
