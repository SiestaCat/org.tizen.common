package org.tizen.common.util.url.classpath;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.ObjectUtil;

public class Handler extends URLStreamHandler {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected final String fqcnOfCaller;
  
  public Handler() {
    this.fqcnOfCaller = pickupCaller();
  }
  
  protected String pickupCaller() {
    StackTraceElement[] callStacks = (new Exception()).getStackTrace();
    boolean bUrl = false;
    byte b;
    int i;
    StackTraceElement[] arrayOfStackTraceElement1;
    for (i = (arrayOfStackTraceElement1 = callStacks).length, b = 0; b < i; ) {
      StackTraceElement element = arrayOfStackTraceElement1[b];
      this.logger.trace("Check stack :{}", element);
      String className = element.getClassName();
      this.logger.trace("Class :{}", className);
      if (ObjectUtil.equals(className, URL.class.getName())) {
        bUrl = true;
      } else if (bUrl) {
        return className;
      } 
      b++;
    } 
    return null;
  }
  
  protected URLConnection openConnection(URL u) throws IOException {
    return new Connection(u, this.fqcnOfCaller);
  }
}
