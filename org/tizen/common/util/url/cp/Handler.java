package org.tizen.common.util.url.cp;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.tizen.common.util.url.classpath.Handler;

public class Handler extends Handler {
  protected URLConnection openConnection(URL u) throws IOException {
    return new Connection(u, this.fqcnOfCaller);
  }
}
