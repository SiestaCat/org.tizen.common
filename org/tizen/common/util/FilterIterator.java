package org.tizen.common.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilterIterator<E> implements Iterator<E> {
  protected final Iterator<? extends E> impl;
  
  protected E acceptableElement;
  
  public FilterIterator(Object[] array) {
    this(Arrays.asList((E[])array));
  }
  
  public FilterIterator(Collection<? extends E> collection) {
    this.impl = (collection == null) ? null : collection.iterator();
  }
  
  public boolean hasNext() {
    if (this.impl == null)
      return false; 
    while (this.impl.hasNext()) {
      E element = this.impl.next();
      if (isAcceptable(element)) {
        this.acceptableElement = element;
        return true;
      } 
    } 
    return false;
  }
  
  public E next() {
    if (this.impl == null)
      throw new NoSuchElementException(); 
    return this.acceptableElement;
  }
  
  public void remove() {
    if (this.impl == null)
      throw new NoSuchElementException(); 
    this.impl.remove();
  }
  
  protected boolean isAcceptable(E element) {
    if (element == null)
      return false; 
    return true;
  }
}
