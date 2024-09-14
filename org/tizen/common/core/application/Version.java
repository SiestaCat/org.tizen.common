package org.tizen.common.core.application;

import org.tizen.common.util.Assert;

public class Version implements Comparable<Version> {
  public static final String REGEXP = "[0-9]+(\\.[0-9]+)+";
  
  public static final String DELIMETER_REGEXP = "\\.";
  
  private String version;
  
  public static final Version ZERO = new Version("0.0");
  
  public static final Version VERSION_3 = new Version("3.0");
  
  public static final Version VERSION_4 = new Version("4.0");
  
  public static final Version VERSION_5 = new Version("5.0");
  
  public static final Version VERSION_55 = new Version("5.5");
  
  public static final Version VERSION_6 = new Version("6.0");
  
  public Version(String version) {
    Assert.notNull(version, "Version can not be null");
    Assert.isTrue(version.matches("[0-9]+(\\.[0-9]+)+"), "Invalid version format");
    this.version = version;
  }
  
  public final String get() {
    return this.version;
  }
  
  public int compareTo(Version ver) {
    if (ver == null)
      return 1; 
    String[] thisParts = get().split("\\.");
    String[] thatParts = ver.get().split("\\.");
    int length = Math.max(thisParts.length, thatParts.length);
    for (int i = 0; i < length; i++) {
      int thisPart = (i < thisParts.length) ? Integer.parseInt(thisParts[i]) : 0;
      int thatPart = (i < thatParts.length) ? Integer.parseInt(thatParts[i]) : 0;
      if (thisPart < thatPart)
        return -1; 
      if (thisPart > thatPart)
        return 1; 
    } 
    return 0;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof Version))
      return false; 
    if (this == obj)
      return true; 
    return (compareTo((Version)obj) == 0);
  }
  
  public boolean isSameOrBiggerThanVersion3() {
    return (compareTo(VERSION_3) >= 0);
  }
  
  public boolean isSameOrBiggerThanVersion4() {
    return (compareTo(VERSION_4) >= 0);
  }
  
  public boolean isSameOrBiggerThanVersion5() {
    return (compareTo(VERSION_5) >= 0);
  }
  
  public boolean isSameOrBiggerThanVersion6() {
    return (compareTo(VERSION_6) >= 0);
  }
}
