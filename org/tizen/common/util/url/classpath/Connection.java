package org.tizen.common.util.url.classpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.Assert;
import org.tizen.common.util.StringUtil;

public class Connection extends URLConnection {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected String fqcn;
  
  protected URL internalUrl = null;
  
  protected Connection(URL url, String fqcn) {
    super(url);
    this.fqcn = fqcn;
    this.logger.debug("Fully qualified class name :{}", fqcn);
  }
  
  public void connect() throws IOException {
    Assert.isNull(this.internalUrl);
    String path = StringUtil.trimLeadingCharacter(getURL().getPath(), '/');
    Collection<ClassLoader> classLoaders = getClassLoaders();
    this.logger.debug("Classloaders :{}", classLoaders);
    for (ClassLoader cl : classLoaders) {
      URL url = cl.getResource(path);
      this.logger.trace("Check url for {} :{}", path, url);
      if (url == null)
        continue; 
      this.internalUrl = url;
      this.connected = true;
      return;
    } 
    if (this.fqcn == null) {
      this.connected = true;
      return;
    } 
    String packageName = StringUtil.removeLastSegment(this.fqcn, ".");
    String fullPath = String.valueOf(StringUtil.trimLeadingCharacter(packageName.replace('.', '/'), '/')) + "/" + path;
    this.logger.trace("Package name :{}, Full path :{}", packageName, fullPath);
    for (ClassLoader cl : classLoaders) {
      URL url = cl.getResource(fullPath);
      this.logger.trace("Check url for {} :{}", fullPath, url);
      if (url == null)
        continue; 
      this.internalUrl = url;
      this.connected = true;
      return;
    } 
  }
  
  protected Collection<ClassLoader> getClassLoaders() {
    LinkedHashSet<ClassLoader> classLoaders = new LinkedHashSet<>();
    if (Thread.currentThread().getContextClassLoader() != null)
      classLoaders.add(Thread.currentThread().getContextClassLoader()); 
    classLoaders.add(Connection.class.getClassLoader());
    classLoaders.add(ClassLoader.getSystemClassLoader());
    return classLoaders;
  }
  
  public InputStream getInputStream() throws IOException {
    if (!this.connected)
      connect(); 
    if (this.internalUrl == null)
      throw new IOException("resource not found"); 
    this.logger.trace("Open stream :{}", this.internalUrl);
    return this.internalUrl.openStream();
  }
}
