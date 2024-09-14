package org.tizen.common.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class CollectionUtil {
  protected static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  
  protected static final byte[] EMPTY_BYTES = EMPTY_BYTE_ARRAY;
  
  private static final String ARRAY_START = "{";
  
  private static final String ARRAY_END = "}";
  
  private static final String EMPTY_ARRAY = "{}";
  
  private static final String ARRAY_ELEMENT_SEPARATOR = ", ";
  
  private static final Set<Class<?>> APPROXIMABLE_COLLECTION_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new Class[] { Collection.class, 
            Set.class, HashSet.class, SortedSet.class, LinkedHashSet.class, TreeSet.class, 
            List.class, LinkedList.class, ArrayList.class })));
  
  private static final Set<Class<?>> APPROXIMABLE_MAP_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new Class[] { Map.class, SortedMap.class, HashMap.class, LinkedHashMap.class, TreeMap.class })));
  
  public static final Collection<Object> EMPTY_COLLECTION = Collections.unmodifiableCollection(new ArrayList());
  
  private static final int MULTIPLIER = 31;
  
  private static final int INITIAL_HASH = 7;
  
  public static boolean isEmpty(Collection<?> collection) {
    if (collection == null)
      return true; 
    return collection.isEmpty();
  }
  
  public static int size(Collection<?> collection) {
    if (collection == null)
      return 0; 
    return collection.size();
  }
  
  public static <T> void iterate(Collection<? extends T> collection, IteratingRunner<T> runner) throws InvocationTargetException {
    iterate(collection, runner, false);
  }
  
  public static <T> void iterate(Collection<? extends T> collection, IteratingRunner<T> runner, boolean bForceProcess) throws InvocationTargetException {
    if (runner == null)
      return; 
    if (isEmpty(collection))
      return; 
    Collection<T> safe = new ArrayList<>(collection);
    for (T arg : safe) {
      if (arg == null && !bForceProcess)
        continue; 
      try {
        runner.run(arg);
      } catch (Throwable e) {
        if (!bForceProcess)
          throw new InvocationTargetException(e); 
      } 
    } 
  }
  
  public static <T> void filter(Collection<? extends T> collection, Collection<T> results, IteratingAcceptor<T> runner, boolean bForceProcess) throws InvocationTargetException {
    if (results == null)
      return; 
    if (isEmpty(collection))
      return; 
    for (T arg : collection) {
      if (arg == null && !bForceProcess)
        continue; 
      try {
        if (runner.accept(arg))
          results.add(arg); 
      } catch (Throwable e) {
        if (!bForceProcess)
          throw new InvocationTargetException(e); 
      } 
    } 
  }
  
  public static <T> T pickupFirst(Collection<T> collection) {
    if (isEmpty(collection))
      return null; 
    Iterator<T> iter = collection.iterator();
    return iter.hasNext() ? iter.next() : null;
  }
  
  public static <T> T removeFirst(Collection<T> collection) {
    if (isEmpty(collection))
      return null; 
    Iterator<T> iter = collection.iterator();
    if (iter.hasNext()) {
      T ret = iter.next();
      iter.remove();
      return ret;
    } 
    return null;
  }
  
  public static <T> T pickupLast(Collection<T> collection) {
    if (isEmpty(collection))
      return null; 
    Iterator<T> iter = collection.iterator();
    T temp = null;
    while (iter.hasNext())
      temp = iter.next(); 
    return temp;
  }
  
  public static <T> T removeLast(Collection<T> collection) {
    if (isEmpty(collection))
      return null; 
    Iterator<T> iter = collection.iterator();
    T temp = null;
    while (iter.hasNext())
      temp = iter.next(); 
    iter.remove();
    return temp;
  }
  
  static class EnumerationAdapter<K> implements Iterator<K> {
    protected final Enumeration<K> enumeration;
    
    public EnumerationAdapter(Enumeration<K> enumeration) {
      Assert.notNull(enumeration);
      this.enumeration = enumeration;
    }
    
    public boolean hasNext() {
      return this.enumeration.hasMoreElements();
    }
    
    public K next() {
      return this.enumeration.nextElement();
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  public static <E> Iterator<E> iterator(Enumeration<E> enumeration) {
    return new EnumerationAdapter<>(enumeration);
  }
  
  public static List<?> asList(Object source) {
    return Arrays.asList(ArrayUtil.toObjectArray(source));
  }
  
  public static void mergeArrayIntoCollection(Object array, Collection<Object> collection) {
    Assert.notNull(collection);
    Object[] arr = ArrayUtil.toObjectArray(array);
    for (int i = 0, n = arr.length; i < n; i++)
      collection.add(arr[i]); 
  }
  
  public static boolean contains(Iterator<Object> iterator, Object element) {
    if (iterator == null)
      return false; 
    while (iterator.hasNext()) {
      Object candidate = iterator.next();
      if (ObjectUtil.equals(candidate, element))
        return true; 
    } 
    return false;
  }
  
  public static boolean contains(Enumeration<Object> enumeration, Object element) {
    if (enumeration == null)
      return false; 
    while (enumeration.hasMoreElements()) {
      Object candidate = enumeration.nextElement();
      if (ObjectUtil.equals(candidate, element))
        return true; 
    } 
    return false;
  }
  
  public static boolean contains(Collection<Object> collection, Object element) {
    if (collection == null)
      return false; 
    for (Object candidate : collection) {
      if (ObjectUtil.equals(candidate, element))
        return true; 
    } 
    return false;
  }
  
  public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
    if (isEmpty(source) || isEmpty(candidates))
      return false; 
    for (Object candidate : candidates) {
      if (source.contains(candidate))
        return true; 
    } 
    return false;
  }
  
  public static boolean isApproximableCollectionType(Class<?> collectionType) {
    return APPROXIMABLE_COLLECTION_TYPES.contains(collectionType);
  }
  
  public static boolean isApproximableMapType(Class<?> mapType) {
    return APPROXIMABLE_MAP_TYPES.contains(mapType);
  }
  
  public static <K> Collection<K> createApproximableCollection(Collection<K> collection, int initialCapacity) {
    if (collection instanceof LinkedList)
      return new LinkedList<>(); 
    if (collection instanceof List)
      return new ArrayList<>(initialCapacity); 
    if (collection instanceof SortedSet)
      return new TreeSet<>(((SortedSet<K>)collection).comparator()); 
    return new LinkedHashSet<>(initialCapacity);
  }
  
  public static <K, V> Map<K, V> createApproximableMap(Map<K, V> map, int initialCapacity) {
    if (map instanceof SortedMap)
      return new TreeMap<>(((SortedMap)map).comparator()); 
    return new LinkedHashMap<>(initialCapacity);
  }
  
  public static String toString(Object obj) {
    if (obj == null)
      return "<<null>>"; 
    if (obj instanceof String)
      return (String)obj; 
    if (obj.getClass().isArray()) {
      int length = Array.getLength(obj);
      if (length == 0)
        return "{}"; 
      StringBuilder buffer = new StringBuilder();
      buffer.append("{");
      for (int i = 0; i < length; i++) {
        if (i != 0)
          buffer.append(", "); 
        buffer.append(toString(Array.get(obj, i)));
      } 
      buffer.append("}");
      return buffer.toString();
    } 
    return ObjectUtil.<String>nvl(new String[] { obj.toString(), "" });
  }
  
  public static <E> String concatenate(Object[] array, String separator) {
    return concatenate((array == null) ? null : new ArrayUtil.ArrayIterator(array), separator);
  }
  
  public static <E> String concatenate(Collection<E> col, String separator) {
    return concatenate((col == null) ? null : col.iterator(), separator);
  }
  
  public static <E> String concatenate(Iterator<E> iter, String separator) {
    if (iter == null)
      return "<<null>>"; 
    if (!iter.hasNext())
      return "{}"; 
    StringBuilder buffer = new StringBuilder();
    boolean bInit = false;
    while (iter.hasNext()) {
      Object obj = iter.next();
      if (bInit)
        buffer.append(separator); 
      bInit = true;
      buffer.append(toString(obj));
    } 
    return buffer.toString();
  }
  
  public static int hashCode(Object obj) {
    if (obj == null)
      return 0; 
    if (obj.getClass().isArray()) {
      if (obj instanceof Object[])
        return hashCode((Object[])obj); 
      if (obj instanceof boolean[])
        return hashCode((boolean[])obj); 
      if (obj instanceof byte[])
        return hashCode((byte[])obj); 
      if (obj instanceof char[])
        return hashCode((char[])obj); 
      if (obj instanceof double[])
        return hashCode((double[])obj); 
      if (obj instanceof float[])
        return hashCode((float[])obj); 
      if (obj instanceof int[])
        return hashCode((int[])obj); 
      if (obj instanceof long[])
        return hashCode((long[])obj); 
      if (obj instanceof short[])
        return hashCode((short[])obj); 
    } 
    return obj.hashCode();
  }
  
  public static int hashCode(Object[] array) {
    if (array == null)
      return 0; 
    int hash = 7;
    for (int i = 0, arraySize = array.length; i < arraySize; i++)
      hash = 31 * hash + hashCode(array[i]); 
    return hash;
  }
  
  public static int hashCode(boolean[] array) {
    if (array == null)
      return 0; 
    int hash = 7;
    for (int i = 0, arraySize = array.length; i < arraySize; i++)
      hash = 31 * hash + hashCode(array[i]); 
    return hash;
  }
  
  public static int hashCode(byte[] array) {
    if (array == null)
      return 0; 
    int hash = 7;
    for (int i = 0, arraySize = array.length; i < arraySize; i++)
      hash = 31 * hash + array[i]; 
    return hash;
  }
  
  public static int hashCode(char[] array) {
    if (array == null)
      return 0; 
    int hash = 7;
    for (int i = 0, arraySize = array.length; i < arraySize; i++)
      hash = 31 * hash + hashCode(array[i]); 
    return hash;
  }
  
  public static int hashCode(double[] array) {
    if (array == null)
      return 0; 
    int hash = 7;
    for (int i = 0, arraySize = array.length; i < arraySize; i++)
      hash = 31 * hash + hashCode(array[i]); 
    return hash;
  }
  
  public static int hashCode(float[] array) {
    if (array == null)
      return 0; 
    int hash = 7;
    for (int i = 0, arraySize = array.length; i < arraySize; i++)
      hash = 31 * hash + hashCode(array[i]); 
    return hash;
  }
  
  public static int hashCode(int[] array) {
    if (array == null)
      return 0; 
    int hash = 7;
    for (int i = 0, arraySize = array.length; i < arraySize; i++)
      hash = 31 * hash + hashCode(array[i]); 
    return hash;
  }
  
  public static int hashCode(long[] array) {
    if (array == null)
      return 0; 
    int hash = 7;
    for (int i = 0, arraySize = array.length; i < arraySize; i++)
      hash = 31 * hash + hashCode(array[i]); 
    return hash;
  }
  
  public static int hashCode(short[] array) {
    if (array == null)
      return 0; 
    int hash = 7;
    for (int i = 0, arraySize = array.length; i < arraySize; i++)
      hash = 31 * hash + hashCode(array[i]); 
    return hash;
  }
  
  public static int hashCode(boolean bool) {
    return bool ? 1231 : 1237;
  }
  
  public static int hashCode(double dbl) {
    long bits = Double.doubleToLongBits(dbl);
    return hashCode(bits);
  }
  
  public static int hashCode(float flt) {
    return Float.floatToIntBits(flt);
  }
  
  public static int hashCode(long lng) {
    return (int)(lng ^ lng >>> 32L);
  }
  
  public static <K> boolean equals(Collection... cols) {
    boolean bInit = false;
    int size = 0;
    byte b;
    int j;
    Collection[] arrayOfCollection;
    for (j = (arrayOfCollection = cols).length, b = 0; b < j; ) {
      Collection<? extends K> col = arrayOfCollection[b];
      if (!bInit) {
        if (col == null) {
          size = -1;
        } else {
          size = col.size();
        } 
        bInit = true;
      } 
      if (size < 0) {
        if (col != null)
          return false; 
      } else if (col == null || col.size() != size) {
        return false;
      } 
      b++;
    } 
    if (size < 0)
      return true; 
    Iterator[] iters = new Iterator[cols.length];
    for (int i = 0, n = iters.length; i < n; i++)
      iters[i] = cols[i].iterator(); 
    while (iters[0].hasNext()) {
      K obj = iters[0].next();
      for (int k = 1, m = iters.length; k < m; k++) {
        K other = iters[k].next();
        if (!ObjectUtil.equals(obj, other))
          return false; 
      } 
    } 
    return true;
  }
  
  public static boolean equals(Object[]... objsVar) {
    boolean bInit = false;
    int size = 0;
    byte b;
    int j;
    Object[][] arrayOfObject;
    for (j = (arrayOfObject = objsVar).length, b = 0; b < j; ) {
      Object[] objs = arrayOfObject[b];
      if (!bInit) {
        if (objs == null) {
          size = -1;
        } else {
          size = objs.length;
        } 
        bInit = true;
      } 
      if (size < 0) {
        if (objs != null)
          return false; 
      } else if (objs == null || objs.length != size) {
        return false;
      } 
      b++;
    } 
    if (size < 0)
      return true; 
    for (int i = 1, n = objsVar.length; i < n; i++) {
      for (int k = 0; k < size; k++) {
        if (!ObjectUtil.equals(objsVar[0][k], objsVar[i][k]))
          return false; 
      } 
    } 
    return true;
  }
  
  public static void swap(Object[] objs, int i, int j) {
    Object temp = objs[i];
    objs[i] = objs[j];
    objs[j] = temp;
  }
  
  public static int isAvailableGenericTypeForCollection(Collection<?> collection, Class<?> targetClass) {
    if (collection.size() < 1)
      return 0; 
    boolean foundNotNull = false;
    for (Object o : collection) {
      if (o != null) {
        foundNotNull = true;
        if (o.getClass().isAssignableFrom(targetClass))
          return 1; 
      } 
    } 
    if (foundNotNull)
      return -1; 
    return 0;
  }
  
  public static <E> List<E> resolveSetAsList(Set<E> set) {
    Iterator<E> itr = set.iterator();
    ArrayList<E> result = new ArrayList<>();
    while (itr.hasNext()) {
      E obj = itr.next();
      result.add(obj);
    } 
    return result;
  }
}
