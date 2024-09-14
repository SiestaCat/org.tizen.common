package org.tizen.common;

public interface Cabinet<T> extends Runnable {
  T getData();
  
  void setData(T paramT);
}
