package org.tizen.common.util;

import java.io.IOException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tizen.common.CommonPlugin;
import org.tizen.common.core.application.InstallProfileConfig;
import org.tizen.common.core.application.ProfileInfo;
import org.tizen.common.core.application.TizenProjectDescription;
import org.tizen.common.launch.LaunchMessages;
import org.tizen.common.rds.ui.preference.RdsPreferencePage;
import org.tizen.common.ui.dialog.NotificationIconType;
import org.tizen.common.ui.dialog.NotificationTrayPopup;
import org.tizen.common.ui.dialog.ShowQuestionToggleDialog;
import org.tizen.sdblib.Device;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.PlatformCapability;
import org.tizen.sdblib.SmartDevelopmentBridge;

public class LaunchUtil {
  private static final String PREF_KEY_PLATFORM_IS_DIFFERENT_ALWAYS = "pref.launch.always.platform.is.different";
  
  private static final String PREF_KEY_PLATFORM_IS_UNKNOWN_ALWAYS = "pref.launch.always.platform.is.unknown";
  
  private static final String CAN_LAUNCH_DELIMITER = "_-_";
  
  private static final Logger logger = LoggerFactory.getLogger(LaunchUtil.class);
  
  public static IDevice getDevice(ILaunchConfiguration configuration, String serialNoKey) throws CoreException {
    String deviceSerialNo = configuration.getAttribute(serialNoKey, "");
    IDevice[] devices = SmartDevelopmentBridge.getBridge().getDevices();
    if (ArrayUtil.isEmpty(devices))
      return null; 
    byte b;
    int i;
    IDevice[] arrayOfIDevice1;
    for (i = (arrayOfIDevice1 = devices).length, b = 0; b < i; ) {
      IDevice device = arrayOfIDevice1[b];
      if (device.getSerialNumber().equals(deviceSerialNo))
        return device; 
      b++;
    } 
    return null;
  }
  
  public static IDevice getDevice(ILaunch launch, String serialNoKey) throws CoreException {
    String deviceSerialNo = launch.getAttribute(serialNoKey);
    IDevice[] devices = SmartDevelopmentBridge.getBridge().getDevices();
    if (ArrayUtil.isEmpty(devices))
      return null; 
    byte b;
    int i;
    IDevice[] arrayOfIDevice1;
    for (i = (arrayOfIDevice1 = devices).length, b = 0; b < i; ) {
      IDevice device = arrayOfIDevice1[b];
      if (device.getSerialNumber().equals(deviceSerialNo))
        return device; 
      b++;
    } 
    return null;
  }
  
  public static boolean isRdsMode(IProject project) {
    return RdsPreferencePage.isRdsMode(project);
  }
  
  public static IProject getProject(ILaunchConfiguration config) throws CoreException {
    IProject project = null;
    String projectName = config.getAttribute("org.tizen.common.CONFIG_ATTR_PROJECT_NAME", "");
    if (StringUtil.isEmpty(projectName)) {
      ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
      if (selection instanceof IStructuredSelection) {
        IStructuredSelection sel = (IStructuredSelection)selection;
        Object res = sel.getFirstElement();
        if (res instanceof IResource)
          project = ((IResource)res).getProject(); 
      } 
    } else {
      project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    } 
    return project;
  }
  
  public static boolean checkProfile(IDevice device, IProject project) throws CoreException {
    Assert.notNull(project);
    String appProfile = LaunchMessages.UNKNOWN_PROFILE;
    TizenProjectDescription description = 
      ProjectUtil.getTizenProjectDescription(project);
    if (description != null)
      appProfile = description.getProfileName(); 
    String deviceType = device.isEmulator() ? "emulator" : "device";
    ProfileInfo profileInfo = ProfileInfo.getProfileInfo(device);
    if (profileInfo == null) {
      String message = 
        NLS.bind(LaunchMessages.DIALOG_QUESTION_CONTINUE_EVEN_IF_IT_IS_UNKNOWN_PLATFORM, 
          deviceType);
      if (!continueToLaunch(
          LaunchMessages.DIALOG_TITLE_UNKNOWN_PLATFORM, 
          message, 
          LaunchMessages.DIALOG_TOGGLE_ALWAYS_LAUNCH_WITHOUT_ASKING, 
          "pref.launch.always.platform.is.unknown"))
        return false; 
      return true;
    } 
    String deviceProfile = profileInfo.getProfile();
    if (deviceProfile == null)
      deviceProfile = LaunchMessages.UNKNOWN_PROFILE; 
    String deviceParentProfile = profileInfo.getParentProfile();
    if (deviceParentProfile == null)
      deviceParentProfile = LaunchMessages.UNKNOWN_PROFILE; 
    if (!appProfile.equalsIgnoreCase("tizen") && !deviceParentProfile.equalsIgnoreCase(appProfile) && 
      !deviceProfile.equalsIgnoreCase(appProfile) && 
      !canLaunch(device, appProfile)) {
      String[] bindWord = { appProfile, deviceProfile, deviceType };
      String message = 
        NLS.bind(LaunchMessages.DIALOG_QUESTION_CONTINUE_EVEN_IF_IT_IS_DIFFERENT_PROFILE, 
          (Object[])bindWord);
      boolean checked = checkAlwaysAsking();
      if (!continueToLaunch(
          LaunchMessages.DIALOG_TITLE_DIFFERENT_PROFILE, 
          message, 
          LaunchMessages.DIALOG_TOGGLE_ALWAYS_LAUNCH_WITHOUT_ASKING, 
          "pref.launch.always.platform.is.different"))
        return false; 
      final String notiMessage = NLS.bind(LaunchMessages.INCOMPATIBLE_PROFILE, appProfile, deviceParentProfile);
      boolean checkedAgain = checkAlwaysAsking();
      ProfileInfo info = InstallProfileConfig.getProfileInfoToProfile(deviceProfile);
      if (checked && checkedAgain && info != null && !info.isCustomProfile())
        SWTUtil.asyncExec(new Runnable() {
              public void run() {
                NotificationTrayPopup.notify(LaunchMessages.INCOMPATIBLE_TITLE_PROFILE, notiMessage, NotificationIconType.INFO);
              }
            }); 
    } 
    return true;
  }
  
  private static boolean checkAlwaysAsking() {
    IPreferenceStore ps = CommonPlugin.getDefault().getPreferenceStore();
    return ps.getBoolean("pref.launch.always.platform.is.different");
  }
  
  private static boolean canLaunch(IDevice device, String appProfile) {
    try {
      PlatformCapability pc = new PlatformCapability((Device)device);
      String canLaunchValue = pc.getCanLaunch();
      if (canLaunchValue == null) {
        logger.debug("Can launch value is null");
        return false;
      } 
      String[] canLaunchValues = canLaunchValue.trim().split("_-_");
      byte b;
      int i;
      String[] arrayOfString1;
      for (i = (arrayOfString1 = canLaunchValues).length, b = 0; b < i; ) {
        String canLaunch = arrayOfString1[b];
        if (canLaunch.equals(appProfile) && !canLaunch.equals("unknown"))
          return true; 
        b++;
      } 
    } catch (IOException e) {
      logger.debug("Could not get platform capability", e);
      return false;
    } 
    return false;
  }
  
  public static boolean continueToLaunch(String title, String message, String toogleMessage, String togglePreferenceKey) {
    ShowQuestionToggleDialog dialogRunnable = new ShowQuestionToggleDialog(title, message, toogleMessage, togglePreferenceKey);
    Display.getDefault().syncExec(dialogRunnable);
    return dialogRunnable.wantToContinue();
  }
}
