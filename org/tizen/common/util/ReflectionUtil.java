package org.tizen.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtil {
  protected static final Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);
  
  public static <T> T tryNewInstance(String className) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return tryNewInstance(className, cl);
  }
  
  public static <T> T tryNewInstance(String className, ClassLoader loader) {
    try {
      Class<?> clazz = loader.loadClass(className);
      return (T)clazz.newInstance();
    } catch (ClassNotFoundException e) {
      logger.info("Class not found:", e);
    } catch (InstantiationException e) {
      logger.info("Fail to instantiate:", e);
    } catch (IllegalAccessException e) {
      logger.info("An access of constructor fail:", e);
    } 
    return null;
  }
  
  public static Field getField(Object instance, String fieldName, boolean force) {
    Assert.notNull(instance);
    try {
      Class<?> clazz = instance.getClass();
      while (clazz != null) {
        try {
          Field field = clazz.getDeclaredField(fieldName);
          if (force && !field.isAccessible())
            field.setAccessible(true); 
          return field;
        } catch (NoSuchFieldException noSuchFieldException) {
          clazz = clazz.getSuperclass();
        } 
      } 
      logger.debug("A field is not found.");
    } catch (IllegalArgumentException e) {
      logger.debug("Failed to get a field from the instance: ", e);
    } 
    return null;
  }
  
  public static Object getObject(Object instance, String fieldName, boolean force) {
    Assert.notNull(instance);
    try {
      Field field = getField(instance, fieldName, force);
      if (field != null)
        return field.get(instance); 
      logger.debug("A object is not found.");
    } catch (IllegalArgumentException e) {
      logger.debug("Failed to get a field from the instance: ", e);
    } catch (IllegalAccessException e) {
      logger.debug("A field is inaccessible: ", e);
    } 
    return null;
  }
  
  public static Object callMethod(Object instance, String methodName, Class[] paramTypes, Object[] params, boolean force) throws InvocationTargetException {
    Assert.notNull(instance);
    try {
      Class<?> clazz = instance.getClass();
      while (clazz != null) {
        try {
          Method method = clazz.getDeclaredMethod(methodName, paramTypes);
          if (force && !method.isAccessible())
            method.setAccessible(true); 
          return method.invoke(instance, params);
        } catch (NoSuchMethodException noSuchMethodException) {
          clazz = clazz.getSuperclass();
        } 
      } 
      logger.debug("A method is not found.");
    } catch (IllegalArgumentException e) {
      logger.debug("Failed to get or call a method from the instance: ", e);
    } catch (IllegalAccessException e) {
      logger.debug("A method is inaccessible: ", e);
    } 
    return null;
  }
}
