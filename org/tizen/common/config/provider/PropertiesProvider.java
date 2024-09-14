package org.tizen.common.config.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.tizen.common.config.PreferenceProvider;

public class PropertiesProvider implements PreferenceProvider {
  protected final Map<String, String> props;
  
  public PropertiesProvider() {
    this(new HashMap<>());
  }
  
  public PropertiesProvider(Properties props) {
    this(props);
  }
  
  public PropertiesProvider(Map<String, String> props) {
    this.props = props;
  }
  
  public Collection<String> keys() {
    Collection<?> ret = this.props.keySet();
    return (Collection)Collections.unmodifiableCollection(ret);
  }
  
  public String get(String key) {
    return this.props.get(key);
  }
  
  public void set(String key, String value) {
    this.props.put(key, value);
  }
}
