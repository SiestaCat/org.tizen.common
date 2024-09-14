package org.tizen.common.util.url.vf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.file.VirtualFileHandler;

public class Connection extends URLConnection {
  protected static VirtualFileHandler vfHandler = new VirtualFileHandler();
  
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  public static VirtualFileHandler getVirtualFileHandler() {
    return vfHandler;
  }
  
  public static void setVirtualFileHandler(VirtualFileHandler vfHandler) {
    Connection.vfHandler = vfHandler;
  }
  
  protected Connection(URL url) {
    super(url);
    this.logger.trace("URL :{}", url);
  }
  
  public void connect() throws IOException {
    this.logger.trace("connect");
  }
  
  public InputStream getInputStream() throws IOException {
    String path = this.url.getPath();
    this.logger.trace("Path :{}", path);
    return vfHandler.read(URLDecoder.decode(path, "UTF-8"));
  }
}
