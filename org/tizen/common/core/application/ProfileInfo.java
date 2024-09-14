package org.tizen.common.core.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.StringUtil;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.PlatformInfo;
import org.tizen.sdblib.util.Assert;

public class ProfileInfo {
  private static final Logger logger = LoggerFactory.getLogger(ProfileInfo.class);
  
  public static final String MOBILE = "mobile";
  
  public static final String WEARABLE = "wearable";
  
  public static final String TV = "tv";
  
  public static final String TIZEN = "tizen";
  
  public static final String CUSTOM = "custom";
  
  private String profile;
  
  private Map<String, String> versions = new HashMap<>();
  
  private Map<String, ProfileVersionInfo> versionInfoMap = new HashMap<>();
  
  private String parentProfile = "common";
  
  private String description = "";
  
  private List<ProfileVersionInfo> descentPlatformVersionList;
  
  private boolean isCustomProfile = false;
  
  private List<ProfileInfo> profileList = new ArrayList<>();
  
  public ProfileInfo() {}
  
  public ProfileInfo(String profile) {
    Assert.notNull(this.versions);
    this.profile = profile;
  }
  
  public ProfileInfo(String profile, Map<String, String> versions) {
    Assert.notNull(versions);
    this.profile = profile;
    this.versions = versions;
  }
  
  public ProfileInfo(String profile, Map<String, String> versions, Map<String, ProfileVersionInfo> versionsInfo) {
    Assert.notNull(versions);
    this.profile = profile;
    this.versions = versions;
    this.versionInfoMap = versionsInfo;
  }
  
  public ProfileInfo(String profile, String version, String path) {
    this.profile = profile;
    this.versions.put(version, path);
  }
  
  public ProfileInfo(String profile, String version, String path, boolean isCustom) {
    this.profile = profile;
    this.versions.put(version, path);
    this.isCustomProfile = isCustom;
  }
  
  public ProfileInfo(String profile, String version, String path, String parentProfile) {
    this.profile = profile;
    this.versions.put(version, path);
    setParentProfile(parentProfile);
  }
  
  public ProfileInfo(String profile, String version, String path, ProfileVersionInfo versionInfo) {
    this.profile = profile;
    this.versions.put(version, path);
    this.versionInfoMap.put(version, versionInfo);
  }
  
  public ProfileInfo(String profile, String parentProfile, String version, String path, ProfileVersionInfo versionInfo, boolean isCustom) {
    this(profile, version, path, versionInfo);
    setParentProfile(parentProfile);
    this.isCustomProfile = isCustom;
  }
  
  public String getProfile() {
    return this.profile;
  }
  
  public void setProfile(String profile) {
    this.profile = profile;
  }
  
  public String getParentProfile() {
    return this.parentProfile;
  }
  
  public void setParentProfile(String parentProfile) {
    if (StringUtil.isEmpty(parentProfile)) {
      this.parentProfile = "common";
      return;
    } 
    this.parentProfile = parentProfile.toLowerCase();
  }
  
  public Set<String> getVersions() {
    return this.versions.keySet();
  }
  
  public Map<String, String> getVersionsMap() {
    return this.versions;
  }
  
  public Set<String> getVersionInfos() {
    return this.versionInfoMap.keySet();
  }
  
  public Map<String, ProfileVersionInfo> getVersionInfoMap() {
    return this.versionInfoMap;
  }
  
  public void setVersionInfoMap(Map<String, ProfileVersionInfo> versionInfoMap) {
    this.versionInfoMap = versionInfoMap;
  }
  
  public void setVersionMap(Map<String, String> versionMap) {
    this.versions = versionMap;
  }
  
  public void appendVersion(String version, String path) {
    this.versions.put(version, path);
  }
  
  public void appendVersionInfo(String version, ProfileVersionInfo versionInfo) {
    if (this.versionInfoMap.get(version) != null) {
      List<String> supportIDEList = ((ProfileVersionInfo)this.versionInfoMap.get(version)).getSupportIDE();
      boolean isSameValue = false;
      for (String ideType : supportIDEList) {
        for (String insertIdeType : versionInfo.getSupportIDE()) {
          if (ideType.equals(insertIdeType))
            isSameValue = true; 
        } 
      } 
      if (!isSameValue) {
        List<ProfileAppTypeInfo> appTypeInfo = ((ProfileVersionInfo)this.versionInfoMap.get(version)).getAppTypeInfoList();
        versionInfo.getAppTypeInfoList().addAll(appTypeInfo);
      } 
    } 
    this.versionInfoMap.put(version, versionInfo);
  }
  
  public void appendProfileVersionInfo(String version, ProfileVersionInfo versionInfo) {
    if (this.versionInfoMap.get(version) != null) {
      List<String> supportIDEList = ((ProfileVersionInfo)this.versionInfoMap.get(version)).getSupportIDE();
      boolean isSameValue = false;
      for (String ideType : supportIDEList) {
        for (String insertIdeType : versionInfo.getSupportIDE()) {
          if (ideType.equals(insertIdeType))
            isSameValue = true; 
        } 
      } 
      if (!isSameValue)
        versionInfo.getSupportIDE().addAll(supportIDEList); 
    } 
    this.versionInfoMap.put(version, versionInfo);
  }
  
  public String getLatestPlatformPath() {
    String version = getLatestPlatformVersion();
    if (version.isEmpty())
      return ""; 
    ProfileVersionInfo versionInfo = this.versionInfoMap.get(version);
    if (versionInfo == null)
      return ""; 
    return versionInfo.getVersionPath();
  }
  
  public String getLatestPlatformVersion() {
    String version = "";
    Version latestVersion = Version.ZERO;
    Version tempVersion = null;
    for (String curVersion : getVersions()) {
      tempVersion = new Version(curVersion);
      if (latestVersion.compareTo(tempVersion) < 0) {
        latestVersion = tempVersion;
        version = curVersion;
      } 
    } 
    return version;
  }
  
  public List<ProfileVersionInfo> getSortDescendPlatformVersion() {
    if (this.descentPlatformVersionList != null && this.descentPlatformVersionList.size() > 0)
      return this.descentPlatformVersionList; 
    this.descentPlatformVersionList = new ArrayList<>();
    if (this.versions == null || this.versionInfoMap == null)
      return this.descentPlatformVersionList; 
    List<ProfileVersionInfo> installedPlatformVersion = new ArrayList<>();
    List<ProfileVersionInfo> notInstalledPlatformVersion = new ArrayList<>();
    SortedSet<String> keys = (new TreeSet<>(getVersions())).descendingSet();
    for (String curVersion : keys) {
      ProfileVersionInfo versionInfo = this.versionInfoMap.get(curVersion);
      if (versionInfo.getVersionName().equalsIgnoreCase("common"))
        continue; 
      if (versionInfo.isInstalledVersion()) {
        installedPlatformVersion.add(versionInfo);
        continue;
      } 
      notInstalledPlatformVersion.add(versionInfo);
    } 
    if (installedPlatformVersion.size() > 0)
      this.descentPlatformVersionList.addAll(installedPlatformVersion); 
    if (notInstalledPlatformVersion.size() > 0)
      this.descentPlatformVersionList.addAll(notInstalledPlatformVersion); 
    return this.descentPlatformVersionList;
  }
  
  public String[] getSupportVersionList(String checkVersion) {
    List<String> versionList = new ArrayList<>();
    Version checkVersionInfo = new Version(checkVersion);
    Version tempVersion = null;
    SortedSet<String> keys = (new TreeSet<>(getVersions())).descendingSet();
    for (String curVersion : keys) {
      tempVersion = new Version(curVersion);
      if (checkVersionInfo.compareTo(tempVersion) <= 0)
        versionList.add(curVersion); 
    } 
    if (versionList.size() < 1)
      return null; 
    String[] versionArr = new String[versionList.size()];
    versionList.toArray(versionArr);
    return versionArr;
  }
  
  public boolean isSupportPlatformVersion(String checkVersion) {
    Version checkVersionInfo = new Version(checkVersion);
    Version tempVersion = null;
    for (String curVersion : getVersions()) {
      tempVersion = new Version(curVersion);
      if (checkVersionInfo.compareTo(tempVersion) <= 0)
        return true; 
    } 
    return false;
  }
  
  public ProfileVersionInfo getVersionInfo(String version) {
    ProfileVersionInfo profileVersionInfo = this.versionInfoMap.get(version);
    if (profileVersionInfo == null)
      return null; 
    return profileVersionInfo;
  }
  
  public String getPlatformPath(String version) {
    String platformPath = this.versions.get(version);
    if (platformPath == null)
      return ""; 
    return platformPath;
  }
  
  public String getPlatformName(String version) {
    if (!this.versions.containsKey(version))
      return ""; 
    return String.format("%s-%s", new Object[] { this.profile, version });
  }
  
  public String getLatestPlatformName() {
    return getPlatformName(getLatestPlatformVersion());
  }
  
  public static String getPlatformName(IDevice device) {
    try {
      ProfileInfo profileInfo = getProfileInfo(device);
      if (profileInfo == null)
        return ""; 
      return profileInfo.getLatestPlatformName();
    } catch (Exception e) {
      logger.error("cannot get device information - " + device, e);
      return "";
    } 
  }
  
  public static ProfileInfo getProfileInfo(IDevice device) {
    PlatformInfo platformInfo = null;
    try {
      platformInfo = device.getPlatformInfo();
      String version = platformInfo.getPlatformVersion();
      if (!version.matches("((\\d+))(\\.\\d+)+"))
        throw new Exception("Failed to get a device's profile version"); 
      String profile = platformInfo.getProfileName();
      String path = null;
      ProfileInfo profileInfo = InstallPathConfig.getProfileInfo(profile);
      if (profileInfo != null)
        path = profileInfo.getPlatformPath(version); 
      return new ProfileInfo(profile, version, path);
    } catch (Exception e) {
      logger.error(String.format("Failed to get a device information from '%s' (%s) ", new Object[] { device, platformInfo }), e);
      return null;
    } 
  }
  
  public boolean isProduct() {
    if (this.parentProfile.equals("common"))
      return false; 
    return true;
  }
  
  public boolean isInstalledProfile() {
    if (this.profileList.size() > 0) {
      for (ProfileInfo profile : this.profileList) {
        if (profile.isInstalledProfile())
          return true; 
      } 
      return false;
    } 
    if (this.versionInfoMap == null || this.versionInfoMap.size() < 1)
      return false; 
    List<ProfileVersionInfo> versionList = new ArrayList<>(this.versionInfoMap.values());
    for (ProfileVersionInfo versionInfo : versionList) {
      if (versionInfo.isInstalledVersion())
        return true; 
    } 
    return false;
  }
  
  @Deprecated
  public String getParentProfileName(String version) {
    return this.parentProfile;
  }
  
  @Deprecated
  public boolean isProduct(String version) {
    if (this.parentProfile.equals("common"))
      return false; 
    return true;
  }
  
  public boolean isTV() {
    boolean isTV = false;
    if ("tv".equals(this.profile) || "tv".equals(this.parentProfile))
      isTV = true; 
    return isTV;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public boolean isCustomProfile() {
    if (this.profileList.size() > 0) {
      for (ProfileInfo profile : this.profileList) {
        if (profile.isCustomProfile())
          return true; 
      } 
      return false;
    } 
    return this.isCustomProfile;
  }
  
  public void setCustomProfile(boolean isCustomProfile) {
    this.isCustomProfile = isCustomProfile;
  }
  
  public List<ProfileInfo> getProfileList() {
    return this.profileList;
  }
  
  public void addToProfileList(ProfileInfo profileInfo) {
    this.profileList.add(profileInfo);
  }
}
