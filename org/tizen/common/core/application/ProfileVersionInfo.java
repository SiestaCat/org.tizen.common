package org.tizen.common.core.application;

import java.util.ArrayList;
import java.util.List;

public class ProfileVersionInfo {
  private String versionName;
  
  private String versionPath;
  
  private List<ProfileAppTypeInfo> appTypeInfoList = new ArrayList<>();
  
  public ProfileVersionInfo() {}
  
  public ProfileVersionInfo(String versionName, String versionPath) {
    this.versionName = versionName;
    this.versionPath = versionPath;
  }
  
  public ProfileVersionInfo(String versionName, String versionPath, String supportIDE) {
    this.versionName = versionName;
    this.versionPath = versionPath;
    setSupportIDE(supportIDE);
  }
  
  public ProfileVersionInfo(String versionName, String versionPath, ProfileAppTypeInfo appTypeInfo) {
    this.versionName = versionName;
    this.versionPath = versionPath;
    setAppTypeInfo(appTypeInfo);
  }
  
  public String getVersionPath() {
    return this.versionPath;
  }
  
  public void setVersionPath(String versionPath) {
    this.versionPath = versionPath;
  }
  
  public String getVersionName() {
    return this.versionName;
  }
  
  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }
  
  public boolean isInstalledVersion() {
    for (ProfileAppTypeInfo info : this.appTypeInfoList) {
      if (info.isInstalled())
        return true; 
    } 
    return false;
  }
  
  public List<ProfileAppTypeInfo> getAppTypeInfoList() {
    return this.appTypeInfoList;
  }
  
  public void setAppTypeInfoList(List<ProfileAppTypeInfo> appTypeInfoList) {
    this.appTypeInfoList = appTypeInfoList;
  }
  
  public void setAppTypeInfo(ProfileAppTypeInfo appTypeInfo) {
    if (this.appTypeInfoList == null)
      this.appTypeInfoList = new ArrayList<>(); 
    if (appTypeInfo != null)
      this.appTypeInfoList.add(appTypeInfo); 
  }
  
  public List<String> getSupportIDE() {
    List<String> appTypeList = new ArrayList<>();
    for (ProfileAppTypeInfo info : this.appTypeInfoList)
      appTypeList.add(info.getProfileAppType().name().toLowerCase()); 
    return appTypeList;
  }
  
  public void setSupportIDE(List<String> supportIDE) {
    if (supportIDE == null || supportIDE.size() < 1)
      return; 
    for (String appTypeStr : supportIDE) {
      ProfileAppTypeInfo appTypeInfo = new ProfileAppTypeInfo(appTypeStr);
      appTypeInfo.setInstalled(true);
      if (appTypeInfo.getProfileAppType() != null)
        this.appTypeInfoList.add(appTypeInfo); 
    } 
  }
  
  public boolean isSupportIDE(String supportIDEType) {
    if (supportIDEType == null || this.appTypeInfoList == null || this.appTypeInfoList.size() < 1)
      return false; 
    for (ProfileAppTypeInfo info : this.appTypeInfoList) {
      if (info.getProfileAppType().name().toLowerCase().equals(supportIDEType.toLowerCase()) && 
        info.isInstalled())
        return true; 
    } 
    return false;
  }
  
  public void setSupportIDE(String supportIDEType) {
    if (this.appTypeInfoList == null)
      this.appTypeInfoList = new ArrayList<>(); 
    if (supportIDEType != null) {
      ProfileAppTypeInfo appTypeInfo = new ProfileAppTypeInfo(supportIDEType);
      appTypeInfo.setInstalled(true);
      this.appTypeInfoList.add(appTypeInfo);
    } 
  }
  
  public String getDisplayName() {
    String displayName = "v" + this.versionName;
    if (!isInstalledVersion())
      displayName = String.valueOf(displayName) + " (Not Installed)"; 
    return displayName;
  }
}
