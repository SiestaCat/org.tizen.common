package org.tizen.common.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.IOUtil;

public class CustomClassLoader extends SecureClassLoader {
  protected Logger logger = LoggerFactory.getLogger(getClass());
  
  protected final ClassLoader parent;
  
  protected final CompositeClassSource source;
  
  public CustomClassLoader(ClassSource... sources) {
    this(null, sources);
  }
  
  public CustomClassLoader(ClassLoader parent, ClassSource... sources) {
    this.parent = parent;
    this.source = new CompositeClassSource(sources);
  }
  
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    String path = String.valueOf(name.replace('.', '/')) + ".class";
    URL url = findResource(path);
    if (url == null)
      return super.findClass(name); 
    InputStream in = null;
    try {
      in = url.openStream();
      byte[] bytes = IOUtil.getBytes(in);
      return defineClass(name, bytes, 0, bytes.length);
    } catch (IOException e) {
      throw new ClassNotFoundException("Class can't be loaded", e);
    } finally {
      IOUtil.tryClose(new Object[] { in });
    } 
  }
  
  public URL getResource(String name) {
    this.logger.trace("Resource Name :{}", name);
    URL url = findResource(name);
    if (url == null)
      if (this.parent != null)
        url = this.parent.getResource(name);  
    return url;
  }
  
  public Enumeration<URL> getResources(String name) throws IOException {
    return Collections.enumeration(this.source.getResources(name));
  }
  
  protected URL findResource(String name) {
    this.logger.trace("Resource Name :{}", name);
    try {
      return this.source.getResource(name);
    } catch (IOException iOException) {
      return null;
    } 
  }
}
