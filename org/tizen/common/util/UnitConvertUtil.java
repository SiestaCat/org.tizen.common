package org.tizen.common.util;

public class UnitConvertUtil {
  private static final int KILO = 1024;
  
  private static final float KILO_F = 1024.0F;
  
  private static final String UNITS = " KMGTPE";
  
  private static final char BYTE = 'B';
  
  public static String toByteUnitString(long bytes) {
    if (bytes < 1024L)
      return String.valueOf(bytes) + " " + 'B'; 
    int unitIndex = 0;
    for (; bytes >= 1048576L; bytes >>= 10L)
      unitIndex++; 
    if (bytes >= 1024L)
      unitIndex++; 
    return String.format("%.2f %c%c", new Object[] { Float.valueOf((float)bytes / 1024.0F), Character.valueOf(" KMGTPE".charAt(unitIndex)), Character.valueOf('B') });
  }
  
  public static String toByteUnitString(String value) {
    if (value == null)
      return "N/A"; 
    try {
      long bytes = Long.parseLong(value);
      return toByteUnitString(bytes);
    } catch (NumberFormatException numberFormatException) {
      return "N/A";
    } 
  }
}
