package org.tizen.common.core.application;

public class ProfileAppTypeInfo {
  private ProfileAppType appType;
  
  private boolean installed = false;
  
  public ProfileAppTypeInfo(String appType) {
    this.appType = ProfileAppType.getAppTypeToName(appType);
  }
  
  public ProfileAppTypeInfo(ProfileAppType appType, boolean installed) {
    this.appType = appType;
    this.installed = installed;
  }
  
  public ProfileAppType getProfileAppType() {
    return this.appType;
  }
  
  public void setProfileAppType(ProfileAppType appType) {
    this.appType = appType;
  }
  
  public boolean isInstalled() {
    return this.installed;
  }
  
  public void setInstalled(boolean installed) {
    this.installed = installed;
  }
}
