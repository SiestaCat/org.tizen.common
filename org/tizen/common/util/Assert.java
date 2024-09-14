package org.tizen.common.util;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

public class Assert {
  public static void fail(Object msg) {
    if (msg instanceof String)
      throw new IllegalArgumentException((String)msg); 
    if (msg != null)
      throw new IllegalArgumentException(msg.toString()); 
  }
  
  public static void isTrue(boolean exp, String msg) {
    if (exp)
      return; 
    fail(msg);
  }
  
  public static void isFalse(boolean exp, String msg) {
    if (!exp)
      return; 
    fail(msg);
  }
  
  public static void isTrue(boolean exp) {
    isTrue(exp, "Expression must be true");
  }
  
  public static void isFalse(boolean exp) {
    isFalse(exp, "Expression must be false");
  }
  
  public static void isNull(Object obj, String msg) {
    isTrue((obj == null), msg);
  }
  
  public static void notNull(Object obj, String msg) {
    isFalse((obj == null), msg);
  }
  
  public static void isNull(Object obj) {
    isNull(obj, "Object must be null");
  }
  
  public static void notNull(Object obj) {
    notNull(obj, "Object must NOT be null");
  }
  
  public static void isEqual(Object obj1, Object obj2, String message) {
    if (ObjectUtil.equals(obj1, obj2))
      return; 
    fail(message);
  }
  
  public static void isEqual(Object obj1, Object obj2) {
    isEqual(obj1, obj2, "Objects are not equal");
  }
  
  public static void hasLength(String text, String msg) {
    if (StringUtil.hasLength(text))
      return; 
    fail(msg);
  }
  
  public static void hasLength(String text) {
    hasLength(text, "String argument must have length; it must not be null or empty");
  }
  
  public static void hasText(String text, String message) {
    if (StringUtil.hasText(text))
      return; 
    fail(message);
  }
  
  public static void hasText(String text) {
    hasText(text, "String argument must have text; it must not be null, empty, or blank");
  }
  
  public static void doesNotContain(String textToSearch, String substring, String msg) {
    if (!StringUtil.hasLength(textToSearch))
      return; 
    if (!StringUtil.hasLength(substring))
      return; 
    if (!textToSearch.contains(substring))
      return; 
    fail(msg);
  }
  
  public static void doesNotContain(String textToSearch, String substring) {
    doesNotContain(
        textToSearch, 
        substring, 
        "String argument must not contain the substring [" + substring + "]");
  }
  
  public static void contains(String textToSearch, String substring, String msg) {
    if (!StringUtil.hasLength(textToSearch))
      return; 
    if (!StringUtil.hasLength(substring))
      return; 
    if (textToSearch.contains(substring))
      return; 
    fail(msg);
  }
  
  public static void contains(String textToSearch, String substring) {
    contains(
        textToSearch, 
        substring, 
        "String argument must contain the substring [" + substring + "]");
  }
  
  public static <T> void notEmpty(Object[] array, String message) {
    if (ArrayUtil.isEmpty(array))
      fail(message); 
  }
  
  public static void notEmpty(Object[] array) {
    notEmpty(array, "Array must not be empty: it must contain at least 1 element");
  }
  
  public static void noNullElements(Object[] array, String msg) {
    for (int i = 0, n = ArrayUtil.size(array); i < n; i++) {
      if (array[i] == null)
        fail(msg); 
    } 
  }
  
  public static void noNullElements(Object[] array) {
    noNullElements(array, "Array must not contain any null elements");
  }
  
  public static void notEmpty(Collection<?> collection, String msg) {
    if (!CollectionUtil.isEmpty(collection))
      return; 
    fail(msg);
  }
  
  public static void notEmpty(Collection<?> collection) {
    notEmpty(collection, "Collection must not be empty: it must contain at least 1 element");
  }
  
  public static void notEmpty(Map<?, ?> map, String msg) {
    if (!MapUtil.isEmpty(map))
      return; 
    fail(msg);
  }
  
  public static void notEmpty(Map<?, ?> map) {
    notEmpty(map, "Map must not be empty; it must contain at least one entry");
  }
  
  public static void isInstanceOf(Class<?> type, Object obj, String msg) {
    notNull(type, "Type to check against must not be null");
    if (type.isInstance(obj))
      return; 
    fail(MessageFormat.format(
          "{0}, Object of class [{1}] must be aan instance of {2}", new Object[] { msg, 
            (obj == null) ? "null" : obj.getClass().getName(), 
            type }));
  }
  
  public static void isInstanceOf(Class<?> type, Object obj) {
    isInstanceOf(type, obj, "");
  }
  
  public static void isAssignable(Class<?> superType, Class<?> subType, String msg) {
    notNull(superType, "Type to check against must not be null");
    notNull(subType, "Type to assign must not be null");
    if (superType.isAssignableFrom(subType))
      return; 
    fail(MessageFormat.format(
          "{0}, {1} is not assignable to {2}", new Object[] { msg, 
            subType, 
            superType }));
  }
  
  public static void isAssignable(Class<?> superType, Class<?> subType) {
    isAssignable(superType, subType, "");
  }
}
