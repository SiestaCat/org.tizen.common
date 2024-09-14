package org.tizen.common.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractClassSource implements ClassSource {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  public InputStream getResourceAsStream(String path) throws IOException {
    this.logger.trace("Path :{}", path);
    URL url = getResource(path);
    this.logger.trace("URL :{}", url);
    return (url != null) ? url.openStream() : null;
  }
}
