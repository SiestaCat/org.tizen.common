package org.tizen.common.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MapUtil {
  public static int length(Map<?, ?> map) {
    if (map == null)
      return 0; 
    return map.size();
  }
  
  public static boolean isEmpty(Map<?, ?> map) {
    return (length(map) == 0);
  }
  
  public static <K, V> Map<K, V> asMap(Object[] keys, Object[] values) {
    HashMap<K, V> map = new HashMap<>();
    if (keys == null || values == null)
      return map; 
    for (int i = 0, n = Math.min(keys.length, values.length); i < n; i++)
      map.put((K)keys[i], (V)values[i]); 
    return map;
  }
  
  public static <A, B> Map<A, B> asMap(Object[][] objs) {
    HashMap<A, B> map = new HashMap<>();
    for (int i = 0, n = ArrayUtil.size(objs); i < n; i++) {
      if (2 == (objs[i]).length)
        map.put((A)objs[i][0], (B)objs[i][1]); 
    } 
    return map;
  }
  
  public static void mergePropertiesIntoMap(Properties props, Map<Object, Object> map) {
    Assert.notNull(map);
    if (props == null)
      return; 
    Enumeration<?> en = props.propertyNames();
    while (en.hasMoreElements()) {
      String key = (String)en.nextElement();
      map.put(key, props.getProperty(key));
    } 
  }
  
  public static String toString(Map<?, ?> map) {
    if (map == null)
      return "<<null>>"; 
    StringBuilder buffer = new StringBuilder();
    buffer.append("{");
    if (!map.isEmpty()) {
      buffer.append("\n");
      for (Object key : map.keySet()) {
        buffer.append("\t");
        buffer.append(key.toString());
        buffer.append("=");
        buffer.append(map.get(key));
        buffer.append("\n");
      } 
    } 
    buffer.append("}");
    return buffer.toString();
  }
}
