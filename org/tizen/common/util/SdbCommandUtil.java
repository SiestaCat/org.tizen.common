package org.tizen.common.util;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.core.application.InstallPathConfig;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.PlatformCapability;

public class SdbCommandUtil {
  private static Logger logger = LoggerFactory.getLogger(SdbCommandUtil.class);
  
  public static boolean filePush(IDevice device, String source, String dest) {
    String command_format = String.valueOf(InstallPathConfig.getSDBPath()) + " -s %s push %s %s ";
    if (device == null)
      return false; 
    if (!(new File(source)).exists())
      return false; 
    String command = String.format(command_format, new Object[] { device.getSerialNumber(), source, dest });
    if (OSChecker.isUnix()) {
      command = String.valueOf(command) + "1>/dev/null";
    } else if (OSChecker.isWindows()) {
      command = String.valueOf(command) + "1>NUL";
    } 
    String command_result = HostUtil.returnExecute(command);
    if ("".equals(command_result))
      return true; 
    return false;
  }
  
  public static void runCommandOnRoot(IDevice device, RootJob job) {
    try {
      if (runRootCommand(device, true))
        job.run(); 
    } finally {
      runRootCommand(device, false);
    } 
  }
  
  public static boolean isMultiUserDevice(IDevice device) {
    boolean isMultiUser = false;
    try {
      PlatformCapability capa = device.getPlatformCapability();
      if ("enabled".equals(capa.getMultiuserSupport())) {
        isMultiUser = true;
      } else {
        isMultiUser = false;
      } 
    } catch (Exception e) {
      isMultiUser = false;
      logger.error("Cannot check whether a device supports multiuser or not. " + device, e);
    } 
    return isMultiUser;
  }
  
  private static boolean runRootCommand(IDevice device, boolean isRoot) {
    String command_format = String.valueOf(InstallPathConfig.getSDBPath()) + " -s %s root %s";
    String command = null;
    command = String.format(command_format, new Object[] { device.getSerialNumber(), isRoot ? "on" : "off" });
    if (OSChecker.isWindows()) {
      command = String.valueOf(command) + " 1>NUL";
    } else {
      command = String.valueOf(command) + " 1>/dev/null";
    } 
    String command_result = HostUtil.returnExecute(command);
    if ("".equals(command_result))
      return true; 
    logger.error("Failed to get root permission on " + device);
    return false;
  }
}
