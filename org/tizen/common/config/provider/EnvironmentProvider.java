package org.tizen.common.config.provider;

import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.config.PreferenceProvider;

public class EnvironmentProvider implements PreferenceProvider {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  public Collection<String> keys() {
    return Collections.unmodifiableCollection(System.getenv().keySet());
  }
  
  public String get(String key) {
    String value = System.getenv(key);
    this.logger.info("Key :{}, Value :{}", key, value);
    return value;
  }
  
  public void set(String key, String value) {
    throw new UnsupportedOperationException();
  }
}
