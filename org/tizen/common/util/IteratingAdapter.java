package org.tizen.common.util;

import java.util.Iterator;

public interface IteratingAdapter<T> {
  Iterator<T> adapt(T paramT);
}
