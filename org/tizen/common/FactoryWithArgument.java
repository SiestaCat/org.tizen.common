package org.tizen.common;

public interface FactoryWithArgument<T, A> {
  T create(A paramA);
}
