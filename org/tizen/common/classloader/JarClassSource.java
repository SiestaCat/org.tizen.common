package org.tizen.common.classloader;

import java.io.IOException;
import java.net.URL;
import org.tizen.common.util.Assert;

public class JarClassSource extends AbstractClassSource implements ClassSource {
  protected final String url;
  
  public JarClassSource(String url) {
    this.logger.info("URL :{}", url);
    Assert.notNull(url);
    this.url = url;
  }
  
  public URL getResource(String path) throws IOException {
    this.logger.trace("Path :{}", path);
    URL u = new URL("jar", "", String.valueOf(this.url) + "!/" + path);
    this.logger.info("Provide url :{}", u);
    return u;
  }
}
