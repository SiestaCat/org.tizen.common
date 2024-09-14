package org.tizen.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.PlatformCapability;

public class TizenPlatformConstants {
  private static final Logger logger = LoggerFactory.getLogger(TizenPlatformConstants.class);
  
  public static final String[] PROFILE_WEARABLES = new String[] { "tizenw", "wearable" };
  
  public static final String PROFILE_MOBILE = "mobile";
  
  public static final String PROFILE_TIZEN = "tizen";
  
  public static final String LOCALHOST = "127.0.0.1";
  
  public static final String ENVIRONMENT_SETTING_CMD = "export ";
  
  public static final String CMD_RESULT_CHECK = "; echo $?;";
  
  public static final String CMD_RESULT_PREFIX = "cmd_ret:";
  
  public static final String CMD_SUFFIX = "; echo cmd_ret:$?;";
  
  public static final String CMD_SUCCESS = "cmd_ret:0";
  
  public static final String CMD_FAILURE = "cmd_ret:1";
  
  public static final String CMD_PROCESS;
  
  public static final int OS = 1024;
  
  public static final String PACKAGENAME_PREFIX = "org.tizen";
  
  public static final String DEBIAN_INSTALL_PATH = "opt/*";
  
  public static final String PKGTYPE_TPK = "TPK";
  
  public static final String PKGTYPE_TEP = "TEP";
  
  public static final String PKGTYPE_WGT = "WGT";
  
  public static final String PKGTYPE_RPM = "RPM";
  
  public static final String PKGTYPE_DEB = "DEB";
  
  public static final String DEBUG_COREFILE_PATH;
  
  public static final String DEBUG_COREFILE_EXTENSION;
  
  public static final String DEBUG_COREFILE_EXTRACT_COMMAND;
  
  public static final String DEBUG_COREFILE_EXTRACT_TEMP_PATH;
  
  public static final String GDBSERVER_CMD;
  
  public static final String GDBSERVER_PLATFORM_CMD;
  
  public static final String DEBUG_ATTACH_CMD_FORMAT;
  
  public static final String ATTACH_OPTION = " --attach ";
  
  public static final String HOST_GDBSERVER_PATH = "/usr/bin/gdbserver";
  
  public static final String SDK_ROOT_COMMAND;
  
  public static final String APP_INSTALL_PATH;
  
  public static final String APP_ICON_INSTALL_PATH;
  
  public static final String TEMPORARY_PKG_PATH;
  
  public static final String TEMPORARY_DIR_PATH;
  
  public static final String PKG_TOOL;
  
  public static final String PKG_TOOL_LIST_COMMAND;
  
  public static final String PKG_TOOL_REMOVE_COMMAND;
  
  public static final String PKG_TOOL_INSTALL_COMMAND;
  
  public static final String PKG_TOOL_INSTALL_OLD_COMMAND;
  
  public static final String PKG_TOOL_TEP_INSTALL_COMMAND;
  
  public static final String PKG_TOOL_TEP_INSTALL_OLD_COMMAND;
  
  public static final String PKG_TOOL_RUNNING_CHECK_COMMAND;
  
  public static final String PKG_TOOL_TERMINATE_COMMAND;
  
  public static final String PKG_TOOL_REINSTALL_COMMAND;
  
  public static final String PKG_TOOL_INSTALL_PATH_COMMAND;
  
  public static final String PKG_TOOL_ROAPP_CHECK_COMMAND;
  
  public static final String PKG_TOOL_APPID_CHECK_COMMAND;
  
  public static final String REMOVE_FILE_COMMAND;
  
  public static final String DLOGUTIL_CMD;
  
  public static final String ROAPP_RESULT;
  
  public static final String RWAPP_RESULT;
  
  public static final String RDS_PUSH_DIRECTORY_COMMAND = "mkdir -p -m 755 \"%s\"; echo cmd_ret:$?;";
  
  public static final String RDS_CHANGE_OWNER_COMMAND = "chown -R app:app \"%s\"; echo cmd_ret:$?;";
  
  public static final String RDS_UNZIP_COMMAND = "unzip -o \"%s\" -d \"%s\"; echo cmd_ret:$?;";
  
  public static final String LAUNCH_CMD;
  
  public static final String LAUNCH_CMD_SUCCESS = "... successfully launched";
  
  public static final String LAUNCH_CMD_FAILURE = "... launch failed";
  
  public static final String ELM_SCALE_GETTER = String.valueOf(TOOLS_TARGET_PATH) + "/elm_scale_getter/get_elm_scale ";
  
  public static final String WIDGET_LAUNCHED_NOTIFICATION = "result: launched";
  
  public static final String CONTROL_EXTENSION = ".control";
  
  public static final String TOOLS_TARGET_PATH = "/home/developer/sdk_tools";
  
  public static final String TOOLS_TARGET_REAL_PATH = "/opt/usr/apps/tmp/sdk_tools";
  
  public static final boolean SIGNING_DEFAULT;
  
  public static final boolean SIGNING_BUILD_PACKAGE;
  
  public static final String CODE_COVERAGE_BUILD_OPTION = "-fprofile-arcs -ftest-coverage";
  
  public static final String CODE_COVERAGE_LAUNCH_OPTION = " __AUL_SDK__ CODE_COVERAGE";
  
  public static final String PROCESS_SIGKILL_COMMAND = "kill -9 %s";
  
  public static final String PKG_TOOL_RUNNUNG_CHECK_COMMAND_RUNNING = "is Running";
  
  public static final String MANIFEST_XML_FILE_NAME;
  
  public static final String CORE_MANIFEST_XML_FILE_NAME;
  
  public static final String PLATFORM_NAME_WEARABLE;
  
  public static final String C_PROJECT_CONFIGURATION_FILE_NAME = ".cproject";
  
  public static final String TIZEN_PROJECT_CONFIGURATION_FILE_NAME = ".tproject";
  
  static {
    GDBSERVER_CMD = String.valueOf(TOOLS_TARGET_PATH) + "/gdbserver/gdbserver";
    GDBSERVER_PLATFORM_CMD = String.valueOf(TOOLS_TARGET_PATH) + "/gdbserver-platform/gdbserver";
    APP_INSTALL_PATH = "/opt/usr/apps";
    TEMPORARY_PKG_PATH = "/opt/usr/apps/tmp/";
    APP_ICON_INSTALL_PATH = "shared/res";
    TEMPORARY_DIR_PATH = "/tmp";
    DEBUG_COREFILE_PATH = "/opt/usr/share/crash/dump";
    DEBUG_COREFILE_EXTENSION = "coredump";
    DEBUG_COREFILE_EXTRACT_COMMAND = "tar -xvf %s -C %s --wildcards --no-anchored '%s'";
    DEBUG_COREFILE_EXTRACT_TEMP_PATH = "/tmp";
    SIGNING_DEFAULT = true;
    SIGNING_BUILD_PACKAGE = true;
    MANIFEST_XML_FILE_NAME = "manifest.xml";
    CORE_MANIFEST_XML_FILE_NAME = "tizen-manifest.xml";
    PLATFORM_NAME_WEARABLE = "tizenw";
    PKG_TOOL = "/usr/bin/pkgcmd";
    PKG_TOOL_LIST_COMMAND = String.valueOf(PKG_TOOL) + " -l  | grep \"\\[%s\\]\"";
    PKG_TOOL_REMOVE_COMMAND = String.valueOf(PKG_TOOL) + " -q -u -t %s -n %s";
    PKG_TOOL_INSTALL_COMMAND = String.valueOf(PKG_TOOL) + " -G -q -i -t %s -p \"%s\"";
    PKG_TOOL_INSTALL_OLD_COMMAND = String.valueOf(PKG_TOOL) + " -q -i -t %s -p \"%s\"";
    PKG_TOOL_TEP_INSTALL_COMMAND = String.valueOf(PKG_TOOL) + " -G -i -q -p \"%s\" -e \"%s\"";
    PKG_TOOL_TEP_INSTALL_OLD_COMMAND = String.valueOf(PKG_TOOL) + " -i -q -p \"%s\" -e \"%s\"";
    PKG_TOOL_REINSTALL_COMMAND = String.valueOf(PKG_TOOL) + " -q -r -t %s -n %s";
    PKG_TOOL_RUNNING_CHECK_COMMAND = String.valueOf(PKG_TOOL) + " -C -t %s -n %s";
    PKG_TOOL_TERMINATE_COMMAND = String.valueOf(PKG_TOOL) + " -k -t %s -n %s";
    PKG_TOOL_INSTALL_PATH_COMMAND = String.valueOf(PKG_TOOL) + " -a";
    PKG_TOOL_ROAPP_CHECK_COMMAND = "/usr/bin/pkginfo --pkg %s | grep -i Removable";
    PKG_TOOL_APPID_CHECK_COMMAND = "/usr/bin/pkginfo --app %s | grep %s";
    ROAPP_RESULT = "Removable: 0";
    RWAPP_RESULT = "Removable: 1";
    REMOVE_FILE_COMMAND = "rm -rf %s";
    LAUNCH_CMD = "/usr/bin/launch_app %s";
    SDK_ROOT_COMMAND = "profile";
    CMD_PROCESS = String.valueOf(SDK_ROOT_COMMAND) + " process";
    DLOGUTIL_CMD = "/usr/bin/dlogutil %s";
    DEBUG_ATTACH_CMD_FORMAT = "-a %s -m debug -P %s -attach %s";
  }
  
  public static String getAppTmpDirectory(IDevice device) {
    return String.valueOf(device.getAppInstallPath()) + TEMPORARY_DIR_PATH;
  }
  
  public static String getToolsTargetDirectory(IDevice device) {
    PlatformCapability capa = null;
    try {
      if (device.isSupportCapability()) {
        capa = device.getPlatformCapability();
        if ("enabled".equals(capa.getMultiuserSupport())) {
          String sdkToolPath = capa.getSdkToolPath();
          if (sdkToolPath != null)
            return sdkToolPath; 
        } 
      } 
    } catch (Exception e) {
      logger.error("Cannot find directory for IDE on device", e);
    } 
    return String.valueOf(getAppTmpDirectory(device)) + "/sdk_tools";
  }
  
  public static String getGDBServerPath(IDevice device) {
    return String.valueOf(getToolsTargetDirectory(device)) + "/gdbserver/gdbserver";
  }
}
