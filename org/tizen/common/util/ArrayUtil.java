package org.tizen.common.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayUtil {
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  
  public static <T> boolean isEmpty(Object[] array) {
    if (array == null)
      return true; 
    return (array.length == 0);
  }
  
  public static <T> T pickupFirst(Object[] array) {
    if (isEmpty(array))
      return null; 
    return (T)array[0];
  }
  
  public static <T> T pickupLast(Object[] array) {
    if (isEmpty(array))
      return null; 
    return (T)array[array.length - 1];
  }
  
  public static <T> T[] newArray(Class<? extends T> type, int length) {
    return (T[])Array.newInstance(type, length);
  }
  
  public static class ArrayIterator<K> implements Iterator<K> {
    protected final K[] objs;
    
    protected final K[] origin;
    
    protected int index = 0;
    
    public ArrayIterator(Object[] objs) {
      this.origin = (K[])objs;
      this.objs = (this.origin == null) ? null : (K[])this.origin.clone();
    }
    
    public boolean hasNext() {
      if (this.objs == null)
        return false; 
      return (this.index < this.objs.length);
    }
    
    public K next() {
      if (this.objs == null)
        throw new NoSuchElementException(); 
      if (this.objs.length <= this.index)
        throw new NoSuchElementException(); 
      if (this.objs[this.index] != this.origin[this.index])
        throw new ConcurrentModificationException(); 
      return this.objs[this.index++];
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  public static <T> Iterator<T> iterator(Object[] array) {
    return new ArrayIterator<>((T[])array);
  }
  
  public static <T> void iterate(Object[] array, IteratingRunner<T> runner) {
    try {
      iterate(array, runner, true);
    } catch (InvocationTargetException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  public static <T> void iterate(Object[] array, IteratingRunner<T> runner, boolean bForceProcess) throws InvocationTargetException {
    if (runner == null)
      return; 
    byte b;
    int i;
    Object[] arrayOfObject;
    for (i = (arrayOfObject = safe(array)).length, b = 0; b < i; ) {
      T arg = (T)arrayOfObject[b];
      if (arg != null || bForceProcess)
        try {
          runner.run(arg);
        } catch (Throwable e) {
          if (!bForceProcess)
            throw new InvocationTargetException(e); 
        }  
      b++;
    } 
  }
  
  public static <T> T[] filter(Object[] array, IteratingAcceptor<T> runner) {
    try {
      return filter((T[])array, runner, true);
    } catch (InvocationTargetException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  public static <T> T[] filter(Object[] array, IteratingAcceptor<T> runner, boolean bForceProcess) throws InvocationTargetException {
    ArrayList<T> list = new ArrayList<>();
    byte b;
    int i;
    Object[] arrayOfObject;
    for (i = (arrayOfObject = safe(array)).length, b = 0; b < i; ) {
      T arg = (T)arrayOfObject[b];
      if (arg != null || bForceProcess)
        try {
          if (runner.accept(arg))
            list.add(arg); 
        } catch (Throwable e) {
          if (!bForceProcess)
            throw new InvocationTargetException(e); 
        }  
      b++;
    } 
    return list.toArray(newArray((Class)array.getClass().getComponentType(), list.size()));
  }
  
  public static Object[] toObjectArray(Object source) {
    if (source instanceof Object[])
      return (Object[])source; 
    if (source == null)
      return EMPTY_OBJECT_ARRAY; 
    if (!source.getClass().isArray())
      throw new IllegalArgumentException("source must be an array"); 
    int length = Array.getLength(source);
    if (length == 0)
      return EMPTY_OBJECT_ARRAY; 
    Class<?> wrapperType = null;
    for (int i = 0; wrapperType == null && i < length; i++) {
      Object obj = Array.get(source, 0);
      if (obj != null)
        wrapperType = obj.getClass(); 
    } 
    if (wrapperType == null)
      return (Object[])source; 
    Object[] newArray = 
      (Object[])Array.newInstance(wrapperType, length);
    for (int j = 0; j < length; j++)
      newArray[j] = Array.get(source, j); 
    return newArray;
  }
  
  public static int size(Object obj) {
    if (obj instanceof Object[])
      return size((Object[])obj); 
    if (obj instanceof boolean[])
      return size((boolean[])obj); 
    if (obj instanceof byte[])
      return size((byte[])obj); 
    if (obj instanceof char[])
      return size((char[])obj); 
    if (obj instanceof short[])
      return size((short[])obj); 
    if (obj instanceof int[])
      return size((int[])obj); 
    if (obj instanceof long[])
      return size((long[])obj); 
    if (obj instanceof float[])
      return size((float[])obj); 
    if (obj instanceof double[])
      return size((double[])obj); 
    if (obj instanceof Collection)
      return CollectionUtil.size((Collection)obj); 
    return 0;
  }
  
  public static int size(boolean[] objs) {
    if (objs == null)
      return 0; 
    return objs.length;
  }
  
  public static int size(byte[] objs) {
    if (objs == null)
      return 0; 
    return objs.length;
  }
  
  public static int size(char[] objs) {
    if (objs == null)
      return 0; 
    return objs.length;
  }
  
  public static int size(short[] objs) {
    if (objs == null)
      return 0; 
    return objs.length;
  }
  
  public static int size(int[] objs) {
    if (objs == null)
      return 0; 
    return objs.length;
  }
  
  public static int size(long[] objs) {
    if (objs == null)
      return 0; 
    return objs.length;
  }
  
  public static int size(float[] objs) {
    if (objs == null)
      return 0; 
    return objs.length;
  }
  
  public static int size(double[] objs) {
    if (objs == null)
      return 0; 
    return objs.length;
  }
  
  public static <T> int size(Object[] objs) {
    if (objs == null)
      return 0; 
    return objs.length;
  }
  
  public static <T> T[] add(Object[] array, T obj) {
    Class<?> compType = Object.class;
    if (array != null) {
      compType = array.getClass().getComponentType();
    } else if (obj != null) {
      compType = obj.getClass();
    } 
    int newArrLength = size(array) + 1;
    Object[] newArr = 
      (Object[])Array.newInstance(compType, newArrLength);
    if (array != null)
      System.arraycopy(array, 0, newArr, 0, array.length); 
    newArr[newArrLength - 1] = obj;
    return (T[])newArr;
  }
  
  public static <T> T[] prepend(Object[] array, T obj) {
    Class<?> compType = Object.class;
    if (array != null) {
      compType = array.getClass().getComponentType();
    } else if (obj != null) {
      compType = obj.getClass();
    } 
    int newArrLength = size(array) + 1;
    Object[] newArr = 
      (Object[])Array.newInstance(compType, newArrLength);
    if (array != null)
      System.arraycopy(array, 0, newArr, 1, array.length); 
    newArr[0] = obj;
    return (T[])newArr;
  }
  
  public static <T> T[] remove(Object[] array, int start, int end) {
    Assert.notNull(array);
    Assert.isTrue(
        (start <= end), 
        "start indnex(" + start + ") is greater than end index(" + end + ")");
    int startIndex = Math.max(0, start);
    int endIndex = Math.min(array.length, end);
    if (endIndex <= startIndex)
      return (T[])array; 
    Class<?> compType = array.getClass().getComponentType();
    int removeSize = endIndex - startIndex;
    int newArrLength = size(array) - removeSize;
    Object[] newArr = 
      (Object[])Array.newInstance(compType, newArrLength);
    System.arraycopy(array, 0, newArr, 0, startIndex);
    System.arraycopy(array, endIndex, newArr, startIndex, array.length - endIndex);
    return (T[])newArr;
  }
  
  public static <T> T[] remove(Object[] array, int index) {
    return remove(array, index, index + 1);
  }
  
  public static <K, V> boolean contains(Object[] array, V element) {
    if (array == null)
      return false; 
    byte b;
    int i;
    Object[] arrayOfObject;
    for (i = (arrayOfObject = array).length, b = 0; b < i; ) {
      Object candidate = arrayOfObject[b];
      if (ObjectUtil.equals(candidate, element))
        return true; 
      b++;
    } 
    return false;
  }
  
  public static <K> K get(Object[] array, int index) {
    if (array == null)
      return null; 
    if (index < 0)
      return null; 
    if (array.length <= index)
      return null; 
    return (K)array[index];
  }
  
  public static Boolean[] convertToWrapper(boolean[] array) {
    int nArray = size(array);
    Boolean[] ret = new Boolean[nArray];
    for (int i = 0; i < nArray; i++)
      ret[i] = Boolean.valueOf(array[i]); 
    return ret;
  }
  
  public static Byte[] convertToWrapper(byte[] array) {
    int nArray = size(array);
    Byte[] ret = new Byte[nArray];
    for (int i = 0; i < nArray; i++)
      ret[i] = Byte.valueOf(array[i]); 
    return ret;
  }
  
  public static Character[] convertToWrapper(char[] array) {
    int nArray = size(array);
    Character[] ret = new Character[nArray];
    for (int i = 0; i < nArray; i++)
      ret[i] = Character.valueOf(array[i]); 
    return ret;
  }
  
  public static Short[] convertToWrapper(short[] array) {
    int nArray = size(array);
    Short[] ret = new Short[nArray];
    for (int i = 0; i < nArray; i++)
      ret[i] = Short.valueOf(array[i]); 
    return ret;
  }
  
  public static Integer[] convertToWrapper(int[] array) {
    int nArray = size(array);
    Integer[] ret = new Integer[nArray];
    for (int i = 0; i < nArray; i++)
      ret[i] = Integer.valueOf(array[i]); 
    return ret;
  }
  
  public static Long[] convertToWrapper(long[] array) {
    int nArray = size(array);
    Long[] ret = new Long[nArray];
    for (int i = 0; i < nArray; i++)
      ret[i] = Long.valueOf(array[i]); 
    return ret;
  }
  
  public static Float[] convertToWrapper(float[] array) {
    int nArray = size(array);
    Float[] ret = new Float[nArray];
    for (int i = 0; i < nArray; i++)
      ret[i] = Float.valueOf(array[i]); 
    return ret;
  }
  
  public static Double[] convertToWrapper(double[] array) {
    int nArray = size(array);
    Double[] ret = new Double[nArray];
    for (int i = 0; i < nArray; i++)
      ret[i] = Double.valueOf(array[i]); 
    return ret;
  }
  
  public static <T> T[] safe(Object[] unsafe) {
    return (T[])ObjectUtil.<Object[]>nvl(new Object[][] { unsafe, EMPTY_OBJECT_ARRAY });
  }
  
  public static Object[] subarray(Object[] array, int startIndexInclusive, int endIndexExclusive) {
    if (array == null)
      return null; 
    if (startIndexInclusive < 0)
      startIndexInclusive = 0; 
    if (endIndexExclusive > array.length)
      endIndexExclusive = array.length; 
    int newSize = endIndexExclusive - startIndexInclusive;
    Class<?> type = array.getClass().getComponentType();
    if (newSize <= 0)
      return (Object[])Array.newInstance(type, 0); 
    Object[] subarray = (Object[])Array.newInstance(type, newSize);
    System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
    return subarray;
  }
}
