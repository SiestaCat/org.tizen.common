package org.tizen.common.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface ClassSource {
  URL getResource(String paramString) throws IOException;
  
  InputStream getResourceAsStream(String paramString) throws IOException;
}
