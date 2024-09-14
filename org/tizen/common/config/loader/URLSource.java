package org.tizen.common.config.loader;

import java.net.URL;
import java.util.Iterator;

public class URLSource implements Source {
  protected final URL url;
  
  public URLSource(URL url) {
    this.url = url;
  }
  
  public Iterator<URL> iterator() {
    return null;
  }
}
