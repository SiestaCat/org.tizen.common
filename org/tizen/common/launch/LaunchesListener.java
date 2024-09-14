package org.tizen.common.launch;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchesListener2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.launch.context.LaunchContext;
import org.tizen.common.util.ArrayUtil;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.SmartDevelopmentBridge;

public class LaunchesListener implements ILaunchesListener2 {
  Logger logger = LoggerFactory.getLogger(getClass());
  
  public static final String LAUNCH_ATTR_KEY_IS_LAUNCHING = "LAUNCH_ATTR_KEY_IS_LAUNCHING";
  
  public static final String LAUNCH_ATTR_VALUE_LAUNCHING_ALREADY = "launched already";
  
  private List<LaunchContext> contexts = new ArrayList<>();
  
  public synchronized void launchesAdded(ILaunch[] launches) {
    byte b;
    int i;
    ILaunch[] arrayOfILaunch;
    for (i = (arrayOfILaunch = launches).length, b = 0; b < i; ) {
      ILaunch launch = arrayOfILaunch[b];
      try {
        ILaunchConfiguration config = launch.getLaunchConfiguration();
        if (config == null) {
          this.logger.debug("launch configuration is null.");
        } else {
          String projectName = config.getAttribute("org.tizen.common.CONFIG_ATTR_PROJECT_NAME", "");
          String serialNum = config.getAttribute("org.tizen.commonCONFIG_ATTR_DEVICE_SERIAL_NO", "");
          if (getContext(projectName, serialNum) != null) {
            this.logger.debug("launch already - project: {}, device serial: {}", projectName, serialNum);
            launch.setAttribute("LAUNCH_ATTR_KEY_IS_LAUNCHING", "launched already");
          } else {
            this.logger.debug("Added - project: {}, device serial: {}", projectName, serialNum);
          } 
          launch.setAttribute("org.tizen.commonCONFIG_ATTR_DEVICE_SERIAL_NO", serialNum);
          addContext(projectName, serialNum);
        } 
      } catch (CoreException e) {
        this.logger.error(e.getMessage(), (Throwable)e);
      } 
      b++;
    } 
  }
  
  private void addContext(String projectName, String deviceSerialNum) {
    LaunchContext context = new LaunchContext();
    setProjectName(context, projectName);
    context.setValue("org.tizen.commonCONFIG_ATTR_DEVICE_SERIAL_NO", deviceSerialNum);
    this.contexts.add(context);
  }
  
  protected void setProjectName(LaunchContext context, String projectName) {
    context.setValue("org.tizen.common.CONFIG_ATTR_PROJECT_NAME", projectName);
  }
  
  protected IDevice getDevice(ILaunchConfiguration config) throws CoreException {
    IDevice[] devices = SmartDevelopmentBridge.getBridge().getDevices();
    if (ArrayUtil.isEmpty(devices))
      return null; 
    String deviceName = config.getAttribute("org.tizen.common.CONFIG_ATTR_DEVICE_NAME", "");
    String deviceSerial = config.getAttribute("org.tizen.commonCONFIG_ATTR_DEVICE_SERIAL_NO", "");
    byte b;
    int i;
    IDevice[] arrayOfIDevice1;
    for (i = (arrayOfIDevice1 = devices).length, b = 0; b < i; ) {
      IDevice device = arrayOfIDevice1[b];
      if (device.isEmulator() && 
        deviceName.equals(device.getDeviceName()))
        return device; 
      if (deviceSerial.equals(device.getSerialNumber()))
        return device; 
      b++;
    } 
    return null;
  }
  
  public LaunchContext getContext(String projectName, String serialNum) {
    for (int i = 0; i < this.contexts.size(); i++) {
      LaunchContext tmpContext = this.contexts.get(i);
      if (getProjectName(tmpContext).equals(projectName) && 
        tmpContext.getValue("org.tizen.commonCONFIG_ATTR_DEVICE_SERIAL_NO").equals(serialNum))
        return tmpContext; 
    } 
    return null;
  }
  
  private static String getProjectName(LaunchContext context) {
    return (String)context.getValue("org.tizen.common.CONFIG_ATTR_PROJECT_NAME");
  }
  
  public void launchesChanged(ILaunch[] launches) {}
  
  public void launchesRemoved(ILaunch[] launches) {
    removeLaunches(launches);
  }
  
  public void launchesTerminated(ILaunch[] launches) {
    removeLaunches(launches);
  }
  
  public void removeLaunches(ILaunch[] launches) {
    byte b;
    int i;
    ILaunch[] arrayOfILaunch;
    for (i = (arrayOfILaunch = launches).length, b = 0; b < i; ) {
      ILaunch launch = arrayOfILaunch[b];
      try {
        String projectName = launch.getLaunchConfiguration().getAttribute("org.tizen.common.CONFIG_ATTR_PROJECT_NAME", "");
        String deviceSerialNum = launch.getAttribute("org.tizen.commonCONFIG_ATTR_DEVICE_SERIAL_NO");
        LaunchContext context = getContext(projectName, deviceSerialNum);
        this.contexts.remove(context);
        this.logger.debug("removed - project: {}, device serial: {}", projectName, deviceSerialNum);
      } catch (CoreException e) {
        this.logger.error(e.getMessage(), (Throwable)e);
      } 
      b++;
    } 
  }
}
