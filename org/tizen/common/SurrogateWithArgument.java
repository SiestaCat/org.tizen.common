package org.tizen.common;

public interface SurrogateWithArgument<T, A> {
  T getAdapter(A paramA);
}
