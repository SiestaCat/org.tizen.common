package org.tizen.common.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DirectoryClassSource extends AbstractClassSource implements ClassSource {
  protected final File root;
  
  public DirectoryClassSource(String root) {
    this.root = new File(root);
  }
  
  public URL getResource(String path) throws IOException {
    File file = new File(this.root, path);
    if (file.exists() && file.canRead())
      return file.toURI().toURL(); 
    return null;
  }
}
