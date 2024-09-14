package org.tizen.common.core.application;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.util.DialogUtil;
import org.tizen.common.util.HostUtil;
import org.tizen.common.util.IOUtil;
import org.tizen.common.util.OSChecker;
import org.tizen.common.util.StringUtil;
import org.tizen.sdblib.IDevice;

public final class InstallPathConfig {
  private static final Logger logger = LoggerFactory.getLogger(InstallPathConfig.class);
  
  private static String defaultHomePath;
  
  private static String sdkInstallPath;
  
  private static String sdkDataPath;
  
  private static String jdkPath;
  
  private static final String DIR_PLATFORMS = "platforms";
  
  private static final String DIR_COMMON_PLATFORM = "platforms" + File.separator + "common";
  
  private static final String DIR_PLATFORMS_VER;
  
  private static final String DIR_PLATFORMS_VER_NUM = "2.2";
  
  private static final String DIR_SAMPLES = "samples";
  
  private static final String DIR_SNIPPETS = "snippets";
  
  private static final String DIR_ON_DEMAND = "on-demand";
  
  private static final String DIR_EMULATOR = "emulator";
  
  private static final String DIR_DEVICE_MANAGER = "device-manager";
  
  private static final String DIR_BATTERY_HISTORIAN = "battery-historian";
  
  private static final String DIR_INSTALLER = "install-manager";
  
  private static final String DIR_TOOLS = "tools";
  
  private static final String DIR_LIBRARY = "library";
  
  private static final String DIR_SDK_DATA = "tizen-sdk-data";
  
  private static final String DIR_INSTALLMANAGER = ".installmanager";
  
  private static final String DIR_CHECKER = "checker";
  
  private static final String DIR_DOCUMENTS = "documents";
  
  private static final String DIR_IDE = "ide";
  
  private static final String DIR_LOG = "logs";
  
  private static final String DIR_BIN = "bin";
  
  private static final String DIR_INFO = ".info";
  
  private static final String DIR_PLATFORM_NATIVE_CHECK_FILE = "features" + File.separator + "native" + File.separator + "feature.xml";
  
  private static final String DIR_PLATFORM_WEB_CHECK_FILE = "features" + File.separator + "feature-wrt.properties";
  
  private static final String DIR_PLATFORM_PLATFORM_CHECK_FILE = "predefined-snapshots" + File.separator + "snapshots.xml";
  
  private static final String DIR_PLATFORM_COMMON = "common";
  
  public static final String REG_PROFILE = "[a-zA-Z\\-]+";
  
  public static final String REG_VERSION = "((\\d+))(\\.\\d+)+";
  
  public static final String PLATFORM_SEPARATOR = "-";
  
  public static final String REG_DIR_PLATFORM = "[a-zA-Z\\-]+-((\\d+))(\\.\\d+)+";
  
  public static final String FORMAT_PLATFORM_NAME = "%s-%s";
  
  private static final String SDKSUFFIX = ".installmanager" + File.separatorChar + "tizensdkpath";
  
  private static final String SDK_INFO_FILE = "sdk.info";
  
  private static final String SDK_INFO_FILE_KEY_INSTALLED_PATH = "TIZEN_SDK_INSTALLED_PATH";
  
  private static final String SDK_INFO_FILE_KEY_DATA_PATH = "TIZEN_SDK_DATA_PATH";
  
  private static final String SDK_INFO_FILE_KEY_JDK_PATH = "JDK_PATH";
  
  private static final String IDE_PROFILE_FILE = "ide_profile.properties";
  
  private static final String ANALYTICS_CONFIG_FILE = "analytics.conf";
  
  private static final String REGISTRY_PATH_OF_SHELL_FOLDER = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders";
  
  private static final String REGISTRY_LOCAL_APP_DATA_OF_SHELL_FOLDER = "\"Local AppData\"";
  
  private static final String REG_VALUE = "REG_SZ";
  
  private static List<ProfileInfo> loadIdeInstallProfileInfo = null;
  
  private static Map<String, List<ProfileInfo>> flattenProfileInfoToAppTypeMap = new HashMap<>();
  
  private static Map<String, List<ProfileInfo>> profileInfoToAppTypeMap = new HashMap<>();
  
  public static boolean isInstalledProduct = false;
  
  static {
    DIR_PLATFORMS_VER = "tizen2.2";
    try {
      if (!isCLI()) {
        Properties properties = new Properties();
        properties.put("log4j.rootLogger", "INFO, tizen");
        properties.put("log4j.appender.tizen", "org.apache.log4j.ConsoleAppender");
        properties.put("log4j.appender.tizen.layout", "org.apache.log4j.PatternLayout");
        PropertyConfigurator.configure(properties);
      } 
      loadSdkPath();
    } catch (Exception e) {
      String msg = "Failed to load sdk path";
      logger.error("Failed to load sdk path", e);
      System.err.println(String.valueOf(msg) + ": " + e.getMessage());
    } 
  }
  
  public static boolean isCLI() {
    try {
      Platform.getInstallLocation();
    } catch (NoClassDefFoundError noClassDefFoundError) {
      return true;
    } 
    return false;
  }
  
  @Deprecated
  public static String getIDEPath() {
    return System.getProperty("user.dir");
  }
  
  public static String getSDKPath() {
    return sdkInstallPath;
  }
  
  public static String getJDKPath() {
    return jdkPath;
  }
  
  public static String getPlatformVersionPath() {
    String path = "";
    String platformPath = String.valueOf(getSDKPath()) + File.separator + "platforms";
    File platforms = getPlatformDir();
    if (platforms != null) {
      ProfileInfo profileInfo = getLatestProfileInfo();
      if (profileInfo != null)
        path = profileInfo.getLatestPlatformPath(); 
    } 
    if (path.isEmpty())
      path = String.valueOf(platformPath) + File.separator + DIR_PLATFORMS_VER; 
    return path;
  }
  
  public static String getCommonPlatformPath() {
    return String.valueOf(getSDKPath()) + File.separator + DIR_COMMON_PLATFORM;
  }
  
  public static String getPlatformPath(ProfileInfo profile, String version) {
    if (profile == null) {
      profile = getLatestProfileInfo();
      if (profile == null)
        return null; 
    } 
    if (version == null)
      version = profile.getLatestPlatformVersion(); 
    return profile.getPlatformPath(version);
  }
  
  public static void getProfileInfosToLocalDirectory() {
    File platformDir = getPlatformDir();
    if (platformDir == null) {
      logger.error("Platforms directory is not available.");
      loadIdeInstallProfileInfo = new ArrayList<>();
      return;
    } 
    File[] tizenVersions = platformDir.listFiles();
    if (tizenVersions == null) {
      logger.error("Failed to get available versions in platforms.");
      loadIdeInstallProfileInfo = new ArrayList<>();
      return;
    } 
    Map<String, ProfileInfo> profileInfoMap = new HashMap<>();
    List<ProfileInfo> profileInfoList = new ArrayList<>();
    byte b;
    int j;
    File[] arrayOfFile1;
    for (j = (arrayOfFile1 = tizenVersions).length, b = 0; b < j; ) {
      File tizenVersion = arrayOfFile1[b];
      if (!".info".equalsIgnoreCase(tizenVersion.getName()))
        if (tizenVersion.isDirectory()) {
          String version = getVersion(tizenVersion.getName());
          if (version != null) {
            File[] profiles = tizenVersion.listFiles();
            if (profiles == null) {
              logger.error("Failed to get available profiles in version.");
            } else {
              byte b1;
              int k;
              File[] arrayOfFile;
              for (k = (arrayOfFile = profiles).length, b1 = 0; b1 < k; ) {
                File profileFile = arrayOfFile[b1];
                String profile = profileFile.getName();
                if (!"common".equalsIgnoreCase(profile)) {
                  ProfileInfo tempInfo = profileInfoMap.get(profile);
                  if (tempInfo != null) {
                    tempInfo.appendVersion(version, profileFile.getPath());
                  } else {
                    tempInfo = new ProfileInfo(profile, version, profileFile.getPath());
                  } 
                  ProfileVersionInfo profileVersionInfo = new ProfileVersionInfo(version, profileFile.getPath());
                  File checkNative = new File(profileFile.getPath(), DIR_PLATFORM_NATIVE_CHECK_FILE);
                  File checkWebNative = new File(profileFile.getPath(), DIR_PLATFORM_WEB_CHECK_FILE);
                  File checkNativePlatform = new File(profileFile.getPath(), DIR_PLATFORM_PLATFORM_CHECK_FILE);
                  if (checkNative.exists())
                    profileVersionInfo.setSupportIDE("native"); 
                  if (checkWebNative.exists())
                    profileVersionInfo.setSupportIDE("web"); 
                  if (checkNativePlatform.exists())
                    profileVersionInfo.setSupportIDE("platform"); 
                  tempInfo.appendVersionInfo(version, profileVersionInfo);
                  if (tempInfo.isProduct())
                    isInstalledProduct = true; 
                  profileInfoMap.put(profile, tempInfo);
                } 
                b1++;
              } 
            } 
          } 
        }  
      b++;
    } 
    profileInfoList = new ArrayList<>(profileInfoMap.values());
    for (int i = 0; i < profileInfoList.size(); i++) {
      String parentProfile = null;
      ProfileInfo checkProfileInfo = profileInfoList.get(i);
      String profileName = checkProfileInfo.getProfile();
      String[] profileNameArr = StringUtil.split(String.valueOf(profileName), "-");
      if (profileNameArr.length > 1) {
        if (profileName.contains("mobile")) {
          parentProfile = "mobile";
        } else if (profileName.contains("wearable")) {
          parentProfile = "wearable";
        } else if (profileName.contains("tv")) {
          parentProfile = "tv";
        } else if (profileName.contains("tizen")) {
          parentProfile = "tizen";
        } 
        if (parentProfile != null) {
          ProfileInfo parentProfileInfo = profileInfoMap.get(parentProfile);
          ProfileInfo productProfileInfo = profileInfoMap.get(profileName);
          if (parentProfileInfo == null || productProfileInfo == null || productProfileInfo.getVersionInfos().size() < 1) {
            profileInfoMap.remove(profileName);
            logger.error("Failed to get available product by not exist the installed profile : {}", profileName);
          } else {
            List<String> versionList = new ArrayList<>(productProfileInfo.getVersionInfos());
            for (String versionKey : versionList) {
              ProfileVersionInfo parentVersionInfo = parentProfileInfo.getVersionInfo(versionKey);
              ProfileVersionInfo productVersionInfo = productProfileInfo.getVersionInfo(versionKey);
              if (parentVersionInfo == null) {
                productProfileInfo.getVersionInfoMap().remove(versionKey);
                productProfileInfo.getVersions().remove(versionKey);
                if (productProfileInfo.getVersionInfoMap().size() < 1)
                  profileInfoMap.remove(profileName); 
                logger.error("Failed to get available product by not exist the installed profile : {}-{}", profileName, versionKey);
                continue;
              } 
              if (productVersionInfo != null)
                productVersionInfo.setSupportIDE(parentVersionInfo.getSupportIDE()); 
            } 
          } 
        } 
      } 
    } 
    profileInfoList = new ArrayList<>(profileInfoMap.values());
    if (profileInfoList.size() > 0) {
      loadIdeInstallProfileInfo = profileInfoList;
    } else {
      loadIdeInstallProfileInfo = new ArrayList<>();
    } 
  }
  
  public static List<ProfileInfo> getProfileInfos() {
    try {
      if (loadIdeInstallProfileInfo == null || loadIdeInstallProfileInfo.size() < 1) {
        String sdkPath = getSDKPath();
        String ideProfilePath = String.valueOf(sdkPath) + File.separator + "platforms" + File.separator + ".info";
        File ideProfileFile = new File(ideProfilePath, "ide_profile.properties");
        if (ideProfileFile.exists()) {
          loadIdeProfileInfo(ideProfileFile);
        } else {
          getProfileInfosToLocalDirectory();
        } 
      } 
    } catch (Exception e) {
      logger.error("Failed to load from ide_profile.properties", e.getMessage());
    } 
    return loadIdeInstallProfileInfo;
  }
  
  public static List<ProfileInfo> flattenPlatformPathInfos() {
    return flattenPlatformPathInfos(getProfileInfos());
  }
  
  public static List<ProfileInfo> getProfileInfoToIdeType(String ideType) {
    List<ProfileInfo> profileInfosList = profileInfoToAppTypeMap.get(ideType);
    if (profileInfosList != null && profileInfosList.size() > 0)
      return profileInfosList; 
    List<ProfileInfo> ideTypeProfileList = new ArrayList<>();
    List<ProfileInfo> flattenProfileInfosList = flattenPlatformPathInfosToIdeType(ideType);
    if (flattenProfileInfosList == null || flattenProfileInfosList.size() < 1) {
      logger.trace("Argument for flatten infos is null. This is GIGO situation.");
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
    profileInfoToAppTypeMap.put(ideType, profileInfosList);
    return profileInfosList;
  }
  
  public static List<ProfileInfo> flattenPlatformPathInfosToIdeType(String ideType) {
    List<ProfileInfo> profileInfosList = flattenProfileInfoToAppTypeMap.get(ideType);
    if (profileInfosList != null && profileInfosList.size() > 0)
      return profileInfosList; 
    List<ProfileInfo> ideTypeProfileList = new ArrayList<>();
    profileInfosList = getProfileInfos();
    if (profileInfosList == null || profileInfosList.size() < 1) {
      logger.trace("Argument for flatten infos is null. This is GIGO situation.");
      return ideTypeProfileList;
    } 
    for (ProfileInfo profileInfo : profileInfosList) {
      String profile = profileInfo.getProfile();
      for (String version : profileInfo.getVersions()) {
        String path = profileInfo.getPlatformPath(version);
        ProfileVersionInfo profileVersionInfo = profileInfo.getVersionInfoMap().get(version);
        if (profileVersionInfo.isSupportIDE(ideType)) {
          ProfileInfo info = new ProfileInfo(profile, version, path);
          info.appendVersionInfo(version, profileVersionInfo);
          ideTypeProfileList.add(info);
        } 
      } 
    } 
    flattenProfileInfoToAppTypeMap.put(ideType, ideTypeProfileList);
    return ideTypeProfileList;
  }
  
  public static boolean isSupportProfileToAppType(ProfileInfo checkProfileInfo, String appType) {
    List<ProfileInfo> profileInfosList = flattenPlatformPathInfosToIdeType(appType);
    if (profileInfosList == null || profileInfosList.size() < 1) {
      logger.trace("Argument for flatten infos is null. This is GIGO situation.");
      return false;
    } 
    for (ProfileInfo profileInfo : profileInfosList) {
      String profile = profileInfo.getProfile();
      if (!profile.equals(checkProfileInfo.getProfile()))
        continue; 
      String[] versionArray = new String[checkProfileInfo.getVersions().size()];
      checkProfileInfo.getVersions().toArray(versionArray);
      byte b;
      int i;
      String[] arrayOfString1;
      for (i = (arrayOfString1 = versionArray).length, b = 0; b < i; ) {
        String versionStr = arrayOfString1[b];
        if (profileInfo.isSupportPlatformVersion(versionStr))
          return true; 
        b++;
      } 
    } 
    return false;
  }
  
  public static boolean isSupportSameProfileToAppType(String profileName, String version, String appType) {
    List<ProfileInfo> profileInfosList = flattenPlatformPathInfosToIdeType(appType.toLowerCase());
    if (profileInfosList == null || profileInfosList.size() < 1) {
      logger.trace("Argument for flatten infos is null. This is GIGO situation.");
      return false;
    } 
    for (ProfileInfo profileInfo : profileInfosList) {
      String profile = profileInfo.getProfile();
      if (!profile.toLowerCase().equals(profileName.toLowerCase()))
        continue; 
      String[] versionArray = new String[profileInfo.getVersions().size()];
      profileInfo.getVersions().toArray(versionArray);
      byte b;
      int i;
      String[] arrayOfString1;
      for (i = (arrayOfString1 = versionArray).length, b = 0; b < i; ) {
        String versionStr = arrayOfString1[b];
        if (versionStr.equals(version))
          return true; 
        b++;
      } 
    } 
    return false;
  }
  
  public static List<ProfileInfo> flattenPlatformPathInfos(List<ProfileInfo> profileInfos) {
    List<ProfileInfo> list = new ArrayList<>();
    if (profileInfos == null) {
      logger.trace("Argument for flatten infos is null. This is GIGO situation.");
      return list;
    } 
    for (ProfileInfo profileInfo : profileInfos) {
      String profile = profileInfo.getProfile();
      for (String version : profileInfo.getVersions()) {
        String path = profileInfo.getPlatformPath(version);
        ProfileVersionInfo tmpVersionInfo = profileInfo.getVersionInfo(version);
        ProfileInfo info = new ProfileInfo(profile, version, path);
        info.appendVersionInfo(version, tmpVersionInfo);
        list.add(info);
      } 
    } 
    return list;
  }
  
  public static ProfileInfo getProfileInfo(String profile) {
    if (profile == null)
      return null; 
    List<ProfileInfo> profileInfoList = getProfileInfos();
    for (ProfileInfo profileInfo : profileInfoList) {
      if (profile.toLowerCase().equals(profileInfo.getProfile()))
        return profileInfo; 
    } 
    return null;
  }
  
  public static ProfileInfo getLatestProfileInfo() {
    ProfileInfo latestProfileInfo = null;
    Version latestVersion = Version.ZERO;
    Version tempVersion = null;
    for (ProfileInfo profileInfo : getProfileInfos()) {
      tempVersion = new Version(profileInfo.getLatestPlatformVersion());
      if (latestVersion.compareTo(tempVersion) < 0) {
        latestVersion = tempVersion;
        latestProfileInfo = profileInfo;
      } 
    } 
    return latestProfileInfo;
  }
  
  public static String getPlatformVersionNum() {
    return getVersion(getPlatformVersionName());
  }
  
  public static String getVersion(String platform) {
    String[] platformSplit = platform.split("-");
    if (platformSplit.length > 1 && 
      platformSplit[platformSplit.length - 1].matches("((\\d+))(\\.\\d+)+"))
      return platformSplit[platformSplit.length - 1]; 
    return null;
  }
  
  public static String getProfile(String platform) {
    int index = platform.lastIndexOf("-");
    if (index <= 0)
      return null; 
    String profile = platform.substring(0, index);
    if (getVersion(platform) == null)
      return null; 
    if (profile.matches("[a-zA-Z\\-]+"))
      return profile; 
    return null;
  }
  
  public static String getPlatformVersionName() {
    ProfileInfo latestProfileInfo = getLatestProfileInfo();
    return (latestProfileInfo == null) ? "" : latestProfileInfo.getLatestPlatformName();
  }
  
  public static File getPlatformDir() {
    String platformPath = String.valueOf(getSDKPath()) + File.separator + "platforms";
    File platformDir = new File(platformPath);
    if (platformDir.isDirectory())
      return platformDir; 
    return null;
  }
  
  public static float getPlatformVersionNumToFloat() {
    float version = 0.0F;
    try {
      version = Float.parseFloat(getPlatformVersionNum());
    } catch (NumberFormatException numberFormatException) {
      return version;
    } 
    return version;
  }
  
  public static String getSamplesPath() {
    return String.valueOf(getPlatformVersionPath()) + File.separator + "samples";
  }
  
  public static String getSamplesPath(ProfileInfo profile, String version) {
    String path = getPlatformPath(profile, version);
    if (StringUtil.isEmpty(path))
      return null; 
    return String.valueOf(path) + File.separator + "samples";
  }
  
  public static String getSnippetsPath() {
    return String.valueOf(getPlatformVersionPath()) + File.separator + "snippets";
  }
  
  public static String getSnippetsPath(ProfileInfo profile, String version) {
    String path = getPlatformPath(profile, version);
    if (StringUtil.isEmpty(path))
      return null; 
    return String.valueOf(path) + File.separator + "samples";
  }
  
  public static String getOnDemandPath() {
    return String.valueOf(getPlatformVersionPath()) + File.separator + "on-demand";
  }
  
  public static String getOnDemandPath(ProfileInfo profile, String version) {
    String path = getPlatformPath(profile, version);
    if (StringUtil.isEmpty(path))
      return null; 
    return String.valueOf(path) + File.separator + "samples";
  }
  
  public static ProfileInfo getProfileInfo(IDevice device) {
    ProfileInfo devicePlatformInfo = ProfileInfo.getProfileInfo(device);
    if (devicePlatformInfo == null)
      return null; 
    String deviceProfile = devicePlatformInfo.getProfile();
    String deviceVersion = devicePlatformInfo.getLatestPlatformVersion();
    Version intDeviceVersion = new Version(deviceVersion);
    Version tempVersion = null;
    Version validVersion = Version.ZERO;
    Version highestVersion = Version.ZERO;
    String validPath = "";
    String highestPath = "";
    List<ProfileInfo> profileInfoList = getProfileInfos();
    for (ProfileInfo profileInfo : profileInfoList) {
      if (deviceProfile.equals(profileInfo.getProfile())) {
        Set<String> versionSet = profileInfo.getVersions();
        for (String version : versionSet) {
          if (deviceVersion.equals(version))
            return devicePlatformInfo; 
          tempVersion = new Version(version);
          if (intDeviceVersion.compareTo(tempVersion) > 0 && validVersion.compareTo(tempVersion) < 0) {
            validVersion = tempVersion;
            validPath = profileInfo.getPlatformPath(version);
          } 
          if (highestVersion.compareTo(tempVersion) < 0) {
            highestVersion = tempVersion;
            highestPath = profileInfo.getPlatformPath(version);
          } 
        } 
        if (validVersion != Version.ZERO)
          return new ProfileInfo(deviceProfile, validVersion.get(), validPath); 
        return new ProfileInfo(deviceProfile, highestVersion.get(), highestPath);
      } 
    } 
    return null;
  }
  
  public static String getLibraryPath() {
    return String.valueOf(getSDKPath()) + File.separator + "library";
  }
  
  public static String getToolsPath() {
    return String.valueOf(getSDKPath()) + File.separator + "tools";
  }
  
  public static String getPlatformToolsPath(String profileName, String version) {
    String platformToolsPath = null;
    ProfileInfo profileInfo = getProfileInfo(profileName);
    if (profileInfo == null)
      return platformToolsPath; 
    ProfileVersionInfo profileVersionInfo = profileInfo.getVersionInfo(version);
    if (profileVersionInfo == null)
      return platformToolsPath; 
    String versionPath = profileVersionInfo.getVersionPath();
    File versionFilePath = new File(versionPath);
    File toolFilePath = new File(versionFilePath.getParent(), "common");
    if (toolFilePath.exists())
      platformToolsPath = toolFilePath.getPath(); 
    return platformToolsPath;
  }
  
  public static String getSDBPath() {
    return String.valueOf(getToolsPath()) + File.separator + (OSChecker.isWindows() ? "sdb.exe" : "sdb");
  }
  
  public static String getSdbWinUsbApiPath() {
    return String.valueOf(getToolsPath()) + File.separator + "SdbWinUsbApi.dll";
  }
  
  public static String getInstallManagerPath() {
    return String.valueOf(getSDKPath()) + File.separator + "install-manager";
  }
  
  public static String getEmulatorPath() {
    return String.valueOf(getToolsPath()) + File.separator + "emulator";
  }
  
  public static String getEmulatorBinPath() {
    return String.valueOf(getEmulatorPath()) + File.separator + "bin";
  }
  
  public static String getDeviceManagerPath() {
    return String.valueOf(getToolsPath()) + File.separator + "device-manager";
  }
  
  public static String getBatteryHistorianPath() {
    return String.valueOf(getToolsPath()) + File.separator + "battery-historian";
  }
  
  public static String getDeviceManagerBinPath() {
    return String.valueOf(getDeviceManagerPath()) + File.separator + "bin";
  }
  
  public static String getUserHomePath() {
    return defaultHomePath;
  }
  
  public static String getCheckerPath() {
    return String.valueOf(getToolsPath()) + File.separator + "checker";
  }
  
  public static String getUserDataPath() {
    if (sdkDataPath != null)
      return sdkDataPath; 
    return String.valueOf(getUserHomePath()) + File.separator + "tizen-sdk-data";
  }
  
  public static String getIdeUserDataPath() {
    return String.valueOf(getUserDataPath()) + File.separator + "ide";
  }
  
  public static String getAnalyticsConfig() {
    String dataPath = getIdeUserDataPath();
    File f = new File(dataPath);
    if (!f.exists())
      f.mkdirs(); 
    Path filePath = Paths.get(dataPath, new String[] { "analytics.conf" });
    return filePath.toString();
  }
  
  public static String getIdeUserDataLogPath() {
    return String.valueOf(getIdeUserDataPath()) + File.separator + "logs";
  }
  
  public static String getDocumentsPath() {
    return String.valueOf(getSDKPath()) + File.separator + "documents";
  }
  
  private static void parseSdkInfoFile(String sdkPath) throws Exception {
    File sdkInfoFile = new File(sdkPath, "sdk.info");
    logger.info("Loading from {}...", sdkInfoFile.getAbsolutePath());
    InputStreamReader reader = null;
    try {
      Properties prop = new Properties() {
          private static final long serialVersionUID = 8616159524414427526L;
          
          public synchronized void load(Reader reader) throws IOException {
            BufferedReader in = new BufferedReader(reader);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String nextLine = null;
            while ((nextLine = in.readLine()) != null) {
              out.write(nextLine.replace("\\", "\\\\").getBytes());
              out.write("\n".getBytes());
            } 
            InputStream is = new ByteArrayInputStream(out.toByteArray());
            super.load(new InputStreamReader(is));
          }
        };
      reader = new InputStreamReader(new FileInputStream(sdkInfoFile), "UTF-8");
      prop.load(reader);
      sdkInstallPath = prop.getProperty("TIZEN_SDK_INSTALLED_PATH");
      sdkDataPath = prop.getProperty("TIZEN_SDK_DATA_PATH");
      jdkPath = prop.getProperty("JDK_PATH");
      logger.info("Loaded (install_path - {}, data_path - {})", sdkInstallPath, sdkDataPath);
    } finally {
      IOUtil.tryClose(new Object[] { reader });
    } 
  }
  
  private static void lookupSdkInfoFile(String path) {
    String sdkPath = path;
    while (sdkPath != null) {
      try {
        parseSdkInfoFile(sdkPath);
        break;
      } catch (FileNotFoundException e) {
        logger.info("Failed to find: " + e.getMessage());
        sdkPath = (new File(sdkPath)).getParent();
      } catch (Exception e) {
        logger.error("Failed to load from " + sdkPath, e);
      } 
    } 
  }
  
  private static void loadSdkPath() throws Exception {
    try {
      String sdkPath = System.getenv("TIZEN_HOME");
      if (sdkPath == null) {
        String eclipsePath = Platform.getInstallLocation().getURL().getPath();
        sdkPath = (new File(eclipsePath)).getParent();
      } 
      lookupSdkInfoFile(sdkPath);
    } catch (NoClassDefFoundError noClassDefFoundError) {
      String path = InstallPathConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      path = URLDecoder.decode(path, "UTF-8");
      lookupSdkInfoFile(path);
    } catch (Throwable t) {
      logger.error("Failed to load from sdk.info", t);
    } finally {
      if (sdkInstallPath == null || sdkDataPath == null)
        loadSdkPathFromDefault(); 
    } 
  }
  
  private static void loadSdkPathFromDefault() throws Exception {
    if (OSChecker.isWindows()) {
      defaultHomePath = System.getenv("LOCALAPPDATA");
      if (defaultHomePath == null)
        defaultHomePath = getRegistryValue("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "\"Local AppData\""); 
    } else if (OSChecker.isLinux() || OSChecker.isUnix() || OSChecker.isMAC()) {
      defaultHomePath = System.getProperty("user.home");
    } else {
      throw new Exception(String.valueOf(System.getProperty("os.name")) + " is not supported currently.");
    } 
    String sdkPath = String.valueOf(defaultHomePath) + File.separatorChar + SDKSUFFIX;
    boolean status = true;
    if (HostUtil.exists(sdkPath)) {
      String contents = HostUtil.getContents(sdkPath);
      if (StringUtil.isEmpty(contents)) {
        status = false;
      } else {
        String[] fileContent = contents.split("=");
        if (HostUtil.exists(fileContent[1])) {
          sdkInstallPath = fileContent[1];
        } else {
          status = false;
        } 
      } 
    } else {
      status = false;
    } 
    if (!status) {
      try {
        if (!isCLI())
          DialogUtil.openMessageDialog("Tizen Studio is not installed properly."); 
      } catch (Exception e) {
        logger.error("Failed to open a dialog", e);
      } 
      throw new Exception("Tizen Studio is not installed properly.");
    } 
  }
  
  private static String getRegistryValue(String node, String key) {
    if (!OSChecker.isWindows())
      return null; 
    BufferedReader br = null;
    String value = "";
    String query = "reg query \"" + node + "\" /v " + key;
    try {
      Process process = Runtime.getRuntime().exec(query);
      String encoding = System.getProperty("sun.jnu.encoding");
      br = new BufferedReader(new InputStreamReader(process.getInputStream(), encoding));
      String line = null;
      while ((line = br.readLine()) != null) {
        int index = line.indexOf("REG_SZ");
        if (index >= 0)
          value = line.substring(index + "REG_SZ".length()).trim(); 
      } 
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } finally {
      IOUtil.tryClose(new Object[] { br });
    } 
    return value;
  }
  
  private static void loadIdeProfileInfo(File ideProfileFile) throws Exception {
    logger.info("Loading from {}...", ideProfileFile.getAbsolutePath());
    InputStreamReader reader = null;
    try {
      Properties ideProfileProp = new Properties() {
          private static final long serialVersionUID = 8616159524414427526L;
          
          public synchronized void load(Reader reader) throws IOException {
            BufferedReader in = new BufferedReader(reader);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String nextLine = null;
            while ((nextLine = in.readLine()) != null) {
              out.write(nextLine.replace("\\", "\\\\").getBytes());
              out.write("\n".getBytes());
            } 
            InputStream is = new ByteArrayInputStream(out.toByteArray());
            super.load(new InputStreamReader(is));
          }
        };
      reader = new FileReader(ideProfileFile);
      ideProfileProp.load(reader);
      List<ProfileInfo> profileInfoList = new ArrayList<>();
      Map<String, ProfileInfo> profileInfoMap = new HashMap<>();
      for (Object profileKey : ideProfileProp.keySet()) {
        Object profileValue = ideProfileProp.get(profileKey);
        if (profileValue == null)
          continue; 
        String[] keyArr = StringUtil.split(String.valueOf(profileKey), "-");
        String[] valueArr = StringUtil.split(String.valueOf(profileValue), ",");
        if (keyArr.length < 3 || valueArr.length < 2)
          continue; 
        String profileInfoKey = null;
        String profileVersion = null;
        String profileIdeType = null;
        if (keyArr.length == 3) {
          profileInfoKey = keyArr[0];
          profileVersion = keyArr[1];
          profileIdeType = keyArr[2];
        } else {
          for (int i = 0; i < keyArr.length - 2; i++) {
            if (profileInfoKey == null) {
              profileInfoKey = keyArr[i];
            } else {
              profileInfoKey = String.valueOf(profileInfoKey) + keyArr[i];
            } 
            if (i != keyArr.length - 3)
              profileInfoKey = String.valueOf(profileInfoKey) + "-"; 
          } 
          profileVersion = keyArr[keyArr.length - 2];
          profileIdeType = keyArr[keyArr.length - 1];
        } 
        String profileParentProfile = valueArr[0];
        String profilePath = (new File(getSDKPath(), String.valueOf(File.separator) + valueArr[1])).getPath();
        ProfileInfo profileInfo = profileInfoMap.get(profileInfoKey.toLowerCase());
        if (profileInfo == null) {
          profileInfo = new ProfileInfo(profileInfoKey.toLowerCase(), profileVersion, profilePath, profileParentProfile);
        } else {
          profileInfo.appendVersion(profileVersion, profilePath);
        } 
        ProfileVersionInfo profileVersionInfo = new ProfileVersionInfo(profileVersion, profilePath, profileIdeType.toLowerCase());
        profileInfo.appendVersionInfo(profileVersion, profileVersionInfo);
        if (profileInfo.isProduct())
          isInstalledProduct = true; 
        profileInfoMap.put(profileInfoKey.toLowerCase(), profileInfo);
      } 
      profileInfoList = new ArrayList<>(profileInfoMap.values());
      if (profileInfoList.size() > 0) {
        loadIdeInstallProfileInfo = profileInfoList;
      } else {
        loadIdeInstallProfileInfo = new ArrayList<>();
      } 
    } finally {
      IOUtil.tryClose(new Object[] { reader });
    } 
  }
  
  public static String getEclipsePath() {
    if (OSChecker.isMAC())
      return String.valueOf(getSDKPath()) + "/TizenStudio.app/Contents/Eclipse"; 
    return String.valueOf(getSDKPath()) + "/ide";
  }
  
  public static String getJava() {
    String installedPath = getSDKPath();
    Path jdkPath = Paths.get(installedPath, new String[] { "jdk" }).normalize();
    String infoJdkStr = getJDKPath();
    if (infoJdkStr != null && !infoJdkStr.isEmpty()) {
      File infoJdkFile = new File(infoJdkStr);
      if (infoJdkFile.exists()) {
        logger.trace("Using sdk.info java at " + infoJdkStr);
        jdkPath = Paths.get(infoJdkStr, new String[0]);
      } else {
        logger.trace("Jdk specified in sdk.info: " + infoJdkStr + " does not exist");
      } 
    } 
    File jdkFile = new File(jdkPath.toUri());
    if (!jdkFile.exists()) {
      logger.trace("In studio: " + jdkPath + " does not exist" + "\n" + "Using System java");
      jdkPath = Paths.get(System.getProperty("java.home"), new String[0]).normalize();
    } 
    String java = "";
    if (OSChecker.isMAC()) {
      java = Paths.get(jdkPath.toString(), new String[] { "Contents", "Home", "bin", "java" }).normalize().toString();
    } else {
      java = Paths.get(jdkPath.toString(), new String[] { "bin", "java" }).normalize().toString();
    } 
    return java;
  }
}
