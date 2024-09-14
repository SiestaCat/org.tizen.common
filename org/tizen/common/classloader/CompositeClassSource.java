package org.tizen.common.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class CompositeClassSource extends AbstractClassSource implements ClassSource {
  protected final ArrayList<ClassSource> sources = new ArrayList<>();
  
  public CompositeClassSource(ClassSource... sources) {
    this.sources.addAll(Arrays.asList(sources));
  }
  
  public URL getResource(String path) throws IOException {
    for (ClassSource source : this.sources) {
      URL url = source.getResource(path);
      if (url != null)
        return url; 
    } 
    return null;
  }
  
  public Collection<URL> getResources(String path) throws IOException {
    ArrayList<URL> ret = new ArrayList<>();
    for (ClassSource source : this.sources) {
      if (source instanceof CompositeClassSource) {
        ret.addAll(((CompositeClassSource)source).getResources(path));
        continue;
      } 
      URL url = source.getResource(path);
      if (url == null)
        continue; 
      ret.add(url);
    } 
    return ret;
  }
}
