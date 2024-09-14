package org.tizen.common.launch.context;

import java.util.ArrayList;
import java.util.HashMap;

public class LaunchContext implements ILaunchContext<Object> {
  protected HashMap<Object, Object> ctxmap = new HashMap<>();
  
  public Object[] getKeys() {
    ArrayList<Object> keys = new ArrayList();
    keys.addAll(this.ctxmap.keySet());
    return keys.toArray();
  }
  
  public Object getValue(Object key) {
    return this.ctxmap.get(key);
  }
  
  public Object setValue(Object key, Object value) {
    return this.ctxmap.put(key, value);
  }
}
