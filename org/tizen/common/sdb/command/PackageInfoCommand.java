package org.tizen.common.sdb.command;

import org.tizen.common.TizenPlatformConstants;
import org.tizen.common.ui.view.console.ITizenConsoleManager;
import org.tizen.sdblib.IDevice;
import org.tizen.sdblib.receiver.PackageInfo;
import org.tizen.sdblib.service.ApplicationCmdService;

public class PackageInfoCommand extends ApplicationCommand {
  public static final String CHECK_ROMOVABLE_COMMAND = "pkginfo --pkg %s | grep -i Removable";
  
  public static final String CHECK_INSTALLED_COMMAND = "pkgcmd -l | grep %s --word-regexp";
  
  public static final String GET_PACKAGE_ID_COMMAND = "pkginfo --app %s | grep %s";
  
  public static final String IS_RUNNING_COMMAND = "pkgcmd -C -n %s";
  
  private String command = "";
  
  public PackageInfoCommand(IDevice device, ITizenConsoleManager console) {
    super(device, console);
  }
  
  public boolean isRemovable(String pkgId) throws Exception {
    if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = getApplicationCmdService();
      PackageInfo pkgInfo = appcmd.queryPackageInfo(pkgId);
      if (pkgInfo == null)
        return false; 
      return pkgInfo.getIsRemovable();
    } 
    this.command = String.format("pkginfo --pkg %s | grep -i Removable", new Object[] { pkgId });
    executeCommand();
    String[] lines = this.helper.getResultLineStrings();
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = lines).length, b = 0; b < i; ) {
      String line = arrayOfString1[b];
      if (TizenPlatformConstants.ROAPP_RESULT.equals(line))
        return false; 
      if (TizenPlatformConstants.RWAPP_RESULT.equals(line))
        return true; 
      b++;
    } 
    return false;
  }
  
  public boolean isInstalled(String pkgId, String pkgType) throws Exception {
    if (ApplicationCommand.isAppCmdSupported(getDevice())) {
      ApplicationCmdService appcmd = getApplicationCmdService();
      PackageInfo pkgInfo = appcmd.queryPackageInfo(pkgId);
      if (pkgInfo != null)
        return true; 
    } else {
      this.command = String.format("pkgcmd -l | grep %s --word-regexp", new Object[] { pkgId });
      executeCommand();
      String endLine = this.helper.getEndLine();
      if (endLine != null && !endLine.isEmpty())
        return true; 
    } 
    return false;
  }
  
  public String getPkgId(String appId) throws Exception {
    String packageStr = "Package: ";
    this.command = String.format("pkginfo --app %s | grep %s", new Object[] { appId, packageStr });
    executeCommand();
    String[] lines = this.helper.getResultLineStrings();
    String pkgId = "";
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = lines).length, b = 0; b < i; ) {
      String line = arrayOfString1[b];
      if (line.startsWith(packageStr)) {
        pkgId = line.substring(packageStr.length());
        break;
      } 
      b++;
    } 
    return pkgId;
  }
  
  public boolean isRunning(String pkgId) throws Exception {
    String isRunningStr = "is Running";
    this.command = String.format("pkgcmd -C -n %s", new Object[] { pkgId });
    executeCommand();
    boolean result = false;
    String[] lines = this.helper.getResultLineStrings();
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = lines).length, b = 0; b < i; ) {
      String line = arrayOfString1[b];
      if (line.endsWith(isRunningStr)) {
        result = true;
        break;
      } 
      b++;
    } 
    return result;
  }
  
  protected void executeCommand() throws Exception {
    this.helper = new SdbCommandHelper(getDevice(), getConsole(), null);
    this.helper.runCommand(getCommand());
  }
  
  protected String getCommand() {
    return this.command;
  }
}
