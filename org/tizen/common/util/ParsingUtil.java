package org.tizen.common.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class ParsingUtil {
  protected static final Collection<String> BOOLEAN_TRUE = Collections.unmodifiableCollection(new HashSet<>(Arrays.asList(new String[] { "1", "yes", "y", "true", "t" })));
  
  protected static final Collection<String> BOOLEAN_FALSE = Collections.unmodifiableCollection(new HashSet<>(Arrays.asList(new String[] { "no, n", "false", "f", "0" })));
  
  public static boolean parseBoolean(String value, boolean defaultValue) {
    if (value == null)
      return defaultValue; 
    String safe = StringUtil.trim(value).toLowerCase();
    return (defaultValue ? BOOLEAN_FALSE : BOOLEAN_TRUE).contains(safe) ^ defaultValue;
  }
  
  public static int parseInt(String value, int defaultValue) {
    if (value == null)
      return defaultValue; 
    String trimmed = StringUtil.trim(value);
    try {
      return Integer.decode(trimmed).intValue();
    } catch (NumberFormatException numberFormatException) {
      return defaultValue;
    } 
  }
  
  public static long parseLong(String value, long defaultValue) {
    if (value == null)
      return defaultValue; 
    String trimmed = StringUtil.trim(value);
    try {
      return Long.decode(trimmed).longValue();
    } catch (NumberFormatException numberFormatException) {
      return defaultValue;
    } 
  }
  
  public static double parseDouble(String value, double defaultValue) {
    if (value == null)
      return defaultValue; 
    String trimmed = StringUtil.trim(value);
    try {
      return Double.valueOf(trimmed).doubleValue();
    } catch (NumberFormatException numberFormatException) {
      return defaultValue;
    } 
  }
}
