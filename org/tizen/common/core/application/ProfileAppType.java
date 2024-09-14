package org.tizen.common.core.application;

public enum ProfileAppType {
  NATIVE(0, "NATIVE_IDE"),
  WEB(1, "WEB_IDE"),
  PLATFORM(2, "PLATFORM_IDE");
  
  private String stateName;
  
  private int stateCode;
  
  ProfileAppType(int stateCode, String stateName) {
    this.stateCode = stateCode;
    this.stateName = stateName;
  }
  
  public String getStateName() {
    return this.stateName;
  }
  
  public String toString() {
    return name();
  }
  
  public int getStateCode() {
    return this.stateCode;
  }
  
  public static ProfileAppType getAppTypeToCode(int stateCode) {
    byte b;
    int i;
    ProfileAppType[] arrayOfProfileAppType;
    for (i = (arrayOfProfileAppType = values()).length, b = 0; b < i; ) {
      ProfileAppType type = arrayOfProfileAppType[b];
      if (type.getStateCode() == stateCode)
        return type; 
      b++;
    } 
    return null;
  }
  
  public static ProfileAppType getAppTypeToName(String appType) {
    byte b;
    int i;
    ProfileAppType[] arrayOfProfileAppType;
    for (i = (arrayOfProfileAppType = values()).length, b = 0; b < i; ) {
      ProfileAppType type = arrayOfProfileAppType[b];
      if (type.name().toLowerCase().equals(appType.toLowerCase()))
        return type; 
      b++;
    } 
    return null;
  }
  
  public static ProfileAppType getAppTypeToStateName(String appType) {
    byte b;
    int i;
    ProfileAppType[] arrayOfProfileAppType;
    for (i = (arrayOfProfileAppType = values()).length, b = 0; b < i; ) {
      ProfileAppType type = arrayOfProfileAppType[b];
      if (type.stateName.toLowerCase().equals(appType.toLowerCase()))
        return type; 
      b++;
    } 
    return null;
  }
}
