package org.tizen.common.util;

import java.util.Iterator;

public class IteratingUtil {
  public static <T> void iterate(T obj, IteratingAdapter<T> adapter, IteratingRunner<T> runner) {
    if (obj == null)
      return; 
    Iterator<T> iter = adapter.adapt(obj);
    if (iter == null)
      return; 
    while (iter.hasNext()) {
      T child = iter.next();
      runner.run(child);
      iterate(child, adapter, runner);
    } 
  }
}
