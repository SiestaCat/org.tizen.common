package org.tizen.common.config.loader;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.commons.collections.iterators.EnumerationIterator;

public class ResourceSource implements Source {
  protected final String resourceName;
  
  public ResourceSource(String resourceName) {
    this.resourceName = resourceName;
  }
  
  public Iterator<URL> iterator() {
    try {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      Enumeration<URL> urls = cl.getResources(this.resourceName);
      return (Iterator<URL>)new EnumerationIterator(urls);
    } catch (IOException e) {
      e.printStackTrace();
      return Collections.<URL>emptySet().iterator();
    } 
  }
}
