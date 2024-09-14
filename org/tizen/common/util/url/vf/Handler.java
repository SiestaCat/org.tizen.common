package org.tizen.common.util.url.vf;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
  protected URLConnection openConnection(URL u) throws IOException {
    return new Connection(u);
  }
}
