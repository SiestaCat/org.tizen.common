package org.tizen.common.core.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.library.sdkutils.api.ISDKPackage;
import org.tizen.library.sdkutils.api.impl.SDKUtils;

public final class InstallProfileConfig {
  private static final Logger logger = LoggerFactory.getLogger(InstallPathConfig.class);
  
  private static List<ProfileInfo> supportAllProfiles = new ArrayList<>();
  
  private static List<ProfileInfo> supportAllProfilesWithCustom = new ArrayList<>();
  
  private static Map<String, ProfileInfo> supportAllProfileMap = new HashMap<>();
  
  private static Map<String, ProfileInfo> supportAllActualProfileMap = new HashMap<>();
  
  private static List<ProfileInfo> installedAllProfiles = new ArrayList<>();
  
  private static Map<String, ProfileInfo> installedAllProfileMap = new HashMap<>();
  
  private static Map<String, List<ProfileInfo>> installedAllProfileToAppTypeMap = new HashMap<>();
  
  private static List<ProfileInfo> flattenSupportAllProfiles = new ArrayList<>();
  
  public static List<ProfileInfo> flattenInstalledAllProfiles = new ArrayList<>();
  
  private static Map<String, List<ProfileInfo>> flattenInstalledProfileInfoToAppTypeMap = new HashMap<>();
  
  public static List<ProfileInfo> getAllSupportedPlatforms() {
    try {
      if (supportAllProfiles == null || supportAllProfiles.isEmpty()) {
        flattenSupportAllProfiles = getAllPlatformInfoToSDKUtils();
        if (flattenSupportAllProfiles == null || flattenSupportAllProfiles.isEmpty())
          flattenSupportAllProfiles = getInstalledPlatformToLocalDirectory(); 
        for (ProfileInfo profileInfo : flattenSupportAllProfiles) {
          String parentProfile = profileInfo.getParentProfile();
          String actualProfile = profileInfo.getProfile();
          ProfileInfo tempInfo = supportAllProfileMap.get(actualProfile);
          if (profileInfo.isInstalledProfile())
            flattenInstalledAllProfiles.add(profileInfo); 
          for (String version : profileInfo.getVersionInfos()) {
            String path = profileInfo.getPlatformPath(version);
            if (tempInfo != null) {
              tempInfo.appendVersion(version, path);
            } else {
              tempInfo = new ProfileInfo(actualProfile, version, path, profileInfo.isCustomProfile());
            } 
            tempInfo.setParentProfile(parentProfile);
            tempInfo.setDescription(profileInfo.getDescription());
            tempInfo.appendVersionInfo(version, profileInfo.getVersionInfo(version));
          } 
          supportAllProfileMap.put(actualProfile, tempInfo);
        } 
        supportAllProfiles = new ArrayList<>(supportAllProfileMap.values());
      } 
    } catch (Throwable e) {
      logger.error("Failed to load the supported platform list", e);
    } 
    return supportAllProfiles;
  }
  
  public static List<ProfileInfo> getAllInstalledPlatforms() {
    try {
      if (installedAllProfiles == null || installedAllProfiles.isEmpty()) {
        if (flattenInstalledAllProfiles == null || flattenInstalledAllProfiles.isEmpty())
          return installedAllProfiles; 
        for (ProfileInfo profileInfo : flattenInstalledAllProfiles) {
          String profile = profileInfo.getProfile();
          ProfileInfo tempInfo = installedAllProfileMap.get(profile);
          for (String version : profileInfo.getVersionInfos()) {
            String path = profileInfo.getPlatformPath(version);
            if (tempInfo != null) {
              tempInfo.appendVersion(version, path);
            } else {
              tempInfo = new ProfileInfo(profile, version, path);
            } 
            tempInfo.setParentProfile(profileInfo.getParentProfile());
            tempInfo.setCustomProfile(profileInfo.isCustomProfile());
            tempInfo.setDescription(profileInfo.getDescription());
            tempInfo.appendVersionInfo(version, profileInfo.getVersionInfo(version));
          } 
          installedAllProfileMap.put(profile, tempInfo);
        } 
        installedAllProfiles = new ArrayList<>(installedAllProfileMap.values());
      } 
    } catch (Throwable e) {
      logger.error("Failed to load the installed profile list from the Package Manager", e);
    } 
    return installedAllProfiles;
  }
  
  public static List<ProfileInfo> flattenInstalledProfileInfoToAppType(String appType) {
    List<ProfileInfo> profileInfosList = flattenInstalledProfileInfoToAppTypeMap.get(appType);
    if (profileInfosList != null && !profileInfosList.isEmpty())
      return profileInfosList; 
    if (supportAllProfiles.isEmpty())
      getAllSupportedPlatforms(); 
    List<ProfileInfo> appTypeProfileList = new ArrayList<>();
    if (flattenSupportAllProfiles.isEmpty()) {
      logger.error("Failed to load the supported profile list");
      return appTypeProfileList;
    } 
    for (ProfileInfo profileInfo : flattenSupportAllProfiles) {
      String profile = profileInfo.getProfile();
      for (String version : profileInfo.getVersions()) {
        String versionPpath = profileInfo.getPlatformPath(version);
        ProfileVersionInfo profileVersionInfo = profileInfo.getVersionInfo(version);
        if (profileVersionInfo.isSupportIDE(appType)) {
          ProfileInfo info = new ProfileInfo(profile, version, versionPpath);
          info.appendVersionInfo(version, profileVersionInfo);
          appTypeProfileList.add(info);
        } 
      } 
    } 
    flattenInstalledProfileInfoToAppTypeMap.put(appType, appTypeProfileList);
    return appTypeProfileList;
  }
  
  public static List<ProfileInfo> getInstalledProfileInfoToAppType(String ideType) {
    List<ProfileInfo> profileInfosList = installedAllProfileToAppTypeMap.get(ideType);
    if (profileInfosList != null && !profileInfosList.isEmpty())
      return profileInfosList; 
    List<ProfileInfo> ideTypeProfileList = new ArrayList<>();
    List<ProfileInfo> flattenProfileInfosList = flattenInstalledProfileInfoToAppType(ideType);
    if (flattenProfileInfosList == null || flattenProfileInfosList.isEmpty()) {
      logger.error("Failed to load the supported profile list");
      return ideTypeProfileList;
    } 
    Map<String, ProfileInfo> profileInfoMap = new HashMap<>();
    for (ProfileInfo profileInfo : flattenProfileInfosList) {
      String profile = profileInfo.getProfile();
      for (String version : profileInfo.getVersions()) {
        ProfileInfo tempInfo = profileInfoMap.get(profileInfo.getProfile());
        String verstionPath = profileInfo.getPlatformPath(version);
        ProfileVersionInfo profileVersionInfo = profileInfo.getVersionInfoMap().get(version);
        if (tempInfo == null) {
          tempInfo = new ProfileInfo(profile, version, verstionPath);
          tempInfo.appendVersionInfo(version, profileVersionInfo);
        } else {
          tempInfo.appendVersion(version, profileInfo.getVersionsMap().get(version));
          tempInfo.appendVersionInfo(version, profileInfo.getVersionInfoMap().get(version));
        } 
        profileInfoMap.put(tempInfo.getProfile(), tempInfo);
      } 
    } 
    profileInfosList = new ArrayList<>(profileInfoMap.values());
    installedAllProfileToAppTypeMap.put(ideType, profileInfosList);
    return profileInfosList;
  }
  
  public static ProfileInfo getInstalledProfileInfo(String profile) {
    if (profile == null)
      return null; 
    if (installedAllProfiles.isEmpty())
      getAllInstalledPlatforms(); 
    for (ProfileInfo profileInfo : installedAllProfiles) {
      if (profile.toLowerCase().equals(profileInfo.getProfile()))
        return profileInfo; 
    } 
    return null;
  }
  
  private static List<ProfileInfo> getAllPlatformInfoToSDKUtils() {
    List<ProfileInfo> supportAllProfileInfos = new ArrayList<>();
    Map<String, ISDKPackage> sdkMetaPkg = null;
    try {
      SDKUtils sdkUtils = SDKUtils.getInstance();
      sdkMetaPkg = sdkUtils.getPackages(null, null, null, ISDKPackage.SearchPackageType.META_PKG, false, true);
    } catch (Throwable e) {
      logger.error("Failed to load the supported profile list from the Package Manager", e);
    } 
    if (sdkMetaPkg != null)
      for (Map.Entry<String, ISDKPackage> entry : sdkMetaPkg.entrySet()) {
        ISDKPackage pkg = entry.getValue();
        if (ISDKPackage.Type.WEB_IDE == pkg.getType() || ISDKPackage.Type.NATIVE_IDE == pkg.getType()) {
          ProfileInfo resultInfo = getProfileInfoToPackageInfo(pkg);
          supportAllProfileInfos.add(resultInfo);
        } 
      }  
    return supportAllProfileInfos;
  }
  
  private static ProfileInfo getProfileInfoToPackageInfo(ISDKPackage pkg) {
    String profile = null;
    String parentProfile = null;
    boolean isCustomProfile = false;
    if (pkg.isExtension()) {
      isCustomProfile = true;
      profile = pkg.getExtensionProfile();
      parentProfile = pkg.getProfile().name().toLowerCase();
    } else {
      profile = pkg.getProfile().name().toLowerCase();
    } 
    String version = pkg.getPlatformVersion().toString();
    String versionPath = pkg.getPlatformPath();
    boolean isInstalled = pkg.isInstalled();
    String appType = pkg.getType().name();
    ProfileAppType type = ProfileAppType.getAppTypeToStateName(appType);
    ProfileAppTypeInfo appTypeInfo = new ProfileAppTypeInfo(type, isInstalled);
    ProfileVersionInfo versionInfo = new ProfileVersionInfo(version, versionPath, appTypeInfo);
    ProfileInfo profileInfo = new ProfileInfo(profile, parentProfile, version, versionPath, versionInfo, isCustomProfile);
    return profileInfo;
  }
  
  private static void getProfileInfoToProduct(List<ProfileInfo> supportAllProfileInfos) {
    List<ProfileInfo> addProfileInfos = new ArrayList<>();
    for (ProfileInfo productInfo : supportAllProfileInfos) {
      if (productInfo.isProduct() && 
        !isCheckSameProfileInfo(supportAllProfileInfos, productInfo)) {
        String profile = productInfo.getParentProfile();
        String version = productInfo.getLatestPlatformVersion();
        Path path = new Path(productInfo.getLatestPlatformPath());
        IPath parentVersionPath = path.removeLastSegments(1).append(profile);
        ProfileVersionInfo productVersionInfo = productInfo.getVersionInfo(version);
        ProfileVersionInfo versionInfo = new ProfileVersionInfo(profile, version, parentVersionPath.toOSString());
        versionInfo.setAppTypeInfoList(productVersionInfo.getAppTypeInfoList());
        ProfileInfo profileInfo = new ProfileInfo(profile, version, parentVersionPath.toOSString(), versionInfo);
        addProfileInfos.add(profileInfo);
      } 
    } 
    supportAllProfileInfos.addAll(addProfileInfos);
  }
  
  private static boolean isCheckSameProfileInfo(List<ProfileInfo> supportAllProfileInfos, ProfileInfo productInfo) {
    String checkProfileName = productInfo.getParentProfile();
    String checkProfileVersion = productInfo.getLatestPlatformVersion();
    for (ProfileInfo profileInfo : supportAllProfileInfos) {
      if (profileInfo.getProfile().equalsIgnoreCase(checkProfileName) && 
        profileInfo.getLatestPlatformVersion().equalsIgnoreCase(checkProfileVersion))
        return true; 
    } 
    return false;
  }
  
  public static ProfileInfo getProfileInfoToProfile(String profileName) {
    ProfileInfo resultProfileInfo = null;
    if (supportAllProfileMap == null || supportAllProfileMap.size() < 1)
      getAllSupportedPlatforms(); 
    resultProfileInfo = supportAllProfileMap.get(profileName);
    if (resultProfileInfo == null)
      resultProfileInfo = supportAllActualProfileMap.get(profileName); 
    return resultProfileInfo;
  }
  
  public static Set<String> getSupportedProfiles() {
    return supportAllProfileMap.keySet();
  }
  
  private static List<ProfileInfo> getInstalledPlatformToLocalDirectory() {
    List<ProfileInfo> supportAllProfileInfos = new ArrayList<>();
    List<ProfileInfo> installProfileInfos = InstallPathConfig.flattenPlatformPathInfos();
    for (ProfileInfo installProfile : installProfileInfos) {
      for (String version : installProfile.getVersionInfos()) {
        ProfileVersionInfo versionInfo = installProfile.getVersionInfo(version);
        boolean isWeb = false;
        boolean isNative = false;
        for (ProfileAppTypeInfo langInfo : versionInfo.getAppTypeInfoList()) {
          if (langInfo.getProfileAppType() == ProfileAppType.NATIVE)
            isNative = true; 
          if (langInfo.getProfileAppType() == ProfileAppType.WEB)
            isWeb = true; 
          langInfo.setInstalled(true);
        } 
        if (!isWeb) {
          ProfileAppTypeInfo webLangInfo = new ProfileAppTypeInfo(ProfileAppType.WEB.name());
          versionInfo.setAppTypeInfo(webLangInfo);
        } 
        if (!isNative) {
          ProfileAppTypeInfo nativeLangInfo = new ProfileAppTypeInfo(ProfileAppType.NATIVE.name());
          versionInfo.setAppTypeInfo(nativeLangInfo);
        } 
      } 
    } 
    supportAllProfileInfos.addAll(installProfileInfos);
    return supportAllProfileInfos;
  }
  
  public static boolean isSupportProfileToAppType(String profileName, String version, String appType) {
    List<ProfileInfo> profileInfosList = flattenInstalledProfileInfoToAppType(appType);
    if (profileInfosList == null || profileInfosList.size() < 1) {
      logger.warn("The Platforms are not Installed");
      return false;
    } 
    for (ProfileInfo profileInfo : profileInfosList) {
      String profile = profileInfo.getProfile();
      if (!profile.equalsIgnoreCase(profileName))
        continue; 
      if (profileInfo.isSupportPlatformVersion(version))
        return true; 
    } 
    return false;
  }
  
  public static List<ProfileInfo> getAllSupportedPlatformsWithCustom() {
    ProfileInfo customProfileInfo = new ProfileInfo("custom");
    customProfileInfo.setCustomProfile(true);
    ProfileInfo wearableProfileInfo = new ProfileInfo("wearable");
    ProfileInfo tvProfileInfo = new ProfileInfo("tv");
    ProfileInfo tizenProfileInfo = new ProfileInfo("tizen");
    if (supportAllProfileMap == null || supportAllProfileMap.size() < 1)
      getAllSupportedPlatforms(); 
    if (supportAllProfilesWithCustom != null && !supportAllProfilesWithCustom.isEmpty())
      return supportAllProfilesWithCustom; 
    for (ProfileInfo profileInfo : supportAllProfiles) {
      if (profileInfo.getProfile().equals("tizen") || profileInfo.getParentProfile().equals("tizen")) {
        tizenProfileInfo.addToProfileList(profileInfo);
        continue;
      } 
      if (profileInfo.getProfile().equals("wearable") || profileInfo.getParentProfile().equals("wearable") || profileInfo.getProfile().equals("mobile") || 
        profileInfo.getParentProfile().equals("mobile")) {
        wearableProfileInfo.addToProfileList(profileInfo);
        continue;
      } 
      if (profileInfo.getProfile().equals("tv") || profileInfo.getParentProfile().equals("tv")) {
        tvProfileInfo.addToProfileList(profileInfo);
        continue;
      } 
      if (profileInfo.isCustomProfile())
        customProfileInfo.addToProfileList(profileInfo); 
    } 
    supportAllProfilesWithCustom.add(customProfileInfo);
    supportAllProfilesWithCustom.add(wearableProfileInfo);
    supportAllProfilesWithCustom.add(tvProfileInfo);
    supportAllProfilesWithCustom.add(tizenProfileInfo);
    return supportAllProfilesWithCustom;
  }
  
  public static List<ProfileInfo> getFlattenInstalledAllProfiles() {
    return flattenInstalledAllProfiles;
  }
}
