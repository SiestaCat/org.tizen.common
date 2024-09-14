package org.tizen.common;

public interface AdaptableWithArgument {
  <T, A> T adapt(Class<T> paramClass, A paramA);
}
