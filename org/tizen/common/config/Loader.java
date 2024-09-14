package org.tizen.common.config;

import java.io.IOException;

public interface Loader {
  void load(String paramString) throws IOException;
}
