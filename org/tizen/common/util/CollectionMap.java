package org.tizen.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionMap<K, V> {
  protected final Map<K, Collection<V>> map;
  
  public CollectionMap() {
    this(new LinkedHashMap<>());
  }
  
  public CollectionMap(Map<K, Collection<V>> map) {
    this.map = map;
  }
  
  protected Collection<V> createValues() {
    return new ArrayList<>();
  }
  
  public void put(K key, V value) {
    Collection<V> values = this.map.get(key);
    if (values == null) {
      values = createValues();
      this.map.put(key, values);
    } 
    values.add(value);
  }
  
  public Collection<K> keySet() {
    return this.map.keySet();
  }
  
  public Collection<V> get(K key) {
    return this.map.get(key);
  }
  
  public Collection<V> set(K key, Collection<V> values) {
    return this.map.put(key, values);
  }
  
  public Collection<V> remove(K key) {
    return this.map.remove(key);
  }
  
  public Collection<V> remove(K key, V value) {
    Collection<V> values = this.map.get(key);
    values.remove(value);
    if (values.isEmpty()) {
      this.map.remove(key);
      return null;
    } 
    return values;
  }
  
  public void clear() {
    this.map.clear();
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("CollectionMap[");
    boolean bInit = false;
    for (K key : this.map.keySet()) {
      if (bInit)
        buffer.append(", "); 
      bInit = true;
      buffer.append(key);
      buffer.append('=');
      buffer.append(this.map.get(key));
    } 
    buffer.append("]");
    return buffer.toString();
  }
}
