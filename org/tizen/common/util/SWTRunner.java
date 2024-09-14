package org.tizen.common.util;

import org.tizen.common.Cabinet;

public abstract class SWTRunner<T> implements Runnable, Cabinet<T> {
  protected T data;
  
  public T getData() {
    return this.data;
  }
  
  public void setData(T data) {
    this.data = data;
  }
  
  public void run() {
    setData(process());
  }
  
  protected abstract T process();
}
