package org.tizen.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ThreadLocalMap<K, V> implements Map<K, V> {
  private ThreadLocal<Map<K, V>> threadLocal;
  
  public ThreadLocalMap() {
    this.threadLocal = new ThreadLocal<Map<K, V>>() {
        protected Map<K, V> initialValue() {
          return new HashMap<>();
        }
      };
  }
  
  public int size() {
    return ((Map)this.threadLocal.get()).size();
  }
  
  public boolean isEmpty() {
    return ((Map)this.threadLocal.get()).isEmpty();
  }
  
  public boolean containsKey(Object key) {
    return ((Map)this.threadLocal.get()).containsKey(key);
  }
  
  public boolean containsValue(Object value) {
    return ((Map)this.threadLocal.get()).containsValue(value);
  }
  
  public V get(Object key) {
    return (V)((Map)this.threadLocal.get()).get(key);
  }
  
  public V put(K key, V value) {
    return ((Map<K, V>)this.threadLocal.get()).put(key, value);
  }
  
  public V remove(Object key) {
    return (V)((Map)this.threadLocal.get()).remove(key);
  }
  
  public void putAll(Map<? extends K, ? extends V> m) {
    ((Map<K, V>)this.threadLocal.get()).putAll(m);
  }
  
  public void clear() {
    ((Map)this.threadLocal.get()).clear();
  }
  
  public Set<K> keySet() {
    return ((Map)this.threadLocal.get()).keySet();
  }
  
  public Collection<V> values() {
    return ((Map)this.threadLocal.get()).values();
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    return ((Map<K, V>)this.threadLocal.get()).entrySet();
  }
}
