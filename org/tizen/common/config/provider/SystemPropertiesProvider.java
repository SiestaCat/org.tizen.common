package org.tizen.common.config.provider;

import java.util.Collection;
import java.util.Collections;
import org.tizen.common.config.PreferenceProvider;

public class SystemPropertiesProvider implements PreferenceProvider {
  public Collection<String> keys() {
    Collection<?> ret = System.getProperties().keySet();
    return (Collection)Collections.unmodifiableCollection(ret);
  }
  
  public String get(String key) {
    return System.getProperty(key);
  }
  
  public void set(String key, String value) {
    throw new UnsupportedOperationException();
  }
}
