package org.tizen.common;

public enum ScreenDensity {
  XHIGH("xhigh", "Xhigh Density", 117),
  HIGH("high", "High Density", 90);
  
  private final int fIconSize;
  
  private final String fDisplayName;
  
  private final String fName;
  
  ScreenDensity(String value, String display, int size) {
    this.fName = value;
    this.fDisplayName = display;
    this.fIconSize = size;
  }
  
  public int getIconSize() {
    return this.fIconSize;
  }
  
  public String getName() {
    return this.fName;
  }
  
  public String getDisplayName() {
    return this.fDisplayName;
  }
  
  public String getIconDirectory() {
    return "screen-density-" + this.fName;
  }
  
  public static ScreenDensity getEnumFromDisplayName(String disName) {
    byte b;
    int i;
    ScreenDensity[] arrayOfScreenDensity;
    for (i = (arrayOfScreenDensity = values()).length, b = 0; b < i; ) {
      ScreenDensity sd = arrayOfScreenDensity[b];
      if (sd.getDisplayName().equals(disName))
        return sd; 
      b++;
    } 
    return null;
  }
  
  public static ScreenDensity getEnum(String name) {
    byte b;
    int i;
    ScreenDensity[] arrayOfScreenDensity;
    for (i = (arrayOfScreenDensity = values()).length, b = 0; b < i; ) {
      ScreenDensity sd = arrayOfScreenDensity[b];
      if (sd.getName().equals(name))
        return sd; 
      b++;
    } 
    return null;
  }
}
