package org.tizen.common;

public interface Adaptable {
  <T> T adapt(Class<T> paramClass);
}
